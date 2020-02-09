package edu.soumya.logstash.filter.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Test;

import edu.soumya.logstash.filter.exceptions.ConfigurationException;

/**
 * @author Soumya Banerjee
 *
 */
public class ConfigurationsCacheTest {
	
	/**
	 * Tests if caching is being done using Least Recently used page replacement
	 * policy or not
	 */
	@Test
	public void testCachingStrategy() {
		try {
			String configPath1 = Paths.get(this.getClass().getClassLoader().getResource("test-files/cache/hwh_json.conf").toURI()).toString();
			String configPath2 = Paths.get(this.getClass().getClassLoader().getResource("test-files/cache/hwh_xml.conf").toURI()).toString();
			String configPath3 = Paths.get(this.getClass().getClassLoader().getResource("test-files/cache/sdh_json.conf").toURI()).toString();
			String configPath4 = Paths.get(this.getClass().getClassLoader().getResource("test-files/cache/sdh_xml.conf").toURI()).toString();
			ConfigurationsCache cache = new ConfigurationsCache(2L);
			
			//Cache miss/hit test
			cache.getConfigFromFileOrCache(configPath1);
			cache.getConfigFromFileOrCache(configPath2);
			cache.getConfigFromFileOrCache(configPath1);
			// At this point configPath2 is least recently used so loading configPath3 will
			// cause configPath2 to be removed
			cache.getConfigFromFileOrCache(configPath3);
			
			assertTrue(cache.getCurrentCacheMap().containsKey(configPath1));
			assertFalse(cache.getCurrentCacheMap().containsKey(configPath2));
			
			//Now configPath1 is least recently used
			cache.getConfigFromFileOrCache(configPath4);
			
			assertFalse(cache.getCurrentCacheMap().containsKey(configPath1));
			
			//Now configPath3 is least recently used
			cache.getConfigFromFileOrCache(configPath1);
			
			assertFalse(cache.getCurrentCacheMap().containsKey(configPath3));
			assertTrue(cache.getCurrentCacheMap().containsKey(configPath4));
		} catch (URISyntaxException | ConfigurationException e) {
			e.printStackTrace();
		} 
	}
}
