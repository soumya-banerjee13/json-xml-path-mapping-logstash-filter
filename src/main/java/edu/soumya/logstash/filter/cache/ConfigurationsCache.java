package edu.soumya.logstash.filter.cache;

import java.io.IOException;
import java.util.Deque;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import edu.soumya.logstash.filter.config.Configurations;
import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * Implementation of Least recently used cache to cache most used configurations,<br>
 * to avoid loading configurations from files every time.
 * 
 * @author SOUMYA BANERJEE
 *
 */
public class ConfigurationsCache {
	Deque<String> configurationsQueue;
	Map<String, Configurations> configurationsMap;
	Long capacity = null;

	public ConfigurationsCache() {
		// Making Queue and the Map thread-safe
		this.configurationsQueue = new LinkedBlockingDeque<String>();
		this.configurationsMap = new Hashtable<String, Configurations>();
	}

	public ConfigurationsCache(Long capacity) {
		this();
		this.capacity = capacity;
	}

	public Configurations getConfigFromFile(String configFilePath) throws IOException, ConfigurationException {
		if (!configurationsMap.containsKey(configFilePath)) {
			if (configurationsQueue.size() == capacity) {
				// Remove the element from the tail of the dequeue
				String last = configurationsQueue.removeLast();
				configurationsMap.remove(last);
			}
		} else {
			// If it is already in the map, it is also in queue.
			// In this case remove the element from the queue and place it on head of the
			// dequeue
			// So that it becomes last used element
			configurationsQueue.remove(configFilePath);
			configurationsQueue.push(configFilePath);
			return configurationsMap.get(configFilePath);
		}
		configurationsQueue.add(configFilePath);
		return Configurations.loadConfigFromFile(configFilePath);
	}
}
