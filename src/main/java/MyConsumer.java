import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySplittedMessage.class);

    @Handler
    public MySplittedMessage consume(@Body MySplittedMessage mySplittedMessage) {

        LOGGER.info("procesing {}", mySplittedMessage);

        return mySplittedMessage;
    }
}
