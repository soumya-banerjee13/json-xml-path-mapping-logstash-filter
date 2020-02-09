package edu.soumya.logstash.filter;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import com.jayway.jsonpath.DocumentContext;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import co.elastic.logstash.api.FilterMatchListener;
import co.elastic.logstash.api.LogstashPlugin;
import co.elastic.logstash.api.PluginConfigSpec;
import edu.soumya.logstash.filter.cache.ConfigurationsCache;
import edu.soumya.logstash.filter.config.Configurations;
import edu.soumya.logstash.filter.constants.Constants;
import edu.soumya.logstash.filter.exceptions.ConfigurationException;
import edu.soumya.logstash.filter.util.JsonParseUtil;
import edu.soumya.logstash.filter.util.PropertiesLoaderUtil;
import edu.soumya.logstash.filter.util.XmlParseUtil;

/**
 * @author Soumya Banerjee
 *
 */
@LogstashPlugin(name = "json_xml_path_filter")
public class JsonXmlPathFilter implements Filter {

	/**
	 * Logger Instance
	 */
	public static final Logger LOGGER = LogManager.getLogger();
	/**
	 * Configuration to set the field of the event from where we will get the
	 * document.<br>
	 * Default value is message.
	 */
	public static final PluginConfigSpec<String> DOC_CONFIG = PluginConfigSpec.stringSetting("document", "message");

	/**
	 * Configuration to set the field of the event from where we will get the type
	 * of the document.<br>
	 * If the type is json/xml it will be processed.<br>
	 * Default value is type.
	 */
	public static final PluginConfigSpec<String> TYPE_CONFIG = PluginConfigSpec.stringSetting("type", "type");

	/**
	 * Configuration setting for the filter containing path of the main properties
	 * file.<br>
	 * This is a required field, should be a valid file path.
	 */
	public static final PluginConfigSpec<String> MAIN_PROPERTIES_PATH_CONFIG = PluginConfigSpec
			.requiredStringSetting("mainProp");

	/**
	 * Configuration setting for the filter, which says maximum how many
	 * configurations files can be stored in cache memory.<br>
	 * If not specified cache size will be infinite, which may cause memory
	 * overflow.
	 */
	public static final PluginConfigSpec<Long> CACHE_SIZE_CONFIG = PluginConfigSpec.numSetting("cacheSize");

	/**
	 * The id of the Logstash Filter
	 */
	private String id;

	/**
	 * Field from Logstash event where the whole xml/json document will be found
	 */
	private String documentField;

	/**
	 * Field from Logstash event where the type of the document will be found
	 */
	private String typeField;

	/**
	 * Full file path to main properties file
	 */
	private Properties mainProperties;

	/**
	 * Instance of ConfigurationsCache
	 */
	private ConfigurationsCache configCache;

	/**
	 * Document Builder Instance for Xml parsing
	 */
	private DocumentBuilder xmlDocBuilder;
	
	/**
	 * XPath Instance for Xml parsing
	 */
	private XPath xPathInstance;

	/**
	 * Constructor
	 */
	public JsonXmlPathFilter(String id, Configuration config, Context context) throws ConfigurationException {
		// constructors should validate configuration options
		this.id = id;
		this.documentField = config.get(DOC_CONFIG);
		this.typeField = config.get(TYPE_CONFIG);
		this.mainProperties = PropertiesLoaderUtil.getPropertiesFromFile(config.get(MAIN_PROPERTIES_PATH_CONFIG));
		this.configCache = new ConfigurationsCache(config.get(CACHE_SIZE_CONFIG));
		this.xmlDocBuilder = XmlParseUtil.createDocBuilderInstance();
		this.xPathInstance = XmlParseUtil.createXPathInstance();
		showFilterPluginInfo(config);
	}
	
	/**
	 * Log the filter info
	 * 
	 * @param config
	 */
	private void showFilterPluginInfo(Configuration config) {
		StringBuilder filterInfo = new StringBuilder();
		
		filterInfo.append("Filter Info: ");
		filterInfo.append("[");
		filterInfo.append(" documentField: ").append(this.documentField).append(",");
		filterInfo.append(" typeField: ").append(this.typeField).append(",");
		filterInfo.append(" mainProp: ").append(config.get(MAIN_PROPERTIES_PATH_CONFIG)).append(",");
		filterInfo.append(" cacheSize: ").append(config.get(CACHE_SIZE_CONFIG)).append(",");
		filterInfo.append("]");
		
		LOGGER.info(filterInfo.toString());
	}


	/* (non-Javadoc)
	 * @see co.elastic.logstash.api.Filter#filter(java.util.Collection, co.elastic.logstash.api.FilterMatchListener)
	 */
	@Override
	public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
		for (Iterator<Event> eventIterator = events.iterator(); eventIterator.hasNext();) {
			Event event = eventIterator.next();
			/**
			 * Inside the file, whose path is given as the value of mainprop in
			 * configuration for the filter, define the four properties
			 * identifier.attribute.path.xml identifier.attribute.path.json
			 * config.location.xml and config.location.json these properties will contain
			 * path of the identifier attribute in xpath or jsonpath format and folder
			 * locations where configuration files for xml and json will be stored
			 */
			Object docType = event.getField(typeField);
			if(docType instanceof String) {
				String docTypeStr = (String) docType;
				if (StringUtils.equals(docTypeStr, Constants.DOC_TYPE_XML)) {
					processXmlDocument(event, eventIterator);
				} else if (StringUtils.equals(docTypeStr, Constants.DOC_TYPE_JSON)) {
					processJsonDocument(event, eventIterator);
				} // else do nothing
			}
			matchListener.filterMatched(event);
		}
		return events;
	}

	/**
	 * Get identifier value from documentField using the value of
	 * <code>identifier.attribute.path.xml</code><br>
	 * Fetch the <code>Identifier_Value.conf</code> file from folder location given
	 * as value of <code>config.location.xml</code><br>
	 * For all xpath in the conf file extract the value from documentField and set
	 * as field in logstash event
	 *
	 * @param event
	 * @param eventIterator
	 */
	private void processXmlDocument(Event event, Iterator<Event> eventIterator) {
		Object document = event.getField(this.documentField);
		if (document instanceof String) {
			String xmlDocument = (String) document;
			Document domDocument;
			String documentId = null;
			try {
				domDocument = XmlParseUtil.getDocument(this.xmlDocBuilder, xmlDocument);
				documentId = XmlParseUtil.getStringFromXPath(domDocument, this.xPathInstance,
						this.mainProperties.getProperty(Constants.XML_IDENTIFIER_KEY));
			} catch (ConfigurationException configEx) {
				handleConfigurationException(event, configEx);
				return;
			}
			// Add identifier field in the event
			event.setField(Constants.IDENTIFIER_EVENT_FIELD, documentId);
			String documentIdWithExt = StringUtils.join(documentId, Constants.CONFIG_FILE_EXTENSION);
			String configFilePath = Paths
					.get(this.mainProperties.getProperty(Constants.XML_CONFIG_FOLDER_PATH_KEY), documentIdWithExt)
					.toString();
			Configurations config = null;
			try {
				config = this.configCache.getConfigFromFileOrCache(configFilePath);
			} catch (ConfigurationException configEx) {
				handleConfigurationException(event, configEx);
				return;
			}
			// If no configuration file or empty configuration file found for the document,
			// do not send it to any output channel.
			if ((!config.getConfigFilePresentFlag()) || config.isEmptyConfigSet()) {
				LOGGER.info(StringUtils.join("No configuration found for: ", documentId,
						". The document will not be sent to any output channel."));
				eventIterator.remove();
				return;
			}
			Map<String, List<String>> destFieldValuesMap = null;
			try {
				destFieldValuesMap = populateDestinationFieldValueMap(config, domDocument, Constants.DOC_TYPE_XML);
			} catch (ConfigurationException configEx) {
				handleConfigurationException(event, configEx);
				return;
			}
			// Add fields to event
			addFieldsToEventFromMap(destFieldValuesMap, event);
		} // else do nothing
	}

	/**
	 * Get identifier value from documentField using the value of
	 * <code>identifier.attribute.path.json</code><br>
	 * Fetch the <code>Identifier_Value.conf</code> file from folder location given
	 * as value of <code>config.location.json</code><br>
	 * For all jsonpath in the conf file extract the value from documentField and
	 * set as field in logstash event
	 *
	 * @param event
	 * @param eventIterator
	 */
	private void processJsonDocument(Event event, Iterator<Event> eventIterator) {
		Object document = event.getField(this.documentField);
		if (document instanceof String) {
			String jsonDocument = (String) document;
			DocumentContext jsonDocumentContext = JsonParseUtil.getDocumentContext(jsonDocument);
			String documentId = null;
			try {
				documentId = JsonParseUtil.getStringFromJsonPath(jsonDocumentContext,
						this.mainProperties.getProperty(Constants.JSON_IDENTIFIER_KEY));
			} catch (ConfigurationException configEx) {
				handleConfigurationException(event, configEx);
				return;
			}
			// Add identifier field in the event
			event.setField(Constants.IDENTIFIER_EVENT_FIELD, documentId);
			String documentIdWithExt = StringUtils.join(documentId, Constants.CONFIG_FILE_EXTENSION);
			String configFilePath = Paths
					.get(this.mainProperties.getProperty(Constants.JSON_CONFIG_FOLDER_PATH_KEY), documentIdWithExt)
					.toString();
			Configurations config = null;
			try {
				config = this.configCache.getConfigFromFileOrCache(configFilePath);
			} catch (ConfigurationException configEx) {
				handleConfigurationException(event, configEx);
				return;
			}
			// If no configuration file or empty configuration file found for the document,
			// do not send it to any output channel.
			if ((!config.getConfigFilePresentFlag()) || config.isEmptyConfigSet()) {
				LOGGER.info(StringUtils.join("No configuration found for: ", documentId,
						". The document will not be sent to any output channel."));
				eventIterator.remove();
				return;
			}
			Map<String, List<String>> destFieldValuesMap = null;
			try {
				destFieldValuesMap = populateDestinationFieldValueMap(config, jsonDocumentContext,
						Constants.DOC_TYPE_JSON);
			} catch (ConfigurationException configEx) {
				handleConfigurationException(event, configEx);
				return;
			}
			// Add fields to event
			addFieldsToEventFromMap(destFieldValuesMap, event);
		} // else do nothing
	}

	/**
	 * When {@link ConfigurationException} occurs logs the error and tag the event
	 * with failure status
	 * 
	 * @param event
	 * @param configEx
	 */
	private void handleConfigurationException(Event event, ConfigurationException configEx) {
		// If there any Configuration Exception occurs add error tag with the event
		LOGGER.error("Configuration Exception Occurred. Failed to parse the document", configEx);
		event.tag(Constants.DOCUMENT_PARSE_FAILURE_TAG);
	}

	/**
	 * Gets the values from xPath or jsonPath, maps them with the targeted field.
	 * 
	 * @param fieldValuesMap
	 * @param event
	 * @throws ConfigurationException
	 */
	private Map<String, List<String>> populateDestinationFieldValueMap(Configurations config, Object currentDocument,
			String documentType) throws ConfigurationException {
		Map<String, List<String>> destFieldValuesMap = new HashMap<String, List<String>>();
		for (String attributePathSource : config.getAllConfigurationKeys()) {
			String destinationField = config.getValue(attributePathSource);
			if (!destFieldValuesMap.containsKey(destinationField)) {
				destFieldValuesMap.put(destinationField, new ArrayList<>());
			}
			if (StringUtils.equals(documentType, Constants.DOC_TYPE_JSON)) {
				destFieldValuesMap.get(destinationField).add(
						JsonParseUtil.getStringFromJsonPath((DocumentContext) currentDocument, attributePathSource));
			} else if (StringUtils.equals(documentType, Constants.DOC_TYPE_XML)) {
				destFieldValuesMap.get(destinationField).add(XmlParseUtil.getStringFromXPath((Document) currentDocument,
						this.xPathInstance, attributePathSource));
			}
		}
		return destFieldValuesMap;
	}

	/**
	 * Adds fields with the values to event, from <code>fieldValuesMap</code>
	 * parameter
	 * 
	 * @param fieldValuesMap
	 * @param event
	 */
	private void addFieldsToEventFromMap(Map<String, List<String>> fieldValuesMap, Event event) {
		for (String fieldKey : fieldValuesMap.keySet()) {
			List<String> destFieldValues = fieldValuesMap.get(fieldKey);
			if ((destFieldValues == null) || destFieldValues.size() == 0)
				continue;
			if (destFieldValues.size() == 1) {
				event.setField(fieldKey, destFieldValues.get(0));
			} else {
				event.setField(fieldKey, destFieldValues);
			}
		}
	}

	/* (non-Javadoc)
	 * @see co.elastic.logstash.api.Plugin#configSchema()
	 */
	@Override
	public Collection<PluginConfigSpec<?>> configSchema() {
		// should return a list of all configuration options for this plugin
		List<PluginConfigSpec<?>> configList = new LinkedList<PluginConfigSpec<?>>();
		configList.add(DOC_CONFIG);
		configList.add(TYPE_CONFIG);
		configList.add(MAIN_PROPERTIES_PATH_CONFIG);
		configList.add(CACHE_SIZE_CONFIG);
		return Collections.unmodifiableList(configList);
	}

	/* (non-Javadoc)
	 * @see co.elastic.logstash.api.Plugin#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}
}
