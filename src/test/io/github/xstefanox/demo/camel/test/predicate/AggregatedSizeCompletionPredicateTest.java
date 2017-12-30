package io.github.xstefanox.demo.camel.test.predicate;

import io.github.xstefanox.demo.camel.aggregator.SizeAwareAggregationStrategy;
import io.github.xstefanox.demo.camel.predicate.AggregatedSizeCompletionPredicate;
import org.apache.camel.Exchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AggregatedSizeCompletionPredicateTest {

    @Mock
    private SizeAwareAggregationStrategy aggregationStrategy;

    @Mock
    private Exchange exchange;

    @Test(expected = IllegalArgumentException.class)
    public void maxSizeLesserThanOneShouldBeRejected() {
        new AggregatedSizeCompletionPredicate(0, aggregationStrategy);
    }

    @Test
    public void aggregatedSizeLesserThanMaxSizeShouldNotCloseTheAggregation() {

        when(aggregationStrategy.getSize(eq(exchange))).thenReturn(5);

        final AggregatedSizeCompletionPredicate completionPredicate = new AggregatedSizeCompletionPredicate(10, aggregationStrategy);

        assertFalse("aggregated size lesser that max size should not close the aggregation", completionPredicate.matches(exchange));
    }

    @Test
    public void aggregatedSizeEqualToMaxSizeShouldCloseTheAggregation() {

        when(aggregationStrategy.getSize(eq(exchange))).thenReturn(10);

        final AggregatedSizeCompletionPredicate completionPredicate = new AggregatedSizeCompletionPredicate(10, aggregationStrategy);

        assertTrue("aggregated size equal to max size should close the aggregation", completionPredicate.matches(exchange));
    }

    @Test
    public void aggregatedSizeGreaterThanMaxSizeShouldCloseTheAggregation() {

        when(aggregationStrategy.getSize(eq(exchange))).thenReturn(15);

        final AggregatedSizeCompletionPredicate completionPredicate = new AggregatedSizeCompletionPredicate(10, aggregationStrategy);

        assertTrue("aggregated size greater than max size should close the aggregation", completionPredicate.matches(exchange));
    }
}
