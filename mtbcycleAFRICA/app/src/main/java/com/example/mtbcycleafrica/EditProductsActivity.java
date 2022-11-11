package com.example.mtbcycleafrica;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProductsActivity extends AppCompatActivity {
    String productId;
    TextInputLayout product_title,product_description,product_price,discount_price,product_quantity,discount_note;
    Button editButton;
    ImageView producticon;
    SwitchCompat discountSwitch;
    TextView product_category,welcomename;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] camerapermissions;
    private String[] storagepermissions;

    private Uri image_uri;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_products);
        product_title = findViewById(R.id.product_title);
        product_description = findViewById(R.id.product_description);
        product_category = findViewById(R.id.product_category);
        product_price = findViewById(R.id.product_price);
        editButton = findViewById(R.id.editproducts1);
        discount_price = findViewById(R.id.discountprice);
        producticon = findViewById(R.id.producticon);
        product_quantity = findViewById(R.id.product_quantity);
        welcomename = findViewById(R.id.welcomename);
        discount_note = findViewById(R.id.discountnote);
        discountSwitch = findViewById(R.id.discountSwitch);
        productId = getIntent().getStringExtra("productId");

        discount_price.setVisibility(View.GONE);
        discount_note .setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        loadProductDetails();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        camerapermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    discount_price.setVisibility(View.VISIBLE);
                    discount_note .setVisibility(View.VISIBLE);


                }else{
                    discount_price.setVisibility(View.GONE);
                    discount_note .setVisibility(View.GONE);

                }
            }
        });

        producticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });


        product_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });


    }
    private void loadProductDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String productId = ""+dataSnapshot.child("productId").getValue();
                        String productTitle = ""+dataSnapshot.child("productTitle").getValue();
                        String productDescription = ""+dataSnapshot.child("productDescription").getValue();
                        String productCategory = ""+dataSnapshot.child("productCategory").getValue();
                        String productQuantity = ""+dataSnapshot.child("productQuantity").getValue();
                        String productIcon = ""+dataSnapshot.child("productIcon").getValue();
                        String originalPrice = ""+dataSnapshot.child("originalPrice").getValue();
                        String discountPrice = ""+dataSnapshot.child("discountPrice").getValue();
                        String discountNote = ""+dataSnapshot.child("discountNote").getValue();
                        String discountAvailable = ""+dataSnapshot.child("discountAvailable").getValue();
                        String timestamp = ""+dataSnapshot.child("timestamp").getValue();
                        String uid = ""+dataSnapshot.child("uid").getValue();

                        if(discountAvailable.equals("true")){
                            discountSwitch.setChecked(true);
                            discount_price.setVisibility(View.VISIBLE);
                            discount_note .setVisibility(View.VISIBLE);


                        }else{
                            discountSwitch.setChecked(false);
                            discount_price.setVisibility(View.GONE);
                            discount_note .setVisibility(View.GONE);
                        }
                        product_title.getEditText().setText(productTitle);
                        product_description.getEditText().setText(productDescription);
                        product_category.setText(productCategory);
                        discount_price.getEditText().setText(discountPrice);
                        product_quantity.getEditText().setText(productQuantity);
                        discount_note.getEditText().setText(discountNote);
                        product_price.getEditText().setText(originalPrice);
                        try{
                            Picasso.get().load(productIcon).placeholder(R.drawable.cart).into(producticon);

                        }catch(Exception e){
                            producticon.setImageResource(R.drawable.cart);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    private void updateProduct() {
        progressDialog.setMessage("Updating Product");
        progressDialog.show();

        if(image_uri ==null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("productTitle",""+producttitle);
            hashMap.put("productDescription",""+productdescription);
            hashMap.put("productCategory",""+productcategory);
            hashMap.put("productQuantity",""+productQuantity);
            hashMap.put("originalPrice",""+originalprice);
            hashMap.put("discountNote",""+discountNote);
            hashMap.put("discountPrice",""+discountprice);
            hashMap.put("discountAvailable",""+discountAvailable);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductsActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
        else{
            String filePathAndName = "product_images/"+""+productId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri dowmloadImageUri = uriTask.getResult();
                            if(uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productTitle",""+producttitle);
                                hashMap.put("productDescription",""+productdescription);
                                hashMap.put("productCategory",""+productcategory);
                                hashMap.put("productQuantity",""+productQuantity);
                                hashMap.put("productIcon",""+dowmloadImageUri);
                                hashMap.put("originalPrice",""+originalprice);
                                hashMap.put("discountNote",""+discountNote);
                                hashMap.put("discountPrice",""+discountprice);
                                hashMap.put("discountAvailable",""+discountAvailable);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductsActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }
    private String producttitle,productdescription,productcategory,originalprice,discountprice,productQuantity,discountNote;
    private Boolean discountAvailable = false;
    private void inputData() {
        producttitle = product_title.getEditText().getText().toString().trim();
        productdescription = product_description.getEditText().getText().toString().trim();
        productcategory = product_category.getText().toString().trim();
        originalprice = product_price.getEditText().getText().toString().trim();
        discountprice = discount_price.getEditText().getText().toString().trim();
        productQuantity = product_quantity.getEditText().getText().toString().trim();
        discountNote = discount_note.getEditText().getText().toString().trim();
        discountAvailable = discountSwitch.isChecked();

        if (discountAvailable){
            discountprice = discount_price.getEditText().getText().toString().trim();
            discountNote = discount_note.getEditText().getText().toString().trim();
        }
        else{
            discountprice = "0";
            discountNote = "";
        }
        updateProduct();

    }



    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Constants.productCategories[which];
                        product_category.setText(category);
                    }
                })
                .show();
    }

    private void showImagePickDialog() {
        String[] options = {"CAMERA","GALLERY"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PICK IMAGE")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == 0) {
                            if (checkCameraPermissions()) {
                                pickFromCamera();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickFromGallery();
                            } else {
                                requestStoragePermission();

                            }
                        }
                    }

                }).show();

    }
    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE );
    }

    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image_Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE );
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;

    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagepermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,camerapermissions,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] ==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&&storageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this, "Camera and storage permissions Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this, "storage permissions Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                producticon.setImageURI(image_uri);
            }
            else if(requestCode==IMAGE_PICK_CAMERA_CODE){
                producticon.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}