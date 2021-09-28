package com.ququ.ofdserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileController implements WebMvcConfigurer {

    @Value("${filepath.local}")
    private String filePathLocal;

    @Value("${filepath.uri}")
    private String filePathUri;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(filePathUri)
                .addResourceLocations("file:" + filePathLocal);
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

}
