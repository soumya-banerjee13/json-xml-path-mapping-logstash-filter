package edu.soumya.logstash.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import co.elastic.logstash.api.FilterMatchListener;
import co.elastic.logstash.api.LogstashPlugin;
import co.elastic.logstash.api.PluginConfigSpec;

/**
 * @author SOUMYA BANERJEE
 *
 */
@LogstashPlugin(name = "json_xml_path_filter")
public class JsonXmlPathFilter implements Filter {
	/**
	 * Configuration to set the 
	 * field of the event from where we will get the document.<br>
	 * Default value is message.
	 */
	public static final PluginConfigSpec<String> DOC_CONFIG =
            PluginConfigSpec.stringSetting("document", "message");
	
	/**
	 * Configuration to set the 
	 * field of the event from where we will get the type of the document.<br>
	 * If the type is json/xml it will be processed.<br>
	 * Default value is type.
	 */
	public static final PluginConfigSpec<String> TYPE_CONFIG = 
			PluginConfigSpec.stringSetting("type", "type");
	
	/**
	 * Configuration setting for the filter 
	 * containing path of the main properties file.<br>
	 * This is a required field, should be a valid file path.
	 */
	public static final PluginConfigSpec<String> MAIN_PROPERTIES_PATH_CONFIG =
			PluginConfigSpec.requiredStringSetting("mainprop");
	
	/**
	 * Configuration setting for the filter 
	 * containing path of the main properties file.<br>
	 * This is a required field, should be a valid file path.
	 */
	public static final PluginConfigSpec<Long> CACHE_SIZE_CONFIG =
			PluginConfigSpec.numSetting("cachesize");
	
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
    private String mainPropertiesFilePath;
    
    private Long cacheSize;

    public JsonXmlPathFilter(String id, Configuration config, Context context) {
        // constructors should validate configuration options
        this.id = id;
        this.documentField = config.get(DOC_CONFIG);
        this.typeField = config.get(TYPE_CONFIG);
        this.mainPropertiesFilePath = config.get(MAIN_PROPERTIES_PATH_CONFIG);
        this.cacheSize = config.get(CACHE_SIZE_CONFIG);
    }
    
    @Override
    public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
        for (Event event : events) {
            Object f = event.getField(documentField);
            if (f instanceof String) {
            	/**
            	 * Inside the file, 
            	 * whose path is given as the value of mainprop in configuration for the filter,
            	 * define the four properties
            	 * identifier.attribute.path.xml
            	 * identifier.attribute.path.json
            	 * config.location.xml 
            	 * and config.location.json
            	 * these properties will contain path of the identifier attribute
            	 * in xpath or jsonpath format
            	 * and folder locations
            	 * where configuration files for xml and json will be stored
            	*/
            	if(StringUtils.equals(typeField, "xml")) {
            		//Get identifier value from documentField using the value of identifier.attribute.path.xml
            		//Fetch the <Identifier_Value>.conf file in location
            		//from value of config.location.xml 
            		//For all xpath in the conf file extract the value from 
            		//documentField and set as field in logstash event
            	} else if(StringUtils.equals(typeField, "json")) {
            		//Get identifier value from documentField using the value of identifier.attribute.path.json
            		//Fetch the <Identifier_Value>.conf file in location
            		//from value of config.location.json
            		//For all jsonpath in the conf file extract the value from 
            		//documentField and set as field in logstash event
            	}
            }
        }
        return events;
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
