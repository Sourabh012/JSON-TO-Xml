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

    public String convertJsonToXml(String jsonInput) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonInput);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        boolean isBadgerFish = detectBadgerFishFormat(jsonObject);

        Element root = isBadgerFish
                ? createElementFromBadgerFishJsonObject(jsonObject, document)
                : createElementFromNormalJsonObject(jsonObject, document);

        document.appendChild(root);

        return documentToXmlString(document);
    }

    private boolean detectBadgerFishFormat(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.startsWith("@") || key.equals("$")) {
                return true;
            }
        }
        return false;
    }

    private Element createElementFromBadgerFishJsonObject(JSONObject jsonObject, Document document) throws Exception {
        Iterator<String> keys = jsonObject.keys();
        Element rootElement = null;

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (rootElement == null) {
                rootElement = createElementWithValidation(key, document);
            }

            if (value instanceof JSONObject) {
                processBadgerFishJsonObject((JSONObject) value, rootElement, document);
            } else if (value instanceof JSONArray) {
                processBadgerFishJsonArray((JSONArray) value, key, rootElement, document);
            }
        }

        return rootElement;
    }

    private void processBadgerFishJsonObject(JSONObject jsonObject, Element parentElement, Document document) throws Exception {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (key.startsWith("@")) {
                parentElement.setAttribute(key.substring(1), sanitizeValue(value.toString()));
            } else if (key.equals("$")) {
                parentElement.appendChild(document.createTextNode(sanitizeValue(value.toString())));
            } else if (value instanceof JSONObject) {
                Element childElement = createElementWithValidation(key, document);
                parentElement.appendChild(childElement);
                processBadgerFishJsonObject((JSONObject) value, childElement, document);
            } else if (value instanceof JSONArray) {
                processBadgerFishJsonArray((JSONArray) value, key, parentElement, document);
            }
        }
    }

    private void processBadgerFishJsonArray(JSONArray jsonArray, String key, Element parentElement, Document document) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object arrayItem = jsonArray.get(i);

            if (arrayItem instanceof JSONObject) {
                Element arrayElement = createElementWithValidation(key, document);
                parentElement.appendChild(arrayElement);
                processBadgerFishJsonObject((JSONObject) arrayItem, arrayElement, document);
            }
        }
    }

    private Element createElementFromNormalJsonObject(JSONObject jsonObject, Document document) throws Exception {
        Iterator<String> keys = jsonObject.keys();
        Element rootElement = null;

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (rootElement == null) {
                rootElement = createElementWithValidation(key, document);
            }

            if (value instanceof JSONObject) {
                processNormalJsonObject((JSONObject) value, rootElement, document);
            } else if (value instanceof JSONArray) {
                processNormalJsonArray((JSONArray) value, key, rootElement, document);
            } else {
                Element childElement = createElementWithValidation(key, document);
                childElement.appendChild(document.createTextNode(sanitizeValue(value.toString())));
                rootElement.appendChild(childElement);
            }
        }

        return rootElement;
    }

    private void processNormalJsonObject(JSONObject jsonObject, Element parentElement, Document document) throws Exception {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                Element childElement = createElementWithValidation(key, document);
                parentElement.appendChild(childElement);
                processNormalJsonObject((JSONObject) value, childElement, document);
            } else if (value instanceof JSONArray) {
                processNormalJsonArray((JSONArray) value, key, parentElement, document);
            } else {
                Element childElement = createElementWithValidation(key, document);
                childElement.appendChild(document.createTextNode(sanitizeValue(value.toString())));
                parentElement.appendChild(childElement);
            }
        }
    }

    private void processNormalJsonArray(JSONArray jsonArray, String key, Element parentElement, Document document) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object arrayItem = jsonArray.get(i);

            if (arrayItem instanceof JSONObject) {
                Element arrayElement = createElementWithValidation(key, document);
                parentElement.appendChild(arrayElement);
                processNormalJsonObject((JSONObject) arrayItem, arrayElement, document);
            } else {
                Element arrayElement = createElementWithValidation(key, document);
                arrayElement.appendChild(document.createTextNode(sanitizeValue(arrayItem.toString())));
                parentElement.appendChild(arrayElement);
            }
        }
    }

    private Element createElementWithValidation(String key, Document document) throws Exception {
        // Sanitize the key to ensure it is a valid XML element name
        String sanitizedKey = key.replaceAll("[^a-zA-Z0-9_:.]", "_");
        return document.createElement(sanitizedKey);
    }

    private String sanitizeValue(String value) {
        // Replace invalid XML characters
        return value.replaceAll("[^\\x20-\\x7E]", "_");
    }

    private String documentToXmlString(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.toString();
    }
}
