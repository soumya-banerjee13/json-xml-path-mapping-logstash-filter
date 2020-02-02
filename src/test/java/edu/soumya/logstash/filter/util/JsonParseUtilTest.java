package edu.soumya.logstash.filter.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Test;

import com.jayway.jsonpath.DocumentContext;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class JsonParseUtilTest {
	@Test
	public void simpleJsonpathExpressionTest() {
		try {
			String filePath1 = Paths.get(this.getClass().getClassLoader().getResource("test-files/jsonUtil/test1.json").toURI()).toString();
			DocumentContext docContext1 = JsonParseUtil.getDocumentContext(getContentFromFile(filePath1));
			test1stJson(docContext1);
			String filePath2 = Paths.get(this.getClass().getClassLoader().getResource("test-files/jsonUtil/test2.json").toURI()).toString();
			DocumentContext docContext2 = JsonParseUtil.getDocumentContext(getContentFromFile(filePath2));
			test2ndJson(docContext2);
		} catch (URISyntaxException | ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void jsonWithArrayTest() {
		try {
			String filePath = Paths.get(this.getClass().getClassLoader().getResource("test-files/jsonUtil/stations.json").toURI()).toString();
			DocumentContext docContext = JsonParseUtil.getDocumentContext(getContentFromFile(filePath));
			assertEquals("hwh",JsonParseUtil.getStringFromJsonPath(docContext, "$.stations[0].station.id"));
			assertEquals("Howrah",JsonParseUtil.getStringFromJsonPath(docContext, "$.stations[0].station.name"));
			assertEquals("30",JsonParseUtil.getStringFromJsonPath(docContext, "$.stations[0].station.trainsToday"));
		} catch (URISyntaxException | ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void test1stJson(DocumentContext docContext) throws ConfigurationException {
		HashMap<String,String> jsonpathExpressionValueMap = new HashMap<String,String>();
		jsonpathExpressionValueMap.put("$.student.firstname", "Soumya");
		jsonpathExpressionValueMap.put("$.student.lastname", "Banerjee");
		jsonpathExpressionValueMap.put("$.student.nickname", "Bittu");
		jsonpathExpressionValueMap.put("$.student.marks", "85");
		for(String key : jsonpathExpressionValueMap.keySet()) {
			assertEquals(jsonpathExpressionValueMap.get(key),JsonParseUtil.getStringFromJsonPath(docContext, key));
		}
	}
	
	private void test2ndJson(DocumentContext docContext) throws ConfigurationException {
		HashMap<String,String> jsonpathExpressionValueMap = new HashMap<String,String>();
		jsonpathExpressionValueMap.put("$.ad.year", "1977");
		jsonpathExpressionValueMap.put("$.ad.make", "Soumya");
		jsonpathExpressionValueMap.put("$.ad.model", "Skyhawk");
		jsonpathExpressionValueMap.put("$.ad.color", "Light blue and white");
		jsonpathExpressionValueMap.put("$.ad.description", "New paint, nearly new interior, 685 hours SMOH, full IFR King avionics");
		jsonpathExpressionValueMap.put("$.ad.price", "23,495");
		jsonpathExpressionValueMap.put("$.ad.seller", "Skyway Aircraft");
		jsonpathExpressionValueMap.put("$.ad.location.city", "Rapid City,");
		jsonpathExpressionValueMap.put("$.ad.location.state", "South Dakota");
		for(String key : jsonpathExpressionValueMap.keySet()) {
			assertEquals(jsonpathExpressionValueMap.get(key),JsonParseUtil.getStringFromJsonPath(docContext, key));
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
