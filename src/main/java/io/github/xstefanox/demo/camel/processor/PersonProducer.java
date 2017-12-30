package io.github.xstefanox.demo.camel.processor;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.github.xstefanox.demo.camel.message.PersonMessage;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonProducer.class);

    private static final Fairy FAIRY = Fairy.create();

    @Handler
    public PersonMessage produce() {

        final Person person = FAIRY.person();

        final PersonMessage m = new PersonMessage(
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getAddress().getAddressLine1());

        LOGGER.info("producing {}", m);

        return m;
    }
}
