package edu.soumya.logstash.filter.util;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class JsonParseUtil {
	/**
	 * @param jsonString
	 * @return
	 */
	public static DocumentContext getDocumentContext(String jsonString) {
		return JsonPath.parse(jsonString);
	}
	
	/**
	 * @param ctx
	 * @param jsonPath
	 * @return
	 * @throws ConfigurationException 
	 */
	public static String getStringFromJsonPath(DocumentContext ctx, String jsonPath) throws ConfigurationException {
		try {
			Object value = ctx.read(jsonPath);
			return String.valueOf(value);
		} catch (RuntimeException e) {
			throw new ConfigurationException("Failed to evaluate the jsonPath expression.", e);
		}
	}
}
