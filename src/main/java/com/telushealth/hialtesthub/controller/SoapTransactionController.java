package com.telushealth.hialtesthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.service.SoapTransactionService;

@Controller
@RequestMapping("/runtest")
public class SoapTransactionController {

	@Autowired
	SoapTransactionService soapTransactionService;

	@GetMapping("/{interactionId}")
	@ResponseBody
	public SoapTransaction runTestWithInteractionId(@PathVariable String interactionId) {
		return soapTransactionService.runSoapTest(interactionId);
	}

	@GetMapping("/sanity")
	@ResponseBody
	public List<SoapTransaction> runSanityTest() {
		return soapTransactionService.runSanityTest();
	}

	@GetMapping("/loadtest")
	@ResponseBody
	public List<SoapTransaction> runLoadTest() {
		return soapTransactionService.runLoadTest(5, 22);
	}

	@GetMapping("/")
	@ResponseBody
	public List<SoapTransaction> getAllSoapTransactions() {
		return soapTransactionService.findAllSoapTransactions();

	}

//	@GetMapping("/")
//	public String viewSoaptransactionsPage(Model model) {
//		model.addAttribute("listSoapTransactions",soapTransactionService.findAllSoapTransactions());
//		return "soaptransactions";
//	}

	@GetMapping("/showsoaptransactions")
	public String showSoapTransactions(Model model) {
		List<SoapTransaction> soapTransactions = soapTransactionService.findAllSoapTransactions();
		model.addAttribute("soapTransactions", soapTransactions);
		return "soap_transactions";
	}

	@GetMapping("/getSoapTransactions")
	@ResponseBody
	public List<SoapTransaction> getSoapTransactions() {
		return soapTransactionService.findAllSoapTransactions();
	}
	
	
	@GetMapping("/details")
    public String showTransactionDetails(@RequestParam("msgId") String msgId, Model model) {
        // Retrieve transactions with the specified msgId (you need to implement this method)
        SoapTransaction soapTransaction = soapTransactionService.retrieveTransactionsByMsgId(msgId);

        // Add transactions to the model
        model.addAttribute("transaction", soapTransaction);

        return "details";
    }
}
