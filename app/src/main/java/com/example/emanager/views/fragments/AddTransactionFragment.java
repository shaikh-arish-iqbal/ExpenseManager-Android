package com.example.emanager.views.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emanager.R;
import com.example.emanager.adapters.AccountsAdapter;
import com.example.emanager.adapters.CategoryAdapter;
import com.example.emanager.adapters.TransactionsAdapter;
import com.example.emanager.databinding.FragmentAddTransactionBinding;
import com.example.emanager.databinding.ListDialogBinding;
import com.example.emanager.models.Account;
import com.example.emanager.models.Category;
import com.example.emanager.models.Transactions;
import com.example.emanager.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddTransactionFragment extends BottomSheetDialogFragment {

    // Firebase Database References
    private FirebaseDatabase database;
    private DatabaseReference transactionsRef;

    private DatabaseReference databaseReference;
    private ArrayList<Transactions> transactionsList;
    private TransactionsAdapter adapter;
    private RecyclerView recyclerView;


    public AddTransactionFragment() {
        // Required empty public constructor
    }

    FragmentAddTransactionBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddTransactionBinding.inflate(inflater);

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance("https://expense-manager-new-c0291-default-rtdb.firebaseio.com/");

        transactionsRef = database.getReference("transactions");

        binding.incomeBtn.setOnClickListener(view -> {
            binding.incomeBtn.setBackground(getContext().getDrawable(R.drawable.income_selector));
            binding.expenseBtn.setBackground(getContext().getDrawable(R.drawable.default_selector));
            binding.expenseBtn.setTextColor(getContext().getColor(R.color.textColor));
            binding.incomeBtn.setTextColor(getContext().getColor(R.color.greenColor));
        });

        binding.expenseBtn.setOnClickListener(view -> {
            binding.incomeBtn.setBackground(getContext().getDrawable(R.drawable.default_selector));
            binding.expenseBtn.setBackground(getContext().getDrawable(R.drawable.expense_selector));
            binding.incomeBtn.setTextColor(getContext().getColor(R.color.textColor));
            binding.expenseBtn.setTextColor(getContext().getColor(R.color.redColor));
        });

        binding.date.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
            datePickerDialog.setOnDateSetListener((datePicker, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
                String dateToShow = dateFormat.format(calendar.getTime());

                binding.date.setText(dateToShow);
            });
            datePickerDialog.show();
        });

        binding.category.setOnClickListener(c -> {
            ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
            AlertDialog categoryDialog = new AlertDialog.Builder(getContext()).create();
            categoryDialog.setView(dialogBinding.getRoot());

            CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), Constants.categories, category -> {
                binding.category.setText(category.getCategoryName());
                categoryDialog.dismiss();
            });

            dialogBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            dialogBinding.recyclerView.setAdapter(categoryAdapter);

            categoryDialog.show();
        });

        binding.account.setOnClickListener(c -> {
            ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
            AlertDialog accountDialog = new AlertDialog.Builder(getContext()).create();
            accountDialog.setView(dialogBinding.getRoot());

            ArrayList<Account> accounts = new ArrayList<>();
            accounts.add(new Account(0, "Cash"));
            accounts.add(new Account(0, "Bank"));
            accounts.add(new Account(0, "UPI"));
            accounts.add(new Account(0, "Other"));

            AccountsAdapter adapter = new AccountsAdapter(getContext(), accounts, account -> {
                binding.account.setText(account.getAccount_name());
                accountDialog.dismiss();
            });

            dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            dialogBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            dialogBinding.recyclerView.setAdapter(adapter);

            accountDialog.show();
        });

        // Save transaction data to Firebase when Save button is clicked
        binding.saveButton.setOnClickListener(v -> {

            String date = binding.date.getText().toString();
            String amountStr = binding.amount.getText().toString();
            String category = binding.category.getText().toString();
            String account = binding.account.getText().toString();
            String note = binding.note.getText().toString();
            String type = binding.incomeBtn.getBackground().getConstantState().equals(getContext().getDrawable(R.drawable.income_selector).getConstantState()) ? "Income" : "Expense";

            // Validate the inputs
            if (date.isEmpty() || amountStr.isEmpty() || category.isEmpty() || account.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            // Convert the date string to Date object
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
            Date transactionDate = null;
            try {
                transactionDate = dateFormat.parse(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (transactionDate == null) {
                Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a Transaction object
            Transactions transaction = new Transactions(type, category, account, note, transactionDate.getTime(), amount, System.currentTimeMillis());

            // Generate a unique ID for the transaction
            String transactionId = transactionsRef.push().getKey();

            if (transactionId != null) {
                // Save transaction to Firebase Realtime Database
                transactionsRef.child(transactionId).setValue(transaction)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Transaction saved", Toast.LENGTH_SHORT).show();
                                dismiss();  // Close the fragment
                            } else {
                                Toast.makeText(getContext(), "Error saving transaction", Toast.LENGTH_SHORT).show();
                            }

                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Firebase error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });;
            }
        });

        return binding.getRoot();
    }
}
