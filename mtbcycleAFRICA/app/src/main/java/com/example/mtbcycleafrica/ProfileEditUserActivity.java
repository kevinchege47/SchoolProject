package com.example.mtbcycleafrica;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileEditUserActivity extends AppCompatActivity {
    TextInputLayout sign_username, sign_email, sign_password, sign_phone;
    TextView deliveryaddressTv;
    Button update;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_edit_user);
        sign_username = findViewById(R.id.sign_username);
        sign_email = findViewById(R.id.sign_email);
        sign_password = findViewById(R.id.sign_password);
        deliveryaddressTv = findViewById(R.id.deliveryaddress);
        sign_phone = findViewById(R.id.sign_phone);
        update = findViewById(R.id.update);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();

            }
        });

        deliveryaddressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileEditUserActivity.this, "locations", Toast.LENGTH_SHORT).show();
                deliveryDialog();
            }
        });
    }


    String username, phone, email, password,deliveryaddress;

    private void inputData() {
        username = sign_username.getEditText().getText().toString();
        email = sign_email.getEditText().getText().toString();
        phone = sign_phone.getEditText().getText().toString();
        password = sign_password.getEditText().getText().toString();
        deliveryaddress = deliveryaddressTv.getText().toString();
        updateProfile();
    }

    private void updateProfile() {
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        //hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("username", "" + username);
        hashMap.put("email", "" + email);
        hashMap.put("password", "" + password);
        hashMap.put("phone", "" + phone);
        hashMap.put("deliveryaddress", "" + deliveryaddress);
//        hashMap.put("timestamp",""+timestamp);
//        hashMap.put("online", "true");
//        hashMap.put("accounttype", "buyer");

        //save to database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileEditUserActivity.this, "Update Completed", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileEditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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
                            String username = ""+ds.child("username").getValue();
                            String email = ""+ds.child("email").getValue();
                            String password = ""+ds.child("password").getValue();
                            String phone = ""+ds.child("phone").getValue();

                            sign_username.getEditText().setText(username);
                            sign_email.getEditText().setText(email);
                            sign_password.getEditText().setText(password);
                            sign_phone.getEditText().setText(phone);

//
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deliveryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Locations Available")
                .setItems(LocationConstants.locations, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String locations = LocationConstants.locations[which];
//                       //product_category.setText(category);
                        deliveryaddressTv.setText(locations);
                    }
                })
                .show();
    }

}