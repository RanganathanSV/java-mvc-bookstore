package com.bookstore.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

public class DynamicTagsCounter {
    private String name;
    private String[] tagNames;
    private String description;
    private MeterRegistry registry;
    private Map<String, Counter> counters = new HashMap<>();

    public DynamicTagsCounter(String name, String description, MeterRegistry registry, String... tags) {
        this.name = name;
        this.tagNames = tags;
        this.registry = registry;
        this.description = description;
    }

    public void increment(String... tagValues) {
        String valuesString = Arrays.toString(tagValues);
        if (tagValues.length != tagNames.length) {
            throw new IllegalArgumentException("Counter tags mismatch! Expected args are " + Arrays.toString(tagNames));
        }
        Counter counter = counters.get(valuesString);
        if (counter == null) {
            List<Tag> tags = new ArrayList<>(tagNames.length);
            for (int i = 0; i < tagNames.length; i++) {
                tags.add(new ImmutableTag(tagNames[i], tagValues[i]));
            }
            counter = Counter.builder(name).description(description).tags(tags).register(registry);
            counters.put(valuesString, counter);
        }
        counter.increment();
    }
}
