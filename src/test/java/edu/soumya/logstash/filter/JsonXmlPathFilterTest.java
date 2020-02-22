package edu.soumya.logstash.filter;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.logstash.plugins.ConfigurationImpl;
import org.logstash.plugins.ContextImpl;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import co.elastic.logstash.api.FilterMatchListener;
import edu.soumya.logstash.filter.constants.Constants;
import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class JsonXmlPathFilterTest {
	@Test
	public void testJsonXmlFilterForJsonDocument() {
		try {
			// Create the filter with configurations
			Filter filter = getFilter();

			String messageField = "message";
			String typeField = "type";

			String jsonFilePath = Paths
					.get(this.getClass().getClassLoader().getResource("test-files/filter/json/hwh.json").toURI())
					.toString();
			String jsonContent = getContentFromFile(jsonFilePath);

			// Create the event
			Event event = new org.logstash.Event();
			event.setField(typeField, Constants.DOC_TYPE_JSON);
			event.setField(messageField, jsonContent);

			Set<Event> eventSet = new HashSet<>();
			eventSet.add(event);

			TestMatchListener matchListener = new TestMatchListener();

			// Apply the filter
			Collection<Event> results = filter.filter(eventSet, matchListener);

			// Test the result
			assertEquals(1, results.size());
			assertEquals("hwh", event.getField(Constants.IDENTIFIER_EVENT_FIELD));
			assertEquals("Howrah", event.getField("name"));
			assertEquals("30", event.getField("noOfTrains"));

		} catch (URISyntaxException | ConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJsonXmlFilterForXmlDocument() {
		try {
			// Create the filter with configurations
			Filter filter = getFilter();

			String messageField = "message";
			String typeField = "type";

			String xmlFilePath = Paths
					.get(this.getClass().getClassLoader().getResource("test-files/filter/xml/sdh.xml").toURI())
					.toString();
			String xmlContent = getContentFromFile(xmlFilePath);

			// Create the event
			Event event = new org.logstash.Event();
			event.setField(typeField, Constants.DOC_TYPE_XML);
			event.setField(messageField, xmlContent);

			Set<Event> eventSet = new HashSet<>();
			eventSet.add(event);

			TestMatchListener matchListener = new TestMatchListener();

			// Apply the filter
			Collection<Event> results = filter.filter(eventSet, matchListener);

			// Test the result
			assertEquals(1, results.size());
			assertEquals("sdh", event.getField(Constants.IDENTIFIER_EVENT_FIELD));
			assertEquals("Sealdah", event.getField("name"));
			assertEquals("50", event.getField("trainsToday"));

		} catch (URISyntaxException | ConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJsonXmlMultipathIdFilterForJsonDocument() {
		try {
			// Create the filter with configurations
			Filter filter = getMultipathIdFilter();

			String messageField = "message";
			String typeField = "type";

			String jsonFilePath1 = Paths.get(this.getClass().getClassLoader()
					.getResource("test-files/filter/multipath-id-json/hwh1.json").toURI()).toString();
			String jsonFilePath2 = Paths.get(this.getClass().getClassLoader()
					.getResource("test-files/filter/multipath-id-json/hwh2.json").toURI()).toString();
			String jsonContent1 = getContentFromFile(jsonFilePath1);
			String jsonContent2 = getContentFromFile(jsonFilePath2);

			// Create the events
			Event event1 = new org.logstash.Event();
			event1.setField(typeField, Constants.DOC_TYPE_JSON);
			event1.setField(messageField, jsonContent1);

			Event event2 = new org.logstash.Event();
			event2.setField(typeField, Constants.DOC_TYPE_JSON);
			event2.setField(messageField, jsonContent2);

			Set<Event> eventSet = new HashSet<>();
			eventSet.add(event1);
			eventSet.add(event2);

			TestMatchListener matchListener = new TestMatchListener();

			// Apply the filter
			Collection<Event> results = filter.filter(eventSet, matchListener);

			// Test the result
			assertEquals(2, results.size());

			assertEquals("hwh", event1.getField(Constants.IDENTIFIER_EVENT_FIELD));
			assertEquals("Howrah", event1.getField("name"));
			assertEquals("30", event1.getField("noOfTrains"));

			assertEquals(event1.getField(Constants.IDENTIFIER_EVENT_FIELD),
					event2.getField(Constants.IDENTIFIER_EVENT_FIELD));
			assertEquals(event1.getField("name"), event2.getField("name"));
			assertEquals(event1.getField("noOfTrains"), event2.getField("noOfTrains"));

		} catch (URISyntaxException | ConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJsonXmlMultipathIdFilterForXmlDocument() {
		try {
			// Create the filter with configurations
			Filter filter = getMultipathIdFilter();

			String messageField = "message";
			String typeField = "type";

			String xmlFilePath1 = Paths.get(
					this.getClass().getClassLoader().getResource("test-files/filter/multipath-id-xml/sdh1.xml").toURI())
					.toString();
			String xmlFilePath2 = Paths.get(
					this.getClass().getClassLoader().getResource("test-files/filter/multipath-id-xml/sdh2.xml").toURI())
					.toString();
			String xmlContent1 = getContentFromFile(xmlFilePath1);
			String xmlContent2 = getContentFromFile(xmlFilePath2);

			// Create the events
			Event event1 = new org.logstash.Event();
			event1.setField(typeField, Constants.DOC_TYPE_XML);
			event1.setField(messageField, xmlContent1);

			Event event2 = new org.logstash.Event();
			event2.setField(typeField, Constants.DOC_TYPE_XML);
			event2.setField(messageField, xmlContent2);

			Set<Event> eventSet = new HashSet<>();
			eventSet.add(event1);
			eventSet.add(event2);

			TestMatchListener matchListener = new TestMatchListener();

			// Apply the filter
			Collection<Event> results = filter.filter(eventSet, matchListener);

			// Test the result
			assertEquals(2, results.size());

			assertEquals("sdh", event1.getField(Constants.IDENTIFIER_EVENT_FIELD));
			assertEquals("Sealdah", event1.getField("name"));
			assertEquals("50", event1.getField("trainsToday"));
			assertEquals(event1.getField(Constants.IDENTIFIER_EVENT_FIELD),
					event2.getField(Constants.IDENTIFIER_EVENT_FIELD));
			assertEquals(event1.getField("name"), event2.getField("name"));
			assertEquals(event1.getField("trainsToday"), event2.getField("trainsToday"));

		} catch (URISyntaxException | ConfigurationException e) {
			e.printStackTrace();
		}
	}

	private Filter getFilter() throws ConfigurationException, URISyntaxException {
		String propFilePath = Paths
				.get(this.getClass().getClassLoader().getResource("test-files/filter/mainProp.properties").toURI())
				.toString();

		HashMap<String, Object> configMap = new HashMap<>();
		configMap.put("mainProp", propFilePath);
		configMap.put("cacheSize", 10L);
		Configuration config = new ConfigurationImpl(Collections.unmodifiableMap(configMap));
		Context context = new ContextImpl(null, null);

		return new JsonXmlPathFilter("test-id", config, context);
	}

	private Filter getMultipathIdFilter() throws ConfigurationException, URISyntaxException {
		String propFilePath = Paths.get(
				this.getClass().getClassLoader().getResource("test-files/filter/multipath-id-prop.properties").toURI())
				.toString();

		HashMap<String, Object> configMap = new HashMap<>();
		configMap.put("mainProp", propFilePath);
		configMap.put("cacheSize", 10L);
		configMap.put("multipathId", true);
		Configuration config = new ConfigurationImpl(Collections.unmodifiableMap(configMap));
		Context context = new ContextImpl(null, null);

		return new JsonXmlPathFilter("test-id-multipath", config, context);
	}

	private String getContentFromFile(String filePath) {
		try (Reader reader = new FileReader(filePath)) {
			BufferedReader buffReader = new BufferedReader(reader, 32768);
			StringBuilder fileContent = new StringBuilder();
			String line;
			while ((line = buffReader.readLine()) != null) {
				fileContent.append(line);
			}
			return fileContent.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
}

class TestMatchListener implements FilterMatchListener {

	private AtomicInteger matchCount = new AtomicInteger(0);

	@Override
	public void filterMatched(Event event) {
		matchCount.incrementAndGet();
	}

	public int getMatchCount() {
		return matchCount.get();
	}
}