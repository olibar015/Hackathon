package com.bingotask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")  // All endpoints
      .allowedOrigins("*")  // Allow all origins (NOT for production!)
      .allowedMethods("*")  // Allow all methods
      .allowedHeaders("*"); // Allow all headers
  }
}
