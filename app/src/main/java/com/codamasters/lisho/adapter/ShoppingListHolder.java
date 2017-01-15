package com.codamasters.lisho.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codamasters.lisho.R;
import com.codamasters.lisho.model.ShoppingList;

/**
 * Created by Juan on 14/01/2017.
 */

public class ShoppingListHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{


    private ShoppingList shoppingList;
    private Context context;

    private final LinearLayout shoppingListLayout;
    private final ImageView shoppingListIcon;
    private final TextView shoppingListTitle;

    public ShoppingListHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        this.shoppingListLayout = (LinearLayout) itemView.findViewById(R.id.shoppingListLayout);
        this.shoppingListIcon = (ImageView) itemView.findViewById(R.id.shoppingListIcon);
        this.shoppingListTitle = (TextView) itemView.findViewById(R.id.shoppingListTitle);


        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

    }

    public void bindStock(ShoppingList shoppingList){

        this.shoppingList = shoppingList;

        if(shoppingList.getType() == ShoppingList.OWN_LIST) {
            this.shoppingListLayout.setBackground(this.context.getResources().getDrawable(R.drawable.shape_rectangle));
            this.shoppingListIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.user));
        }
        else {
            this.shoppingListLayout.setBackground(this.context.getResources().getDrawable(R.drawable.shape_rectangle_group));
            this.shoppingListIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.group));
        }

        this.shoppingListTitle.setText(shoppingList.getTitle());

    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
