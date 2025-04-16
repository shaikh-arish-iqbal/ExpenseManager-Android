package com.example.emanager.views.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.emanager.adapters.TransactionsAdapter;
import com.example.emanager.models.Transactions;
import com.example.emanager.utils.Constants;
import com.example.emanager.utils.Helper;
import com.example.emanager.views.fragments.AddTransactionFragment;
import com.example.emanager.R;
import com.example.emanager.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Calendar;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Calendar calendar;
    ArrayList<Transactions> dummyTransactions = new ArrayList<>(); // Placeholder until Firebase is integrated

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
        updateDate();

        binding.nextDateBtn.setOnClickListener(c -> {
            calendar.add(Calendar.DATE, 1);
            updateDate();
        });

        binding.previousDateBtn.setOnClickListener(c -> {
            calendar.add(Calendar.DATE, -1);
            updateDate();
        });

        binding.floatingActionButton.setOnClickListener(c -> {
            // Correct usage of show() with a non-null tag.
            new AddTransactionFragment().show(getSupportFragmentManager(), "AddTransactionFragmentTag");
        });

        // Temporary sample transactions
        dummyTransactions.add(new Transactions(Constants.INCOME, "Business", "Cash", "Note 1", null, 500, System.currentTimeMillis()));
        dummyTransactions.add(new Transactions(Constants.EXPENSE, "Investment", "Cash", "Note 2", null, 900, System.currentTimeMillis()));
        dummyTransactions.add(new Transactions(Constants.INCOME, "Rent", "Cash", "Note 3", null, 500, System.currentTimeMillis()));

        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(this, dummyTransactions);
        binding.transactionsList.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionsList.setAdapter(transactionsAdapter);
    }

    void updateDate() {
        binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
        // You can later re-enable loading from Firebase here
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}