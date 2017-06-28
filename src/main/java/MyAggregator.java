import org.apache.camel.Handler;

public class MyAggregator {

    @Handler
    public MyAggregatedMessage aggregate(MyAggregatedMessage inMessage, MySplittedMessage outMessage) {

        if (inMessage == null) {
            inMessage = new MyAggregatedMessage();
        }

        inMessage.setCount(inMessage.getCount() + 1);

        return inMessage;

    }
}
