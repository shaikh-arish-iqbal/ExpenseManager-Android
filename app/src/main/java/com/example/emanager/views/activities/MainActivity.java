package com.example.emanager.views.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emanager.utils.Helper;
import com.example.emanager.views.fragments.AddTransactionFragment;
import com.example.emanager.R;
import com.example.emanager.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("Transaction");

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
            new AddTransactionFragment().show(getSupportFragmentManager(), null);

        });
    }

    void updateDate(){
        binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}