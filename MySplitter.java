package test;

import java.util.Iterator;
import java.util.stream.IntStream;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static test.HEADER_COUNT;

public class MySplitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySplitter.class);

    @Handler
    public Iterator<MySplittedMessage> split(@Body MyMessage myMessage, Exchange exchange) {

        exchange.getOut().setHeader(HEADER_COUNT, myMessage.getChildren());

        return IntStream
                .range(0, myMessage.getChildren())
                .mapToObj(i -> {

                    MySplittedMessage m = new MySplittedMessage();
                    m.setId(myMessage.getId());
                    m.setNumber(i);

                    LOGGER.info("creating split {}", m);

                    return m;
                })
                .iterator();
    }
}
