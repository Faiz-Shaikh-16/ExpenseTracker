package com.example.expensetracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.db.DataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TodayFragment extends Fragment {

    Spinner spinnerDialog;
    FloatingActionButton floatingAddBtn;
    EditText etAmount;
    EditText etDate;
    ListView lvExpenses;
    Button buttonAddExpense,buttonAddIncome;
    TextView tvGetToday;
    TextView tvTitle;

    DataBaseHelper dataBaseHelper;
    ArrayList<ListModel> expensesList;
    ArrayList<String> categoriesListForSpinner;
    CustomAdapterTodayListExpenses custom;

    Calendar c;
    int year;
    int month;
    int day;

    PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_today, container, false);

        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        month = month + 1;

        dataBaseHelper = new DataBaseHelper(getContext());

        buttonAddExpense = rootview.findViewById(R.id.button_today);
        buttonAddIncome = rootview.findViewById(R.id.button_income);
        tvGetToday = rootview.findViewById(R.id.tv_total_amount);
        tvTitle = rootview.findViewById(R.id.tv_today_title);
        lvExpenses = rootview.findViewById(R.id.lv_fragm_today);

        categoriesListForSpinner = new ArrayList<>();

        tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvGetToday.setText(dataBaseHelper.totalForToday() + "");
        expensesList = dataBaseHelper.getTodayData();

        floatingAddBtn = rootview.findViewById(R.id.floatingAddBtn);

        ArrayList<PieChart> pieChartArrayList = new ArrayList<>();

        pieChart = rootview.findViewById(R.id.pieChart);
        PieModel slice1 = new PieModel("Expense",30f, Color.RED);
        pieChart.addPieSlice(slice1);

        PieModel slice2 = new PieModel("R",40f, Color.BLUE);
        pieChart.addPieSlice(slice2);

        PieModel slice3 = new PieModel("R",25f, Color.YELLOW);
        pieChart.addPieSlice(slice3);

        PieModel slice4 = new PieModel("R",25f, Color.GREEN);
        pieChart.addPieSlice(slice4);

        pieChart.setInnerPadding(40);
//        pieChart.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.linear_interpolator));
        pieChart.startAnimation();

        custom = new CustomAdapterTodayListExpenses(getContext(),
                R.layout.row,
                expensesList);
        lvExpenses.setAdapter(custom);

        buttonAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogAddExpense();
            }
        });


        buttonAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddIncome();
            }
        });

        floatingAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddExpense();
            }
        });

        return rootview;


    }
    // Add this method for handling income button click
    public void dialogAddIncome() {
        AlertDialog.Builder builderAddIncome = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog, null);

        builderAddIncome.setView(view);
        builderAddIncome.setTitle("Add new income!");

        etAmount = view.findViewById(R.id.editText_dialog);
        spinnerDialog = view.findViewById(R.id.spinner_dialog);
        etDate = view.findViewById(R.id.et_Date);

        // Populate spinner with categories (if applicable)
        categoriesListForSpinner = dataBaseHelper.getCategoriesList();

        ArrayAdapter<String> arrayOfCatForSpinner = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categoriesListForSpinner);
        arrayOfCatForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialog.setAdapter(arrayOfCatForSpinner);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                c.set(Calendar.MONTH, monthOfYear);
                updateLabel();
            }
        };

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show DatePickerDialog
                new DatePickerDialog(getContext(), date, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        builderAddIncome.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String incAmount = etAmount.getText().toString();
                        String xxDate = etDate.getText().toString();

                        if (incAmount.trim().length() == 0 || xxDate.trim().length() == 0) {
                            Toast.makeText(getActivity(), "Please insert amount and date!", Toast.LENGTH_SHORT).show();
                        } else {
                            int incomeAmount = Integer.parseInt(incAmount);

                            // Subtract the income amount from the total
                            int newTotal = dataBaseHelper.totalForToday() - incomeAmount;

                            // Add the income with the adjusted total
                            AddExp("Income", incomeAmount, newTotal, xxDate);

                            // Update the UI
                            tvGetToday.setText(dataBaseHelper.totalForToday() + "");
                            expensesList = dataBaseHelper.getTodayData();
//                            custom = new CustomAdapterTodayListExpenses(getContext(), R.layout.row, expensesList);
//                            lvExpenses.setAdapter(custom);
//                            custom.notifyDataSetChanged();
                        }
                    }
                })
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builderAddIncome.create();
        dialog.show();
    }


    public void dialogAddExpense() {
        AlertDialog.Builder builderAddExp = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog, null);

        builderAddExp.setView(view);
        builderAddExp.setTitle("          Add new expense!");

        etAmount = view.findViewById(R.id.editText_dialog);
        spinnerDialog = view.findViewById(R.id.spinner_dialog);
        etDate = view.findViewById(R.id.et_Date);

        categoriesListForSpinner = dataBaseHelper.getCategoriesList();

        ArrayAdapter<String> arrayOfCatForSpinner = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categoriesListForSpinner);
        arrayOfCatForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialog.setAdapter(arrayOfCatForSpinner);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                c.set(Calendar.MONTH, monthOfYear);
                updateLabel();
            }
        };

       /* etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.MONTH)).show();
            }
        });*/
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getContext(), date, year, month, day).show();
            }
        });


        builderAddExp.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String expAmount = etAmount.getText().toString();
                        String xxDate = etDate.getText().toString();

                        if (expAmount.trim().length() == 0 || xxDate.trim().length() ==0) {
                            Toast.makeText(getActivity(), "Please insert amount and date!", Toast.LENGTH_SHORT).show();
                        } else {

                            int eAmount = Integer.parseInt(expAmount);
                            String expSpinnerCat = (String) spinnerDialog.getSelectedItem();

                            if (tvGetToday.getText().toString().isEmpty()) {

                                AddExp(expSpinnerCat, eAmount, eAmount, xxDate);
                                tvGetToday.setText(dataBaseHelper.totalForToday() + "");
                                expensesList = dataBaseHelper.getTodayData();
                                custom = new CustomAdapterTodayListExpenses(getContext(),
                                        R.layout.row,
                                        expensesList);
                                lvExpenses.setAdapter(custom);
                                custom.notifyDataSetChanged();

                            } else {
                                String previousValue = tvGetToday.getText().toString();
                                int preValueInt = Integer.parseInt(previousValue);
                                int currentValueInt = Integer.parseInt(expAmount);
                                int sum = currentValueInt + preValueInt;

                                AddExp(expSpinnerCat, eAmount, sum, xxDate);
                                tvGetToday.setText(dataBaseHelper.totalForToday() + "");

                                expensesList = dataBaseHelper.getTodayData();
                                custom = new CustomAdapterTodayListExpenses(getContext(),
                                        R.layout.row,
                                        expensesList);
                                lvExpenses.setAdapter(custom);
                                custom.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builderAddExp.create();
        dialog.show();
    }


    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        etDate.setText(sdf.format(c.getTime()));

    }

    public void AddExp(String category, int amount, int total, String date) {
        boolean insertData;

        // Check if it's an expense or income
        if (category.equals("Income")) {
            // Subtract the income amount from the total
            int newTotal = dataBaseHelper.totalForToday() - amount;
            insertData = dataBaseHelper.addIncome(category, amount, date);




        } else {
            // For expense, subtract the amount from the total
            int newTotal = dataBaseHelper.totalForToday() - amount;
            insertData = dataBaseHelper.addIncome(category, amount, date);




        }

        if (insertData) {
            Toast.makeText(getContext(), "Successfully inserted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
        }
    }
}