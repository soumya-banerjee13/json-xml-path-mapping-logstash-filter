package edu.soumya.logstash.filter.constants;

/**
 * @author Soumya Banerjee
 *
 */
public final class Constants {
	/**
	 * Xml Document Type
	 */
	public static final String DOC_TYPE_XML = "xml";

	/**
	 * Json Document Type
	 */
	public static final String DOC_TYPE_JSON = "json";

	/**
	 * Key to get the identifier xPath in xml documents
	 */
	public static final String XML_IDENTIFIER_KEY = "identifier.attribute.path.xml";

	/**
	 * Key to get the identifier jsonPath in json documents
	 */
	public static final String JSON_IDENTIFIER_KEY = "identifier.attribute.path.json";

	/**
	 * Key to get the location of configuration files to parse xml documents
	 */
	public static final String XML_CONFIG_FOLDER_PATH_KEY = "config.location.xml";

	/**
	 * Key to get the location of configuration files to parse json documents
	 */
	public static final String JSON_CONFIG_FOLDER_PATH_KEY = "config.location.json";

	/**
	 * Field to add to the events with identifier value
	 */
	public static final String IDENTIFIER_EVENT_FIELD = "doc_id";

	/**
	 * File extension for configuration files
	 */
	public static final String CONFIG_FILE_EXTENSION = ".conf";

	/**
	 * Tag to add to the events in case of failure during parsing documents
	 */
	public static final String DOCUMENT_PARSE_FAILURE_TAG = "_documentparsefailure";

	/**
	 * Charset Encoding UTF-8
	 */
	public static final String CHARSET_ENCODING_UTF8 = "UTF-8";
}
