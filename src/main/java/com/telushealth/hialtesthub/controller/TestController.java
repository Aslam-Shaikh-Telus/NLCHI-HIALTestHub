package com.telushealth.hialtesthub.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.TestCase;
import com.telushealth.hialtesthub.service.TestService;

@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	TestService testService;
	
	@GetMapping("/")
	public List<TestCase> getAllTestCases (){
		return testService.getAll();
	}
	@PostMapping("/")
	public TestCase save (@RequestBody TestCase test){
		return testService.save(test);
	}
	
	
}
