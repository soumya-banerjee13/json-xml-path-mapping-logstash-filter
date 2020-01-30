package edu.soumya.logstash.filter.util;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Soumya Banerjee
 *
 */
public class XmlParseUtil {
	/**
	 * @param xmlString
	 * @return
	 */
	public static Document getDocument(String xmlString) {
		DocumentBuilder docBuilder = null;
		Document document = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			document = docBuilder.parse(xmlString);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * @param document
	 * @param xPath
	 * @return
	 */
	public static String getStringFromXPath(Document document, String xPath) {
		String valueFromXPath = StringUtils.EMPTY;
		try {
			valueFromXPath = (String) XPathFactory.newInstance().newXPath().compile(xPath).evaluate(document,
					XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valueFromXPath;
	}
}
