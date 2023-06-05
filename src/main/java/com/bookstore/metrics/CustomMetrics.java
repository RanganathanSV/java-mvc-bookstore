package com.bookstore.metrics;

import com.bookstore.controllers.MetricsController;

import io.micrometer.core.instrument.Counter;

public class CustomMetrics {
    private static Counter totalRequests = Counter.builder("total_requests_custom_metric")
            .register(MetricsController.registry);

    public static void incrementTotalRequests() {
        totalRequests.increment();
    }

    public static double getTotalReuqests() {
        return totalRequests.count();
    }
}
