package com.example.mtbcycleafrica;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class Buyer_dashboard extends AppCompatActivity {
    //firebase instance
    FirebaseAuth firebaseAuth;
    ImageButton editProfileButton,cartButton;
    EditText searchProductEt;
    ImageButton filterProductBtn;
    TextView filteredProductsTv,CartCountTv;
    Button checkoutBtn,OrdersBtn;
    RecyclerView productsRV;
    RelativeLayout productRl,ordersRl;
    //TextView OrdersTv,productsTv;
    private String shopUid;


    ProgressDialog progressDialog;
    private ArrayList<ModelProduct> productList;
    private AdapterProductUser adapterProductUser;

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private EasyDB easyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buyer_dashboard);

        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        //OrdersTv = findViewById(R.id.OrdersTv);
        productsRV = findViewById(R.id.productsRV);
        CartCountTv = findViewById(R.id.CartCountTv);
       // productsTv = findViewById(R.id.productsTv);
        productRl = findViewById(R.id.productRl);
        //ordersRl = findViewById(R.id.ordersRl);
        shopUid = getIntent().getStringExtra("shopUid");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        cartButton = findViewById(R.id.cartButton);
        //loadAllProducts();
        editProfileButton = findViewById(R.id.editProfileButton);
        checkUser();
        loadShopProducts();
       easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();
        deleteCartData();

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try{
                    adapterProductUser.getFilter().filter(s);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Buyer_dashboard.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    loadShopProducts();
                                }
                                else{
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });


        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ProfileEditUserActivity.class);
                startActivity(intent);


            }
        });
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCartDialog();

            }
        });
    }

    private void deleteCartData() {

        easyDB.deleteAllDataFromTable();
    }
    public void cartCount(){
        int count = easyDB.getAllData().getCount();
        if(count<=0){
            CartCountTv.setVisibility(View.GONE);
        }else{
            CartCountTv.setVisibility(View.VISIBLE);
            CartCountTv.setText(""+count);
        }
    }


    public double allTotalPrice = 0.00;
    public TextView sTotalsTv,allTotalPriceTv;
    private void showCartDialog() {

        cartItemList = new ArrayList<>();
        View view  = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);

        RecyclerView cartItemsRv;
        Button checkoutBtn;

        cartItemsRv = view.findViewById(R.id.cartItemsRv);
        sTotalsTv = view.findViewById(R.id.TotalsTv);
        allTotalPriceTv= view.findViewById(R.id.TotalFeeTv);
        checkoutBtn = view.findViewById(R.id.checkoutBtn);
        TextView TotalFeeLabelTv = view.findViewById(R.id.TotalFeeLabelTv);
        TextView TotalLabelTv = view.findViewById(R.id.TotalLabelTv);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        EasyDB easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while(res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(
                    ""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );
            cartItemList.add(modelCartItem);
        }
        adapterCartItem = new AdapterCartItem(this,cartItemList);
        cartItemsRv.setAdapter(adapterCartItem);


        sTotalsTv.setText("KES"+ String.format("%.2f",allTotalPrice));
        allTotalPriceTv.setText("KES"+allTotalPrice);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                allTotalPrice = 0.00;
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(cartItemList.size() == 0){
                    Toast.makeText(Buyer_dashboard.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                    return;

                    }
                    submitOrder();


            }
        });

    }
    //Store order to DB
    private void submitOrder() {
        progressDialog.setMessage("Placing Order");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();
        String cost = allTotalPriceTv.getText().toString().trim().replace("KES", "");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "In progress");
        hashMap.put("orderCost", "" + cost);
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + shopUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        for (int i = 0; i < cartItemList.size(); i++) {
                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();

                            HashMap<String, String> hashmap1 = new HashMap<>();
                            hashmap1.put("pId", pId);
                            hashmap1.put("name", name);
                            hashmap1.put("cost", cost);
                            hashmap1.put("price", price);
                            hashmap1.put("quantity", quantity);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashmap1);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(Buyer_dashboard.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Buyer_dashboard.this,OrderDetailsUsersActivity.class);
                        intent.putExtra("orderTo",shopUid);
                        intent.putExtra("orderId",timestamp);
                        startActivity(intent);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Buyer_dashboard.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //load the products associated with a particular branch
    private void loadShopProducts() {
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //ref.orderByChild("accounttype").equalTo("seller").orderByChild("Products")
        productList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Products")
        //ref.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        productList.clear();
                        for(DataSnapshot ds: datasnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        adapterProductUser = new AdapterProductUser(Buyer_dashboard.this,productList);
                        productsRV.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null){
            Intent i = new Intent(getApplicationContext(),login.class);
            startActivity(i);
            finish();
        }else{
            loadInfo();
        }
    }

    private void loadInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        for(DataSnapshot ds: datasnapshot.getChildren()){

                            Toast.makeText(Buyer_dashboard.this, "Info", Toast.LENGTH_SHORT).show();
                            //loadOrders();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void logout(View view) {
        firebaseAuth.signOut();
    }
    }
