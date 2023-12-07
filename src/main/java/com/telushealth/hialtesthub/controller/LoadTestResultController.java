package com.telushealth.hialtesthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.LoadTestResult;
import com.telushealth.hialtesthub.entity.ReportStats;
import com.telushealth.hialtesthub.service.LoadTestResultService;
import com.telushealth.hialtesthub.service.SoapTransactionService;

@RestController
@RequestMapping("/api/loadtestresult")
public class LoadTestResultController {

	@Autowired
	LoadTestResultService loadTestResultService;
	
	@Autowired
	SoapTransactionService soapTransactionService;

	@GetMapping("/getall")
	private List<LoadTestResult> getAllLoadTestResult() {
		return loadTestResultService.getAllResult();
	}

	@GetMapping("/getByStartTime")
	private LoadTestResult getLoadTestResultByStartTime(@RequestParam("startTestTime") String startTestTime) {
		return loadTestResultService.findTestResultByStartTime(startTestTime);
	}

	@PostMapping("/save")
	private void saveLoadTestResult(@RequestBody LoadTestResult loadTestResult) {
		loadTestResultService.saveLoadTestResult(loadTestResult);
	}

	@GetMapping("/getLatestStartTime")
	private String getLatestLoadTestStartTime() {
		LoadTestResult latestLoadTestResult = loadTestResultService.getLatestResult();

		// Check if the latestLoadTestResult is not null and return the startTestTime
		return latestLoadTestResult != null ? latestLoadTestResult.getStartTestTime() : null;
	}

	@GetMapping("/getReportStats")
	private List<ReportStats> getAllReportStatsForReport(@RequestParam("startTestTime") String startTestTime,
			@RequestParam("endTestTime") String endTestTime) {
		return soapTransactionService.generateReportStats(startTestTime, endTestTime);
	}
}
