package com.fabiogouw.holder;

public class OperationResponse {
    private String _originalValue;
    private String _value;

    public String getOriginalValue() {
        return _originalValue;
    }
    public void setOriginalValue(String value){

        _originalValue = value;
    }

    public String getValue() {
        return _value;
    }
    public void setValue(String value){

        _value = value;
    }

    public OperationResponse() {

    }

    public OperationResponse(String originalValue, String value) {
        _originalValue = originalValue;
        _value = value;
    }
}