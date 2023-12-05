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
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.service.SoapTransactionService;

@RestController
@RequestMapping("/api/transactions")
public class SoapTransactionController {

	@Autowired
	SoapTransactionService soapTransactionService;

//	@GetMapping("/")
//	public List<SoapTransaction> getAllSoapTransactions() {
//		return soapTransactionService.findAllSoapTransactions();
//
//	}

	@GetMapping("/retrieveByMsgId")
    public SoapTransaction retrieveTransactionsByMsgId(@RequestParam("msgId") String msgId) {
        // Assuming "MsgId" is the query parameter name
        return soapTransactionService.retrieveTransactionsByMsgId(msgId);
    }

	@GetMapping("/getAll")
	public List<SoapTransaction> getSoapTransactions() {
		return soapTransactionService.findAllSoapTransactions();
	}

}
