package com.telushealth.hialtesthub.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.LoadTestResult;
import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.entity.TestCase;
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

	@PostMapping
    @ResponseBody
    public SoapTransaction runTestWithTestCase(@RequestBody TestCase testCase) {
       return runTestService.runSoapTest(testCase);
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
