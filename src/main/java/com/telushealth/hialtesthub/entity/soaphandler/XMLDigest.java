package com.telushealth.hialtesthub.entity.soaphandler;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.Base64;

import org.apache.xml.security.c14n.Canonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLDigest {

    private static final Logger logger = LoggerFactory.getLogger(XMLDigest.class);

    private final String msgId;
    private final String xmlString;
    private String canonicalString;
    private String digestValue;

    public String getCanonicalString() {
        return canonicalString;
    }

    public String getDigestValue() {
        return digestValue;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getXmlString() {
        return xmlString;
    }

    public XMLDigest(String msgId, String xmlString) {
        this.msgId = msgId;
        this.xmlString = xmlString;
    }

    public void digestBody() {
        try {
            logger.debug("Service - START: Generating SOAP Digest Value");

            byte[] bodyByteArray = this.xmlString.getBytes();

            org.apache.xml.security.Init.init();
            Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
            ByteArrayOutputStream canonicalXmlOutputStream = new ByteArrayOutputStream();
            canon.canonicalize(bodyByteArray, canonicalXmlOutputStream, false);

            byte[] canonXmlBytes = canonicalXmlOutputStream.toByteArray();

            this.canonicalString = new String(canonXmlBytes);
            logger.debug("Canonical Body: {}", this.canonicalString);

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(canonXmlBytes);
            byte[] hash = digest.digest();

            this.digestValue = Base64.getEncoder().encodeToString(hash);
            logger.info("Digest Value: {}", this.digestValue);

            logger.debug("Service - END: Generating SOAP Digest Value");
        } catch (Exception e) {
            throw new RuntimeException("Error processing XML digest.", e);
        }
    }
}
