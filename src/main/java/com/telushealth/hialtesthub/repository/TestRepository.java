package com.telushealth.hialtesthub.repository;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telushealth.hialtesthub.entity.TestCase;

@Repository
public class TestRepository implements CommandLineRunner {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Value("${testCase.json.file.path}")
	private String testCaseJsonFile;

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public void run(String... args) throws Exception {
		// Load JSON data from file
		List<TestCase> testCases = null;
		try {
			// Load the resource file
			Resource resource = resourceLoader.getResource("file:" + testCaseJsonFile);

			// Map JSON file to list of TestCase objects
			testCases = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<TestCase>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Delete all existing test cases in MongoDB
		mongoTemplate.remove(new Query(), TestCase.class);

		// Insert new test cases into MongoDB
		if (testCases != null && !testCases.isEmpty()) {
			mongoTemplate.insertAll(testCases);
		}
	}

	public List<TestCase> find() {
		return mongoTemplate.findAll(TestCase.class);
	}

	public TestCase save(TestCase testCase) {
		return mongoTemplate.save(testCase);

	}

	public TestCase findByInteractionId(String interactionId) {
		Query query = new Query(Criteria.where("interactionId").is(interactionId));
		return mongoTemplate.findOne(query, TestCase.class);
	}

	public List<TestCase> findAllTestCases() {
		return mongoTemplate.findAll(TestCase.class);
	}
}
