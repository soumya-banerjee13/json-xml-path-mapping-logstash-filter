package edu.soumya.logstash.filter.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author SOUMYA BANERJEE
 *
 */
public class Configurations {

	public static final String KEY_VALUE_SEPARATOR = "=>";
	Map<String, String> keyValueConfigs;

	public Configurations() {
		keyValueConfigs = new HashMap<String, String>();
	}

	/**
	 * Loads the Configurations from the files, whose location is shared as
	 * <code>configFilePath</code
	 * 
	 * @param configFilePath
	 * @return Configurations
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public static Configurations loadConfigFromFile(String configFilePath) throws IOException, ConfigurationException {
		Reader reader = new FileReader(configFilePath);
		Configurations configs = new Configurations();
		configs.load(reader);
		return configs;
	}

	/**
	 * Loads the Key and Value from the lines of the Content supplied by the
	 * <code>reader.</code> <br>
	 * Splits every line by the delimiter: => .<br>
	 * If after splitting exact two strings are not found or same key found multiple
	 * times, throws {@link ConfigurationException}
	 * 
	 * @param reader
	 * @throws IOException,ConfigurationException
	 * @throws ConfigurationException
	 */
	public void load(Reader reader) throws IOException, ConfigurationException {
		BufferedReader buffReader = new BufferedReader(reader, 32768);
		String line;
		while ((line = buffReader.readLine()) != null) {
			if (StringUtils.isBlank(line))
				continue;
			String[] keyValue = line.split(KEY_VALUE_SEPARATOR);
			if (keyValue.length != 2)
				throw new ConfigurationException("Improper Configuration Supplied");
			String key = keyValue[0];
			String value = keyValue[1];
			if (this.keyValueConfigs.containsKey(key))
				throw new ConfigurationException("Duplicate Key Found in Confugration");
			this.keyValueConfigs.put(key, value);
		}
	}
	
	/**
	 * Searches for the configuration with the specified key in this configuration list.<br>
	 * The method returns
     * {@code null} if the configuration is not found.
	 * 
	 * @param key
	 * @return
	 */
	public String getConfiguration(String key) {
		return this.keyValueConfigs.get(key);
	}
	
	/**
	 * This method returns all the loaded Configuration keys
	 * @return All Configuration keys
	 */
	public Set<String> getAllConfigurationKeys() {
		return this.keyValueConfigs.keySet();
	}
}
