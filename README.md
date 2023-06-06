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

- Apache Tomcat [download](https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.75/bin/apache-tomcat-9.0.75.exe

- MySQL [download](https://dev.mysql.com/get/Downloads/MySQLInstaller/mysql-installer-community-8.0.33.0.msi)

> ### 3.1. Setting Environment Variables

- Set the **MAVEN_HOME** environment variable in system variables to installation directory of Maven. For Example: _YOUR-DOWNLOADED-LOCATION\apache-maven-3.9.2_

- Set the **JAVA_HOME** enviroment variable in system variables to installation directory of JDK. For example: _YOUR-DOWNLOADED-LOCATION\Java\jdk-20_

- After setting up **MAVEN_HOME** and **JAVA_HOME** system variables, add the below paths in the **Path** system variable
	- %MAVEN_HOME%\bin
	- %JAVA_HOME%\bin

> ### 3.2. Tables Creation Queries:

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

To run this Java MVC application, open a terminal at the root location of the cloned project and execute the following commands one after another:
> 1. mvn compile
> 2. mvn install
> 3. mvn package

After executing the above commands, a BookStoreMVC.war file would be generated on the _ROOT-LOCATION-OF-PROJECT/target_ folder. Copy this file to _TOMCAT-INSTALLED-LOCATION\Tomcat 9.0\webapps_ location.

Now to start the tomcat server (default port: 8080), Start the **Monitor Tomcat** app. Now the BookStoreMVC application can be viewed on _http://localhost:8080/BookStoreMVC_.
