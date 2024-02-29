package com.telushealth.hialtesthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.telushealth.hialtesthub.entity.Endpoint;
import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.entity.TestCase;
import com.telushealth.hialtesthub.service.EndpointService;
import com.telushealth.hialtesthub.service.SoapTransactionService;
import com.telushealth.hialtesthub.service.TestService;

@Controller
@RequestMapping
public class WebController {

	@Autowired
	SoapTransactionService soapTransactionService;

	@Autowired
	TestService testService;

	@Autowired
	EndpointService endpointService;

	@GetMapping("")
	public String showIndexPage(Model model) {
		return "redirect:/home";
	}
	
	

	@GetMapping("/login")
	public String showLoginPage(Model model) {

		Endpoint endpoint = endpointService.getEndpoint();

		// If the user is not logged in, redirect to the login page
		if (endpoint == null) {
			// Clear session attribute when showing the login page
			model.addAttribute("endpoint", new Endpoint());

			return "login";
		}

		// Add endpoint to the model for use in the Thymeleaf template
		model.addAttribute("endpoint", endpoint);
		return "redirect:/home";

	}

	@PostMapping("/login")
	public String login(Endpoint endpoint, Model model) {
		// Save the endpoint in the session attribute
		model.addAttribute("endpoint", endpoint);
		// Save the endpoint in database
		endpointService.saveEndpoint(endpoint);

		// Redirect to the home page
		return "redirect:/home";
	}



//	@GetMapping("/home")
//	public String showHomePage(Model model) {
//		return "home";
//	}

	private boolean redirectToLoginIfEndpointNotSet(Model model) {
        // Retrieve the endpoint from the session attribute
        Endpoint endpoint = endpointService.getEndpoint();

        // If the user is not logged in, redirect to the login page
        if (endpoint == null) {
            return true;
        }

        // Add endpoint to the model for use in the Thymeleaf template
        model.addAttribute("endpoint", endpoint);
        return false;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        if (redirectToLoginIfEndpointNotSet(model)) {
            return "redirect:/login";
        }

        return "home";
    }

    @GetMapping("/sanity")
    public String showSanityTestPage(Model model) {
        if (redirectToLoginIfEndpointNotSet(model)) {
            return "redirect:/login";
        }

        return "sanity";
    }

    @GetMapping("/loadtest")
    public String showLoadTestPage(Model model) {
        if (redirectToLoginIfEndpointNotSet(model)) {
            return "redirect:/login";
        }

        return "loadtest";
    }

	@GetMapping("/logout")
	public String logout(Model model) {
		// Add logging for debugging
		System.out.println("Logout requested");

		// Clear the session attribute and redirect to the login page
		model.addAttribute("endpoint", null);
		endpointService.clearEndpointDatabaseDocument();

		return "redirect:/login";
	}

	@GetMapping("/showsoaptransactions")
	public String showSoapTransactions(Model model) {
		List<SoapTransaction> soapTransactions = soapTransactionService.findAllSoapTransactions();
		model.addAttribute("soapTransactions", soapTransactions);
		return "soap_transactions";
	}

	@GetMapping("/details")
	public String showTransactionDetails(@RequestParam("msgId") String msgId, Model model) {
		// Retrieve transactions with the specified msgId (you need to implement this
		// method)
		SoapTransaction soapTransaction = soapTransactionService.retrieveTransactionsByMsgId(msgId);

		// Add transactions to the model
		model.addAttribute("transaction", soapTransaction);

		return "details";
	}
	
	@GetMapping("/endpoint")
	public String showEndpoint(Model model) {
		Endpoint endpoint = endpointService.getEndpoint();
		model.addAttribute("endpoint", endpoint);
		return "endpoint";
	}
	
	@PostMapping("/endpoint")
	public String updateEndpoint(Endpoint endpoint, Model model) {
		// Save the endpoint in the session attribute
		model.addAttribute("endpoint", endpoint);
		// Save the endpoint in database
		endpointService.saveEndpoint(endpoint);

		// Redirect to the home page
		return "endpoint";
	}
}
