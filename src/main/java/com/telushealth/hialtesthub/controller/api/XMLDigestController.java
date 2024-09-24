package com.telushealth.hialtesthub.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telushealth.hialtesthub.entity.soaphandler.XMLDigest;

@RestController
@RequestMapping("/api/digestXml")
public class XMLDigestController {
	
	@PostMapping("/")
	public XMLDigest getXMLDigest(@RequestParam("xmlString") String xmlString) {
		XMLDigest soapDigest = new XMLDigest("NULL", xmlString);
        soapDigest.digestBody();
		return soapDigest;
	}

}
