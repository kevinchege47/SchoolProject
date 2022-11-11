package com.example.mtbcycleafrica;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerOrdersActivity extends AppCompatActivity {
    EditText searchOrderEt;
    ImageButton filterOrderBtn;
    TextView filteredOrdersTv;
    RecyclerView ordersRV;
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_orders);
        ordersRV = findViewById(R.id.ordersRV);
        filteredOrdersTv = findViewById(R.id.filteredOrdersTv);
        filterOrderBtn = findViewById(R.id.filterOrderBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        loadallorders();

        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options = {"All","In Progress","Complete","Cancelled"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SellerOrdersActivity.this);
                builder.setTitle("Filtering Orders:")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                if(which==0){
                                    filteredOrdersTv.setText("Showing all Orders ");
                                    adapterOrderShop.getFilter().filter("");

                                }else{
                                    String optionClicked =options[which];
                                    filteredOrdersTv.setText("Showing"+ optionClicked+"Orders");
                                    adapterOrderShop.getFilter().filter(optionClicked);

                                }

                            }
                        })
                        .show();
            }
        });
    }

    private void loadallorders() {
        orderShopArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        orderShopArrayList.clear();
                        for (DataSnapshot ds:datasnapshot.getChildren()){
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);
                            orderShopArrayList.add(modelOrderShop);
                        }
                        adapterOrderShop = new AdapterOrderShop(SellerOrdersActivity.this,orderShopArrayList);
                        ordersRV.setAdapter(adapterOrderShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}