package com.codamasters.lisho.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codamasters.lisho.model.ShoppingList;

import java.util.List;

/**
 * Created by Juan on 14/01/2017.
 */

public class ShoppingListRecAdapter extends RecyclerView.Adapter<ShoppingListHolder> {

    private final List<ShoppingList> shoppingLists;
    private final List<String> shoppingListsKeys;
    private Context context;
    private int itemResource;


    public ShoppingListRecAdapter(Context context, int itemResource, List<ShoppingList> shoppingLists, List<String> shoppingListsKeys) {

        // 1. Initialize our adapter
        this.shoppingLists = shoppingLists;
        this.context = context;
        this.itemResource = itemResource;
        this.shoppingListsKeys = shoppingListsKeys;
    }

    @Override
    public ShoppingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new ShoppingListHolder(this.context, view, shoppingLists, this);
    }

    @Override
    public void onBindViewHolder(ShoppingListHolder holder, int position) {
        ShoppingList shoppingList = this.shoppingLists.get(position);
        holder.bindStock(shoppingList, this.shoppingListsKeys.get(position));
    }

    @Override
    public int getItemCount() {
        return this.shoppingLists.size();
    }
}
