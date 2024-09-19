package com.example.demo.service;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.badgerfish.BadgerFishDOMDocumentSerializer;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import org.xml.sax.SAXException;

@Service
public class XMLToJsonConverter {

    public String convertXmlToJson(String xmlInput) throws ParserConfigurationException, SAXException, IOException {
        // Step 1: Parse the input XML into a DOM Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlInput)));

        // Step 2: Create an OutputStream to hold the JSON result
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Step 3: Use BadgerFishDOMDocumentSerializer to serialize the Document's root element
        BadgerFishDOMDocumentSerializer serializer = new BadgerFishDOMDocumentSerializer(outputStream);
        serializer.serialize(document.getDocumentElement());  // Get root element from Document

        // Step 4: Return the JSON string from the OutputStream
        return outputStream.toString();
    }
}
