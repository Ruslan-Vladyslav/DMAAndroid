package com.example.lab1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import android.content.Intent;

public class fragment_input extends Fragment {

    Spinner spinner;
    Button buttonOk;
    TextView textError;

    /**
     * A simple {@link Fragment} subclass.
     * Use the {@link fragment_result#} factory method to
     * create an instance of this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_input, container, false);

        spinner = view.findViewById(R.id.spinnerLanguages);
        buttonOk = view.findViewById(R.id.buttonOk);
        textError = view.findViewById(R.id.textError);

        String[] languages = {"Choose...", "Java", "Kotlin", "C++", "C#", "Python"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                languages
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    textError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        buttonOk.setOnClickListener(v -> {

            if (spinner.getSelectedItemPosition() == 0) {
                textError.setText("Choose language!");
                textError.setVisibility(View.VISIBLE);

            } else {
                textError.setVisibility(View.GONE);

                String selected = spinner.getSelectedItem().toString();
                saveToDB(selected);

                fragment_result resultFragment = new fragment_result();

                Bundle bundle = new Bundle();
                bundle.putString("language", selected);
                resultFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, resultFragment)
                        .commit();
            }
        });

        Button buttonOpen = view.findViewById(R.id.buttonOpen);

        buttonOpen.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StorageActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void saveToDB(String text) {
        Database dbHelper = new Database(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", text);

        db.insert("languages", null, values);
        db.close();

        Toast.makeText(getContext(), "Saved successfully!", Toast.LENGTH_SHORT).show();
    }
}