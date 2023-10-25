
package com.telushealth.hialtesthub.repository;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telushealth.hialtesthub.entity.Endpoint;

@Repository
public class EndpointRespository implements CommandLineRunner{

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Value("${endpoint.json.file.path}")
	private String endpointJsonFile;
	
	public List<Endpoint> find() {
		return mongoTemplate.findAll(Endpoint.class);
	}

	@Override
	public void run(String... args) throws Exception {
		// Load JSON data from file
				List<Endpoint> endpoints = null;
				try {
					
					ClassPathResource resource = new ClassPathResource(endpointJsonFile);
					endpoints = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Endpoint>>() {
					});
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Insert data into MongoDB
				mongoTemplate.insertAll(endpoints);
	}

	public Endpoint findByHostname(String hostname) {
		Query query = new Query(Criteria.where("hostname").is(hostname));
		return mongoTemplate.findOne(query, Endpoint.class);
	}

}
