package com.telushealth.hialtesthub.entity.soaphandler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLUtils {

	public static String readFileToString(String filePath) throws IOException {
		try {
			byte[] encodedBytes = Files.readAllBytes(Paths.get(filePath));
			System.out.println("Reading file " + filePath);
			return new String(encodedBytes, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw e;
		}
	}

	public static void writeStringToFile(String content, String filePath) throws IOException {
		try {
			byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
			Files.write(Paths.get(filePath), bytes);
			System.out.println("Writing to " + filePath);
		} catch (IOException e) {
			throw e;
		}
	}

	public static Document stringToXmlDocument(String xmlString) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xmlString)));

	}

	public static String xmlDocumentToString(Document document) throws Exception {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			DOMSource source = new DOMSource(document);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			return writer.toString();
		} catch (Exception e) {
			throw e;
		}
	}

	public static String xmlDocumentToFormattedString(Document document) throws Exception {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		} catch (Exception e) {
			throw e;
		}
	}

	public static Document replaceNodeInXmlDocument(Document xmlDocument, String nodeName, Document updatedNode) {
		NodeList nodes = xmlDocument.getElementsByTagName(nodeName);
		if (nodes.getLength() > 0) {
			Node nodeToReplace = nodes.item(0);
			Node parentNode = nodeToReplace.getParentNode();
			Node importedNode = xmlDocument.importNode(updatedNode.getDocumentElement(), true);
			parentNode.replaceChild(importedNode, nodeToReplace);
		} else {
			throw new IllegalArgumentException("Node with name '" + nodeName + "' not found in the XML document.");
		}
		return xmlDocument;
	}

	public static String xmlStringToPrettyPrintString(String xmlString) throws Exception {
		Document xmlDoc = stringToXmlDocument(xmlString);
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, "html");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(xmlDoc), new StreamResult(writer));
			return writer.toString();
		} catch (Exception e) {
			throw e;
		}
	}

	public static Document updateValueInXMLDocument(Document doc, String elementName, String value) throws Exception {
		NodeList elementNode = doc.getElementsByTagName(elementName);
		if (elementNode.getLength() > 0) {
			Element element = (Element) elementNode.item(0);
			element.setTextContent(value);
		} else {
		}
		return doc;
	}

	public static String getElementValue(Document document, String elementName) {
		NodeList nodeList = document.getElementsByTagName(elementName);
		if (nodeList.getLength() > 0) {
			return nodeList.item(0).getTextContent();
		}
		return null;
	}

	public static String getAttributeValue(Document document, String elementName, String attributeName) {
		NodeList nodeList = document.getElementsByTagName(elementName);
		if (nodeList.getLength() > 0) {
			Element element = (Element) nodeList.item(0);
			if (element.hasAttribute(attributeName)) {
				return element.getAttribute(attributeName);
			}
		}
		return null;
	}

	public static Document createNewDocument() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.newDocument();
	}
}
