package edu.soumya.logstash.filter.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.soumya.logstash.filter.constants.Constants;
import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class XmlParseUtil {

	/**
	 * @return A new {@link DocumentBuilder} Instance
	 * @throws ConfigurationException
	 */
	public static DocumentBuilder createDocBuilderInstance() throws ConfigurationException {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ConfigurationException("Failed to get the Xml Document Builder.", e);
		}
	}

	/**
	 * @return A new {@link XPath} Instance
	 */
	public static XPath createXPathInstance() {
		return XPathFactory.newInstance().newXPath();
	}

	/**
	 * Returns the {@link Document} instance after parsing the xml.
	 * 
	 * @param xmlString
	 * @return
	 * @throws ConfigurationException
	 */
	public static Document getDocument(DocumentBuilder docBuilder, String xmlString) throws ConfigurationException {
		Document document = null;
		try {
			InputStream input = new ByteArrayInputStream(xmlString.getBytes(Constants.CHARSET_ENCODING_UTF8));
			document = docBuilder.parse(input);
		} catch (SAXException | IOException e) {
			throw new ConfigurationException("Failed to Parse the xml document.", e);
		}
		return document;
	}

	/**
	 * Returns the string type value from the <code>document</code> , using the given
	 * <code>xPathExpression</code>
	 * 
	 * @param document
	 * @param xPathExpression
	 * @return
	 * @throws ConfigurationException
	 *             In case of invalid <code>xPath</code> is given
	 */
	public static String getStringFromXPath(Document document, XPath xPath, String xPathExpression)
			throws ConfigurationException {
		String valueFromXPath = StringUtils.EMPTY;
		try {
			valueFromXPath = ((String) xPath.compile(xPathExpression).evaluate(document, XPathConstants.STRING)).trim();
		} catch (XPathExpressionException e) {
			throw new ConfigurationException("Failed to evaluate the xpath expression.", e);
		}
		return valueFromXPath;
	}
}
