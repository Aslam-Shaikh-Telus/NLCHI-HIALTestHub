package com.telushealth.hialtesthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telushealth.hialtesthub.entity.TestCase;
import com.telushealth.hialtesthub.repository.TestRepository;

@Service
public class TestService {

	@Autowired
	TestRepository testRespository;

	@Autowired
	ObjectMapper objectMapper;

	public List<TestCase> getAll() {
		return testRespository.find();
	}

	public TestCase save(TestCase test) {
		return testRespository.save(test);
	}

	public TestCase findTestCaseByInteractionId(String interactionId) {
		return testRespository.findByInteractionId(interactionId);
	}

	public List<TestCase> findAllTestCases() {
		return testRespository.findAllTestCases();
	}

	
}
