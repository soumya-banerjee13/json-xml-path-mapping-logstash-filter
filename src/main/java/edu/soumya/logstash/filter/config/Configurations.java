package edu.soumya.logstash.filter.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class Configurations {
	
	/**
	 * Logger Instance
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(Configurations.class);
	
	/**
	 * Delimiter used to generate key and values from configuration file
	 */
	public static final String KEY_VALUE_SEPARATOR = "=>";
	
	/**
	 * Generate keys and values from configuration file.
	 * Should be initialized lazily when any configuration found.
	 */
	private Map<String, String> keyValueConfigs;
	
	/**
	 * Indicates if configuration file present. If the file not present set this
	 * flag to false.
	 */
	private Boolean configFilePresentFlag = Boolean.TRUE;

	public Configurations() {
		
	}

	/**
	 * @return the configFilePresentFlag
	 */
	public Boolean getConfigFilePresentFlag() {
		return configFilePresentFlag;
	}

	/**
	 * @param configFilePresentFlag the configFilePresentFlag to set
	 */
	public void setConfigFilePresentFlag(Boolean configFilePresentFlag) {
		this.configFilePresentFlag = configFilePresentFlag;
	}

	/**
	 * Loads the Configurations from the files, whose location is shared as
	 * <code>configFilePath</code>
	 * 
	 * @param configFilePath
	 * @return Configurations
	 * @throws ConfigurationException
	 */
	public static Configurations loadConfigFromFile(String configFilePath) throws ConfigurationException {
		Configurations configs = new Configurations();
		try (Reader reader = new FileReader(configFilePath)) {
			configs.load(reader);
		} catch (FileNotFoundException e) {
			// If file is not found set configFilePresentFlag = false.
			// Print the error log and suppress the Exception
			configs.setConfigFilePresentFlag(Boolean.FALSE);
			LOGGER.error("Configuration file not found in the path:", configFilePath, e);
		} catch (IOException ioException) {
			throw new ConfigurationException("Failed to close the Configuration file Stream", ioException);
		}
		return configs;
	}

	/**
	 * Loads the Key and Value from the lines of the Content supplied by the
	 * <code>reader.</code> <br>
	 * Splits every line by the delimiter: => .<br>
	 * If after splitting exact two strings are not found in a line or same key
	 * found multiple times, throws {@link ConfigurationException} <br>
	 * It does not close the stream of the reader, calling method should close the
	 * supplied reader after calling this method.
	 * 
	 * 
	 * @param reader
	 * @throws ConfigurationException
	 */
	public void load(Reader reader) throws ConfigurationException {
		if(reader == null) throw new ConfigurationException("Error in loading configuration. Reader not initialized.");
		BufferedReader buffReader = new BufferedReader(reader, 32768);
		String line;
		try {
			while ((line = buffReader.readLine()) != null) {
				if (StringUtils.isBlank(line))
					continue;
				String[] keyValue = line.split(KEY_VALUE_SEPARATOR);
				if (keyValue.length != 2)
					throw new ConfigurationException("Improper Configuration Supplied");
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();
				//Lazily initialize the keyValueConfigs Map
				if(this.keyValueConfigs==null) {
					keyValueConfigs = new HashMap<String, String>();
				}
				if (this.keyValueConfigs.containsKey(key))
					throw new ConfigurationException("Duplicate Key Found in Confugration");
				this.keyValueConfigs.put(key, value);
			}
		} catch(IOException ioException) {
			throw new ConfigurationException("Failed to read line, from the reader:"+ reader,ioException);
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
	public String getValue(String key) {
		return this.keyValueConfigs.get(key);
	}
	
	/**
	 * This method returns all the loaded Configuration keys
	 * @return All Configuration keys
	 */
	public Set<String> getAllConfigurationKeys() {
		return this.keyValueConfigs.keySet();
	}
	
	public Boolean isEmptyConfigSet() {
		return Boolean.valueOf((this.keyValueConfigs==null) || (this.keyValueConfigs.size()==0));
	}

}
