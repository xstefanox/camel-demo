package io.github.xstefanox.demo.camel.processor;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.camel.Handler;

import static java.util.stream.Collectors.toList;

public class PersonGenerator {

    private static final Fairy FAIRY = Fairy.create();

    private final Integer multiplier;

    public PersonGenerator(final Integer multiplier) {
        this.multiplier = multiplier;
    }

    @Handler
    public List<Person> generate() {

        return IntStream.range(0, multiplier)
                .mapToObj(i -> FAIRY.person())
                .collect(toList());
    }
}
