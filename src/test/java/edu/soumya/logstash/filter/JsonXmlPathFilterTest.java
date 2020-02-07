package edu.soumya.logstash.filter;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.FilterMatchListener;

/**
 * @author Soumya Banerjee
 *
 */
public class JsonXmlPathFilterTest {
	@Test
    public void testJsonXmlFilter() {
		//Configuration config = new ConfigurationImpl(Collections.singletonMap("source", sourceField));
        //Context context = new ContextImpl(null,null);
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