package com.fabiogouw.holder;

public class OperationResponse {
    private String _value;

    public String getValue() {
        return _value;
    }
    public void setValue(String value){
        _value = value;
    }

    public OperationResponse() {

    }

    public OperationResponse(String value) {
        _value = value;
    }
}