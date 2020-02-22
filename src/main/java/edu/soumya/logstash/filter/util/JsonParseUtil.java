package edu.soumya.logstash.filter.util;

import org.apache.commons.lang3.StringUtils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class JsonParseUtil {
	/**
	 * Returns the {@link DocumentContext} instance after parsing the json.
	 * 
	 * @param jsonString
	 * @return
	 */
	public static DocumentContext getDocumentContext(String jsonString) {
		return JsonPath.parse(jsonString);
	}

	/**
	 * Returns the string type value from the given <code>jsonPath</code> in
	 * <code>context</code>
	 * 
	 * @param context
	 * @param jsonPath
	 * @return
	 * @throws ConfigurationException
	 */
	public static String getStringFromJsonPath(DocumentContext context, String jsonPath) throws ConfigurationException {
		try {
			Object value = context.read(jsonPath);
			return String.valueOf(value);
		} catch(PathNotFoundException pnfe) {
			return StringUtils.EMPTY;
		} catch (RuntimeException e) {
			throw new ConfigurationException("Failed to evaluate the jsonPath expression.", e);
		}
	}
}
