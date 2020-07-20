package io.shoito.elastic;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
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
        for (;;) {
            try {
                search();
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private RestClient restClient;

    private void search() throws IOException {
        System.out.println("Searching...");
        Request request = new Request("GET", "/");
        request.addParameter("pretty", "true");
        Response response = restClient.performRequest(request);
        System.out.println(EntityUtils.toString(response.getEntity()));
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
