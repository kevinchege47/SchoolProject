package com.example.mtbcycleafrica;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderDetailsSellerActivity extends AppCompatActivity {
    String orderId,orderBy,deliveryaddress,email,phone;
    TextView orderIdTv,dateTv,orderStatusTv,emailTv,phoneTv,totalItemsTv,amountTv,deliveryTv;
    RecyclerView itemsRv;
    ImageButton editButton;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrdereditem> ordereditemArrayList;
    private AdapterOrdereditem adapterOrdereditem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_order_details_seller);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        deliveryTv = findViewById(R.id.deliveryTv);
        itemsRv = findViewById(R.id.itemsRv);
        editButton = findViewById(R.id.editButton);
        firebaseAuth =FirebaseAuth.getInstance();


        orderId = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editOrderStatusDialog();
            }
        });
        loadCustomerInfo();
        loadOrderDetails();
        loadOrderedItems();


    }

    private void editOrderStatusDialog() {
        String[] options = {"In Progress","Completed","Cancelled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selectedOption = options[i];
                        editOrderStatus(selectedOption);


                    }
                }).show();
    }

    private void editOrderStatus(String selectedOption) {
        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("orderStatus",""+selectedOption);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(OrderDetailsSellerActivity.this, "order is"+selectedOption, Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsSellerActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadOrderDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        String orderBy = ""+datasnapshot.child("orderBy").getValue();
                        String orderCost = ""+datasnapshot.child("orderCost").getValue();
                        String orderId = ""+datasnapshot.child("orderId").getValue();
                        String orderStatus = ""+datasnapshot.child("orderStatus").getValue();
                        String orderTime = ""+datasnapshot.child("orderTime").getValue();
                        String orderTo = ""+datasnapshot.child("orderTo").getValue();


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


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadOrderedItems(){
        ordereditemArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        ordereditemArrayList.clear();
                        for(DataSnapshot ds:datasnapshot.getChildren()){
                            ModelOrdereditem modelOrdereditem = ds.getValue(ModelOrdereditem.class);
                            ordereditemArrayList.add(modelOrdereditem);
                        }
                        adapterOrdereditem = new AdapterOrdereditem(OrderDetailsSellerActivity.this,ordereditemArrayList);
                        itemsRv.setAdapter(adapterOrdereditem);
                        totalItemsTv.setText(""+datasnapshot.getChildrenCount());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadCustomerInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                        deliveryaddress = ""+datasnapshot.child("deliveryaddress").getValue();
                        email = ""+datasnapshot.child("email").getValue();
                        phone = ""+datasnapshot.child("phone").getValue();
                        deliveryaddress = ""+datasnapshot.child("deliveryaddress").getValue();

                        emailTv.setText(email);
                        phoneTv.setText(phone);
                        deliveryTv.setText(deliveryaddress);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}