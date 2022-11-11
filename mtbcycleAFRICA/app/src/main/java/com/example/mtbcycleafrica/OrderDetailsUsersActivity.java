package com.example.mtbcycleafrica;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class OrderDetailsUsersActivity extends AppCompatActivity {

    private String orderTo,orderId;
    TextView orderIdTv,dateTv,orderStatusTv,sellerNameTv,totalItemsTv,amountTv,addressTv;
    RecyclerView itemsRv;
    private FirebaseAuth firebaseAuth;

    private ArrayList <ModelOrdereditem> ordereditemArrayList;
    private AdapterOrdereditem adapterOrdereditem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_users);
        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        sellerNameTv = findViewById(R.id.sellerNameTv);
        totalItemsTv= findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        itemsRv = findViewById(R.id.itemsRv);
        addressTv = findViewById(R.id.addressTv);
        firebaseAuth = FirebaseAuth.getInstance();
        loadSellerInfo();
        loadOrderDetails();
        loadOrderedItems();

    }

    private void loadOrderedItems() {
        ordereditemArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        ordereditemArrayList.clear();
                        for(DataSnapshot ds: datasnapshot.getChildren()){
                            ModelOrdereditem modelOrdereditem = ds.getValue(ModelOrdereditem.class);
                            ordereditemArrayList.add(modelOrdereditem);
                        }
                        adapterOrdereditem = new AdapterOrdereditem(OrderDetailsUsersActivity.this,ordereditemArrayList);
                        itemsRv.setAdapter(adapterOrdereditem);
                        totalItemsTv.setText(""+datasnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadOrderDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        String orderBy = ""+datasnapshot.child("orderBy").getValue() ;
                        String orderCost = ""+datasnapshot.child("orderCost").getValue() ;
                        String orderId = ""+datasnapshot.child("orderId").getValue() ;
                        String orderStatus = ""+datasnapshot.child("orderStatus").getValue() ;
                        String orderTime = ""+datasnapshot.child("orderTime").getValue() ;
                        String orderTo = ""+datasnapshot.child("orderTo").getValue() ;
                        String delivery = ""+datasnapshot.child("deliveryaddress").getValue() ;

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();
                        if(orderStatus.equals("In Progress")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.blue));
                        }
                        else if(orderStatus.equals("Completed")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.greenn));
                        }
                        else if(orderStatus.equals("Cancelled")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.red));

                        }
                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("KES"+orderCost);
                        dateTv.setText(formatedDate);
                        addressTv.setText(delivery);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadSellerInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        String sellerName = ""+datasnapshot.child("username").getValue() ;
                        sellerNameTv.setText(sellerName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}