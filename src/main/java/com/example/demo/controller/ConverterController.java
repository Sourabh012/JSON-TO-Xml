package com.example.demo.controller;

import com.example.demo.service.ConverterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConverterController {

    private final ConverterService converterService;

    public ConverterController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @PostMapping(value = "/convert/json-to-xml", consumes = "application/json", produces = "application/xml")
    public ResponseEntity<String> convertJsonToXml(@RequestBody Object jsonData) {
        return converterService.convertJsonToXml(jsonData);
    }

    @PostMapping(value = "/convert/xml-to-json", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<String> convertXmlToJson(@RequestBody String xml) {
        return converterService.convertXmlToJson(xml);
    }
}
