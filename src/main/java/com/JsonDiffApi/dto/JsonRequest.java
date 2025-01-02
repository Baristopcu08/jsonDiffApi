package com.JsonDiffApi.dto;

public class JsonRequest {
    private String id;
    private String jsonData;

    // Getter ve Setter'lar
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
}