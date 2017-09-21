package io.shoito.elastic;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.util.set.Sets;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@Slf4j
@SpringBootApplication(scanBasePackages = "io.shoito.elastic")
public class ElasticApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ElasticApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        indexMales();
        indexFemales();

        searchMales();
        searchFemales();
    }

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private void indexMales() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(Male.INDEX, Male.TYPE, "1")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("chest", 90)
                        .field("prefectureId", 12)
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
        bulkRequest.add(new IndexRequest(Male.INDEX, Male.TYPE, "2")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("chest", 85)
                        .field("prefectureId", 13)
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
        bulkRequest.add(new IndexRequest(Male.INDEX, Male.TYPE, "3")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("chest", 83)
                        .field("prefectureId", 13)
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
        bulkRequest.add(new IndexRequest(Male.INDEX, Male.TYPE, "4")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("chest", 88)
                        .field("prefectureId", 14)
                        .field("excludeUsers", Sets.newHashSet("13", "14"))
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
        restHighLevelClient.bulk(bulkRequest);

        restHighLevelClient.index(new IndexRequest(Male.INDEX, Male.TYPE, "5")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("chest", 88)
                        .field("prefectureId", 14)
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
    }

    private void indexFemales() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(Female.INDEX, Female.TYPE, "11")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("bust", 78)
                        .field("prefectureId", 12)
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
        bulkRequest.add(new IndexRequest(Female.INDEX, Female.TYPE, "12")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("bust", 82)
                        .field("prefectureId", 13)
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
        bulkRequest.add(new IndexRequest(Female.INDEX, Female.TYPE, "13")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("bust", 88)
                        .field("prefectureId", 14)
                        .field("excludeUsers", Sets.newHashSet("1", "2"))
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
        restHighLevelClient.bulk(bulkRequest);

        restHighLevelClient.index(new IndexRequest(Female.INDEX, Female.TYPE, "14")
                .source(JsonXContent.contentBuilder().startObject()
                        .field("bust", 86)
                        .field("prefectureId", 13)
                        .endObject()
                ).timeout(TimeValue.timeValueSeconds(3)));
    }

    private void searchMales() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder()
                .must(QueryBuilders.rangeQuery("chest").lte(88))
                .must(QueryBuilders.termQuery("prefectureId", 13))
                .mustNot(QueryBuilders.termQuery("excludeUsers", "13")); // id:13の女性の検索クエリを想定
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);
        searchSourceBuilder.sort("chest", SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(Male.INDEX);
        searchRequest.types(Male.TYPE);
        searchRequest.source(searchSourceBuilder);

        restHighLevelClient.searchAsync(searchRequest, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                System.out.println(searchRequest.toString());
                searchResponse.getHits().iterator().forEachRemaining(h -> System.out.println(h.getSourceAsString()));
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void searchFemales() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder()
                .must(QueryBuilders.rangeQuery("bust").gte(80))
                .must(QueryBuilders.termQuery("prefectureId", 13))
                .mustNot(QueryBuilders.termQuery("excludeUsers", "1")); // id:13の女性の検索クエリを想定
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);
        searchSourceBuilder.sort("bust", SortOrder.ASC);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(Female.INDEX);
        searchRequest.types(Female.TYPE);
        searchRequest.source(searchSourceBuilder);

        restHighLevelClient.searchAsync(searchRequest, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                System.out.println(searchRequest.toString());
                searchResponse.getHits().iterator().forEachRemaining(h -> System.out.println(h.getSourceAsString()));
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }
/**
{
  "query": {
    "bool": {
      "must": [
         { "range": { "bust" : { "gte": 80 } } },
         { "term": {"prefectureId": 13 } }
      ],
      "must_not": [
         { "term": {"excludeUsers": 1 } }
      ],
      "should": []
    }
  },
  "from": 0,
  "size": 10
}
 */
}
