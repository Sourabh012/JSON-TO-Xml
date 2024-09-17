package com.example.demo.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ConverterService {

    ResponseEntity<String> convertJsonToXml(Object jsonData);

    ResponseEntity<String> convertXmlToJson(String xml);
}


