package com.telushealth.hialtesthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.service.SoapTransactionService;

@RestController
@RequestMapping("/runtest")
public class SoapTransactionController {

	@Autowired
	SoapTransactionService soapTransactionService;

	@GetMapping("/{interactionId}")
	public SoapTransaction runTestWithInteractionId(@PathVariable String interactionId) {
		return soapTransactionService.runSoapTest(interactionId);
	}

	@GetMapping("/sanity")
	public List<SoapTransaction> runSanityTest() {
		return soapTransactionService.runSanityTest();
	}

	@GetMapping("/loadtest")
	public List<SoapTransaction> runLoadTest() {
		return soapTransactionService.runLoadTest(5, 22);
	}
	
	@GetMapping("/")
	public List<SoapTransaction> getAllSoapTransactions() {
		return soapTransactionService.findAllSoapTransactions();
		
	}
}
