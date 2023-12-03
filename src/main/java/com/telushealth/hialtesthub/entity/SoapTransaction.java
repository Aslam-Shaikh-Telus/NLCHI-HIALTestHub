package com.telushealth.hialtesthub.entity;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class SoapTransaction {

	private String msgId = UUID.randomUUID().toString().toUpperCase();

	@Id
	private String id;

	@Field("soapRequest")
	SoapRequest soapRequest;
	@Field("soapResponse")
	SoapResponse soapResponse;

	public SoapTransaction() {
		super();
	}

	public SoapTransaction(Endpoint endpoint, TestCase testCase) {

		this.soapRequest = new SoapRequest(msgId, endpoint, testCase);
		this.soapResponse = new SoapResponse(msgId, this.soapRequest);
	}

	public SoapRequest getSoapRequest() {
		return soapRequest;
	}

	public void setSoapRequest(SoapRequest soapRequest) {
		this.soapRequest = soapRequest;
	}

	public SoapResponse getSoapResponse() {
		return soapResponse;
	}

	public void setSoapResponse(SoapResponse soapResponse) {
		this.soapResponse = soapResponse;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

}
