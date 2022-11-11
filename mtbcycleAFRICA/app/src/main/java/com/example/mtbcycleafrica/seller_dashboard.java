package com.example.mtbcycleafrica;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class seller_dashboard extends AppCompatActivity {
    RelativeLayout addproduct,showProduct,showOrder,editprofile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_seller_dashboard);
        addproduct = findViewById(R.id.first_card);
        showOrder = findViewById(R.id.second_card);
        showProduct = findViewById(R.id.third_card);
        editprofile = findViewById(R.id.fourth_card);

        showOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load orders
                Intent i = new Intent(getApplicationContext(),SellerOrdersActivity
                        .class);
                startActivity(i);
            }
        });
        showProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load products
                Intent i = new Intent(getApplicationContext(),ShowProducts.class);
                startActivity(i);
            }
        });
        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), add_products.class);
                startActivity(i);
            }
        });
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profileEditSeller.class);
                startActivity(i);
            }
        });
    }
}