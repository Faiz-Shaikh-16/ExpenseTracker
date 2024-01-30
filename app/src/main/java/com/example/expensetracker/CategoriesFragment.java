package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.expensetracker.db.DataBaseHelper;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    private DataBaseHelper dataBaseHelper;
    private ListView lv_Cat;
    static ArrayList<String> categoriesList;
    ArrayAdapter arrayAdapter;

    @SuppressLint("ResourceType")

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,  Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);


        lv_Cat = rootView.findViewById(R.id.list_view_category);
        dataBaseHelper = new DataBaseHelper(getContext());
        categoriesList = dataBaseHelper.getCategoriesList();
        arrayAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                categoriesList);
        lv_Cat.setAdapter(arrayAdapter);

        lv_Cat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int position, final long l) {

                final AlertDialog.Builder removeCatDialog = new AlertDialog.Builder(getContext());
                removeCatDialog.setTitle("Do you want to remove the selected category?");
                removeCatDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String nameOfCat = adapterView.getItemAtPosition(position).toString();
                        dataBaseHelper.deleteCatRow(nameOfCat);
                        Toast.makeText(getContext(), "Category " + nameOfCat + " deleted", Toast.LENGTH_LONG).show();

                        categoriesList = dataBaseHelper.getCategoriesList();
                        arrayAdapter = new ArrayAdapter(getContext(),
                                android.R.layout.simple_list_item_1,
                                categoriesList);
                        lv_Cat.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();

                    }
                });

                removeCatDialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                final AlertDialog dialogRemoveCat = removeCatDialog.create();
                dialogRemoveCat.show();
            }
        });
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_cat) {

            final AlertDialog.Builder addDialogCat = new AlertDialog.Builder(getContext());
            addDialogCat.setTitle("Add new category!");
            final EditText etDialogCat = new EditText(getActivity());
            etDialogCat.setMaxLines(1);
            etDialogCat.setLineHeight(1);
            addDialogCat.setView(etDialogCat);
            addDialogCat.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            addDialogCat.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            final AlertDialog dialog = addDialogCat.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textFromETAddCat = etDialogCat.getText().toString();

                    if (textFromETAddCat.trim().length() == 0) {
                        Toast.makeText(getActivity(), "Please insert some text!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), textFromETAddCat, Toast.LENGTH_SHORT).show();
                        AddCat(textFromETAddCat);
                        categoriesList = dataBaseHelper.getCategoriesList();
                        arrayAdapter = new ArrayAdapter(getContext(),
                                android.R.layout.simple_list_item_1,
                                categoriesList);
                        lv_Cat.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public void AddCat(String newEntry) {
        boolean insertData = dataBaseHelper.addCat(newEntry);
        if (insertData == true)
            Toast.makeText(getContext(), "Successfully inserted!", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
    }
}