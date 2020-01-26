package edu.soumya.logstash.filter.cache;

import java.util.Deque;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import edu.soumya.logstash.filter.config.Configurations;
import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * Implementation of Least recently used cache to cache most used configurations<br>
 * and remove least recently used one when a cache miss occurs,<br>
 * to avoid loading configurations from files every time.
 * 
 * @author Soumya Banerjee
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

	/**
	 * @param configFilePath
	 * @return
	 * @throws ConfigurationException
	 */
	public Configurations getConfigFromFile(String configFilePath) throws ConfigurationException {
		if (!configurationsMap.containsKey(configFilePath)) {
			//If capacity is null do nothing.
			//Otherwise check if dequeue size has reached the capacity
			// Remove the element from the tail of the dequeue and from the map
			if (capacity!=null && (configurationsQueue.size() == capacity.intValue())) {
				String last = configurationsQueue.removeLast();
				configurationsMap.remove(last);
			}
		} else {
			// If it is already in the map, it is also in queue.
			// In this case remove the element from the queue and place it on head of the
			// dequeue
			// So that it becomes the last used element
			configurationsQueue.remove(configFilePath);
			configurationsQueue.push(configFilePath);
			return configurationsMap.get(configFilePath);
		}
		configurationsQueue.add(configFilePath);
		return Configurations.loadConfigFromFile(configFilePath);
	}
}
