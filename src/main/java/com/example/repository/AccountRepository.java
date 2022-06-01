package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.example.Model.Account;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountRepository {

	private final Cluster cluster;
	private final Bucket bucket;
	private final Collection collection;

	@Autowired
	public AccountRepository(Cluster cluster, Bucket bucket) {
		this.cluster = cluster;
		this.bucket = bucket;
		this.collection = bucket.defaultCollection();
	}

	private String n1qlSelectEntityDefaultWhere(String bucketName, boolean isCount) {
		String selectEntity = n1qlSelectEntity(bucketName, isCount);
		return selectEntity;
	}

	private String n1qlSelectEntity(String bucketName, boolean isCount) {
		String b = "`" + bucketName + "`";
		String entity = "META(" + b + ").cas AS version";
		String count = "COUNT(*) AS count";
		String selectEntity;
		if (isCount) {
			selectEntity = "SELECT " + count + " FROM " + b;
		} else {
			selectEntity = "SELECT " + b + ".* " + ", " + entity + " FROM " + b;
		}
		return selectEntity;
	}

	private long getCount(String query, JsonArray parameters) {
		log.debug("Query: \"{}\", Parameters: {}", query, parameters);
		QueryOptions options = QueryOptions.queryOptions().parameters(parameters);
		QueryResult result = cluster.query(query, options);
		Optional<JsonObject> jsonObject = result.rowsAsObject().stream().findFirst();
		return jsonObject.filter(object -> object.containsKey("count")).map(object -> object.getLong("count"))
				.orElse(0L);
	}
	
	public Account save(Account account) {
		log.debug("Insert document {}", account);
		try {
			MutationResult result = collection.insert(account.getId(), account);
		} catch (DocumentExistsException e) {
			throw e;
		}
		return account;
	}

	public void delete(Account account) {
		log.debug("Delete document {}", account.getId());
		try {
			collection.remove(account.getId());
		} catch (DocumentNotFoundException ex) {
			log.warn("Document did not exist when trying to remove");
		}
	}

	public long count() {
		String query = n1qlSelectEntityDefaultWhere(bucket.name(), true);
		return getCount(query, JsonArray.create());
	}
	
	public List<JsonObject> select(){
		String query = "SELECT * FROM `sample`";
		QueryResult result = cluster.query(query);
		List<JsonObject> jsonObject = result.rowsAsObject().stream().collect(Collectors.toList());
		return jsonObject;
	}

}