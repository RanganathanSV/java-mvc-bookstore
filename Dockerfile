# For Java Spring MVC application
FROM tomcat:latest as bookstore

# Copy the .war file generated by Maven into the Tomcat webapps directory
COPY target/BookStoreMVC.war /usr/local/tomcat/webapps

# Exposing default tomcat port
EXPOSE 8080

# Start tomcat when the container starts
CMD ["catalina.sh", "run"]

# --------------------------------------------------------------------------------- #

# For MySQL
FROM mysql:latest as mydb

# Copy the SQL script to create tables into the container
COPY create-tables.sql /docker-entrypoint-initdb.d/

# Exposing default MySQL port
EXPOSE 3306