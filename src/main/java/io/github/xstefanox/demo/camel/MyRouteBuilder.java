package io.github.xstefanox.demo.camel;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.github.xstefanox.demo.camel.aggregator.HeaderForwardingAggregationStrategy;
import io.github.xstefanox.demo.camel.aggregator.SizeAwareAggregationStrategy;
import io.github.xstefanox.demo.camel.predicate.AggregatedSizeCompletionPredicate;
import io.github.xstefanox.demo.camel.processor.ElasticsearchIndexer;
import io.github.xstefanox.demo.camel.processor.PersonGenerator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedMessageAggregationStrategy;

import static io.github.xstefanox.demo.camel.MyRouteBuilder.Header.GROUP;
import static io.github.xstefanox.demo.camel.MyRouteBuilder.Header.RANDOM;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.apache.camel.LoggingLevel.DEBUG;
import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

public class MyRouteBuilder extends RouteBuilder {

    public static final class Header {

        private Header() {
        }

        public static final String GROUP = "GROUP";

        public static final Random RANDOM = new Random();
    }

    private static final Integer GROUP_COUNT = 1;
    private static final Integer MAX_SIZE = (int) (5 * Math.pow(1024, 2));   // 5 MB

    @Override
    public void configure() {

        final SizeAwareAggregationStrategy sizeAwareAggregationStrategy = new SizeAwareAggregationStrategy(new GroupedMessageAggregationStrategy());
        final AggregatedSizeCompletionPredicate completionPredicate = new AggregatedSizeCompletionPredicate(MAX_SIZE, sizeAwareAggregationStrategy);
        final HeaderForwardingAggregationStrategy headerForwardingAggregationStrategy = new HeaderForwardingAggregationStrategy(singleton(GROUP), sizeAwareAggregationStrategy);

        // @formatter:off

        from("timer:TIMER_1?period=1")
            .split()
                .method(new PersonGenerator(1000))
                .setHeader(GROUP, () -> RANDOM.nextInt(GROUP_COUNT))
                .marshal().json(Jackson)
            .to("seda:persons")
        ;

        from("seda:persons?concurrentConsumers=" + 100)
            .bean(ElasticsearchIndexer.class, "index")
        ;

//        from("seda:persons?concurrentConsumers=" + GROUP_COUNT)
//            .aggregate()
//                .header(GROUP)
//                .aggregationStrategy(headerForwardingAggregationStrategy)
//                .completionPredicate(completionPredicate)
//                .parallelProcessing()
//            .bean(ElasticsearchIndexer.class, "bulkIndex")
//        ;

        // @formatter:on
    }
}
