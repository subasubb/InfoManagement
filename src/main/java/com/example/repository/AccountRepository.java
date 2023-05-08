package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.couchbase.client.core.error.CasMismatchException;
import com.couchbase.client.core.error.DecodingFailureException;
import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.example.Model.Account;
import com.example.Model.RegisterUser;
import com.example.Model.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Scope("prototype")
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
	
	public Account save(String accountId, Account account) {
		log.debug("Insert document {}", account);
		MutationResult result;
		try {
			if (StringUtils.isNotBlank(accountId)) {
				log.debug("Replace document {}", account);
				try {
					result = collection.replace(accountId, account);
				} catch (CasMismatchException | DocumentNotFoundException e) {
					throw e;
				}
			} else {
				result = collection.insert(account.getId(), account);
			}
		} catch (DocumentExistsException e) {
			throw e;
		}
		return account;
	}

	public void delete(String accountId) {
		log.debug("Delete document {}", accountId);
		try {
			collection.remove(accountId);
		} catch (DocumentNotFoundException ex) {
			log.warn("Document did not exist when trying to remove");
		}
	}

	public long count() {
		String query = n1qlSelectEntityDefaultWhere(bucket.name(), true);
		return getCount(query, JsonArray.create());
	}

	public List<JsonObject> select(String createdBy) {
		String query;
		if(createdBy.equalsIgnoreCase("Admin")){
			query = "SELECT * FROM `info` where id like 'ID_%'";
		}else {
			query = "SELECT * FROM `info` where id like 'ID_%' AND createdBy =  \"" + createdBy + "\"";
		}
		QueryResult result = cluster.query(query);
		List<JsonObject> jsonObject = result.rowsAsObject().stream()
				.collect(Collectors.toList());
		return jsonObject;
	}
	
	public List<JsonObject> selectUsingId(String accountId) {
		String query = "SELECT * FROM `info` where id = " + accountId ;
		QueryResult result = cluster.query(query);
		List<JsonObject> jsonObject = result.rowsAsObject().stream()
				.collect(Collectors.toList());
		return jsonObject;
	}
	
	public String registerUser(RegisterUser registerUser) {
		log.debug("Insert document {}", registerUser);
		MutationResult result;
		try {
				result = collection.insert(registerUser.getUserName(), registerUser);
		} catch (DocumentExistsException e) {
			throw e;
		}
		return result.toString();
	}

//	public List<JsonObject> signUpUser(String userName) {
//		String query = "SELECT * FROM `info` where meta().id = \"" + userName + "\"" ;
//		QueryResult result = cluster.query(query);
//		List<JsonObject> jsonObject = result.rowsAsObject().stream()
//				.collect(Collectors.toList());
//		return jsonObject;
//	}
	
	public Optional<User> signUpUser(String userName) {
		log.debug("Get document by id {}", userName);
		try {
			GetResult result = collection.get(userName);
			User user = result.contentAs(User.class);
			return Optional.of(user);
		} catch (DocumentNotFoundException | DecodingFailureException e) {
			return Optional.empty();
		}
	}

}