package edu.soumya.logstash.filter.util;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Test;

import edu.soumya.logstash.filter.constants.Constants;
import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class PropertiesLoaderUtilTest {
	@Test
	public void standardPropertiesFileLoadTest() throws ConfigurationException {
		String filePath;
		try {
			filePath = Paths.get(this.getClass().getClassLoader().getResource("test-files/propUtil/test.properties").toURI()).toString();
			Properties prop = PropertiesLoaderUtil.getPropertiesFromFile(filePath);
			assertEquals("/bookstore/book/title",prop.get(Constants.XML_IDENTIFIER_KEY));
			assertEquals("$.bookstore.book.title",prop.get(Constants.JSON_IDENTIFIER_KEY));
			assertEquals("/folder/xml-folder",prop.get(Constants.XML_CONFIG_FOLDER_PATH_KEY));
			assertEquals("/folder/json-folder",prop.get(Constants.JSON_CONFIG_FOLDER_PATH_KEY));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
