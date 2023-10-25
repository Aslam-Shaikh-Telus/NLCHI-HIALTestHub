package com.telushealth.hialtesthub.entity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.telushealth.hialtesthub.entity.soaphandler.XMLDigest;
import com.telushealth.hialtesthub.entity.soaphandler.XMLSigner;
import com.telushealth.hialtesthub.entity.soaphandler.XMLUtils;

@org.springframework.data.mongodb.core.mapping.Document
public class SoapRequest {

	private static final Logger logger = LoggerFactory.getLogger(SoapRequest.class);
	@Id
	private String id;
	private String msgId;
	private String interactionId;
	private String description;
	private String soapAction;
	private String endpointUrl;
	private String requestXml;

	public String getMsgId() {
		return msgId;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public String getDescription() {
		return description;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public String getRequestXml() {
		return requestXml;
	}

	public SoapRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SoapRequest(String msgId, Endpoint endpoint, TestCase test) {

		this.msgId = msgId;
		this.interactionId = test.getInteractionId();
		this.description = test.getDescription();
		this.soapAction = test.getSoapAction();
		this.endpointUrl = "http://" + endpoint.getIpAddress() + endpoint.getTestOrprod() + test.getEndpoint();

		try {
			this.requestXml = generateSoapRequest(msgId, endpoint.getUsername(), endpoint.getPassword(), test.getXml());
		} catch (Exception e) {
			logger.error("Error generating SOAP request XML", e);
		}
	}

	private String generateSoapRequest(String msgId, String username, String password, String testXml)
			throws Exception {
		logger.info("Service - START: Generating SOAP Request");

		String queryId = UUID.randomUUID().toString().toUpperCase();
		testXml = XMLUtils.xmlDocumentToString(XMLUtils.stringToXmlDocument(testXml));
		logger.debug("Imported SOAP XML from testCase:\n{}", XMLUtils.xmlStringToPrettyPrintString(testXml));

		String soapXml = updateSoapXmlWithValues(msgId, queryId, username, password, testXml);

		logger.debug("SoapXML with updated {}, {}, {}, and {}:\n{}", "msgId", "queryId", "username", "password",
				XMLUtils.xmlStringToPrettyPrintString(soapXml));

		Document soapMessage = XMLUtils.stringToXmlDocument(soapXml);
		Document soapBody = extractElementFromDocument("soap:Body", soapMessage);
		String soapBodyString = XMLUtils.xmlDocumentToString(soapBody);

		logger.info("Extracted SOAP Body:\n{}", soapBodyString);

		XMLDigest soapDigest = new XMLDigest(msgId, soapBodyString);
		soapDigest.digestBody();
		String digestValue = soapDigest.getDigestValue();
		String canonicalBodyString = soapDigest.getCanonicalString();

		Document canonicalBody = XMLUtils.stringToXmlDocument(canonicalBodyString);
		Document signedInfo = extractElementFromDocument("ds:SignedInfo", soapMessage);

		logger.debug("Service - START: Updating SignedInfo with DigestValue");
		String updatedSignedInfoString = XMLUtils.xmlDocumentToFormattedString(
				XMLUtils.updateValueInXMLDocument(signedInfo, "ds:DigestValue", digestValue));
		logger.debug("Service - END: Updating SignedInfo with DigestValue");

		XMLSigner sig = new XMLSigner(msgId, password, updatedSignedInfoString);
		sig.signLoadMsg();
		String signatureValue = sig.getSignatureValue();

		Document soapRequest = soapMessage;
		String soapRequestString = XMLUtils.xmlDocumentToString(soapRequest);
		logger.debug("SOAP Message:\n{}", soapRequestString);

		logger.debug("Service - START: Updating SOAP Message with Canonical SOAP Body");
		soapRequest = XMLUtils.replaceNodeInXmlDocument(soapRequest, "soap:Body", canonicalBody);
		logger.debug("Service - END: Updating SOAP Message with Canonical SOAP Body");

		logger.debug("Service - START: Updating SOAP Message with SignedInfo");
		soapRequest = XMLUtils.replaceNodeInXmlDocument(soapRequest, "ds:SignedInfo",
				XMLUtils.stringToXmlDocument(updatedSignedInfoString));
		logger.debug("Service - END: Updating SOAP Message with SignedInfo");

		soapRequest = XMLUtils.updateValueInXMLDocument(soapRequest, "ds:SignatureValue", signatureValue);
		soapXml = XMLUtils.xmlDocumentToString(soapRequest);

		logger.info("Final SOAP Request XML:\n{}", soapXml);
		logger.info("Service - END: Generating SOAP Request");

		return soapXml;
	}

	private Document extractElementFromDocument(String elementName, Document doc) throws Exception {
		Element element = (Element) doc.getElementsByTagName(elementName).item(0);

		if (element != null) {
			Document elementDoc = XMLUtils.createNewDocument();
			Node importedNode = elementDoc.importNode(element, true);
			elementDoc.appendChild(importedNode);
			return elementDoc;
		} else {
			throw new IOException(elementName + " not found in the message.");
		}
	}

	private String updateSoapXmlWithValues(String msgId, String queryId, String username, String password,
			String testXml) {
		testXml = testXml.replace("${#TestCase#username}", username);
		testXml = testXml.replace("${#TestCase#digestPassword}", generateDigestPassword(password));
		testXml = testXml.replace("${#TestCase#MSG_ID}", msgId);
		testXml = testXml.replace("${#TestCase#queryid}", queryId);
		return testXml;
	}

	public String generateDigestPassword(String password) {
		try {
			logger.debug("Service - START: Generate Digest Password");
			byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			sha.reset();
			sha.update(passwordBytes);
			byte[] passwordDigest = sha.digest();

			String passwordDigestString = Base64.getEncoder().encodeToString(passwordDigest).trim();
			logger.info("Password Digest: " + passwordDigestString);

			logger.debug("Service - END: Generate Digest Password");
			return passwordDigestString;
		} catch (NoSuchAlgorithmException e) {
			logger.error("SHA-1 algorithm not available.", e);
			throw new RuntimeException("SHA-1 algorithm not available.", e);
		}
	}
}
