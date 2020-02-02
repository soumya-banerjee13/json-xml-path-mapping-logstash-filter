package edu.soumya.logstash.filter.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;

import org.junit.Test;
import org.w3c.dom.Document;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class XmlParseUtilTest {
	@Test
	public void simpleXmlXpathExpressionTest() {
		try {
			DocumentBuilder docBuilder = XmlParseUtil.createDocBuilderInstance();
			XPath xPath = XmlParseUtil.createXPathInstance();
			String filePath1 = Paths.get(this.getClass().getClassLoader().getResource("test-files/xmlUtil/test1.xml").toURI()).toString();
			Document doc1 = XmlParseUtil.getDocument(docBuilder,getContentFromFile(filePath1));
			test1stXml(doc1,xPath);
			String filePath2 = Paths.get(this.getClass().getClassLoader().getResource("test-files/xmlUtil/test2.xml").toURI()).toString();
			Document doc2 = XmlParseUtil.getDocument(docBuilder,getContentFromFile(filePath2));
			test2ndXml(doc2,xPath);
		} catch (ConfigurationException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private void test1stXml(Document doc,XPath xPath) throws ConfigurationException {
		HashMap<String,String> xpathExpressionValueMap = new HashMap<String,String>();
		xpathExpressionValueMap.put("/class/student/firstname", "Soumya");
		xpathExpressionValueMap.put("/class/student/lastname", "Banerjee");
		xpathExpressionValueMap.put("/class/student/nickname", "Bittu");
		xpathExpressionValueMap.put("/class/student/marks", "85");
		for(String key : xpathExpressionValueMap.keySet()) {
			assertEquals(xpathExpressionValueMap.get(key),XmlParseUtil.getStringFromXPath(doc, xPath, key));
		}
	}
	
	private void test2ndXml(Document doc,XPath xPath) throws ConfigurationException {
		HashMap<String,String> xpathExpressionValueMap = new HashMap<String,String>();
		xpathExpressionValueMap.put("/planes_for_sale/ad/year", "1977");
		xpathExpressionValueMap.put("/planes_for_sale/ad/make", "Soumya");
		xpathExpressionValueMap.put("/planes_for_sale/ad/model", "Skyhawk");
		xpathExpressionValueMap.put("/planes_for_sale/ad/color", "Light blue and white");
		xpathExpressionValueMap.put("/planes_for_sale/ad/description", "New paint, nearly new interior," + 
				"			685 hours SMOH, full IFR King avionics");
		xpathExpressionValueMap.put("/planes_for_sale/ad/price", "23,495");
		xpathExpressionValueMap.put("/planes_for_sale/ad/seller", "Skyway Aircraft");
		xpathExpressionValueMap.put("/planes_for_sale/ad/location/city", "Rapid City,");
		xpathExpressionValueMap.put("/planes_for_sale/ad/location/state", "South Dakota");
		for(String key : xpathExpressionValueMap.keySet()) {
			assertEquals(xpathExpressionValueMap.get(key),XmlParseUtil.getStringFromXPath(doc, xPath, key));
		}
	}
	
	private String getContentFromFile(String filePath) {
		try (Reader reader = new FileReader(filePath)) {
			BufferedReader buffReader = new BufferedReader(reader, 32768);
			StringBuilder fileContent = new StringBuilder();
			String line;
			while ((line = buffReader.readLine()) != null) {
				fileContent.append(line);
			}
			return fileContent.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
}
