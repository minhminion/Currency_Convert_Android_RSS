package com.example.currencyconvert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>{

    private List<Currency> currencies;

    public CurrencyAdapter(List<Currency> currencies) {
        this.currencies = currencies;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new CurrencyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_currency,parent,false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        holder.setCurrencyData(currencies.get(position));
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    static  class  CurrencyViewHolder extends RecyclerView.ViewHolder {

        private TextView currencyCode;

        CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyCode = itemView.findViewById(R.id.currency_code);
        }

        void setCurrencyData (Currency currency) {
            currencyCode.setText(currency.getCode());
        }
    }
}
