package com.telushealth.hialtesthub.entity.soaphandler;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class XMLSigner {

	private static final Logger logger = LoggerFactory.getLogger(XMLSigner.class);

	private final String msgID;
	private final String password;
	private byte[] secretKey;
	private String signatureValue;
	private final String xmlString;

	public XMLSigner(String msgId, String password, String xmlString) {
		this.msgID = msgId;
		this.password = password;
		this.xmlString = xmlString;
	}

	public String getPassword() {
		return this.password;
	}

	public byte[] getSecretKey() {
		return this.secretKey;
	}

	public String getSignatureValue() {
		return this.signatureValue;
	}

	public String getXmlString() {
		return xmlString;
	}

	private byte[] generateKey(String password) {
		try {
			logger.debug("Service - START: Generating Key");
			byte[] pwBytes = password.getBytes();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			sha.reset();
			byte[] key20 = sha.digest(pwBytes);
			sha.reset();
			key20 = sha.digest(key20);
			byte[] key16 = new byte[16];
			System.arraycopy(key20, 0, key16, 0, 16);
			logger.debug("Service - END: Generating Key");
			return key16;
		} catch (NoSuchAlgorithmException e) {
			logger.error("SHA-1 algorithm not available.", e);
			throw new RuntimeException("SHA-1 algorithm not available.", e);
		}
	}

	public void signLoadMsg() {
		try {
			logger.debug("Service - START: Signing Load Message");
			this.secretKey = generateKey(this.password);
			SecretKeySpec secKeySpec = new SecretKeySpec(this.secretKey, "AES");
			logger.debug("SignedInfo is :\n{}", Arrays.toString(this.xmlString.getBytes()));
			String modifiedInputString = this.xmlString.replaceAll("\r", "");
			byte[] modifiedInputFile = modifiedInputString.getBytes(StandardCharsets.UTF_8);
			logger.debug("Modified SignedInfo is :\n{}", Arrays.toString(modifiedInputFile));
			Init.init();
			Document doc = XMLUtils.createNewDocument();
			SignatureAlgorithm sigAlgorithmA = new SignatureAlgorithm(doc,
					"http://www.w3.org/2000/09/xmldsig#hmac-sha1", 256);
			sigAlgorithmA.initSign(secKeySpec);
			sigAlgorithmA.update(modifiedInputFile);
			byte[] signatureValue = sigAlgorithmA.sign();
			String signatureValueString = Base64.getEncoder().encodeToString(signatureValue);
			logger.info("Signature Value: {}", signatureValueString);
			this.signatureValue = signatureValueString;
			logger.debug("Service - END: Signing Load Message");
		} catch (Exception e) {
			logger.error("Error during signing operation.", e);
			throw new RuntimeException("Error during signing operation.", e);
		}
	}
}
