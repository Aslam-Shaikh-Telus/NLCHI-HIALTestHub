package com.telushealth.hialtesthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.Endpoint;
import com.telushealth.hialtesthub.entity.TestCase;
import com.telushealth.hialtesthub.service.EndpointService;
import com.telushealth.hialtesthub.service.TestService;

@RestController
@RequestMapping("/api/endpoint")
public class EndpointController {

	@Autowired
	EndpointService endpointService;

	@GetMapping("/get")
	public List<Endpoint> getAllEndpoint() {
		return endpointService.getAll();
	}

	@PostMapping("/save")
	public void saveEndpoint(@RequestBody Endpoint endpoint) {
		endpointService.saveEndpoint(endpoint);
	}

}
