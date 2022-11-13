package com.example.mtbcycleafrica;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class seller_activity extends AppCompatActivity {
    TextView name_text;
    //Firebase Instance
    FirebaseAuth firebaseAuth;
    Button logout;
    ImageButton editProfileButton;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 100;

    private String[] camera_permissions;
    private String[] storage_permissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_seller);
        name_text = findViewById(R.id.name);
        logout = findViewById(R.id.logout_button);
        editProfileButton = findViewById(R.id.editProfileButton);
        //initialise firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                           // String accounttype = ""+ds.child("accounttype").getValue();
//                            name_text.setText(name + "("+accounttype+")");
                            name_text.setText(username);
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