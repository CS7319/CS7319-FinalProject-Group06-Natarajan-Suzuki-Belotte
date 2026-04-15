package com.CS7319.Group06.eventual.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Creates the Elasticsearch indices with their mappings on application startup if they do not already exist.
 *
 * @author harininatarajan
 */
@Slf4j
@Component
public class ElasticsearchIndexInitializer {

    private final ElasticsearchClient client;
    private final ElasticsearchConfig config;

    public ElasticsearchIndexInitializer(ElasticsearchClient client, ElasticsearchConfig config) {
        this.client = client;
        this.config = config;
    }

    @PostConstruct
    public void initialize() {
        createEventsIndex();
        createGroupsIndex();
    }

    //Create Index for Events
    private void createEventsIndex() {
        String index = config.getEventsIndex();
        try {
            boolean exists = client.indices().exists(e -> e.index(index)).value();
            if (exists) {
                log.info("Elasticsearch index '{}' already exists — skipping creation", index);
                return;
            }
            /**
             * Event index description
             *
             * Text fields (title, description, name) are analyzed for full-text search.
             * Keyword fields (event_type, organizer_email, category_types) are used for exact filters.
             * Date fields support range queries.
             */
            client.indices().create(c -> c.index(index).mappings(m -> m
                            .properties("title", p -> p.text(t -> t))
                            .properties("description", p -> p.text(t -> t))
                            .properties("location", p -> p.text(t -> t.fields("keyword", f -> f.keyword(k -> k))))
                            .properties("start_datetime", p -> p.date(d -> d))
                            .properties("end_datetime", p -> p.date(d -> d))
                            .properties("organizer_email", p -> p.keyword(k -> k))
                            .properties("organizer_name", p -> p.text(t -> t))
                            .properties("event_type", p -> p.keyword(k -> k))
                            .properties("group_id", p -> p.integer(i -> i))
                            .properties("category_types", p -> p.keyword(k -> k))
                            .properties("capacity", p -> p.integer(i -> i))
                            // This is for semantic/vector search
                            .properties("embedding", p -> p.denseVector(dv -> dv
                                    .dims(768)  // nomic-embed-text output dimension
                                    .index(true)
                                    .similarity("cosine")))
                    )
            );
            log.info("Elasticsearch index '{}' created successfully", index);

        } catch (IOException e) {
            log.warn("Could not create Elasticsearch index '{}': {}", index, e.getMessage());
        }
    }


    //Create Index for Groups
    private void createGroupsIndex() {
        String index = config.getGroupsIndex();
        try {
            boolean exists = client.indices().exists(e -> e.index(index)).value();
            if (exists) {
                log.info("Elasticsearch index '{}' already exists — skipping creation", index);
                return;
            }
            /**
             * Group index description
             *
             * Text fields (name, description) are analyzed for full-text search.
             * Keyword fields (owner_email) are used for exact filters.
             * member_count support range queries.
             */
            client.indices().create(c -> c.index(index).mappings(m -> m
                            .properties("name", p -> p.text(t -> t))
                            .properties("description", p -> p.text(t -> t))
                            .properties("owner_email", p -> p.keyword(k -> k))
                            .properties("is_public", p -> p.boolean_(b -> b))
                            .properties("member_count", p -> p.integer(i -> i))

                            //For semantic/vector search
                            .properties("embedding", p -> p.denseVector(dv -> dv
                                    .dims(768)  // nomic-embed-text output dimension
                                    .index(true)
                                    .similarity("cosine")))
                    )
            );
            log.info("Elasticsearch index '{}' created successfully", index);

        } catch (IOException e) {
            log.warn("Could not create Elasticsearch index '{}': {}", index, e.getMessage());
        }
    }
}
