package com.example.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CustomAdapterTodayListExpenses extends ArrayAdapter<ListModel> {

    public CustomAdapterTodayListExpenses(Context context, int resource, ArrayList<ListModel> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.row, parent, false);

        String amount = getItem(position).getAmount();
        String category = getItem(position).getCategory();
        String date = getItem(position).getDate();

        ListModel listModel = new ListModel(category, amount, date);

        TextView tvCategory = convertView.findViewById(R.id.tv_exp_list1);
        TextView tvAmount = convertView.findViewById(R.id.tv_exp_list2);
        TextView tvDate = convertView.findViewById(R.id.tv_exp_list3Date);

        tvAmount.setText(amount);
        tvCategory.setText(category);
        tvDate.setText(date);

        return convertView;
    }
}

