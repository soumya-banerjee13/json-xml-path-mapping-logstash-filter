package edu.soumya.logstash.filter.constants;

/**
 * @author Soumya Banerjee
 *
 */
public final class Constants {
	public static final String DOC_TYPE_XML = "xml";
	public static final String DOC_TYPE_JSON = "json";
	public static final String XML_IDENTIFIER_KEY = "identifier.attribute.path.xml";
	public static final String JSON_IDENTIFIER_KEY = "identifier.attribute.path.json";
	public static final String XML_CONFIG_FOLDER_PATH_KEY = "config.location.xml";
	public static final String JSON_CONFIG_FOLDER_PATH_KEY = "config.location.json";
	public static final String IDENTIFIER_EVENT_FIELD = "doc_id";
	public static final String CONFIG_FILE_EXTENSION = ".conf";
	public static final String DOCUMENT_PARSE_FAILURE_TAG = "_documentparsefailure";
	public static final String CHARSET_ENCODING_UTF8 = "UTF-8";
	//Logging Configuration Constants
	public static final String LOG_FOLDER_PATH = "logFolderPath";
	public static final String LOGGING_MAX_HISTORY = "loggingMaxHistory";
	public static final String LOG_LEVEL = "logLevel";
}
