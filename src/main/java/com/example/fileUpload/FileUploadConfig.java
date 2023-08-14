package com.example.fileUpload;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class FileUploadConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
