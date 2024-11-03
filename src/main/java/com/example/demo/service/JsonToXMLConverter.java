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
        Element root;

        // Determine the root element based on the number of keys
        if (jsonObject.length() == 1) {
            // Use the single key as the root element
            String singleKey = (String) jsonObject.keys().next();  // Corrected here
            root = createElementWithValidation(singleKey, document);
            document.appendChild(root);
            processJsonObject(jsonObject.getJSONObject(singleKey), root, document, isBadgerFish);
        } else {
            // Create a default root element
            root = createElementWithValidation("root", document);
            document.appendChild(root);
            processJsonObject(jsonObject, root, document, isBadgerFish);
        }

        return documentToXmlString(document);
    }

    private boolean detectBadgerFishFormat(JSONObject jsonObject) throws Exception {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (key.startsWith("@") || key.equals("$")) {
                return true;
            }

            if (value instanceof JSONObject && detectBadgerFishFormat((JSONObject) value)) {
                return true;
            }

            if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object arrayItem = jsonArray.get(i);
                    if (arrayItem instanceof JSONObject && detectBadgerFishFormat((JSONObject) arrayItem)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void processJsonObject(JSONObject jsonObject, Element parentElement, Document document, boolean isBadgerFish) throws Exception {
        if (isBadgerFish) {
            processBadgerFishJsonObject(jsonObject, parentElement, document);
        } else {
            processNormalJsonObject(jsonObject, parentElement, document);
        }
    }

    private void processNormalJsonObject(JSONObject jsonObject, Element parentElement, Document document) throws Exception {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONArray) {
                // Directly process the array without wrapping it
                processNormalJsonArray((JSONArray) value, parentElement, document, key);
            } else {
                Element childElement = createElementWithValidation(key, document);
                if (value instanceof JSONObject) {
                    parentElement.appendChild(childElement);
                    processNormalJsonObject((JSONObject) value, childElement, document);
                } else {
                    childElement.appendChild(document.createTextNode(sanitizeValue(value)));
                    parentElement.appendChild(childElement);
                }
            }
        }
    }

    private void processNormalJsonArray(JSONArray jsonArray, Element parentElement, Document document, String key) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object arrayItem = jsonArray.get(i);
            if (arrayItem instanceof JSONObject) {
                Element teamElement = createElementWithValidation(key, document); // Create a team element
                parentElement.appendChild(teamElement);
                processNormalJsonObject((JSONObject) arrayItem, teamElement, document);
            }
        }
    }

    private void processBadgerFishJsonObject(JSONObject jsonObject, Element parentElement, Document document) throws Exception {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (key.startsWith("@")) {
                // Handle attributes
                parentElement.setAttribute(key.substring(1), sanitizeValue(value));
            } else if (key.equals("$")) {
                // Handle value
                parentElement.appendChild(document.createTextNode(sanitizeValue(value)));
            } else {
                Element childElement = createElementWithValidation(key, document);
                parentElement.appendChild(childElement);
                if (value instanceof JSONObject) {
                    processBadgerFishJsonObject((JSONObject) value, childElement, document);
                } else if (value instanceof JSONArray) {
                    processBadgerFishJsonArray((JSONArray) value, childElement, document);
                } else {
                    childElement.appendChild(document.createTextNode(sanitizeValue(value)));
                }
            }
        }
    }

    private void processBadgerFishJsonArray(JSONArray jsonArray, Element parentElement, Document document) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object arrayItem = jsonArray.get(i);
            Element arrayElement = createElementWithValidation("item", document); // or some other naming strategy
            parentElement.appendChild(arrayElement);
            if (arrayItem instanceof JSONObject) {
                processBadgerFishJsonObject((JSONObject) arrayItem, arrayElement, document);
            } else {
                arrayElement.appendChild(document.createTextNode(sanitizeValue(arrayItem)));
            }
        }
    }

    private Element createElementWithValidation(String name, Document document) {
        return document.createElement(name);
    }

    private String sanitizeValue(Object value) {
        return value != null ? value.toString() : "";
    }

    private String documentToXmlString(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        return writer.toString();
    }
}
