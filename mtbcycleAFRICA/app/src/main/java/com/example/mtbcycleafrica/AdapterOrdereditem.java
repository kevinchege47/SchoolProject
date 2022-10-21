package com.example.mtbcycleafrica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterOrdereditem extends RecyclerView.Adapter<AdapterOrdereditem.HolderOrdereditem> {
    private Context context;
    private ArrayList<ModelOrdereditem> ordereditemArrayList;

    public AdapterOrdereditem(Context context, ArrayList<ModelOrdereditem> ordereditemArrayList) {
        this.context = context;
        this.ordereditemArrayList = ordereditemArrayList;
    }

    @NonNull
    @Override
    public HolderOrdereditem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordereditem,parent,false);
        return new HolderOrdereditem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrdereditem holder, int position) {
        ModelOrdereditem modelOrdereditem = ordereditemArrayList.get(position);
        String getpId = modelOrdereditem.getpId();
        String name = modelOrdereditem.getName();
        String cost = modelOrdereditem.getCost();
        String price = modelOrdereditem.getPrice();
        String quantity = modelOrdereditem.getQuantity();

        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("KES"+price);
        holder.itemPriceTv.setText("KES"+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");



    }

    @Override
    public int getItemCount() {
        return ordereditemArrayList.size();
    }

    class HolderOrdereditem extends RecyclerView.ViewHolder {
        TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;
        public HolderOrdereditem(@NonNull View itemView) {
            super(itemView);
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}
