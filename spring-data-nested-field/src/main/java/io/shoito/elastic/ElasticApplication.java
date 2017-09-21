package io.shoito.elastic;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.elasticsearch.common.util.set.Sets.newHashSet;

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
    private ElasticsearchTemplate elasticsearchTemplate;

    private void indexMales() {
        List<IndexQuery> indexQueries = new ArrayList<>();
        Arrays.asList(
                Male.builder().userId("1").chest(90).prefectureId(12).build(),
                Male.builder().userId("2").chest(85).prefectureId(13).build(),
                Male.builder().userId("3").chest(83).prefectureId(13).build(),
                Male.builder().userId("4").chest(88).prefectureId(14)
                        .excludeUsers(newHashSet("13", "14")).build() // id:13, 14の女性をブロック
        ).forEach(m -> indexQueries.add(new IndexQueryBuilder().withId(m.getUserId()).withObject(m).build()));
        elasticsearchTemplate.bulkIndex(indexQueries);

        Male m5 = Male.builder().userId("5").chest(88).prefectureId(14).build();
        elasticsearchTemplate.index(new IndexQueryBuilder().withId(m5.getUserId()).withObject(m5).build());
    }

    private void indexFemales() {
        List<IndexQuery> indexQueries = new ArrayList<>();
        Arrays.asList(
                Female.builder().userId("11").bust(78).prefectureId(12).build(),
                Female.builder().userId("12").bust(82).prefectureId(13).build(),
                Female.builder().userId("13").bust(88).prefectureId(14)
                        .excludeUsers(newHashSet("1", "2")).build() // id:1, 2の男性をブロック
        ).forEach(f -> indexQueries.add(new IndexQueryBuilder().withId(f.getUserId()).withObject(f).build()));
        elasticsearchTemplate.bulkIndex(indexQueries);

        Female f4 = Female.builder().userId("14").bust(86).prefectureId(13).build();
        elasticsearchTemplate.index(new IndexQueryBuilder().withId(f4.getUserId()).withObject(f4).build());
    }

    private void searchMales() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(Male.INDEX)
                .withTypes(Male.TYPE)
                .withQuery(
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.rangeQuery("chest").lte(88))
                                .must(QueryBuilders.termQuery("prefectureId", 13))
                                .mustNot(QueryBuilders.termQuery("excludeUsers", "13")) // id:13の女性の検索クエリを想定
                )
                .withPageable(PageRequest.of(0, 100))
                .withSort(SortBuilders.fieldSort("chest").sortMode(SortMode.MIN))
                .build();
        System.out.println(searchQuery.getQuery().toString());
        elasticsearchTemplate.queryForList(searchQuery, Male.class).forEach(System.out::println);
    }

    private void searchFemales() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(Female.INDEX)
                .withTypes(Female.TYPE)
                .withQuery(QueryBuilders.matchAllQuery())
                .withQuery(
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.rangeQuery("bust").gte(80))
                                .must(QueryBuilders.termQuery("prefectureId", 13))
                                .mustNot(QueryBuilders.termQuery("excludeUsers", "1")) // id:1の男性の検索クエリを想定
                )
                .withPageable(PageRequest.of(0, 100))
                .withSort(SortBuilders.fieldSort("bust").sortMode(SortMode.MAX))
                .build();
        System.out.println(searchQuery.getQuery().toString());
        elasticsearchTemplate.queryForList(searchQuery, Female.class).forEach(System.out::println);
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
