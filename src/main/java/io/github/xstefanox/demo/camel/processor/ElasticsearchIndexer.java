package io.github.xstefanox.demo.camel.processor;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.xstefanox.demo.camel.MyRouteBuilder.Header.GROUP;
import static org.elasticsearch.common.xcontent.XContentType.JSON;

public class ElasticsearchIndexer implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndexer.class);

    private final RestHighLevelClient client;

    public ElasticsearchIndexer() {

        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    @Handler
    public void index(final @Body String person, final @Header(GROUP) String group) throws IOException {

        final IndexRequest indexRequest = new IndexRequest("idx_" + group, "person")
                .source(person, JSON);

        client.index(indexRequest);
    }

    @Handler
    public void bulkIndex(final @Body List<Message> body, final @Header(GROUP) String group) throws IOException {

        LOGGER.info("indexing {} persons", body.size());

        final BulkRequest bulkRequest = new BulkRequest();

        for (final Message message : body) {

            final IndexRequest indexRequest = new IndexRequest("idx_" + group, "person")
                    .source(new String(message.getBody(byte[].class)), JSON);

            bulkRequest.add(indexRequest);
        }

        client.bulk(bulkRequest);
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
