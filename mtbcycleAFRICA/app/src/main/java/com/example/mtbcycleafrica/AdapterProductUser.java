package com.example.mtbcycleafrica;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;
//
//import p32929.androideasysql_library.Column;
//import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {
    private Context context;
    public ArrayList<ModelProduct> productsList,filterList;
    private FilterProductsUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

//    @Override
//    public Filter getFilter() {
//        return null;
//    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {

        ModelProduct modelProduct = productsList.get(position);
//        String id = modelProduct.getProductId();
//        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productIcon = modelProduct.getProductIcon();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDescription = modelProduct.getProductDescription();

        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity+"Units Available");
        holder.discountedNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText("KES" + discountPrice);
        holder.originalPriceTv.setText("KES" + originalPrice);
        holder.descriptionTv.setText(productDescription);

        if(discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);

        }
        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.cart).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.cart);

        }
        holder.addtoCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuantityDialog(modelProduct);
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });


    }
    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;

    private void showQuantityDialog(ModelProduct modelProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        ImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv = view.findViewById(R.id.discountedNoteTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv = view.findViewById(R.id.priceDiscountedTv);
        TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        TextView productquantityTv= view.findViewById(R.id.productquantityTv);
        ImageButton incrementBtn= view.findViewById(R.id.incrementBtn);
        Button continueBtn= view.findViewById(R.id.continueBtn);

        //getdatamodel
        String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String description = modelProduct.getProductDescription();
        String discountNote = modelProduct.getDiscountNote();
        String image = modelProduct.getProductIcon();

        String price;
        if(modelProduct.getDiscountAvailable().equals("true")){
            price = modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else{
            discountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price = modelProduct.getOriginalPrice();
        }

        cost = Double.parseDouble(price.replaceAll("KES",""));
        finalCost = Double.parseDouble(price.replaceAll("KES",""));
        quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try{
            Picasso.get().load(image).placeholder(R.drawable.shopping_cart).into(productIv);

        }catch(Exception e){
            productIv.setImageResource(R.drawable.shopping_cart);

        }
         titleTv.setText(""+title);
         productquantityTv.setText(""+quantity);
         descriptionTv.setText(""+description);
         discountedNoteTv.setText("KES"+discountNote);
         originalPriceTv.setText("KES"+modelProduct.getDiscountPrice());
         finalPriceTv.setText("KES"+finalCost);
         quantityTv.setText(""+productQuantity);

         AlertDialog dialog = builder.create();
         dialog.show();

         incrementBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //if(quantity>=productQuantity){}
                 finalCost = finalCost+cost;
                 quantity++;
                 finalPriceTv.setText("KES" + finalCost);
                 productquantityTv.setText(""+quantity);

             }
         });

        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity>1) {
                    finalCost = finalCost - cost;
                    quantity--;
                    finalPriceTv.setText("KES" + finalCost);
                    productquantityTv.setText("" + quantity);
                }

            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleTv.getText().toString();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("KES","");
                String quantity = productquantityTv.getText().toString();

                //addToCart(productId,title,priceEach,totalPrice,quantity);
                addToCart(productId,title,priceEach,totalPrice,quantity);

                //add to dbSQL lite

                dialog.dismiss();
            }
        });

    }

    private int itemId = 1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;
        EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id",itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();
        Toast.makeText(context, "Successfully added to cart", Toast.LENGTH_SHORT).show();

        ((Buyer_dashboard)context).cartCount();

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }
    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterProductsUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{
        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,quantityTv,discountedPriceTv,originalPriceTv,addtoCartTv,descriptionTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
            addtoCartTv = itemView.findViewById(R.id.addtoCartTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);

        }
    }
}
