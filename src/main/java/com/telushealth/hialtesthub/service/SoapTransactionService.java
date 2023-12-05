package com.telushealth.hialtesthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.entity.TestResultStat;
import com.telushealth.hialtesthub.repository.SoapTransactionRepository;

@Service
public class SoapTransactionService {

	@Autowired
	TestService testService;

	@Autowired
	EndpointService endpointService;

	@Autowired
	SoapTransactionRepository soapTransactionRepository;

	public List<SoapTransaction> findAllSoapTransactions() {
		return soapTransactionRepository.findAll();
	}

	public SoapTransaction retrieveTransactionsByMsgId(String msgId) {
		return soapTransactionRepository.findByMsgId(msgId);
	}

	public void save(SoapTransaction soapTransaction) {
		soapTransactionRepository.save(soapTransaction);
	}

	public List<TestResultStat> runLoadTestAggregation(String loadTestStartTime) {
		List<TestResultStat> testResultStats = soapTransactionRepository.runLoadTestAggregation(loadTestStartTime);
		return testResultStats;
	}

}
