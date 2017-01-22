package com.codamasters.lisho.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan on 15/01/2017.
 */

public class User {

    private String name;
    private List<String> shoppingLists;

    public User(){
    }

    public User(String name){
        this.name = name;
        this.shoppingLists = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<String> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }
}
