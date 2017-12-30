package io.github.xstefanox.demo.camel.predicate;

import io.github.xstefanox.demo.camel.aggregator.SizeAwareAggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * A Camel {@link Predicate} that can be used in conjunction with {@link SizeAwareAggregationStrategy} to mark an
 * aggregation as completed when the size of the aggregated bodies is greater thatn a given value.
 */
public class AggregatedSizeCompletionPredicate implements Predicate {

    /**
     * The mimimum accepted value of the aggregation max size.
     */
    private static final Integer MAX_SIZE_LOWER_BOUND = 0;

    private final Integer maxSize;
    private final SizeAwareAggregationStrategy aggregationStrategy;

    public AggregatedSizeCompletionPredicate(
            final Integer maxSize,
            final SizeAwareAggregationStrategy aggregationStrategy) {

        requireNonNull(maxSize, "maxSize must not be null");
        requireNonNull(aggregationStrategy, "aggregationStrategy must not be null");

        if (maxSize <= MAX_SIZE_LOWER_BOUND) {
            throw new IllegalArgumentException("max size must be greater than " + MAX_SIZE_LOWER_BOUND);
        }

        this.maxSize = maxSize;
        this.aggregationStrategy = aggregationStrategy;
    }

    @Override
    public boolean matches(final Exchange exchange) {

        final Integer size = aggregationStrategy.getSize(exchange);

        return size != null && size >= maxSize;
    }
}
