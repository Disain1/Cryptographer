package com.disain.main;

import javafx.beans.property.SimpleStringProperty;

public class KeyStore {
    public SimpleStringProperty name;
    public SimpleStringProperty key;

    public KeyStore(String name, String key) {
        this.name = new SimpleStringProperty(name);
        this.key = new SimpleStringProperty(key);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getKey() {
        return key.get();
    }

    public SimpleStringProperty keyProperty() {
        return key;
    }

    public void setKey(String key) {
        this.key.set(key);
    }
}
