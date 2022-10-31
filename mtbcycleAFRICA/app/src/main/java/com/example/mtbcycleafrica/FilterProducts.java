package com.example.mtbcycleafrica;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterProducts extends Filter {
    private AdapterProductSeller adapter;
    private ArrayList<ModelProduct> filterlist;

    public FilterProducts(AdapterProductSeller adapter, ArrayList<ModelProduct> filterlist) {
        this.adapter = adapter;
        this.filterlist = filterlist;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint !=null && constraint.length()>0){

            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0;i<filterlist.size();i++){
                if(filterlist.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterlist.get(i).getProductCategory().toUpperCase().contains(constraint)){
                    filteredModels.add(filterlist.get(i));
                }
            }
            results.count =filteredModels.size();
            results.values =filteredModels;
        }
        else{
            results.count =filterlist.size();
            results.values =filterlist;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productList = (ArrayList<ModelProduct>) results.values;
        adapter.notifyDataSetChanged();

    }
}
