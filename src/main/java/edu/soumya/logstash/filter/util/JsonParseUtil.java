package edu.soumya.logstash.filter.util;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;

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
	 */
	public static String getStringFromJsonPath(DocumentContext ctx, String jsonPath) {
		return ctx.read(jsonPath,new TypeRef<String>(){});
	}
}
