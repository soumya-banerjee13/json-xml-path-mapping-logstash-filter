package edu.soumya.logstash.filter;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * @author Soumya Banerjee
 *
 */
@LogstashPlugin(name = "json_xml_path_filter")
public class JsonXmlPathFilter implements Filter {

	/**
	 * Logger Instance
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(JsonXmlPathFilter.class);
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
			.requiredStringSetting("mainprop");

	/**
	 * Configuration setting for the filter containing path of the main properties
	 * file.<br>
	 * This is a required field, should be a valid file path.
	 */
	public static final PluginConfigSpec<Long> CACHE_SIZE_CONFIG = PluginConfigSpec.numSetting("cachesize");

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
	 * 
	 */
	private ConfigurationsCache configCache;

	public JsonXmlPathFilter(String id, Configuration config, Context context) throws ConfigurationException {
		// constructors should validate configuration options
		this.id = id;
		this.documentField = config.get(DOC_CONFIG);
		this.typeField = config.get(TYPE_CONFIG);
		this.mainProperties = PropertiesLoaderUtil.getPropertiesFromFile(config.get(MAIN_PROPERTIES_PATH_CONFIG));
		this.configCache = new ConfigurationsCache(config.get(CACHE_SIZE_CONFIG));
	}

	@Override
	public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
		for (Event event : events) {
			/**
			 * Inside the file, whose path is given as the value of mainprop in
			 * configuration for the filter, define the four properties
			 * identifier.attribute.path.xml identifier.attribute.path.json
			 * config.location.xml and config.location.json these properties will contain
			 * path of the identifier attribute in xpath or jsonpath format and folder
			 * locations where configuration files for xml and json will be stored
			 */
			if (StringUtils.equals(typeField, "xml")) {
				processXmlDocument(event);
			} else if (StringUtils.equals(typeField, "json")) {
				processJsonDocument(event);
			} // else do nothing
		}
		return events;
	}

	/**
	 * Get identifier value from documentField using the value of
	 * identifier.attribute.path.xml Fetch the <Identifier_Value>.conf file in
	 * location from value of config.location.xml For all xpath in the conf file
	 * extract the value from documentField and set as field in logstash event
	 *
	 * @param event
	 */
	private void processXmlDocument(Event event) {

	}

	/**
	 * Get identifier value from documentField using the value of
	 * identifier.attribute.path.json Fetch the <Identifier_Value>.conf file in
	 * location from value of config.location.json For all jsonpath in the conf file
	 * extract the value from documentField and set as field in logstash event
	 *
	 * @param event
	 */
	private void processJsonDocument(Event event) {
		Object document = event.getField(this.documentField);
		if (document instanceof String) {
			String jsonDocument = (String) document;
			DocumentContext jsonDocumentContext = JsonParseUtil.getDocumentContext(jsonDocument);
			String documentId = JsonParseUtil.getStringFromJsonPath(jsonDocumentContext,
					this.mainProperties.getProperty(Constants.JSON_IDENTIFIER_KEY));
			// Add identifier field in the event
			event.setField(Constants.IDENTIFIER_EVENT_FIELD, documentId);
			String documentIdWithExt = StringUtils.join(documentId, Constants.CONFIG_FILE_EXTENSION);
			String configFilePath = Paths
					.get(this.mainProperties.getProperty(Constants.JSON_CONFIG_FOLDER_PATH_KEY), documentIdWithExt)
					.toString();
			Configurations config = null;
			try {
				config = this.configCache.getConfigFromFile(configFilePath);
			} catch (ConfigurationException e) {
				LOGGER.error("Configuration Exception Occurred. Failed to parse the document", e);
				event.tag(Constants.DOCUMENT_PARSE_FAILURE_TAG);
				return;
			}
			Map<String, List<String>> destFieldValuesMap = new HashMap<String, List<String>>();
			for (String jsonPathSource : config.getAllConfigurationKeys()) {
				String destinationField = config.getValue(jsonPathSource);
				if (!destFieldValuesMap.containsKey(destinationField)) {
					destFieldValuesMap.put(destinationField, new ArrayList<>());
				}
				destFieldValuesMap.get(destinationField)
						.add(JsonParseUtil.getStringFromJsonPath(jsonDocumentContext, destinationField));
			}
			// Add fields to event
			for (String destFieldKey : destFieldValuesMap.keySet()) {
				List<String> destFieldValues = destFieldValuesMap.get(destFieldKey);
				if ((destFieldValues == null) || destFieldValues.size() == 0)
					continue;
				if (destFieldValues.size() == 1) {
					event.setField(destFieldKey, destFieldValues.get(0));
				} else {
					event.setField(destFieldKey, destFieldValues);
				}
			}
		} // else do nothing
	}

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

	@Override
	public String getId() {
		return this.id;
	}
}
