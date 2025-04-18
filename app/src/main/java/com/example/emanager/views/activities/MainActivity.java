package com.example.emanager.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.emanager.R;
import com.example.emanager.adapters.TransactionsAdapter;
import com.example.emanager.databinding.ActivityMainBinding;
import com.example.emanager.models.Transactions;
import com.example.emanager.utils.Constants;
import com.example.emanager.utils.Helper;
import com.example.emanager.views.fragments.AddTransactionFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Calendar calendar;
    ArrayList<Transactions> transactionsList = new ArrayList<>();
    TransactionsAdapter transactionsAdapter;

    FirebaseDatabase database;
    DatabaseReference transactionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("Transaction");

        Constants.setCategories();

        calendar = Calendar.getInstance();

        database = FirebaseDatabase.getInstance("https://expense-manager-new-c0291-default-rtdb.firebaseio.com/");

        transactionsRef = database.getReference("transactions");

        transactionsAdapter = new TransactionsAdapter(this, transactionsList);
        binding.transactionsList.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionsList.setAdapter(transactionsAdapter);

        updateDate();

        binding.nextDateBtn.setOnClickListener(v -> {
            calendar.add(Calendar.DATE, 1);
            updateDate();
        });

        binding.previousDateBtn.setOnClickListener(v -> {
            calendar.add(Calendar.DATE, -1);
            updateDate();
        });

        binding.floatingActionButton.setOnClickListener(v -> {
            new AddTransactionFragment().show(getSupportFragmentManager(), "AddTransactionFragmentTag");
        });

    }

    private void updateDate() {
        binding.currentDate.setText(Helper.formatDate(calendar.getTimeInMillis()));
        loadAllTransactions(); // load all transactions, no filtering
    }

    private void loadAllTransactions() {
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionsList.clear();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Object raw = childSnapshot.getValue();
                    Log.d("RAW_FIREBASE_DATA", "Snapshot: " + raw);

                    try {
                        Transactions transaction = childSnapshot.getValue(Transactions.class);
                        if (transaction != null) {
                            transactionsList.add(transaction);
                            Log.d("TRANSACTION_DEBUG", "Transaction: " + transaction.getNote());
                        }
                    } catch (Exception e) {
                        Log.e("DESERIALIZATION_ERROR", "Error parsing transaction: " + e.getMessage());
                    }
                }

                transactionsAdapter.notifyDataSetChanged();
                updateTotals();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error loading transactions", Toast.LENGTH_SHORT).show();
                Log.e("TRANSACTION_DEBUG", "Firebase Error: " + error.getMessage());
            }
        });
    }



    private void updateTotals() {
        double income = 0;
        double expense = 0;

        for (Transactions t : transactionsList) {
            if (t.getType().equals(Constants.INCOME)) {
                income += t.getAmount();
            } else {
                expense += t.getAmount();
            }
        }

        binding.textView6.setText(String.valueOf(income));
        binding.textView4.setText(String.valueOf(expense));
        binding.textView.setText(String.valueOf(income - expense));
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
