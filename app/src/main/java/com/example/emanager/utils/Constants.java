package com.example.emanager.utils;

import com.example.emanager.models.Category;
import com.example.emanager.R;

import java.util.ArrayList;

public class Constants {
    public static String INCOME = "Income";
    public static String EXPENSE = "Expense";

    public static ArrayList<Category> categories;

    public static void setCategories()
    {
        categories = new ArrayList<>();
        categories.add(new Category("Salary", R.drawable.ic_salary));
        categories.add(new Category("Investment", R.drawable.ic_investment));
        categories.add(new Category("Business", R.drawable.ic_business));
        categories.add(new Category("Loan", R.drawable.ic_loan));
        categories.add(new Category("Rent", R.drawable.ic_rent));
        categories.add(new Category("Other", R.drawable.ic_other));
    }

    public static Category getCategory(String categoryName){
        for (Category cat :
                categories) {
            if (cat.getCategoryName().equals(categoryName)) {
                return cat;
            }
        }
        return null;
    }
}
