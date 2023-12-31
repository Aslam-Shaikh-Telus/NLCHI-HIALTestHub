package com.telushealth.hialtesthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telushealth.hialtesthub.entity.Endpoint;
import com.telushealth.hialtesthub.repository.EndpointRespository;

@Service
public class EndpointService {
	
	@Autowired
	EndpointRespository endpointRepository;
	
	public List<Endpoint> getAll() {
		return endpointRepository.find();
	}

	public Endpoint findByHostname(String string) {
		return endpointRepository.findByHostname(string);
	}

}
