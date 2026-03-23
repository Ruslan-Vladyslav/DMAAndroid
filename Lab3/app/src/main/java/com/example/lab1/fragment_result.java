package com.example.lab1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_result#} factory method to
 * create an instance of this fragment.
 */
public class fragment_result extends Fragment {

    TextView textResult;
    Button buttonCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        textResult = view.findViewById(R.id.textResult);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Отримуємо дані
        String language = getArguments().getString("language");

        // Виводимо результат
        textResult.setText("Chosen: " + language);

        buttonCancel.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new fragment_input())
                    .commit();
        });
    }
}