package com.codamasters.lisho.model;

/**
 * Created by Juan on 15/01/2017.
 */

public class ShoppingList {

    public final static int OWN_LIST = 0;
    public final static int GROUP_LIST = 1;

    private String id;
    private int type;
    private String title;

    public ShoppingList(){

    }

    public ShoppingList(String id, int type, String title){
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
