package com.ecom.orderservice.log;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class MDCLoggingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest http = (HttpServletRequest) request;

		try {
			// Required correlation fields
			MDC.put("requestId", UUID.randomUUID().toString());
			MDC.put("correlationId", UUID.randomUUID().toString());

			// User + session info (if available)
			MDC.put("userId", http.getHeader("X-User-Id"));
			MDC.put("sessionId", http.getSession(false) != null ? http.getSession().getId() : null);
			MDC.put("tenantId", http.getHeader("X-Tenant-Id"));

			// Request info
			MDC.put("ipAddress", request.getRemoteAddr());
			MDC.put("method", http.getMethod());
			MDC.put("path", http.getRequestURI());
			MDC.put("query", http.getQueryString());
			MDC.put("userAgent", http.getHeader("User-Agent"));

		

			chain.doFilter(request, response);
		} finally {
			MDC.clear(); // Prevent thread-local leaks
		}
	}
}
