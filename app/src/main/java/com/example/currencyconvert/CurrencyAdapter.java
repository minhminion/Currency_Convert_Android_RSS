package com.example.currencyconvert;

import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.security.auth.callback.Callback;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>{

    interface Callback {
        void onItemClick(View v);
    }

    private List<Currency> currencies;
    Callback callback = null;
    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(callback != null)
                callback.onItemClick(v);
        }
    };


    public CurrencyAdapter(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public CurrencyAdapter(List<Currency> currencies, Callback callback) {
        this.currencies = currencies;
        this.callback = callback;
    }



    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);
        itemView.setOnClickListener(clickListener);
        return new CurrencyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        holder.setCurrencyData(currencies.get(position));
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    static class CurrencyViewHolder extends RecyclerView.ViewHolder {

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
