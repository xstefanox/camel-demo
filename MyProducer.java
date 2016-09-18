package test;

import java.util.Random;
import java.util.UUID;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyProducer.class);

    @Handler
    public MyMessage produce() {

        Random r = new Random();

        MyMessage m = new MyMessage();
        m.setId(UUID.randomUUID().toString());
        m.setValid(r.nextBoolean());
        m.setChildren(r.nextInt(1000));

        LOGGER.info("producing {}", m);

        return m;
    }
}
