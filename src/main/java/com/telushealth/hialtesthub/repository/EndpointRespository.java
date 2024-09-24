
package com.telushealth.hialtesthub.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;

import com.telushealth.hialtesthub.entity.Endpoint;

@Repository
public class EndpointRespository implements CommandLineRunner {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Value("${endpoint.json.file.path}")
	private String endpointJsonFile;

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public void run(String... args) throws Exception {
		// Load JSON data from file
		List<Endpoint> endpoints = null;
		try {

//			ClassPathResource resource = new ClassPathResource(endpointJsonFile);
			Resource resource = resourceLoader.getResource("file:" + endpointJsonFile);

			endpoints = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Endpoint>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Insert data into MongoDB
		mongoTemplate.insertAll(endpoints);
	}

	public void saveToFile() {
		try {
			// Retrieve data from MongoDB
			List<Endpoint> endpoints = mongoTemplate.findAll(Endpoint.class);

			// Get the File object from ClassPathResource
			ClassPathResource resource = new ClassPathResource(endpointJsonFile);
			File file = resource.getFile();

			// Write data to the file using FileOutputStream
			try (FileOutputStream fos = new FileOutputStream(file)) {
				objectMapper.writeValue(fos, endpoints);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Endpoint find() {
		// Create a query to find the latest entry based on the "timestamp" field
		Query query = new Query().with(Sort.by(Order.desc("timestamp"))).limit(1);

		return mongoTemplate.findOne(query, Endpoint.class);
	}

	public Endpoint findByHostname(String hostname) {
		Query query = new Query(Criteria.where("hostname").is(hostname));
		return mongoTemplate.findOne(query, Endpoint.class);
	}

	public void deleteAll() {
		mongoTemplate.dropCollection(Endpoint.class);
	}

	public void save(Endpoint endpoint) {
		deleteAll();
		mongoTemplate.save(endpoint);
	}

	public List<Endpoint> findAll() {
		return mongoTemplate.findAll(Endpoint.class);

	}

}
