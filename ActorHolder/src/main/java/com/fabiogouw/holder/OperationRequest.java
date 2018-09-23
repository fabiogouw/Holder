package com.fabiogouw.holder;

public class OperationRequest {
    private String _value;

    public String getValue() {
        return _value;
    }
    public void setValue(String value){
        _value = value;
    }

    public OperationRequest() {

    }

    public OperationRequest(String value) {
        _value = value;
    }
}