package kg.apc.jmeter.reporters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.onetesthub.jmeter.AggregatePerfMetrics;
import com.onetesthub.jmeter.InfluxObject;
import com.onetesthub.jmeter.JsonObject;
import com.onetesthub.jmeter.MySampleResult;
import com.onetesthub.jmeter.SamplePerfMetrics;

public class OnetesthubAggregator {

    private static final Logger log = LoggingManager.getLoggerForClass();
    private SortedMap<Long, List<MySampleResult>> buffer = new TreeMap<Long, List<MySampleResult>>();
    private static final long SEND_SECONDS = 3;
    private long lastTime = 0;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public void addSample(MySampleResult mysampleresult) {
        if (log.isDebugEnabled()) {
            log.debug("Got sample to process: " + mysampleresult);
        }
        
        log.info("Got sample to process: " + mysampleresult);

        Long time = mysampleresult.getEndTime() / 1000;
        if (!buffer.containsKey(time)) {
            // we need to create new sec list
            if (time < lastTime) {
                // a problem with times sequence - taking last available
                Iterator<Long> it = buffer.keySet().iterator();
                while (it.hasNext()) {
                    time = it.next();
                }
            }
            buffer.put(time, new LinkedList<MySampleResult>());
        }
        lastTime = time;
        buffer.get(time).add(mysampleresult);
        
        log.info("Buffer size is : " + buffer.size());
        
        log.info("lastTime is : " + lastTime);
    }

    public boolean haveDataToSend() {
        return buffer.size() > SEND_SECONDS + 1;
    }

    public List<InfluxObject> getDataToSend() {
    	
        //JSONArray data = new JSONArray();
    	
    	List<InfluxObject> data = new ArrayList<InfluxObject>();
        
        Iterator<Long> it = buffer.keySet().iterator();
        int cnt = 0;
        while (cnt < SEND_SECONDS && it.hasNext()) {      	
            Long sec = it.next();
            List<MySampleResult> mysampleresultList = buffer.get(sec);
            
            //data = aggregateMetricsSecond(mysampleresultList);
            
            data.addAll(aggregateMetricsSecond(mysampleresultList));
            it.remove();
            cnt++;
            
            log.info("every second list of samples size is : " + mysampleresultList.size());
            log.info("every second sent sample data element is : " + mysampleresultList.get(0).getSampleLabel());
        }
        
        log.info("data returned as influxdb object size is : " + data.size());
        log.info("first element in influxdb object  is : " + data.get(1).getName());
        log.info("first element in influxdb object  is : " + data.get(2).getName());
        return data;
        
    }
    
    public List<InfluxObject> aggregateMetricsSecond(List<MySampleResult> mysampleresultList ) {
    	
    	//map to store sampled bale aggregated data every second
    	SamplePerfMetrics samplemetrics = new SamplePerfMetrics();
    	HashMap<String, SamplePerfMetrics> perfmetricsMap = new HashMap<String,SamplePerfMetrics>();
    	
    	List<SamplePerfMetrics> listSamplePerfMetrics = new ArrayList<SamplePerfMetrics>();
    	
    	//object to store aggregate perf metrics
    	AggregatePerfMetrics perfmetrics = new AggregatePerfMetrics();
    	
    	String sampleLabel;
    	long sampleResponseTime=0;
        long sampleLatency=0;
        long sampleCountResponseCode2xx=0;
        long sampleCountResponseCodeNon2xx=0;
        long sampleCountSuccessSample=0; 
        long sampleCountNonSuccessSample=0;
        long countSampleLabel=0;
        String sampleResponseCode;
        long sampleIsSuccessful=0;
        
        
        long isSuccessful;
        long countResponseCode2xx = 0;
        long countResponseCodeNon2xx = 0;
        long countSuccessSample = 0; 
        long countNonSuccessSample=0;
        long countTotalThread = 0;
        long bytesReceived = 0;
        long bytesSent = 0;
        long responseTime = 0;
        long counter=0;
        
        int listSize=0;
        listSize = mysampleresultList.size();
        
        List<InfluxObject> influxObjectList = new ArrayList<InfluxObject>();
    	
    	
    	for (MySampleResult mysampleresult:mysampleresultList) {
    		
    			
    			
    			counter++;
	    		sampleLabel= mysampleresult.getSampleLabel();
	    		sampleResponseTime = mysampleresult.getResponseTime();
	    		sampleLatency = mysampleresult.getLatency();
	    		sampleResponseCode = mysampleresult.getResponseCode();
	    		//returns 1 if success otherwise 0
	    		sampleIsSuccessful = mysampleresult.getIsSuccessful();
	    		
	    		if(mysampleresult.getResponseCode().contains("20")) {
	    			sampleCountResponseCode2xx = 1; 
	    		}
	    		else{
	    			sampleCountResponseCodeNon2xx = 1;
	    		}
	    		
	    		
	    		
	    		if (!perfmetricsMap.containsKey(sampleLabel)){
	    			
	    			samplemetrics.setSampleLabel(sampleLabel);
		    		samplemetrics.setSampleResponseTime(sampleResponseTime);
		    		samplemetrics.setSampleLatency(sampleLatency);
		    		samplemetrics.setSampleCountSuccess(sampleIsSuccessful);	    		
		    		samplemetrics.setSampleCountNonSuccess(sampleIsSuccessful);
		    		samplemetrics.setSampleCountResponseCode2xx(sampleCountResponseCode2xx);
		    		samplemetrics.setSampleCountResponseCodeNon2xx(sampleCountResponseCodeNon2xx);
		    		samplemetrics.setCountSampleLabel(1);
		    		
	    			perfmetricsMap.put(sampleLabel, samplemetrics);
	    		}
	    		else if(perfmetricsMap.containsKey(sampleLabel)){
	    			
	    			samplemetrics = perfmetricsMap.get(sampleLabel);
	    			
	    			samplemetrics.setSampleResponseTime(samplemetrics.getSampleResponseTime()+sampleResponseTime);
	    			samplemetrics.setSampleLatency(samplemetrics.getSampleLatency()+sampleLatency);
	    			samplemetrics.setSampleCountSuccess(samplemetrics.getSampleCountSuccess()+sampleIsSuccessful);
	    			samplemetrics.setSampleCountNonSuccess(samplemetrics.getSampleCountNonSuccess() + sampleIsSuccessful);
	    			samplemetrics.setSampleCountResponseCode2xx(samplemetrics.getSampleCountResponseCode2xx() + sampleCountResponseCode2xx);
	    			samplemetrics.setSampleCountResponseCodeNon2xx(samplemetrics.getSampleCountResponseCodeNon2xx() + sampleCountResponseCodeNon2xx);
	    			samplemetrics.setCountSampleLabel(samplemetrics.getCountSampleLabel() + 1);
	    			
	    			perfmetricsMap.put(sampleLabel, samplemetrics);
	    			
	    			
	    		}
	    		
	    		if (counter == listSize){
	    			
	    			for (String key : perfmetricsMap.keySet()) {
	    				
	    			SamplePerfMetrics sampleperfmetrics = new SamplePerfMetrics();
	    			
	    			sampleperfmetrics= perfmetricsMap.get(key);
	    			
	    			log.info("\nkey name in agrregate metrics method is :" + key);
	    			
	    			int labelCount = (int) sampleperfmetrics.getCountSampleLabel();
	    			
	    			sampleperfmetrics.setSampleResponseTime(sampleperfmetrics.getSampleResponseTime()/labelCount);
	    			sampleperfmetrics.setSampleLatency(sampleperfmetrics.getSampleLatency()/labelCount);
	    			sampleperfmetrics.setSampleCountSuccess(sampleperfmetrics.getSampleCountSuccess()/labelCount);
	    			sampleperfmetrics.setSampleCountNonSuccess(sampleperfmetrics.getSampleCountNonSuccess()/labelCount);
	    			sampleperfmetrics.setSampleCountResponseCode2xx(sampleperfmetrics.getSampleCountResponseCode2xx()/labelCount);
	    			sampleperfmetrics.setSampleCountResponseCodeNon2xx(sampleperfmetrics.getSampleCountResponseCodeNon2xx()/labelCount);
	    			
	    			//listSamplePerfMetrics.add(sampleperfmetrics);
	    			
	    			//InfluxObject influxObject = new InfluxObject();
	    			
	    			log.info("\n key name in agrregate metrics method is :" + key);
	    			
	    			log.info("\n sampleperfmetrics.getSampleLabel() is :" + sampleperfmetrics.getSampleLabel());
	    			
	    			log.info("\n Long.toString(sampleperfmetrics.getSampleResponseTime()) is :" + Long.toString(sampleperfmetrics.getSampleResponseTime()));
	    			
	    			
	    			String name = "TransactionMetrics";
	    			List<String> columnsList = new ArrayList<String>();
	    			ArrayList<Object> pointsList = new ArrayList<Object>();
	    			
	    			ArrayList<ArrayList<Object>> ArrpointsList = new ArrayList<ArrayList<Object>>();
	    			
	    			
	    			
	    			columnsList.add("TransactionName");
	    			pointsList.add(sampleperfmetrics.getSampleLabel());
	    			
	    			columnsList.add("SampleResponseTime");
	    			pointsList.add(sampleperfmetrics.getSampleResponseTime());
	    			
	    			columnsList.add("SampleLatency");
	    			pointsList.add(sampleperfmetrics.getSampleLatency());
	    			
	    			columnsList.add("SampleCountSuccess");
	    			pointsList.add(sampleperfmetrics.getSampleCountSuccess());
	    			
	    			columnsList.add("SampleCountNonSuccess");
	    			pointsList.add(sampleperfmetrics.getSampleCountNonSuccess());
	    			
	    			columnsList.add("SampleCountResponseCode2xx");
	    			pointsList.add(sampleperfmetrics.getSampleCountResponseCode2xx());
	    			
	    			columnsList.add("SampleCountResponseCodeNon2xx");
	    			pointsList.add(sampleperfmetrics.getSampleCountResponseCodeNon2xx());
	    			
	    			columnsList.add("SampleCount");
	    			pointsList.add(sampleperfmetrics.getCountSampleLabel());
	    			
	    			ArrpointsList.add(pointsList);
	    			
	    			influxObjectList.add(new InfluxObject(name,columnsList,ArrpointsList));
	    			
	    		 }
	    			
	    			
	    	}
	    		
	    		
	    		//get aggregate of all samples, later divide by count to get average for all samples in a second
	    		countTotalThread += mysampleresult.getThreadCount();
	    		responseTime += sampleResponseTime;
	    		bytesReceived += mysampleresult.getReceivedBytes();
	    		//bytesSent += mysampleresult.getSentBytes();
	    		countSuccessSample += mysampleresult.getIsSuccessful();
	    		countNonSuccessSample = counter - countSuccessSample;
	    		if(mysampleresult.getResponseCode().contains("20")) {
	    			countResponseCode2xx++; 
	    		}
	    		else{
	    			countResponseCodeNon2xx++;
	    		}
    		
    	}
    	
    	String aname = "AggregateMetrics";
		List<String> acolumnsList = new ArrayList<String>();
		ArrayList<Object> apointsList = new ArrayList<Object>();
		
		ArrayList<ArrayList<Object>> finalListPoints = new ArrayList<ArrayList<Object>>();
		
		acolumnsList.add("bytesReceived");
		apointsList.add(bytesReceived);
		
//		acolumnsList.add("bytesSent");
//		apointsList.add(Long.toString(bytesReceived));
		
		acolumnsList.add("countResponseCode2xx");
		apointsList.add(countResponseCode2xx);
		
		acolumnsList.add("countResponseCodeNon2xx");
		apointsList.add(countResponseCodeNon2xx);
		
		acolumnsList.add("countNonSuccessSample");
		apointsList.add(countNonSuccessSample);
		
		acolumnsList.add("countSuccessSample");
		apointsList.add(countSuccessSample);
		
		acolumnsList.add("countTotalThread");
		apointsList.add(countTotalThread);
		
		acolumnsList.add("responseTime");
		apointsList.add(responseTime);
		
		finalListPoints.add(apointsList);
		
		influxObjectList.add(new InfluxObject(aname,acolumnsList,finalListPoints));
		
		return influxObjectList;
		
    	
		//    	AggregatePerfMetrics aggMetrics = new AggregatePerfMetrics();
		//    	
		//    	aggMetrics.setBytesReceived(bytesReceived);
		//    	aggMetrics.setBytesSent(bytesSent);
		//    	aggMetrics.setCountResponseCode2xx(countResponseCode2xx);
		//    	aggMetrics.setCountResponseCodeNon2xx(countResponseCodeNon2xx);
		//    	aggMetrics.setCountSampleNonSuccessSample(counter - countSuccessSample);
		//    	aggMetrics.setCountSuccessSample(countSuccessSample);
		//    	aggMetrics.setCountTotalThread(countTotalThread);
		//    	aggMetrics.setResponseTime(responseTime);
    	
    	
    }

    
}
