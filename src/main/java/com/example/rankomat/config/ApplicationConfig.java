package com.example.rankomat.config;

import org.modelmapper.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper(Set<Converter> converters) {
        ModelMapper modelMapper = new ModelMapper();
        converters.forEach(modelMapper::addConverter);
        return modelMapper;
    }
}
