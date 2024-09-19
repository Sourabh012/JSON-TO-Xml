package com.example.demo.service;

import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;

import java.util.Map;

public interface ConverterService {

    String convertJsonToXml(Map<String, Object> jsonData);

    ResponseEntity<String> convertXmlToJson(String xml);
}


