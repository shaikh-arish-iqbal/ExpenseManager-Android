package com.example.emanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emanager.R;
import com.example.emanager.databinding.RowAccountsBinding;
import com.example.emanager.models.Account;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder>{

    Context context;
    ArrayList<Account> accountArrayList;

    public interface AccountsClickListner{
        void onAccountClicked(Account account);
    }

    AccountsClickListner accountsClickListner;

    public AccountsAdapter(Context context, ArrayList<Account> accountArrayList, AccountsClickListner accountsClickListner) {
        this.context = context;
        this.accountArrayList = accountArrayList;
        this.accountsClickListner = accountsClickListner;
    }

    @NonNull
    @Override
    public AccountsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountsViewHolder(LayoutInflater.from(context).inflate(R.layout.row_accounts,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountsViewHolder holder, int position) {
        Account account = accountArrayList.get(position);
        holder.binding.accountName.setText(account.getAccount_name());
        holder.itemView.setOnClickListener(c -> {
            accountsClickListner.onAccountClicked(account);
        });

    }

    @Override
    public int getItemCount() {
        return accountArrayList.size() ;
    }

    public class AccountsViewHolder extends RecyclerView.ViewHolder {

        RowAccountsBinding binding;

        public AccountsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowAccountsBinding.bind(itemView);
        }
    }

}
