package com.bookstore.controllers;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bookstore.metrics.DynamicTagsCounter;

import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import java.io.IOException;
import java.io.Writer;

@WebServlet("/metrics")
public class MetricsController extends HttpServlet {

    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    
    // Create / Add custom metrics to this registry here
    public static final DynamicTagsCounter totalRequests = new DynamicTagsCounter("bookstore_requests",
            "total requests", registry,
            "handler", "method");
    public static final DynamicTagsCounter successResponses = new DynamicTagsCounter("bookstore_success_responses",
            "total successful responses", registry, "handler", "method", "code");
    public static final DynamicTagsCounter failedResponses = new DynamicTagsCounter("bookstore_failed_responses",
            "total failed responses",
            registry, "handler", "method", "code");

    @SuppressWarnings("resource")
    public void init() {
        new JvmThreadMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);
        new LogbackMetrics().bindTo(registry);
        new ClassLoaderMetrics().bindTo(registry);
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(TextFormat.CONTENT_TYPE_004);

        Writer writer = resp.getWriter();
        try {
            registry.scrape(writer);
            writer.flush();
        } finally {
            writer.close();
        }
    }
}
