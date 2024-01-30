package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.expensetracker.db.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReportsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    String getDate;
    int year;
    int month;
    int dayOfMonth;
    TextView tvDate;
    TextView tvTotalAmountDate;
    ListView lvTest;

    Calendar c;
    DatePickerDialog.OnDateSetListener datePickerDialog;
    DataBaseHelper dataBaseHelper;
    CustomAdapterTodayListExpenses custom;
    ArrayList<ListModel> listAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);

        tvDate = rootView.findViewById(R.id.tv_date);
        tvTotalAmountDate = rootView.findViewById(R.id.tv_total_report);
        lvTest = rootView.findViewById(R.id.lv_test);

        dataBaseHelper = new DataBaseHelper(getContext());

        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        month = month + 1;

        datePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                c.set(Calendar.MONTH, monthOfYear);

                updateLabel();
                setListForCertainDate();
            }
        };
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_report_sett, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filter_expensesDate_menu_item) {
            View menuItemView = getActivity().findViewById(R.id.filter_expensesDate_menu_item);
            showPopupMenu(menuItemView);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.setOnMenuItemClickListener(ReportsFragment.this);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.fragment_report_popup, popupMenu.getMenu());
        popupMenu.show();
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMAN);
        tvDate.setText(sdf.format(c.getTime()));
        getDate = tvDate.getText().toString();
        tvTotalAmountDate.setText(dataBaseHelper.totalForCertainDate(getDate) + "");
    }

    private void setListForCertainDate() {
        ArrayList<ListModel> listCertainDate = dataBaseHelper.getCertainDateData(getDate);
        custom = new CustomAdapterTodayListExpenses(getContext(),
                R.layout.row,
                listCertainDate);
        lvTest.setAdapter(custom);
        custom.notifyDataSetChanged();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.today_filter_option) {
            tvDate.setText("Today");
            tvTotalAmountDate.setText(dataBaseHelper.totalForToday() + "");

            ArrayList<ListModel> listToday = dataBaseHelper.getTodayData();
            custom = new CustomAdapterTodayListExpenses(getContext(),
                    R.layout.row,
                    listToday);
            lvTest.setAdapter(custom);
            custom.notifyDataSetChanged();
            return true;
        }
            else if (menuItem.getItemId()==R.id.week_filter_option) {
                tvDate.setText("Last 7 days");
                tvTotalAmountDate.setText(dataBaseHelper.totalForWeek() + "");

                ArrayList<ListModel> listWeek = dataBaseHelper.getLasTWeekData();
                custom = new CustomAdapterTodayListExpenses(getContext(),
                        R.layout.row,
                        listWeek);
                lvTest.setAdapter(custom);
                custom.notifyDataSetChanged();

                return true;
            }

            else if(menuItem.getItemId()==R.id.month_filter_option) {
                tvDate.setText("Last 30 days");
                tvTotalAmountDate.setText(dataBaseHelper.totalForMonth() + "");

                ArrayList<ListModel> listMonth = dataBaseHelper.getLastMonthData();
                custom = new CustomAdapterTodayListExpenses(getContext(),
                        R.layout.row,
                        listMonth);
                lvTest.setAdapter(custom);
                custom.notifyDataSetChanged();

                return true;
            }

            else if(menuItem.getItemId()==R.id.date_filter_option) {
                new DatePickerDialog(getContext(), datePickerDialog, c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.MONTH)).show();
                return true;
            }

            else if(menuItem.getItemId()== R.id.all_filter_option) {
            tvDate.setText("All expenses");
            tvTotalAmountDate.setText(dataBaseHelper.total() + "");

            listAll = dataBaseHelper.getExpensesList();
            custom = new CustomAdapterTodayListExpenses(getContext(),
                    R.layout.row,
                    listAll);
            lvTest.setAdapter(custom);
            custom.notifyDataSetChanged();

            return true;
        }

            else {
            return false;

        }

    }


}
