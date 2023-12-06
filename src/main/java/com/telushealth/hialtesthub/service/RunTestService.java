package com.telushealth.hialtesthub.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telushealth.hialtesthub.entity.Endpoint;
import com.telushealth.hialtesthub.entity.LoadTestResult;
import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.entity.TestCase;

@Service
public class RunTestService {

	@Autowired
	TestService testService;

	@Autowired
	EndpointService endpointService;

	@Autowired
	SoapTransactionService soapTransactionService;

	@Autowired
	LoadTestResultService loadTestResultService;

	private ExecutorService executor;
	private CountDownLatch latch;

	public SoapTransaction runSoapTest(TestCase testCase) {

		Endpoint endpoint = endpointService.getEndpoint();

		SoapTransaction soapTransaction = new SoapTransaction(endpoint, testCase);
		soapTransactionService.save(soapTransaction);

		return soapTransactionService.retrieveTransactionsByMsgId(soapTransaction.getMsgId());

	}

	public SoapTransaction runSoapTestWithInteractionId(String interactionId) {

		TestCase testCase = testService.findTestCaseByInteractionId(interactionId);

		SoapTransaction soapTransaction = runSoapTest(testCase);

		return soapTransaction;
	}

	public List<SoapTransaction> runSanityTest() {

		List<TestCase> testCases = testService.findAllTestCases();

		List<SoapTransaction> soapTransactions = new ArrayList<>();

		for (TestCase testCase : testCases) {
			SoapTransaction soapTransaction = runSoapTest(testCase);
			soapTransactions.add(soapTransaction);
		}

		return soapTransactions;
	}

	public LoadTestResult runLoadTest(int numThreads, long testDurationSeconds) {
		if (executor != null) {
			stopLoadTest(); // Stop the current load test if it's running
		}

		List<TestCase> testCases = testService.findAllTestCases();
		Endpoint endpoint = endpointService.getEndpoint();

		// Create a thread pool
		executor = Executors.newFixedThreadPool(numThreads);

		// Create a CountDownLatch to wait for all threads to finish
		latch = new CountDownLatch(numThreads);

		LoadGenerator.active = true;

		String loadTestStartTime = LocalDateTime.now().toString();

		createLoadTestResult(loadTestStartTime, numThreads, testDurationSeconds);

		for (int i = 0; i < numThreads; i++) {
			executor.execute(new LoadGenerator(endpoint, testCases, latch, soapTransactionService));
		}

		// Schedule a task to update the results every 2 seconds
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> updateLoadTestResult(loadTestStartTime), 0, 2, TimeUnit.SECONDS);

		// Allow threads to run for the specified duration
		try {
			Thread.sleep(testDurationSeconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Stop the load test after the specified duration
			stopLoadTest(loadTestStartTime);
			scheduler.shutdown(); // Shutdown the scheduler when the test is stopped
		}
		
		
		return loadTestResultService.findTestResultByStartTime(loadTestStartTime);
	}

	private void updateLoadTestResult(String loadTestStartTime) {
		loadTestResultService.updateTestResult(loadTestStartTime);
	}

	private void createLoadTestResult(String loadTestStartTime, int numThreads, long testDurationSeconds) {

		LoadTestResult loadTestResult = loadTestResultService.createLoadTestResult(loadTestStartTime, numThreads,
				testDurationSeconds);

		loadTestResultService.saveLoadTestResult(loadTestResult);

	}

	public void stopLoadTest(String loadTestStartTime) {
		if (executor != null) {
			LoadGenerator.active = false;
			executor.shutdown(); // Allow threads to finish
			try {
				if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
					executor.shutdownNow(); // Stop all threads forcefully if not terminated
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				executor = null;
				latch = null;
				loadTestResultService.updateEndTestTimeInResult(loadTestStartTime,LocalDateTime.now().toString());
			}
		}

	}

	public void stopLoadTest() {
		if (executor != null) {
			LoadGenerator.active = false;
			executor.shutdown(); // Allow threads to finish
			try {
				if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
					executor.shutdownNow(); // Stop all threads forcefully if not terminated
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				executor = null;
				latch = null;
			}
		}

	}
	public String runLoadTestReturnStartTime(int numThreads, long testDurationSeconds) {
		// Reuse existing method for running load test
		LoadTestResult result = runLoadTest(numThreads, testDurationSeconds);
		return result.getStartTestTime();
	}
}