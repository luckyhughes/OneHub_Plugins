package kg.apc.jmeter.reporters;

import kg.apc.jmeter.JMeterPluginsUtils;

import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.onetesthub.jmeter.OnetesthubAPIClient;
import com.onetesthub.jmeter.MySampleResult;
import com.onetesthub.jmeter.StatusNotifierCallback;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class OnetesthubUploader extends ResultCollector implements StatusNotifierCallback, Runnable, TestStateListener {

    private static final Logger log = LoggingManager.getLoggerForClass();
    public static final String TITLE = "title";
    public static final String COLOR = "color";
    public static final String UPLOAD_TOKEN = "uploadToken";
    public static final String PROJECT = "project";
    public static final String APIURL = "apiUrl";
    public static final String USE_ONLINE = "useOnline";
    private static final Object LOCK = new Object();
    private boolean isOnlineInitiated = true;
    private OnetesthubAPIClient apiClient;
    private BlockingQueue<SampleEvent> processingQueue;
    private Thread processorThread;
    private OnetesthubAggregator aggregator;
    private OnetesthubUploadingNotifier  perfMonNotifier = OnetesthubUploadingNotifier.getInstance();;
    
    

    public OnetesthubUploader() {
        super();
        //address = JMeterUtils.getPropDefault("loadosophia.address", "https://loadosophia.org/");
    }

    @Override
    public void testStarted(String host) {
        synchronized (LOCK) {
        	
        	log.info("In started test -1");
            this.apiClient = getAPIClient();

            try {
				initiateOnline();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        super.testStarted(host);
        log.info("In started test -2");
        perfMonNotifier.startCollecting();
        
        log.info("In started test -3");
    }

    @Override
    public void testEnded(String host) {
        super.testEnded(host);
        synchronized (LOCK) {
            
            if (isOnlineInitiated) {
                //finishOnline();
            }

        clearData();
        perfMonNotifier.endCollecting();
    }
    }
    
    private void initiateOnline() throws IOException {
        if (isUseOnline()) {
            log.info("In initiate online test -4");
			//informUser("Started active test: " + apiClient.startOnline());
			aggregator = new OnetesthubAggregator();
			processingQueue = new LinkedBlockingQueue<SampleEvent>();
			processorThread = new Thread(this);
			processorThread.setDaemon(true);
			isOnlineInitiated = true;
			processorThread.start();
        }
    }

    public void setProject(String proj) {
        setProperty(PROJECT, proj);
    }

    public String getProject() {
        return getPropertyAsString(PROJECT);
    }

    public void setUploadToken(String token) {
        setProperty(UPLOAD_TOKEN, token);
    }

    public String getUploadToken() {
        return getPropertyAsString(UPLOAD_TOKEN);
    }

    public void setTitle(String prefix) {
        setProperty(TITLE, prefix);
    }

    public String getTitle() {
        return getPropertyAsString(TITLE);
    }
    
    public void setApiUrl(String apiurl) {
        setProperty(APIURL, apiurl);
    }
    
    public String getApiUrl() {
        return getPropertyAsString(APIURL);
    }


    public void setColorFlag(String color) {
        setProperty(COLOR, color);
    }

    public String getColorFlag() {
        return getPropertyAsString(COLOR);
    }
    
    private void informUser(String string) {
        log.info(string);
        if (getVisualizer() != null && getVisualizer() instanceof OnetesthubUploaderGui) {
            ((OnetesthubUploaderGui) getVisualizer()).inform(string);
        } else {
            log.info(string);
        }
    }


    protected OnetesthubAPIClient getAPIClient() {
        return new OnetesthubAPIClient(this, getApiUrl(), getUploadToken(), getProject(), getColorFlag(), getTitle());
    }

    @Override
    public void notifyAbout(String info) {
        informUser(info);
    }

    public boolean isUseOnline() {
        return getPropertyAsBoolean(USE_ONLINE);
    }

    public void setUseOnline(boolean selected) {
        setProperty(USE_ONLINE, selected);
    }

    @Override
    public void sampleOccurred(SampleEvent event) {
        super.sampleOccurred(event);
        
        log.info("sampleOccurred -5");
        
        if (isOnlineInitiated) {
            try {
                if (!processingQueue.offer(event, 1, TimeUnit.SECONDS)) {
                    log.warn("Failed first dequeue insert try, retrying");
                    if (!processingQueue.offer(event, 1, TimeUnit.SECONDS)) {
                        log.error("Failed second try to inser into deque, dropped sample");
                    }
                }
            } catch (InterruptedException ex) {
                log.info("Interrupted while putting sample event into deque", ex);
            }
        }
    }

    @Override
    public void run() {
    	
    	log.info("Run Thread - 6");
    	
        while (isOnlineInitiated) {
            try {
                SampleEvent event = processingQueue.poll(1, TimeUnit.SECONDS);
                if (event != null) {

                	MySampleResult mysampleresult = getResponseObject(event.getResult());
                	aggregator.addSample(mysampleresult);
                	
                	log.info("call addsample and add object mysampleresult to buffer - 7");
                }

                if (aggregator.haveDataToSend()) {
                    try {
                    	log.info("aggregator.haveDataToSend -8");
                    	apiClient.sendOnlineData(aggregator.getDataToSend());
                    } catch (IOException ex) {
                        log.warn("Failed to send active test data", ex);
                    }
                }
            } catch (InterruptedException ex) {
                log.debug("Interrupted while taking sample event from deque", ex);
                break;
            }
        }
    }
    
    public MySampleResult getResponseObject(SampleResult result) throws InterruptedException
    {
    	
    	MySampleResult mysampleresult = new MySampleResult();
        if (result != null) {
        	
        	
        	log.info("result object result.getResponseCode() :" + result.getResponseCode());
        	log.info("result object result.getTime() :" + result.getTime());
        	log.info("result object result.isSuccessful() :" + result.isSuccessful());
        	log.info("result object result.getSampleLabel() :" + result.getSampleLabel());
        	log.info("result object result.getBytes() :" + result.getBytes());
        	
        	mysampleresult.setThreadName(result.getThreadName());
        	mysampleresult.setSampleLabel(result.getSampleLabel());
        	mysampleresult.setIsSuccessful(result.isSuccessful()?1:0);
        	mysampleresult.setLatency(result.getLatency());
        	mysampleresult.setReceivedBytes(result.getBytes());
        	//mysampleresult.setSentBytes(result.getSamplerData().length());
        	mysampleresult.setResponseTime(result.getTime());
        	mysampleresult.setThreadCount(result.getAllThreads());
        	mysampleresult.setResponseCode(result.getResponseCode());
        	mysampleresult.setEndTime(result.getEndTime());
        }
        return mysampleresult;
    	
    	
    }

    

    private void finishOnline() throws IOException {
        isOnlineInitiated = false;
        processorThread.interrupt();
        while (processorThread.isAlive() && !processorThread.isInterrupted()) {
            log.info("Waiting for aggregator thread to stop...");
            try {
                Thread.sleep(50);
                processorThread.interrupt();
            } catch (InterruptedException ex) {
                log.warn("Interrupted sleep", ex);
            }
        }
        log.info("Ending Onetesthub online test");
    }
}
