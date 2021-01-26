package de.format.salzzy.Rechnungsmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class RechnungsmanagerApplication extends SpringBootServletInitializer {

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RechnungsmanagerApplication.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(RechnungsmanagerApplication.class, args);
	}

}
