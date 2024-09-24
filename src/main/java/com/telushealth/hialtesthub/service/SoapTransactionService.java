package com.telushealth.hialtesthub.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telushealth.hialtesthub.entity.ReportStats;
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

	public List<ReportStats> generateReportStats(String startTestTime, String endTestTime) {
		List<String> responseTimes = soapTransactionRepository.getAllResponseTimeBetweenTimeGiven(startTestTime,
				endTestTime);

		List<ReportStats> reportStatsList = new ArrayList<>();

		int[] durationRanges = { 2, 5, 10, 15, 20, 25, 30 };
		int totalInteractions = responseTimes.size();
		double totalAvgDuration = responseTimes.stream().mapToDouble(Double::parseDouble).average().orElse(0.0);

		final int[] startIndex = { 0 }; // Using an array to make it mutable

		for (int endIndex : durationRanges) {
			long interactionsInRange = responseTimes.stream().mapToDouble(Double::parseDouble)
					.filter(duration -> duration >= startIndex[0] && duration < endIndex).count();

			double percentageTotal = (interactionsInRange / (double) totalInteractions) * 100;

			double avgDuration = interactionsInRange == 0 ? 0.0
					: responseTimes.stream().mapToDouble(Double::parseDouble)
							.filter(duration -> duration >= startIndex[0] && duration < endIndex).average().orElse(0.0);

			String durationRangeStr = endIndex == 30 ? ">30" : startIndex[0] + "-" + endIndex;

			ReportStats reportStats = new ReportStats(durationRangeStr, interactionsInRange, percentageTotal,
					avgDuration);
			reportStatsList.add(reportStats);

			startIndex[0] = endIndex;
		}

		double totalPercentage = reportStatsList.stream().mapToDouble(ReportStats::getPercentageTotal).sum();
		reportStatsList.add(new ReportStats("Total", totalInteractions, totalPercentage, totalAvgDuration));

		return reportStatsList;
	}

	public List<SoapTransaction> findAllSoapTransactionsByStartTime(String startTime) {
		// TODO Auto-generated method stub
		return soapTransactionRepository.getSoapTransactionsByStartTime(startTime);
	}

}
