package com.example.lab1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import android.widget.Button;

public class StorageActivity extends AppCompatActivity {

    ListView listView;
    Button buttonClear;
    Button buttonBack;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        listView = findViewById(R.id.listViewData);
        buttonClear = findViewById(R.id.buttonClear);
        buttonBack = findViewById(R.id.buttonBack);

        loadData();

        buttonClear.setOnClickListener(v -> clearData());
        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadData() {
        list = new ArrayList<>();

        Database dbHelper = new Database(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM languages", null);

        if (cursor.getCount() == 0) {
            list.add("No data available");
        } else {
            while (cursor.moveToNext()) {
                int index = 1;

                while (cursor.moveToNext()) {
                    String name = cursor.getString(1);
                    list.add(index + ". " + name);
                    index++;
                }
            }
        }

        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                list
        );

        listView.setAdapter(adapter);
    }

    private void clearData() {
        Database dbHelper = new Database(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM languages");
        db.close();

        list.clear();
        list.add("No data available");

        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
    }
}