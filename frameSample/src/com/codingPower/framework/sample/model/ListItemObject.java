package com.codingPower.framework.sample.model;

import java.io.Serializable;

public class ListItemObject implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4479202650960368231L;

    private String name;

    public ListItemObject(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
