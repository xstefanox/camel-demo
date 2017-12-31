package io.github.xstefanox.demo.camel.aggregator;

import java.util.Map;
import java.util.Set;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class HeaderForwardingAggregationStrategy implements CompletionAwareAggregationStrategy {

    private final Set<String> headers;
    private final AggregationStrategy aggregationStrategy;

    public HeaderForwardingAggregationStrategy(final Set<String> headers, final AggregationStrategy aggregationStrategy) {

        requireNonNull(headers, "headers must not be null");
        requireNonNull(aggregationStrategy, "aggregationStrategy must not be null");

        if (headers.isEmpty()) {
            throw new IllegalArgumentException("headers must not be empty");
        }

        this.headers = headers;
        this.aggregationStrategy = aggregationStrategy;
    }

    @Override
    public Exchange aggregate(final Exchange oldExchange, final Exchange newExchange) {

        final Exchange aggregate = aggregationStrategy.aggregate(oldExchange, newExchange);

        if (oldExchange == null) {
            final Map<String, Object> forwardedHeaders = newExchange.getIn()
                    .getHeaders()
                    .entrySet()
                    .stream()
                    .filter(entry -> headers.contains(entry.getKey()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            aggregate.getIn().setHeaders(forwardedHeaders);
        }

        return aggregate;
    }

    @Override
    public void onCompletion(final Exchange exchange) {
        if (aggregationStrategy instanceof CompletionAwareAggregationStrategy) {
            ((CompletionAwareAggregationStrategy) aggregationStrategy).onCompletion(exchange);
        }
    }
}
