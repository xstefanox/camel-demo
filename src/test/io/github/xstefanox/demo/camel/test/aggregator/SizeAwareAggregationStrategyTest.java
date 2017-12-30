package io.github.xstefanox.demo.camel.test.aggregator;

import io.github.xstefanox.demo.camel.aggregator.SizeAwareAggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SizeAwareAggregationStrategyTest {

    private static final Integer IN_MESSAGE_SIZE_1 = 10;

    private static final Integer IN_MESSAGE_SIZE_2 = 5;

    private static final Integer IN_MESSAGE_SIZE_3 = 5;

    @Mock
    private AbstractListAggregationStrategy delegate;

    @Mock
    private Exchange aggregation1Exchange1;

    @Mock
    private Exchange aggregation1Exchange2;

    @Mock
    private Exchange aggregation2Exchange1;

    @Mock
    private Exchange aggregatedExchange1;

    @Mock
    private Exchange aggregatedExchange2;

    @Mock
    private Exchange otherExchange;

    @Mock
    private Message inMessage1;

    @Mock
    private Message inMessage2;

    @Mock
    private Message inMessage3;

    @InjectMocks
    private SizeAwareAggregationStrategy sizeAwareAggregationStrategy;

    @Before
    public void setUp() {

        // first aggregation
        when(delegate.aggregate(null, aggregation1Exchange1)).thenReturn(aggregatedExchange1);
        when(delegate.aggregate(aggregatedExchange1, aggregation1Exchange2)).thenReturn(aggregatedExchange1);
        when(delegate.aggregate(aggregatedExchange1, null)).thenReturn(aggregatedExchange1);

        // second aggregation
        when(delegate.aggregate(null, aggregation2Exchange1)).thenReturn(aggregatedExchange2);

        when(aggregation1Exchange1.getIn()).thenReturn(inMessage1);
        when(aggregation1Exchange2.getIn()).thenReturn(inMessage2);
        when(aggregation2Exchange1.getIn()).thenReturn(inMessage3);

        when(inMessage1.getBody()).thenReturn(new byte[IN_MESSAGE_SIZE_1]);
        when(inMessage2.getBody()).thenReturn(new byte[IN_MESSAGE_SIZE_2]);
        when(inMessage3.getBody()).thenReturn(new byte[IN_MESSAGE_SIZE_3]);
    }

    @Test
    public void theSizeOfNonAggregatedExchangeShouldNotBeReturned() {
        assertThat("the size of non aggregated exchange should not be reported", sizeAwareAggregationStrategy.getSize(otherExchange), nullValue());
    }

    @Test
    public void theInitialSizeOfTheAggregationShouldBeTheSizeOfTheFirstExchange() {

        final Exchange result = sizeAwareAggregationStrategy.aggregate(null, aggregation1Exchange1);

        assertThat("the aggregation should be delegated", result, equalTo(aggregatedExchange1));
        assertThat("the initial size of the aggregation should be the size of the first exchange", sizeAwareAggregationStrategy.getSize(result), equalTo(IN_MESSAGE_SIZE_1));
    }

    @Test
    public void incomingFirstNullExchangeShouldBeIgnored() {

        final Exchange result = sizeAwareAggregationStrategy.aggregate(aggregatedExchange1, null);

        assertThat("the aggregation should be delegated", result, equalTo(aggregatedExchange1));
        assertThat("the initial size of the aggregation should be the size of the first exchange", sizeAwareAggregationStrategy.getSize(result), nullValue());
    }

    @Test
    public void incomingNullExchangeShouldBeIgnored() {

        final Exchange firstAggregate = sizeAwareAggregationStrategy.aggregate(null, aggregation1Exchange1);

        final Exchange result = sizeAwareAggregationStrategy.aggregate(firstAggregate, null);

        assertThat("the aggregation should be delegated", result, equalTo(firstAggregate));
        assertThat("the initial size of the aggregation should be the size of the first exchange", sizeAwareAggregationStrategy.getSize(result), equalTo(IN_MESSAGE_SIZE_1));
    }

    @Test
    public void incomingExchnageSizeShouldBeAddedToTheCurrentSize() {

        final Exchange firstAggregate = sizeAwareAggregationStrategy.aggregate(null, aggregation1Exchange1);

        final Exchange secondAggregate = sizeAwareAggregationStrategy.aggregate(firstAggregate, aggregation1Exchange2);

        assertThat("the initial size of the aggregation should be the size of the first exchange", sizeAwareAggregationStrategy.getSize(secondAggregate), equalTo(IN_MESSAGE_SIZE_1 + IN_MESSAGE_SIZE_2));
    }

    @Test
    public void concurrentAggregationSizeShouldBeStoredSeparatedly() {

        // aggregation 1
        final Exchange firstAggregate = sizeAwareAggregationStrategy.aggregate(null, aggregation1Exchange1);

        // aggregation 2
        final Exchange secondAggregate = sizeAwareAggregationStrategy.aggregate(null, aggregation2Exchange1);

        // aggregation 1 again
        final Exchange thirdAggregate = sizeAwareAggregationStrategy.aggregate(firstAggregate, aggregation1Exchange2);

        assertThat("the first aggregation size should not be influenced by the second", sizeAwareAggregationStrategy.getSize(thirdAggregate), equalTo(IN_MESSAGE_SIZE_1 + IN_MESSAGE_SIZE_2));
        assertThat("the second aggregation size should not be influenced by the first", sizeAwareAggregationStrategy.getSize(secondAggregate), equalTo(IN_MESSAGE_SIZE_3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void oldAndNewExchangeCannotBeBothNull() {
        sizeAwareAggregationStrategy.aggregate(null, null);
    }
}
