package com.example.recorder.entity;

public class TestType {

    private String coreType;//Required, the requested kernel type
    private String refText;//Required,reference text
    
    public TestType(String coreType, String refText) {
        this.coreType = coreType;
        this.refText = refText;
    }
    
    
    public String getCoreType() {
        return coreType;
    }

    public void setCoreType(String coreType) {
        this.coreType = coreType;
    }

    public String getRefText() {
        return refText;
    }

    public void setRefText(String refText) {
        this.refText = refText;
    }
}
