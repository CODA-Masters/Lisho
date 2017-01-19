package com.codamasters.lisho.model;

/**
 * Created by Juan on 15/01/2017.
 */

public class User {

    private String name;

    public User(){

    }

    public User(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
