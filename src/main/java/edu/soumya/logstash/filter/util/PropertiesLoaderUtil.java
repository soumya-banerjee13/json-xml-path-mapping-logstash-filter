package edu.soumya.logstash.filter.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * @author SOUMYA BANERJEE
 *
 */
public class PropertiesLoaderUtil {
	/**
	 * Return 
	 * @param filePath
	 * @return properties loaded from <code>filePath</code>
	 * @throws IOException if the <code>filePath</code> is not the location of a file<br>
	 * or an error occurred when reading the input file 
	 */
	public static Properties getPropertiesFromFile(String filePath) throws IOException {
		Reader reader = new FileReader(filePath);
		Properties prop = new Properties();
		prop.load(reader);
		return prop;
	}
}
