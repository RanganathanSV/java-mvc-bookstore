package com.bookstore.metrics;

import java.io.IOException;
import java.io.Writer;

import com.bookstore.controllers.MetricsController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;

public class CustomMetrics {
    private static Counter totalRequests = Counter.builder("total_requests_custom_metric")
            .register(MetricsController.registry);

    public static void incrementTotalRequests() {
        totalRequests.increment();
    }

    public static double getTotalReuqests() {
        return totalRequests.count();
    }

    public static void writeCustomCounters(Writer writer) throws IOException {
        try {
            writeCustomCounter(writer, totalRequests);
        } finally {
        }
    }

    public static void writeCustomCounter(Writer writer, Counter counter) throws IOException {
        Meter.Id counterId = counter.getId();
        String counterType = counterId.getType().toString();
        String counterName = counterId.getName().toString();

        try {
            writer.write("# TYPE " + counterName + " " + counterType + "\n");
            writer.write(counterName + " " + counter.count() + "\n");
        } finally {
        }
    }
}
