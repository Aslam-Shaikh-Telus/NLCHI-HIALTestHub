package com.telushealth.hialtesthub.controller.api;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.TestCase;
import com.telushealth.hialtesthub.service.TestService;

@RestController
@RequestMapping("/api/testcase")
public class TestController {

	@Autowired
	TestService testService;
	
	@GetMapping("/getAll")
	@ResponseBody
	public List<TestCase> getAllTestCases (){
		return testService.getAll();
	}
	
	@PostMapping("/save")
	public TestCase save (@RequestBody TestCase test){
		return testService.save(test);
	}
	
	
}
