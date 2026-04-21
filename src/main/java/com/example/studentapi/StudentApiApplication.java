package com.example.studentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ENTRY POINT — This is where the app starts.
 *
 * @SpringBootApplication is shorthand for three annotations:
 *
 *   @Configuration       — This class can define Spring beans
 *   @EnableAutoConfiguration — Tell Spring Boot to auto-configure based
 *                              on what's on the classpath (e.g. H2 found →
 *                              configure a DataSource automatically)
 *   @ComponentScan       — Scan this package and sub-packages for
 *                          @Component, @Service, @Repository, @Controller etc.
 *
 * SpringApplication.run() bootstraps the whole application:
 *   1. Creates the Spring IoC container (ApplicationContext)
 *   2. Registers all beans
 *   3. Starts embedded Tomcat
 *   4. App is ready to serve HTTP requests
 */
@SpringBootApplication
public class StudentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentApiApplication.class, args);
    }
}
