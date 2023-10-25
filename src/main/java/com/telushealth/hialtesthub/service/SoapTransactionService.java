package com.telushealth.hialtesthub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.telushealth.hialtesthub.entity.Endpoint;
import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.entity.TestCase;
import com.telushealth.hialtesthub.repository.SoapTransactionRepository;

@Repository
public class SoapTransactionService {

	@Autowired
	TestService testService;

	@Autowired
	EndpointService endpointService;

	@Autowired
	SoapTransactionRepository soapTransactionRepository;

	public SoapTransaction runSoapTest(String interactionId) {

		TestCase testCase = testService.findTestCaseByInteractionId(interactionId);
		Endpoint endpoint = endpointService.findByHostname("nlap002");

		SoapTransaction soapTransaction = new SoapTransaction(endpoint, testCase);
		soapTransactionRepository.save(soapTransaction);

		return soapTransactionRepository.findByInteractionId(interactionId);

	}

	public List<SoapTransaction> runSanityTest() {

		List<TestCase> testCases = testService.findAllTestCases();
		Endpoint endpoint = endpointService.findByHostname("nlap002");

		List<SoapTransaction> soapTransactions = new ArrayList<>();

		for (TestCase testCase : testCases) {

			SoapTransaction soapTransaction = new SoapTransaction(endpoint, testCase);
			soapTransactions.add(soapTransaction);
			soapTransactionRepository.save(soapTransaction);
		}

		return soapTransactionRepository.findAll();
	}

//	public List<SoapTransaction> runLoadTest(int numThreads, long testDurationSeconds) {
//
//		List<TestCase> testCases = testService.findAllTestCases();
//		Endpoint endpoint = endpointService.findByHostname("nlap002");
//
//		for (int i = 0; i < numThreads; i++) {
//
//			Thread thread = new Thread(new CustomRunner(endpoint, testCases));
//			thread.start();
//		}
//
//		// Allow threads to run for the specified duration
//		try {
//			Thread.sleep(testDurationSeconds * 1000);
//		} catch (InterruptedException e) {
//			// Handle interruption if needed
//			e.printStackTrace();
//		}
//
//		CustomRunner.active = false;
//
//		// return soapTransactionRepository.findAll();
//		return null;
//	}
	
	public List<SoapTransaction> runLoadTest(int numThreads, long testDurationSeconds) {
	    List<TestCase> testCases = testService.findAllTestCases();
	    Endpoint endpoint = endpointService.findByHostname("nlap002");

	    // Create a thread pool
	    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
	    
	    // Create a CountDownLatch to wait for all threads to finish
	    CountDownLatch latch = new CountDownLatch(numThreads);

	    for (int i = 0; i < numThreads; i++) {
	        executor.execute(new CustomRunner(endpoint, testCases, latch));
	    }

	    // Allow threads to run for the specified duration
	    try {
	        Thread.sleep(testDurationSeconds * 1000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }

	    CustomRunner.active = false;
//	    // Stop all threads
//	    for (int i = 0; i < numThreads; i++) {
//	        ((CustomRunner) executor).stop();
//	    }

	    // Wait for all threads to finish
	    try {
	        latch.await();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }

	    // Shutdown the thread pool
	    executor.shutdown();

	    // return soapTransactionRepository.findAll();
	    return null;
	}	

	public List<SoapTransaction> findAllSoapTransactions() {
		// TODO Auto-generated method stub
		return soapTransactionRepository.findAll();
	}

}
