package com.example.demo.controller;

import com.example.demo.service.XMLToJsonConverter;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/convert")
public class XMLConversionController {

    private final XMLToJsonConverter converter;

    public XMLConversionController(XMLToJsonConverter converter) {
        this.converter = converter;
    }

    @PostMapping(value = "/xml-to-json", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<String> convertXmlToJson(@RequestBody String xmlInput) {
        try {
            String jsonOutput = converter.convertXmlToJson(xmlInput);
            return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error converting XML to JSON: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

