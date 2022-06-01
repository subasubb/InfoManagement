package com.example.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Configuration
public class CouchbaseConfig {

    @Autowired
    Cluster cluster;

    @Autowired
    Environment environment;
    /**
     * ISP Addition for waiting till DB is UP
     * in case id APP is UP before DB
     */
    @PostConstruct
    public void init() {
        cluster.waitUntilReady(Duration.ofSeconds(environment.getProperty(
                "spring.couchbase.wait-until-ready", Long.class, 30L)));
    }

    @Bean
	public Bucket couchbaseBucket() {
		Bucket bucket = cluster.bucket(environment
				.getRequiredProperty("spring.couchbase.bucket-name"));
		bucket.waitUntilReady(Duration.ofSeconds(environment.getProperty(
                "spring.couchbase.wait-until-ready", Long.class, 30L)));
		return bucket;
	}
}
