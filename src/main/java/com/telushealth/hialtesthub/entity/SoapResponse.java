package com.telushealth.hialtesthub.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;
import org.w3c.dom.Document;

import com.telushealth.hialtesthub.entity.soaphandler.XMLUtils;

@org.springframework.data.mongodb.core.mapping.Document
public class SoapResponse {

	private static final Logger logger = LoggerFactory.getLogger(SoapResponse.class);

	@Id
	private String id;
	@Field("timeStamp")
	private String timeStamp = LocalDateTime.now().toString();
	private ResponseStatus responseStatus;
	private String responseMessageId;
	private String responseRelateTo;
	private String responseTypeCode;
	private String responseTime;
	private String responseXml;
	@Transient
	private String msgId;

	public String getResponseMessageId() {
		return responseMessageId;
	}

	public void setResponseMessageId(String responseMessageId) {
		this.responseMessageId = responseMessageId;
	}

	public String getResponseRelateTo() {
		return responseRelateTo;
	}

	public void setResponseRelateTo(String responseRelateTo) {
		this.responseRelateTo = responseRelateTo;
	}

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseTypeCode() {
		return responseTypeCode;
	}

	public void setResponseTypeCode(String responseTypeCode) {
		this.responseTypeCode = responseTypeCode;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getResponseXml() {
		return responseXml;
	}

	public void setResponseXml(String responseXml) {
		this.responseXml = responseXml;
	}

	public static Logger getLogger() {
		return logger;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public SoapResponse() {
		super();
	}

	public SoapResponse(String msgId, SoapRequest soapRequest) {

		this.msgId = msgId;

		try {
			getResponseForSoapRequest(soapRequest);
		} catch (Exception e) {
			logger.error(" | {} | Error in SoapResponse constructor: {}", msgId, e.getMessage());
		}

		validateSOAPResponse(this.responseXml);
	}

	private void validateSOAPResponse(String responseXml) {
		logger.info(" | {} | Service - START: Validating SOAP Response", msgId);

		try {
			Document soapResponseDoc = XMLUtils.stringToXmlDocument(responseXml);

			// Assertion 1: Check if MessageID and RelatesTo elements are not null
			this.responseStatus = checkMessageIDAndRelatesToInResponse(msgId, soapResponseDoc);

			// Assertion 2: Find typeCode element under acknowledgement element and check
			// for code="AA"
			if (this.responseStatus == ResponseStatus.PASS) {
				checkTypeCodeValue(soapResponseDoc);
			}

		} catch (Exception e) {
			logger.error(" | {} | Error validating SOAP response: {}", msgId, e.getMessage());
		}

		logger.info(" | {} | Service - END: Validating SOAP Response", msgId);
	}

	private void checkTypeCodeValue(Document soapResponseDoc) throws XPathExpressionException {
		logger.debug(" | {} | Service - START: Checking typeCode Value", msgId);
		// Create an XPath instance
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();

		// Set a custom namespace context
		NamespaceContext nsContext = new NamespaceContext() {

			@Override
			public String getNamespaceURI(String prefix) {
				if ("hl7".equals(prefix)) {
					return "urn:hl7-org:v3";
				}
				return null;
			}

			@Override
			public String getPrefix(String namespaceURI) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Iterator<String> getPrefixes(String namespaceURI) {
				throw new UnsupportedOperationException();
			}
		};
		xpath.setNamespaceContext(nsContext);

		// Define the XPath expression to select the code attribute
		XPathExpression expr = xpath.compile("//hl7:acknowledgement/hl7:typeCode/@code");

		// Evaluate the XPath expression on the document
		this.responseTypeCode = (String) expr.evaluate(soapResponseDoc, XPathConstants.STRING);

		if ("AA".equals(this.responseTypeCode)) {
			logger.info(" | {} | Assertion 2 passed: typeCode=\"AA\"", msgId);
		} else  {
			logger.warn(" | {} | Assertion 2 failed: typeCode=\"{}\"", msgId, this.responseTypeCode);
//			this.responseStatus = ResponseStatus.FAIL;
		}

		logger.debug(" | {} | Service - END: Checking typeCode Value", msgId);
	}

	private ResponseStatus checkMessageIDAndRelatesToInResponse(String msgId, Document soapResponseDoc) {
		logger.debug(" | {} | Service - START: Checking MessageID and RelatesTo", msgId);

		this.responseMessageId = XMLUtils.getElementValue(soapResponseDoc, "wsa:MessageID");
		this.responseRelateTo = XMLUtils.getElementValue(soapResponseDoc, "wsa:RelatesTo");

		if (this.responseMessageId != null && this.responseRelateTo != null) {
			if (this.responseRelateTo.equals(msgId)) {
				logger.info(" | {} | Assertion 1 passed: MessageID and RelatesTo elements are not null",
						getResponseRelateTo());
				logger.info(" | {} | Response MessageID: {}", msgId, this.responseMessageId);
				logger.info(" | {} | Response RelatesTo: {}", msgId, this.responseRelateTo);
				return ResponseStatus.PASS;
			} else {
				logger.info(" | {} | Assertion 1 failed: Response is not related to the original soap request", msgId);
				return ResponseStatus.FAIL;
			}

		} else {
			logger.warn(" | {} | Assertion 1 failed: MessageID or RelatesTo is null", msgId);
			return ResponseStatus.FAIL;
		}
	}

	private void getResponseForSoapRequest(SoapRequest soapRequest) throws Exception {
		logger.info(" | {} | Service - START: Getting SOAP Response", msgId);
		logger.info(" | {} | Request Message ID : {}", msgId, msgId);
		logger.info(" | {} | Endpoint URL : {}", msgId, soapRequest.getEndpointUrl());
		logger.info(" | {} | Soap Action : {}", msgId, soapRequest.getSoapAction());

		long startTime = System.currentTimeMillis(); // Record the start time

		try {
			// Open an HTTP connection to the specified endpointURL
			HttpURLConnection connection = openConnection(soapRequest.getEndpointUrl(), soapRequest.getSoapAction());

			// Write the XML request to the output stream
			try (OutputStream os = connection.getOutputStream()) {
				byte[] xmlBytes = soapRequest.getRequestXml().getBytes(StandardCharsets.UTF_8);
				os.write(xmlBytes, 0, xmlBytes.length);
			}

			// Get the HTTP response code
			int responseCode = connection.getResponseCode();

			this.responseTime = calculateFormattedResponseTime(startTime);

			// Read and return the SOAP response based on the response code
			if (responseCode == HttpURLConnection.HTTP_OK) {
				this.responseXml = readSOAPResponse(connection.getInputStream());
			} else {
				this.responseXml = readErrorResponse(connection.getErrorStream(), responseCode);
				this.responseStatus = ResponseStatus.FAIL;
			}

		} catch (Exception e) {
			logger.error(" | {} | Error getting SOAP response: {}", msgId, e.getMessage());
			this.responseXml = "Error getting SOAP response: " + e.getMessage();
			this.responseStatus = ResponseStatus.FAIL;
		}

		logger.info(" | {} | Service - END: Getting SOAP Response", msgId);
	}

	private String readErrorResponse(InputStream errorStream, int responseCode) throws IOException {
		logger.debug(" | {} | Service - START: Reading Error Response", msgId);

		StringBuilder errorResponse = new StringBuilder();
		errorResponse.append("HTTP Response Code: ").append(responseCode).append("\n");

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				errorResponse.append(line);
			}
		}

		logger.debug(" | {} | Service - END: Reading Error Response", msgId);
		return errorResponse.toString();
	}

	private String readSOAPResponse(InputStream inputStream) throws Exception {
		logger.debug(" | {} | Service - START: Reading SOAP Response", msgId);

		String responseXmlString;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			responseXmlString = response.toString();
			logger.info(" | {} | Soap response: \n{}", msgId, XMLUtils.xmlStringToPrettyPrintString(responseXmlString));
			logger.info(" | {} | Response time for this Request: {}", msgId, this.responseTime + " msec");
		}

		logger.debug(" | {} | Service - END: Reading SOAP Response", msgId);
		return responseXmlString;
	}

	private String calculateFormattedResponseTime(long startTime) {
		logger.debug(" | {} | Service - START: Calculating Response Time", msgId);

		long endTime = System.currentTimeMillis();
		long responseTimeMillis = endTime - startTime;

		double responseTimeSeconds = (double) responseTimeMillis / 1000;

		// Format the response time with 3 decimal places
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		String formattedResponseTime = decimalFormat.format(responseTimeSeconds);

		logger.debug(" | {} | Service - END: Calculating Response Time", msgId);
		return formattedResponseTime;
	}

	private HttpURLConnection openConnection(String endpointUrl, String soapAction) throws IOException {
		logger.debug(" | {} | Service - START: Opening Connection", msgId);

		URL url = new URL(endpointUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("SOAPAction", soapAction);
		connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		connection.setDoOutput(true);
		connection.setDoInput(true);

		logger.debug(" | {} | Service - END: Opening Connection", msgId);
		return connection;
	}
}

enum ResponseStatus {
	PASS, FAIL
}
