package com.codamasters.lisho.model;

import com.yalantis.beamazingtoday.interfaces.BatModel;

/**
 * Created by Juan on 15/01/2017.
 */

public class ShoppingItem implements BatModel{

    private String name;
    private boolean isChecked;

    public ShoppingItem(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNamme(String name) {
        this.name = name;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public String getText() {
        return getName();
    }

}
