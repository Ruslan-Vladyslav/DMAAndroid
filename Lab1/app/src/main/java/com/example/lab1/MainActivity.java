package com.example.lab1;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    Button buttonOk;
    Button buttonCancel;
    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinnerLanguages);
        buttonOk = findViewById(R.id.buttonOk);
        buttonCancel = findViewById(R.id.buttonCancel);
        textResult = findViewById(R.id.textResult);

        String[] languages = {
                "Choose language...",
                "Java",
                "Kotlin",
                "C++",
                "Python",
                "C#",
                "JavaScript"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                languages
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(
                            MainActivity.this,
                            "Please, Choose language!\n" +
                                    "Сomplete entering all data",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    String selectedLanguage = spinner.getSelectedItem().toString();
                    textResult.setText("Chosen Language: " + selectedLanguage);
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textResult.setText("");
                spinner.setSelection(0);
            }
        });
    }
}