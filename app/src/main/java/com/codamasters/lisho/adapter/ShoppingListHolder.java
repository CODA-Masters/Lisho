package com.codamasters.lisho.adapter;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codamasters.lisho.R;
import com.codamasters.lisho.model.ShoppingList;
import com.codamasters.lisho.ui.ShoppingDetailListActivity;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

/**
 * Created by Juan on 14/01/2017.
 */

public class ShoppingListHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{


    private ShoppingList shoppingList;
    private Context context;

    private final LinearLayout shoppingListLayout;
    private final ImageView shoppingListIcon;
    private final TextView shoppingListTitle;
    private final BoomMenuButton bmb;

    public ShoppingListHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        this.shoppingListLayout = (LinearLayout) itemView.findViewById(R.id.shoppingListLayout);
        this.shoppingListIcon = (ImageView) itemView.findViewById(R.id.shoppingListIcon);
        this.shoppingListTitle = (TextView) itemView.findViewById(R.id.shoppingListTitle);
        this.bmb = (BoomMenuButton) itemView.findViewById(R.id.bmb);

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


        this.initHamButton();

    }

    @Override
    public void onClick(View view) {
        ((Activity) this.context).getFragmentManager().beginTransaction();

        Fragment newFragment = new ShoppingDetailListActivity();
        FragmentTransaction transaction = ((Activity) this.context).getFragmentManager().beginTransaction();

        transaction.replace(R.id.content_main, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public boolean onLongClick(View view) {
        bmb.boom();
        return true;
    }

    private void initHamButton(){
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextInsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_1);
        bmb.setInList(true);

        bmb.clearBuilders();

        TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.butterfly)
                .normalText("Share");
        bmb.addBuilder(builder);

        builder = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.cat)
                .normalText("Edit");
        bmb.addBuilder(builder);

        builder = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.dolphin)
                .normalText("Delete");
        bmb.addBuilder(builder);

    }
}
