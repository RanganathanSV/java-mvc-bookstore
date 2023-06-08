# Adding Prometheus Monitoring (Custom Metrics) to Java Spring MVC Application


## 1. Objective:

The objective of this SOP is to provide step-by-step instructions for adding Prometheus Monitoring (Custom Metrics) to a Java Spring MVC application.


## 2. Technologies used:

- Build Tool: Apache Maven 
- Server: Apache Tomcat 
- Database: MySQL 
- Backend
	- JDBC
	- Spring MVC
	- Servlet
- Frontend
	- JSP
	- Bootstrap
	- CSS


## 3. Pre-requisites:

Before you can run this project, ensure that you have the following prerequisites installed:

- Java Development Kit(JDK) [download](https://download.oracle.com/java/20/latest/jdk-20_windows-x64_bin.exe)

- Apache Maven [download](https://dlcdn.apache.org/maven/maven-3/3.9.2/binaries/apache-maven-3.9.2-bin.zip)

- Apache Tomcat [download](https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.75/bin/apache-tomcat-9.0.75.exe)

- MySQL [download](https://dev.mysql.com/get/Downloads/MySQLInstaller/mysql-installer-community-8.0.33.0.msi)

**NOTE: While installing MySQL, create an user with `username: root` and `password: root`.**


### 3.1. Setting Environment Variables

- Set the **MAVEN_HOME** environment variable in system variables to installation directory of Maven. For Example: _YOUR-EXTRACTED-LOCATION\apache-maven-3.9.2_

- Set the **JAVA_HOME** enviroment variable in system variables to installation directory of JDK. For example: _YOUR-DOWNLOADED-LOCATION\Java\jdk-20_

- After setting up **MAVEN_HOME** and **JAVA_HOME** system variables, add the below paths in the **Path** system variable
	- %MAVEN_HOME%\bin
	- %JAVA_HOME%\bin


### 3.2. Tables Creation Queries:

**Before running the project, create the tables by running this sql commands**
```sql
create database bookstore;

create table bookstore.user
(
id int(50) primary key auto_increment, 
first_name varchar(220),  
last_name varchar(220), 
phone varchar(120), 
email varchar(120), 
password varchar(220),
date_created timestamp default now()
);

create table bookstore.admin
(
	id int(50) primary key auto_increment, 
	user_id int(45),  
	is_admin boolean default(false),
	FOREIGN KEY (user_id) REFERENCES bookstore.user(id)
);

create table bookstore.book
(
	id int(50) primary key auto_increment, 
	title varchar(220), 
	author varchar(120), 
	genre varchar(120), 
	description text, 
	date_created timestamp default now(), 
	copy int(10), 
	price decimal(2)
);

create table bookstore.employee
(
	id int(50) primary key auto_increment, 
	name varchar(120), 
	salary decimal(50), 
	department varchar(120), 
	reg_date timestamp default now()
);

create table bookstore.feedback 
(
	id int(50) primary key auto_increment, 
	name varchar(120), 
	phone varchar(120), 
	email varchar(120), 
	feedback text, 
	date_created timestamp default now()
);

create table bookstore.purchase_detail
(
	id int(50) primary key auto_increment,  
	name varchar(120), 
	phone varchar(120), 
	books text, 
	quantity int(50), 
	total_price decimal(50), 
	date_purchased timestamp default now()
);
```


## 4. Running the Application:

- To run this Java MVC application, open a terminal at the root location of the cloned project and execute the following commands one after another:
> 1. mvn compile
> 2. mvn install
> 3. mvn package

- After executing the above commands, a BookStoreMVC.war file would be generated on the _ROOT-LOCATION-OF-THE-PROJECT/target_ folder. Copy this file to _TOMCAT-INSTALLED-LOCATION\Tomcat 9.0\webapps_ location.

- Now to start the tomcat server (default port: 8080), start the **Monitor Tomcat** app. Now the BookStoreMVC application can be viewed on _http://localhost:8080/BookStoreMVC_.


## 5. Steps Followed to Implement Prometheus Monitoring by Adding Custom Metrics to this Java MVC Application:


### 5.1. Add Prometheus Dependency:

- Update the **pom.xml** file in the root location of the Java Spring MVC application to include the Prometheus dependencies. 
```java
// Add these dependencies inside <dependencies> ... </dependencies>

<dependency>
	<groupId>io.micrometer</groupId>
	<artifactId>micrometer-registry-prometheus</artifactId>
	<version>1.9.2</version>
</dependency>

<dependency>
	<groupId>ch.qos.logback</groupId>
	<artifactId>logback-classic</artifactId>
	<version>1.1.11</version>
</dependency>
```


### 5.2. Add Custom Metrics:

- Create a new file **DynamicTagsCounter.java** inside _/src/main/java/com/bookstore/metrics_ to add and define custom prometheus metrics to the application.
```java
// DynamicTagsCounter.java

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
```


### 5.3. Expose '_/metrics_' Endpoint:

- Create a new file **MetricsController.java** inside _/src/main/java/com/bookstore/controllers_ to expose _/metrics_ route that can be used by prometheus to pull all the metrics of the application.
```java
// MetricsController.java

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
```


### 5.4. Updating Custom Metrics:

#### 5.4.1. Using Request Interceptor

- Create a new file **GlobalRequestInterceptor.java** inside _/src/main/java/com/bookstore/interceptors_ to intercept all the requests that is made to the application and update the custom counter metric.
```java
// GlobalRequestInterceptor.java

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
		// checking for response status (failed / success)
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
```

- Add this interceptor to the **spring-servlet.xml** inside _/src/main/webapp_.
```java
// Add the below lines to add the RequestsCounterInterceptor.java in spring-servlet.xml

<mvc:interceptors>
	<bean id="GlobalRequestInterceptor" class="com.bookstore.interceptors.GlobalRequestInterceptor" />
</mvc:interceptors>
```

#### 5.4.2. Using Exception Handler

- Create a new file **GlobalExceptionHandler.java** inside _/src/main/java/com/bookstore/interceptors_. This exception handler catches any exception thrown from our controllers.
```java
// GlobalExceptionHandler.java

package com.bookstore.interceptors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.bookstore.controllers.MetricsController;

@ControllerAdvice()
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, HttpServletRequest request) {
		// Update the failed responses counter
        MetricsController.failedResponses.increment(request.getRequestURI(), request.getMethod(), "500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
```


### 5.5. Configuring Prometheus:

- Download Prometheus [download](https://github.com/prometheus/prometheus/releases/download/v2.44.0/prometheus-2.44.0.windows-amd64.zip)

- After downloading and extracting the files, open the **prometheus.yml** and add the _/metrics_ route exposed by the application to pull all the metrics of the application.
```yml
# Add the below job inside scrape_configs to pull the application metrics

- job_name: "spring-mvc-bookstore"
    metrics_path: '/BookStoreMVC/metrics'
    static_configs: 
      - targets: ["localhost:3002"]
```

- To start prometheus, open a terminal and navigate to the Prometheus root directory and run the following command:
```bash
./prometheus --config.file=prometheus.yml
```

- To access the Prometheus UI, open the browser and visit _http://localhost:9090_. To visualize the custome created metrics of the Java MVC application, query / search for the custom metric with its name to visualize it.


## 6. Issues Faced / Troubleshooting

During the process of integrating Prometheus with the Java Spring MVC application, the following issues were enountered along with their corresponding solutions:

### 6.1. Incompatible Maven Version

**Issue:** The application failed to build or run due to an incomptible Maven version.

**Solution:** Change the Maven compiler source from 1.7 to 1.8 in _pom.xml_ files.
```java
// Made this change in pom.xml file

  <properties>
  	<project.build.sourceEncoding>UTF-8<s/project.build.sourceEncoding>
	<maven.compiler.source>1.8</maven.compiler.source>  // changed it from 1.7 to 1.8
	<maven.compiler.target>1.8</maven.compiler.target>  // changed it from 1.7 to 1.8
  </properties>
```


### 6.2. Incorrect Database Credentials

**Issue:** The application failed to connect to the database due to incorrect username and password.

**Solution:** Verify the database credentials specified in the _spring-servlet.xml_ file inside _/src/main/webapp_.
```xml
<!-- Updated the username and password for sql connection in spring-servlet.xml file -->

<bean id="ds" class="org.springframework.jdbc.datasource.DriverManagerDataSource">    
	<property name="driverClassName" value="com.mysql.jdbc.Driver"></property>    
	<property name="url" value="jdbc:mysql://localhost:3306/bookstore"></property>    
	<property name="username" value="root"></property>    <!-- updated the correct username in the value field -->
	<property name="password" value="root"></property>    <!-- updated the correct password in the value field -->
</bean>   
```


## 7. Conclusion:

By following the steps outlined in this SOP, Prometheus monitoring will be successfully integrated into the Java Spring MVC application, allowing the collection and visualization of application metrics.