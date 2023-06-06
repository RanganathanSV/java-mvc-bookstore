package com.bookstore.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.bookstore.metrics.CustomMetrics;

public class RequestsCounterInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object Handler) {
        CustomMetrics.incrementTotalRequests();
        return true;
    }
}
