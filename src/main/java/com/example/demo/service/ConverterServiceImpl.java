package com.example.demo.service;

import com.example.demo.exception.ConversionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConverterServiceImpl implements ConverterService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public ResponseEntity<String> convertJsonToXml(Object jsonData) throws ConversionException {
        try {
            // Configure the XmlMapper to write the XML declaration
            xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
            // Convert the JSON data (Object) into a string
            String xmlOutput = serializeToXml(jsonData);

            return ResponseEntity.ok(xmlOutput);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ConversionException("Failed to convert JSON to XML", e);
        }
    }

    private String serializeToXml(Object data) throws Exception {
        StringBuilder xmlOutput = new StringBuilder();

        if (data instanceof Map) {
            Map<String, Object> jsonMap = (Map<String, Object>) data;
            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    // Handle lists or arrays directly without wrapping in extra tags
                    for (Object item : (Iterable<?>) entry.getValue()) {
                        xmlOutput.append("<").append(entry.getKey()).append(">")
                                .append(serializeToXml(item))  // Recursive call for nested maps
                                .append("</").append(entry.getKey()).append(">");
                    }
                } else {
                    xmlOutput.append("<").append(entry.getKey()).append(">")
                            .append(serializeToXml(entry.getValue()))  // Recursive call for nested maps
                            .append("</").append(entry.getKey()).append(">");
                }
            }

        } else if (data instanceof Iterable) {
            // Handle list/array items directly without wrapping in extra tags
            for (Object item : (Iterable<?>) data) {
                xmlOutput.append(serializeToXml(item));
            }
        } else {
            // For simple types, just append the value
            xmlOutput.append(data.toString());
        }

        return xmlOutput.toString();
    }

    @Override
    public ResponseEntity<String> convertXmlToJson(String xml) {
        try {
            // Normalize XML string by removing unnecessary line breaks and spaces
            xml = xml.replaceAll("\\s+", " ").trim();

            // Convert XML to a Map structure
            Map<String, Object> map = xmlMapper.readValue(xml, Map.class);

            // Dynamically determine the root element
            String rootElementName = xml.substring(xml.indexOf("<") + 1, xml.indexOf(">")).replaceAll("[^a-zA-Z0-9]", "");

            // Wrap the parsed map inside a new map with the root element
            Map<String, Object> rootWrappedMap = Map.of(rootElementName, map);

            // Convert the wrapped map to JSON
            String json = objectMapper.writeValueAsString(rootWrappedMap);

            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error during conversion: " + e.getMessage());
        }
    }

    private String getRootElementName(String xml) {
        // Extract the root element name dynamically from the XML string
        int startIndex = xml.indexOf("<") + 1;
        int endIndex = xml.indexOf(" ", startIndex); // To handle attributes in the tag
        if (endIndex == -1) {
            endIndex = xml.indexOf(">", startIndex);
        }
        return xml.substring(startIndex, endIndex).replaceAll("[^a-zA-Z0-9]", "");
    }

}
