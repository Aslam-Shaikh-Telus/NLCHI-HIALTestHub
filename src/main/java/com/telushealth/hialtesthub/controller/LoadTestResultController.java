package com.telushealth.hialtesthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.LoadTestResult;
import com.telushealth.hialtesthub.service.LoadTestResultService;

@RestController
@RequestMapping("/api/loadtestresult")
public class LoadTestResultController {

	@Autowired
	LoadTestResultService loadTestResultService;
	
	
	@GetMapping("/getall")
	private List<LoadTestResult> getAllLoadTestResult(){
		return loadTestResultService.getAllResult();
	}
	
	@PostMapping("/save")
	private void saveLoadTestResult(@RequestBody LoadTestResult loadTestResult ){
		loadTestResultService.saveLoadTestResult(loadTestResult);
	}
}
