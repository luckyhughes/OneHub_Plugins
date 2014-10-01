package kg.apc.jmeter.reporters;

import java.io.Serializable;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class OnetesthubUploadingNotifier implements Serializable {

    private static final Logger log = LoggingManager.getLoggerForClass();
    private static OnetesthubUploadingNotifier instance;

    private OnetesthubUploadingNotifier() {
    }

    public static OnetesthubUploadingNotifier getInstance() {
        if (instance == null) {
            instance = new OnetesthubUploadingNotifier();
        }

        return instance;
    }

    public void startCollecting() {
        log.debug("Start files collection");
    }

    public void endCollecting() {
        log.debug("Ended files collection, clear files list");
       
    }

    
}
