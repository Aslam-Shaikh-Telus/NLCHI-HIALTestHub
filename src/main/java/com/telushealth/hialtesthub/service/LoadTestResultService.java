package com.telushealth.hialtesthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telushealth.hialtesthub.entity.LoadTestResult;
import com.telushealth.hialtesthub.repository.LoadTestResultRepository;

@Service
public class LoadTestResultService {

	@Autowired
	LoadTestResultRepository loadTestResultRepository;

	@Autowired
	SoapTransactionService soapTransactionService;

	public List<LoadTestResult> getAllResult() {
		return loadTestResultRepository.getAll();
	}

	public void saveLoadTestResult(LoadTestResult loadTestResult) {
		loadTestResultRepository.saveResult(loadTestResult);
	}

	public LoadTestResult createLoadTestResult(String loadTestStartTime, int numThreads, long testDurationSeconds) {

		return new LoadTestResult(loadTestStartTime, numThreads, testDurationSeconds,
				soapTransactionService.runLoadTestAggregation(loadTestStartTime));

	}

	public void updateTestResult(String loadTestStartTime) {
		loadTestResultRepository.updateResult(loadTestStartTime,
				soapTransactionService.runLoadTestAggregation(loadTestStartTime));
	}

	public LoadTestResult findTestResultByStartTime(String loadTestStartTime) {
		return loadTestResultRepository.getByStartTime(loadTestStartTime);
	}

}
