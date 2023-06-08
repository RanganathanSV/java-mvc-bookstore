package com.bookstore.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bookstore.controllers.MetricsController;

public class GlobalRequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object Handler) {
        MetricsController.totalRequests.increment(request.getRequestURI(), request.getMethod());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object Handler,
            ModelAndView modelAndView) {
        int responseStatus = response.getStatus();
        if (responseStatus >= 200 && responseStatus < 300) {
            MetricsController.successResponses.increment(request.getRequestURI(), request.getMethod(),
                    String.valueOf(responseStatus));
        } else {
            MetricsController.failedResponses.increment(request.getRequestURI(), request.getMethod(),
                    String.valueOf(responseStatus));
        }
    }
}
