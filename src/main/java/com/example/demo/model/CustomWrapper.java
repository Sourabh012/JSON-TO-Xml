package com.example.demo.model;


import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Map;

@XmlRootElement(name = "root")
public class CustomWrapper {

    private Map<String, Object> map;

    public CustomWrapper() {}

    public CustomWrapper(Map<String, Object> map) {
        this.map = map;
    }

    @XmlAnyElement(lax = true)
    @XmlElement
    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}

