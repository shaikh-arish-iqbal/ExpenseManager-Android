package com.example.emanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.emanager.databinding.RowTransactionBinding;
import com.example.emanager.models.Category;
import com.example.emanager.models.Transactions;
import com.example.emanager.utils.Constants;
import com.example.emanager.utils.Helper;
import com.example.emanager.R;

import java.util.ArrayList;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>{

    Context context;
    ArrayList<Transactions> transactions;

    public TransactionsAdapter(Context context, ArrayList<Transactions> transactions){
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionsViewHolder(LayoutInflater.from(context).inflate(R.layout.row_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        Transactions transaction = transactions.get(position);

        holder.binding.transactionAmount.setText(String.valueOf(transaction.getAmount()));
        holder.binding.accountLbl.setText((transaction.getAccount()));

        holder.binding.transactionDate.setText(Helper.formatDate(transaction.getDate()));
        holder.binding.transactionCategory.setText(transaction.getCategory());

        Category transactionsCategory = Constants.getCategory(transaction.getCategory());

        holder.binding.categoryIcon.setImageResource(transactionsCategory.getCategoryImage());
//        holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(transactionsCategory.getCategoryColor()));

        if (transaction.getType().equals(Constants.INCOME))
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.greenColor));
        else if (transaction.getType().equals(Constants.EXPENSE))
        holder.binding.transactionAmount.setTextColor(context.getColor(R.color.redColor));

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class TransactionsViewHolder extends RecyclerView.ViewHolder {

        RowTransactionBinding binding;

        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowTransactionBinding.bind(itemView);
        }
    }
}