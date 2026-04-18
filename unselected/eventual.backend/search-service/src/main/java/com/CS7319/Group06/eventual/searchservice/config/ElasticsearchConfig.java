package com.CS7319.Group06.eventual.searchservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch client configuration.
 * Connects to the shared 3-node ES cluster defined in /elasticsearch/docker-compose.yml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchConfig {

    private String eventsIndex;
    private String groupsIndex;

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUri;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Parse the URI to extract host and port
        String uri = elasticsearchUri.replace("http://", "").replace("https://", "");
        String host = uri.contains(":") ? uri.substring(0, uri.lastIndexOf(':')) : uri;
        int port = uri.contains(":") ? Integer.parseInt(uri.substring(uri.lastIndexOf(':') + 1)) : 9200;

        RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
