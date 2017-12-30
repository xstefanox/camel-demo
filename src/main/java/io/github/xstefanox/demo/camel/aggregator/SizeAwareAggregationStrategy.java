package io.github.xstefanox.demo.camel.aggregator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;

import static java.util.Objects.requireNonNull;

public class SizeAwareAggregationStrategy implements CompletionAwareAggregationStrategy {

    private final Map<Exchange, Integer> size = new ConcurrentHashMap<>();
    private final AbstractListAggregationStrategy aggregationStrategy;

    public SizeAwareAggregationStrategy(final AbstractListAggregationStrategy aggregationStrategy) {
        requireNonNull(aggregationStrategy, "aggregationStrategy must not be null");
        this.aggregationStrategy = aggregationStrategy;
    }

    @Override
    public void onCompletion(final Exchange exchange) {
        aggregationStrategy.onCompletion(exchange);
        size.remove(exchange);
    }

    @Override
    public Exchange aggregate(final @Nullable Exchange oldExchange, final @Nullable Exchange newExchange) {

        if (oldExchange == null && newExchange == null) {
            throw new IllegalArgumentException("old and new exchange cannot be both null");
        }

        final Exchange aggregate = aggregationStrategy.aggregate(oldExchange, newExchange);

        if (newExchange != null) {

            final byte[] bytes = (byte[]) newExchange.getIn().getBody();

            size.compute(aggregate, (k, v) -> {
                if (v == null) {
                    return bytes.length;
                } else {
                    return v + bytes.length;
                }
            });
        }

        return aggregate;
    }

    @Nullable
    public Integer getSize(final Exchange exchange) {
        return size.get(exchange);
    }
}
