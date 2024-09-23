package com.example.demo.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Iterator;

@Service
public class JsonToXMLConverter {

    public String convertBadgerFishJsonToXml(String jsonInput) throws Exception {
        // Step 1: Parse the JSON input into a JSONObject
        JSONObject jsonObject = new JSONObject(jsonInput);

        // Step 2: Create an empty DOM Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // Step 3: Recursively build the XML from the JSON
        Element root = createElementFromJsonObject(jsonObject, document);
        document.appendChild(root);

        // Step 4: Transform the DOM Document to an XML String
        return documentToXmlString(document);
    }

    private Element createElementFromJsonObject(JSONObject jsonObject, Document document) throws Exception {
        Iterator<String> keys = jsonObject.keys();
        Element rootElement = null;

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            // Handle the root element
            if (rootElement == null) {
                rootElement = document.createElement(key);
            }

            if (value instanceof JSONObject) {
                processJsonObject((JSONObject) value, rootElement, document);
            } else if (value instanceof JSONArray) {
                processJsonArray((JSONArray) value, key, rootElement, document);
            }
        }

        return rootElement;
    }

    private void processJsonObject(JSONObject jsonObject, Element parentElement, Document document) throws Exception {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (key.startsWith("@")) {
                // Attribute handling: key is the attribute name (without '@')
                parentElement.setAttribute(key.substring(1), value.toString());
            } else if (key.equals("$")) {
                // Text value handling
                parentElement.appendChild(document.createTextNode(value.toString()));
            } else if (value instanceof JSONObject) {
                // Nested object, create a new element
                Element childElement = document.createElement(key);
                parentElement.appendChild(childElement);
                processJsonObject((JSONObject) value, childElement, document);
            } else if (value instanceof JSONArray) {
                // Array handling
                processJsonArray((JSONArray) value, key, parentElement, document);
            } else {
                // Simple value, create a new element with text content
                Element childElement = document.createElement(key);
                childElement.appendChild(document.createTextNode(value.toString()));
                parentElement.appendChild(childElement);
            }
        }
    }

    private void processJsonArray(JSONArray jsonArray, String key, Element parentElement, Document document) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object arrayItem = jsonArray.get(i);

            if (arrayItem instanceof JSONObject) {
                Element arrayElement = document.createElement(key);
                parentElement.appendChild(arrayElement);
                processJsonObject((JSONObject) arrayItem, arrayElement, document);
            }
        }
    }

    private String documentToXmlString(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Pretty print the XML output
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.toString();
    }
}
