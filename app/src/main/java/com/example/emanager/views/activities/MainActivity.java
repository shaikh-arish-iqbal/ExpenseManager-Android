package com.example.emanager.views.activities;

import android.content.Intent;
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
import com.example.emanager.views.fragments.StatsFragment;
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

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.transactions) {
                // Launch MainActivity
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.stats) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ConstraintLayout, new StatsFragment())
                        .commit();
                updateToolbarTitle("Stats");

            }

            return true;
        });



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

    public void updateToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void updateDate() {
        binding.currentDate.setText(Helper.formatDate(calendar.getTimeInMillis()));
        loadTransactionsForDate(calendar.getTimeInMillis());
    }

    private ValueEventListener transactionsListener;

    private void loadTransactionsForDate(long selectedDateMillis) {
        if (transactionsListener != null) {
            transactionsRef.removeEventListener(transactionsListener);
        }

        transactionsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionsList.clear();

                Calendar selected = Calendar.getInstance();
                selected.setTimeInMillis(selectedDateMillis);
                int selectedYear = selected.get(Calendar.YEAR);
                int selectedMonth = selected.get(Calendar.MONTH);
                int selectedDay = selected.get(Calendar.DAY_OF_MONTH);

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        Transactions transaction = childSnapshot.getValue(Transactions.class);
                        if (transaction != null) {
                            Calendar txnCal = Calendar.getInstance();
                            txnCal.setTimeInMillis(transaction.getDate());

                            int txnYear = txnCal.get(Calendar.YEAR);
                            int txnMonth = txnCal.get(Calendar.MONTH);
                            int txnDay = txnCal.get(Calendar.DAY_OF_MONTH);

                            if (txnYear == selectedYear && txnMonth == selectedMonth && txnDay == selectedDay) {
                                transactionsList.add(transaction);
                            }
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
        };

        transactionsRef.addValueEventListener(transactionsListener);
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
