package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashMap;
import java.util.Map;

@JsonRootName(value = "root")
public class RootWrapper {

    private Map<String, Object> root = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getRoot() {
        return root;
    }

    @JsonAnySetter
    public void setRoot(String key, Object value) {
        this.root.put(key, value);
    }
}
