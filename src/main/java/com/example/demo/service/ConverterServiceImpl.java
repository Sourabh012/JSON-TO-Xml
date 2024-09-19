package com.example.demo.service;

import com.example.demo.exception.ConversionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConverterServiceImpl implements ConverterService {

    private static final Logger logger = LoggerFactory.getLogger(ConverterServiceImpl.class);

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    // Converts JSON to XML with <root> element and without HashMap tag
    @Override
    public String convertJsonToXml(Map<String, Object> jsonData) throws ConversionException {
        try {
            logger.info("Starting JSON to XML conversion.");

            // Wrap the incoming JSON Map in a root element
            String xmlOutput = xmlMapper.writer()
                    .withRootName("root") // Set the root element name
                    .writeValueAsString(jsonData);

            logger.info("JSON to XML conversion successful.");
            return xmlOutput;

        } catch (Exception e) {
            logger.error("Error occurred during JSON to XML conversion: {}", e.getMessage());
            throw new ConversionException("Failed to convert JSON to XML", e);
        }
    }

    // Converts XML to JSON

    @Override
    public ResponseEntity<String> convertXmlToJson(String xml) {
        try {
            // Normalize XML string by removing unnecessary line breaks and spaces
            xml = xml.replaceAll("\\s+", " ").trim();

            // Convert XML to a Map structure
            Map<String, Object> map = xmlMapper.readValue(xml, Map.class);

            // Extract the root element's name using a regular expression or manual parsing
            String rootElementName = xml.substring(xml.indexOf("<") + 1, xml.indexOf(">")).replaceAll("[^a-zA-Z0-9]", "");

            // Wrap the parsed map inside a new map with the root element
            Map<String, Object> rootWrappedMap = Map.of(rootElementName, map);

            // Convert the wrapped map to JSON
            String json = jsonMapper.writeValueAsString(rootWrappedMap);

            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error during conversion: " + e.getMessage());
        }
    }


}
