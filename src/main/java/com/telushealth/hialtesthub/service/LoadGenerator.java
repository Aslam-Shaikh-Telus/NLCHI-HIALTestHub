package com.telushealth.hialtesthub.service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.telushealth.hialtesthub.entity.Endpoint;
import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.entity.TestCase;

public class LoadGenerator implements Runnable {

	private SoapTransactionService soapTransactionService;

	private Endpoint endpoint;
	private List<TestCase> testCases;

	private CountDownLatch latch;

	public static volatile boolean active = true; // Flag to control thread activity

	public LoadGenerator(Endpoint endpoint, List<TestCase> testCases, CountDownLatch latch,
			SoapTransactionService soapTransactionService) {
		this.endpoint = endpoint;
		this.testCases = testCases;
		this.latch = latch;
		this.soapTransactionService = soapTransactionService;
	}

//	public void stop() {
//		active = false; // Set the flag to stop the thread
//	}

	@Override
	public void run() {
		try {

			while (active) {
				for (TestCase testCase : testCases) {
					if (!active) {
						break; // Check if the test should be stopped
					}

					SoapTransaction soapTransaction = new SoapTransaction(endpoint, testCase);
					soapTransactionService.save(soapTransaction);
				}
				// Simulate a pause between test iterations
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			latch.countDown(); // Signal that this thread has finished
		}
	}

//    private void simulateSoapTesting(SoapTransaction soapTransaction) {
//        // Simulate SOAP testing logic
//        // For example, you might send SOAP requests, receive responses, and store the results
//        // This is a placeholder, and you need to replace it with your actual SOAP testing logic.
//        soapTransactionRepository.save(soapTransaction);
//        System.out.println(
//                "Thread: " + Thread.currentThread().getName() + " - SOAP test completed for Interaction ID: "
//                        + soapTransaction.getInteractionId());
//    }
}
