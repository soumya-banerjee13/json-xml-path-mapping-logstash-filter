package edu.soumya.logstash.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import co.elastic.logstash.api.FilterMatchListener;
import co.elastic.logstash.api.LogstashPlugin;
import co.elastic.logstash.api.PluginConfigSpec;

@LogstashPlugin(name = "json_xml_path_filter")
public class JsonXmlPathFilter implements Filter {
	/**
	 * Field of the event from where we will get the document
	 */
	public static final PluginConfigSpec<String> DOC_CONFIG =
            PluginConfigSpec.stringSetting("document", "message");
	
	/**
	 * Field of the event from where we will get the type of the document.
	 * If the type is json/xml it will be processed
	 */
	public static final PluginConfigSpec<String> TYPE_CONFIG = 
			PluginConfigSpec.stringSetting("type", "type");
	
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

    public JsonXmlPathFilter(String id, Configuration config, Context context) {
        // constructors should validate configuration options
        this.id = id;
        this.documentField = config.get(DOC_CONFIG);
        this.typeField = config.get(TYPE_CONFIG);
    }
    
    @Override
    public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
        for (Event event : events) {
            Object f = event.getField(documentField);
            if (f instanceof String) {
            	/**
            	 * Define main-config.properties file with four properties
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
    	return Collections.unmodifiableList(Arrays.asList(new PluginConfigSpec[] {DOC_CONFIG,TYPE_CONFIG}));
    }
    
    @Override
    public String getId() {
        return this.id;
    }
}
