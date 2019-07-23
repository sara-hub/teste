package org.sid.web;

import org.sid.ProjetBanqueApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

public class WebInitialize extends SpringBootServletInitializer{
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		
		return builder.sources(ProjetBanqueApplication.class);
	}

}
