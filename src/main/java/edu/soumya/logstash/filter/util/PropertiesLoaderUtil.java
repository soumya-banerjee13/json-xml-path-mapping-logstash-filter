package edu.soumya.logstash.filter.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class PropertiesLoaderUtil {
	/**
	 * Return 
	 * @param filePath
	 * @return properties loaded from <code>filePath</code>
	 * @throws ConfigurationException 
	 * @throws IOException if the <code>filePath</code> is not the location of a file<br>
	 * or an error occurred when reading the input file 
	 */
	public static Properties getPropertiesFromFile(String filePath) throws ConfigurationException {
		Properties prop = new Properties();
		try {
			Reader reader = new FileReader(filePath);
			prop.load(reader);
		} catch(IOException ioException) {
			throw new ConfigurationException("Unable to parse the properties file", ioException);
		}
		return prop;
	}
}
