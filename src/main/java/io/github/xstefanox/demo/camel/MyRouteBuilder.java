package io.github.xstefanox.demo.camel;

import io.github.xstefanox.demo.camel.aggregator.SizeAwareAggregationStrategy;
import io.github.xstefanox.demo.camel.predicate.AggregatedSizeCompletionPredicate;
import io.github.xstefanox.demo.camel.processor.PersonProducer;
import io.github.xstefanox.demo.camel.processor.ResultsPrinter;
import java.util.Random;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedMessageAggregationStrategy;

import static io.github.xstefanox.demo.camel.MyRouteBuilder.Header.GROUP;
import static io.github.xstefanox.demo.camel.MyRouteBuilder.Header.RANDOM;
import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

public class MyRouteBuilder extends RouteBuilder {

    public static final class Header {

        private Header() {
        }

        public static final String GROUP = "GROUP";

        public static final Random RANDOM = new Random();
    }

    private static final Integer MAX_SIZE = (int) (5 * Math.pow(1024, 1));
//    private static final Integer MAX_SIZE = 500;

    @Override
    public void configure() {

        final GroupedMessageAggregationStrategy groupedMessageAggregationStrategy = new GroupedMessageAggregationStrategy();
        final SizeAwareAggregationStrategy aggregationStrategy = new SizeAwareAggregationStrategy(groupedMessageAggregationStrategy);
        final AggregatedSizeCompletionPredicate completionPredicate = new AggregatedSizeCompletionPredicate(MAX_SIZE, aggregationStrategy);

        // @formatter:off

        from("timer:TIMER_1?period=1")
            .bean(PersonProducer.class)
            .setHeader(GROUP, () -> RANDOM.nextInt(1))
            .marshal().json(Jackson)
            .to("seda:persons")
        ;

        from("seda:persons?concurrentConsumers=5")
            .aggregate()
                .header(GROUP)
                .aggregationStrategy(aggregationStrategy)
                .completionPredicate(completionPredicate)
                .parallelProcessing()
            .bean(ResultsPrinter.class)
        ;

        // @formatter:on
    }
}
