package com.codamasters.lisho.adapter;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codamasters.lisho.R;
import com.codamasters.lisho.model.ShoppingList;
import com.codamasters.lisho.ui.ShoppingDetailListFragment;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.List;

/**
 * Created by Juan on 14/01/2017.
 */

public class ShoppingListHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private final static String PREF_TAG = "Lisho";

    private List<ShoppingList> shoppingLists;
    private ShoppingList shoppingList;
    private String shoppingListKey;
    private Context context;
    private ShoppingListRecAdapter shoppingListRecAdapter;

    private final LinearLayout shoppingListLayout;
    private final ImageView shoppingListIcon;
    private final TextView shoppingListTitle;
    private final BoomMenuButton bmb;

    public ShoppingListHolder(Context context, View itemView, List<ShoppingList> shoppingLists, ShoppingListRecAdapter shoppingListRecAdapter) {
        super(itemView);


        this.context = context;
        this.shoppingLists = shoppingLists;
        this.shoppingListRecAdapter = shoppingListRecAdapter;

        this.shoppingListLayout = (LinearLayout) itemView.findViewById(R.id.shoppingListLayout);
        this.shoppingListIcon = (ImageView) itemView.findViewById(R.id.shoppingListIcon);
        this.shoppingListTitle = (TextView) itemView.findViewById(R.id.shoppingListTitle);
        this.bmb = (BoomMenuButton) itemView.findViewById(R.id.bmb);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

    }

    public void bindStock(ShoppingList shoppingList, String shoppingListKey){

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
        this.shoppingListKey = shoppingListKey;


        this.initHamButton();

    }

    @Override
    public void onClick(View view) {
        ((Activity) this.context).getFragmentManager().beginTransaction();

        Fragment newFragment = new ShoppingDetailListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", shoppingList.getType());
        bundle.putString("key", shoppingListKey);
        newFragment.setArguments(bundle);
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
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_2);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_4_2);
        bmb.setInList(true);

        bmb.clearBuilders();

        TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.butterfly)
                .normalText("Share")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {

                    }
                });
        bmb.addBuilder(builder);

        builder = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.cat)
                .normalText("Users")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {

                    }
                });
        bmb.addBuilder(builder);

        builder = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.cat)
                .normalText("Edit")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        showUpdateNameDialog();
                    }
                });
        bmb.addBuilder(builder);

        builder = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.dolphin)
                .normalText("Delete")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        removeAt(getAdapterPosition());
                        removeFromOwnUser(shoppingList, shoppingListKey);
                    }
                });
        bmb.addBuilder(builder);

    }

    private void showUpdateNameDialog() {
        new LovelyTextInputDialog(context, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.md_deep_orange_800)
                .setTitle(R.string.text_input_title)
                .setMessage(R.string.text_input_title_change_name)
                .setIcon(R.drawable.bat)
                .setInputFilter(R.string.text_input_error_message, new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return text.matches("\\w+");
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        updateName(shoppingListKey, text);
                    }
                })
                .show();
    }


    public void updateName(String key, final String name){
        FirebaseDatabase.getInstance().getReference().child("lists").child(key).child("title").setValue(name, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                // TODO : forced change, not caused by Firebase onChanged
                if(databaseError==null)
                    shoppingListTitle.setText(name);
                else
                    Toast.makeText(context, "Error updating name. Try again.", Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void removeFromOwnUser(ShoppingList shoppingList, String key){

        // Eliminamos la referencia al usuario
        SharedPreferences prefs = context.getSharedPreferences(PREF_TAG, context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        userId = "juan@gmail_com";


        FirebaseDatabase.getInstance().getReference().child("user").child(userId).child(key).removeValue();

        // TODO : ELIMINAR LISTAS (deberia omitirse para estudiar información)
        // En caso de que sea el ultimo usuario que tenga acceso a la lista se eliminará completamente de la base de datos
        if(shoppingList.getUsers().size() == 1) {
            FirebaseDatabase.getInstance().getReference().child("lists").child(key).removeValue();
            FirebaseDatabase.getInstance().getReference().child("detailLists").child(key).removeValue();
        }
    }


    public void removeAt(int position) {
        shoppingLists.remove(position);
        shoppingListRecAdapter.notifyItemRemoved(position);
        shoppingListRecAdapter.notifyItemRangeChanged(position, shoppingLists.size());
    }

}
