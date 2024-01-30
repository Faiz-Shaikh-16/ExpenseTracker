package com.example.expensetracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TodayFragment extends Fragment {

    Spinner spinnerDialog;
    EditText etAmount;
    EditText etDate;
    ListView lvExpenses;
    Button buttonAddExpense;
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
        tvGetToday = rootview.findViewById(R.id.tv_total_amount);
        tvTitle = rootview.findViewById(R.id.tv_today_title);
        lvExpenses = rootview.findViewById(R.id.lv_fragm_today);

        categoriesListForSpinner = new ArrayList<>();

        tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvGetToday.setText(dataBaseHelper.totalForToday() + "");
        expensesList = dataBaseHelper.getTodayData();
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

        return rootview;


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

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.MONTH)).show();
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
        boolean insertData = dataBaseHelper.addExp(category, amount, total, date);

        if (insertData == true)
            Toast.makeText(getContext(), "Successfully inserted!", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
    }
}
