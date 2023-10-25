package com.telushealth.hialtesthub.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Document
//@Component
public class TestCase {

	@Id
	private String id;
	@Indexed(unique = true)
	private String interactionId;
	private String description;
	private String soapAction;
	private String endpoint;
	private String xml;

	public TestCase() {
		super();
	}

	public TestCase(String interactionId, String description, String soapAction, String endpoint, String xml) {
		super();
		this.interactionId = interactionId;
		this.description = description;
		this.soapAction = soapAction;
		this.endpoint = endpoint;
		this.xml = xml;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	
	
}
