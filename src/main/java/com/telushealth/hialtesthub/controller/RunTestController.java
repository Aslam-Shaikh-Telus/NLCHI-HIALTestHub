package com.telushealth.hialtesthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.LoadTestResult;
import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.service.RunTestService;

@RestController
@RequestMapping("/api/runtest")
public class RunTestController {

	@Autowired
	RunTestService runTestService;

	@GetMapping("/{interactionId}")
	public SoapTransaction runTestWithInteractionId(@PathVariable String interactionId) {
		return runTestService.runSoapTestWithInteractionId(interactionId);
	}

	@GetMapping("/sanity")
	public List<SoapTransaction> runSanityTest() {
		return runTestService.runSanityTest();
	}

	@GetMapping("/loadtest")
	public LoadTestResult runLoadTest(@RequestParam("numThreads") int numThreads,
			@RequestParam("testDurationSeconds") long testDurationSeconds) {
		return runTestService.runLoadTest(numThreads, testDurationSeconds);
	}
	
	@GetMapping("/loadtest/return-start-time")
	public String runLoadTestReturnStartTime(@RequestParam("numThreads") int numThreads,
			@RequestParam("testDurationSeconds") long testDurationSeconds) {
		return runTestService.runLoadTestReturnStartTime(numThreads, testDurationSeconds);
	}

	@GetMapping("/loadtest/stop")
	public void stopLoadTest() {
		runTestService.stopLoadTest();
	}
}
