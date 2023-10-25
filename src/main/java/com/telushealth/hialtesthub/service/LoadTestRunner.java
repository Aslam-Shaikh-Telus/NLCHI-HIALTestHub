//package com.telushealth.hialtesthub.service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.telushealth.hialtesthub.entity.SoapTransaction;
//
//@Service
//public class LoadTestRunner  {
//
//	private int numTreads;
//	private long testDuractionSeconds;
//
//	@Autowired
//	private SoapTransactionService soapTransactionService;
//
//	public LoadTestRunner(int numThreads, long testDurationSeconds) {
//		this.numTreads = numThreads;
//		this.testDuractionSeconds = testDurationSeconds;
//	}
//
////	public List<SoapTransaction> runLoadTest(int numThreads, long testDurationSeconds) {
////		List<SoapTransaction> soapTransactions = new ArrayList<>();
////	
////		// Create a thread pool
////		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
////
////		// Start the threads
////		for (int i = 0; i < numThreads; i++) {
////			Runnable customRunner = new CustomRunner(endpoint, testCases, soapTransactions);
////			executorService.submit(customRunner);
////		}
////
////		// Allow threads to run for the specified duration
////		try {
////			Thread.sleep(testDurationSeconds * 1000);
////		} catch (InterruptedException e) {
////			// Handle interruption if needed
////			e.printStackTrace();
////		}
////
////		// Explicitly interrupt threads after the specified duration
////		executorService.shutdownNow();
////		
////		return soapTransactions;
////	}
//
//	public List<SoapTransaction> run() {
//
//		for (int i = 0; i <= numTreads; i++) {
//
//			
//		}
//		return null;
//
//	}
//}
