package edu.soumya.logstash.filter.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class ConfigurationsTest {
	
	@Test
	public void notAFileLocationTest() throws ConfigurationException {
		try {
			String folderPath = Paths.get(this.getClass().getClassLoader().getResource("test-files/configs/").toURI()).toString();
			Configurations configs = Configurations.loadConfigFromFile(folderPath);
			assertTrue(configs.isEmptyConfigSet());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void emptyConfigFileTest() throws ConfigurationException {
		try {
			String filePath = Paths.get(this.getClass().getClassLoader().getResource("test-files/configs/emptyConfig.conf").toURI()).toString();
			Configurations configs = Configurations.loadConfigFromFile(filePath);
			assertTrue(configs.isEmptyConfigSet());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void properConfigurationFileTest() throws ConfigurationException {
		try {
			String filePath = Paths.get(this.getClass().getClassLoader().getResource("test-files/configs/properConfig.conf").toURI()).toString();
			Configurations configs = Configurations.loadConfigFromFile(filePath);
			assertFalse(configs.isEmptyConfigSet());
			validateConfigKeySet(configs);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private void validateConfigKeySet(Configurations configs) {
		Map<String,String> expectedKeyValueMap = new HashMap<>();
		expectedKeyValueMap.put("/bookstore/book/title","title");
		expectedKeyValueMap.put("/bookstore/book/name","name");
		
		for(String key : expectedKeyValueMap.keySet()) {
			assertTrue(configs.getAllConfigurationKeys().contains(key));
			validateConfigValue(configs,key,expectedKeyValueMap.get(key));
		}
	}
	
	private void validateConfigValue(Configurations configs,String key,String expectedValue) {
		assertEquals(expectedValue, configs.getValue(key));
	}
}
