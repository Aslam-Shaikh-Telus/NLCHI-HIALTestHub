package com.telushealth.hialtesthub.service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.telushealth.hialtesthub.entity.Endpoint;
import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.entity.TestCase;

public class CustomRunner implements Runnable {
	private Endpoint endpoint;
	private List<TestCase> testCases;
	private List<SoapTransaction> soapTransactions;
	private CountDownLatch latch;
	public static boolean active = true; // Flag to control thread activity

	public CustomRunner(Endpoint endpoint, List<TestCase> testCases, CountDownLatch latch) {
		this.endpoint = endpoint;
		this.testCases = testCases;
		this.latch = latch;
	}

	public void stop() {
		active = false; // Set the flag to stop the thread
	}

	@Override
	public void run() {

		while (active) {
			try {
				for (int i = 0; i <= 5; i++) {
					if (active) {
						try {
							System.out.println("\n" + i + " : " + Thread.currentThread().getName());
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}
			} finally {
				latch.countDown(); // Signal that this thread has finished
			}
		}
	}
//	for (TestCase testCase : testCases) {
//	// Create SoapTransaction and add it to the shared list
//	SoapTransaction soapTransaction = new SoapTransaction(endpoint, testCase);
//	soapTransactions.add(soapTransaction);
//}
}
