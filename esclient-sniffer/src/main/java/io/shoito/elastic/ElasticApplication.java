package io.shoito.elastic;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;
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
            }
        }
    }

    @Autowired
    private RestClient restClient;

    private void search() throws IOException {
        Request request = new Request("GET", "/");
        request.addParameter("pretty", "true");
        try {
            restClient.performRequest(request);
        } catch (ResponseException e) {
            System.err.println("Error: " + e.getResponse().getStatusLine());
        } catch (IOException e) {
            System.err.println("Request failed.");
        }
    }
}
