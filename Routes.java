package test;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JavaUuidGenerator;
import org.apache.camel.processor.aggregate.AggregationStrategyBeanAdapter;

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

@Singleton
public class Routes extends RouteBuilder {

    public static final String HEADER_JOB_ID = "JOB_ID";
    public static final String HEADER_COUNT = "COUNT";

    @Inject
    public Routes(
            final EndpointUriFactory endpointUriFactory,
            final Configuration configuration) {
        this.endpointUriFactory = endpointUriFactory;
        this.configuration = configuration;
    }

    @Override
    public void configure() throws Exception {

        from("timer:prova1?period=1000000")
                .setHeader(HEADER_JOB_ID, method(new JavaUuidGenerator()))
                .bean(MyProducer.class)
                .marshal().json(Jackson)
                .to("jms:new-messages")
        ;

        from("jms:new-messages")
                .unmarshal().json(Jackson, MyMessage.class)
                .split()
                    .method(MySplitter.class)
                    .streaming()
                    .parallelProcessing()
                .marshal().json(Jackson)
                .to("jms:splitted-messages")
        ;

        from("jms:splitted-messages?concurrentConsumers=15")
                .unmarshal().json(Jackson, MySplittedMessage.class)
                .bean(MyConsumer.class)
                .marshal().json(Jackson)
                .to("jms:splitted-results")
        ;


        AggregationStrategyBeanAdapter aggregationStrategy = new AggregationStrategyBeanAdapter(MyAggregator.class);
        aggregationStrategy.setAllowNullOldExchange(true);
        aggregationStrategy.setAllowNullNewExchange(true);

        from("jms:splitted-results?concurrentConsumers=5")
                .unmarshal().json(Jackson, MySplittedMessage.class)
                    .aggregate(header(HEADER_JOB_ID))
                    .aggregationStrategy(aggregationStrategy)
                    .aggregationRepository(new MyMemoryAggregationRepository())
                    .completionSize(header(HEADER_COUNT))
                    .parallelProcessing()
                .marshal().json(Jackson)
                .to("jms:results")
        ;
    }
}
