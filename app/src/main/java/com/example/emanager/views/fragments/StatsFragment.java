package com.example.emanager.views.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.emanager.databinding.FragmentStatsBinding;
import com.example.emanager.views.activities.MainActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private DatabaseReference transactionsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);

        transactionsRef = FirebaseDatabase.getInstance("https://expense-manager-new-c0291-default-rtdb.firebaseio.com/")
                .getReference("transactions");

        loadPieChart();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).updateToolbarTitle("Stats");
    }

    private void loadPieChart() {
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalIncome = 0f;
                float totalExpense = 0f;

                for (DataSnapshot txnSnapshot : snapshot.getChildren()) {
                    String type = txnSnapshot.child("type").getValue(String.class);
                    Double amount = txnSnapshot.child("amount").getValue(Double.class);

                    if (type != null && amount != null) {
                        if (type.equalsIgnoreCase("Income")) {
                            totalIncome += amount;
                        } else if (type.equalsIgnoreCase("Expense")) {
                            totalExpense += amount;
                        }
                    }
                }

                ArrayList<PieEntry> entries = new ArrayList<>();
                if (totalIncome > 0) entries.add(new PieEntry(totalIncome, "Income"));
                if (totalExpense > 0) entries.add(new PieEntry(totalExpense, "Expense"));

                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setColors(new int[]{Color.parseColor("#4CAF50"), Color.parseColor("#F44336")});
                dataSet.setValueTextColor(Color.WHITE);
                dataSet.setValueTextSize(16f);
                dataSet.setSliceSpace(4f);

                PieData pieData = new PieData(dataSet);
                PieChart chart = binding.pieChart;

                chart.setData(pieData);
                chart.setUsePercentValues(true);
                chart.setDrawEntryLabels(true);
                chart.setEntryLabelColor(Color.BLACK);
                chart.setEntryLabelTextSize(14f);
                chart.setCenterText("Income vs Expense");
                chart.setCenterTextSize(18f);
                chart.setHoleRadius(40f);
                chart.setTransparentCircleRadius(45f);

                Description desc = new Description();
                desc.setText("");
                chart.setDescription(desc);

                Legend legend = chart.getLegend();
                legend.setEnabled(true);
                legend.setTextSize(14f);
                legend.setForm(Legend.LegendForm.CIRCLE);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

                chart.animateY(1200);
                chart.invalidate();

                float total = totalIncome + totalExpense;
                float incomePercent = (total > 0) ? (totalIncome / total) * 100 : 0;
                float expensePercent = (total > 0) ? (totalExpense / total) * 100 : 0;

                binding.incomePercent.setText(String.format("Income: %.1f%%", incomePercent));
                binding.expensePercent.setText(String.format("Expense: %.1f%%", expensePercent));


                binding.totalIncome.setText("Income: ₹ " + totalIncome);
                binding.totalExpense.setText("Expense: ₹ " + totalExpense);
                binding.totalBalance.setText("Total: ₹ " + (totalIncome - totalExpense));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional: Handle errors gracefully
            }
        });
    }
}
