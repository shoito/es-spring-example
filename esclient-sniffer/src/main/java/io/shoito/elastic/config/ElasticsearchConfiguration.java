package io.shoito.elastic.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfiguration {
    @Bean
    RestClient restClient() {
        int socketTimeout = (3 * 3000) + 1000;
        SniffOnFailureListener sniffOnFailureListener =
                new SniffOnFailureListener();
        RestClient restClient = RestClient.builder(
                new HttpHost("172.31.43.44", 9200, "http"),
                new HttpHost("172.31.27.103", 9200, "http"),
                new HttpHost("172.31.2.159", 9200, "http"))
                .setFailureListener(sniffOnFailureListener)
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setMaxConnPerRoute(100)
                        .setMaxConnTotal(300))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(30 * 1000) //ms
                        .setSocketTimeout(socketTimeout)
                        .setConnectionRequestTimeout(30 * 1000))
                .build();
        Sniffer sniffer = Sniffer.builder(restClient)
                .build();
        sniffOnFailureListener.setSniffer(sniffer);
        return restClient;
    }
}
