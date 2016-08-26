package org.synchrnoss.test.util.cucumber.converters;

import com.google.common.base.Preconditions;

import java.util.Map;

final class KeyValuePair implements Map.Entry<String, String> {

    private String key;
    private String value;


    public KeyValuePair(String equalsSeperatedString){
        Preconditions.checkArgument(equalsSeperatedString.contains("="), "String \""+equalsSeperatedString +"\" does not contain \"=\".");
        try {
            String[] split = equalsSeperatedString.split("=", 2);
            key = split[0];
            value = split[1];
            if (value.startsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
        }catch(Exception e){
            throw new RuntimeException("unable to process "+ equalsSeperatedString, e);
        }
    }

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        throw new UnsupportedOperationException();
    }
}
