package com.example.demo.controller;

import com.example.demo.service.ConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConverterController {

    private final ConverterService converterService;

    @Autowired
    public ConverterController(ConverterService converterService) {
        this.converterService = converterService;

    }

    @PostMapping(value = "/convert/json-to-xml", consumes = "application/json", produces = "application/xml")
    public String convertJsonToXml(@RequestBody Map<String, Object> jsonData) {
        try {
            // Log the input JSON
            System.out.println("Input JSON: " + jsonData);
            // Pass the valid JSON data to the service for conversion
            return converterService.convertJsonToXml(jsonData);
        } catch (Exception e) {
            // Handle errors
            throw new RuntimeException(e);
        }
    }

//    @PostMapping(value = "/convert/xml-to-json", consumes = "application/xml", produces = "application/json")
//    public ResponseEntity<String> convertXmlToJson(@RequestBody String xml) {
//        try {
//            // Log the input XML
//            System.out.println("Input XML: " + xml);
//            String json = xmlToJsonService.convertXmlToJson(xml);
//            if (json != null && !json.isEmpty()) {
//                return ResponseEntity.ok(json);
//            } else {
//                return ResponseEntity.badRequest().body("Error converting XML to JSON");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Internal Server Error");
//        }
//    }



}
