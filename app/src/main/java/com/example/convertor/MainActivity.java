package com.example.convertor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner currency_spinner;
    private EditText input_field;
    private TextView result_field;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private DataBase db = new DataBase(this);
    private ArrayAdapter<String> adapter;
    private Button save_btn, delete_btn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_field = findViewById(R.id.input_field);
        result_field = findViewById(R.id.result_field);
        listView = findViewById(R.id.listView);
        save_btn = findViewById(R.id.save_btn);
        delete_btn = findViewById(R.id.delete_btn);
        sharedPreferences = getPreferences(MODE_PRIVATE);

        loadAllResults();

        currency_spinner = findViewById(R.id.currency_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items, R.layout.spinner_color);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        currency_spinner.setAdapter(adapter);

        input_field.setText(sharedPreferences.getString("value", "0"));
        changeTextAction();

        save_btn.setOnClickListener(view -> saveResult());


    }


    private void changeTextAction() {
        input_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("value", String.valueOf(input_field.getText()));
                editor.apply();
                if (validation()) {
                    result_field.setText(convert());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    @SuppressLint("DefaultLocale")
    public String convert() {
        float num = Float.parseFloat(String.valueOf(input_field.getText()));
        switch (currency_spinner.getSelectedItem().toString()) {
            case "USD" -> {
                float euro = num / 1.07f;
                float idr = num * 15351.99f;
                return String.format("%.2f EURO или %.2f IDR", euro, idr);
            }
            case "EURO" -> {
                float usd = num * 1.07f;
                float idr = num * 16464.21f;
                return String.format("%.2f USD или %.2f IDR", usd, idr);
            }
            case "IDR" -> {
                float usd = num / 15351.99f;
                float euro = num / 16464.21f;
                return String.format("%.2f USD или %.2f EURO", usd, euro);
            }
        }
        return "0";
    }

    public boolean validation() {
        boolean result = false;
        try {
            Float.parseFloat(String.valueOf(input_field.getText()));
            result = true;
        } catch (NumberFormatException e) {
            result_field.setText("Введите число");
        }
        return result;
    }

    private void loadAllResults() {
        List<String> results = db.getData();
        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, R.layout.result_list, R.id.result_text, results);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(results);
            adapter.notifyDataSetChanged();
        }
    }

    private void saveResult() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Добавить результат")
                .setPositiveButton("Добавить", (dialogInterface, i) -> {
                    db.insertData(input_field.getText().toString() +
                            " " + currency_spinner.getSelectedItem().toString() +
                            " - " + result_field.getText().toString());
                    result_field.startAnimation(AnimationUtils.loadAnimation(this, R.anim.add_animation));
                    loadAllResults();
                })
                .setNegativeButton("Отмена", null)
                .create();
        alertDialog.show();
    }

    public void deleteResult(View view) {
        View parent = (View) view.getParent();
        TextView textView = parent.findViewById(R.id.result_text);
        String result = String.valueOf(textView.getText());
        db.deleteData(result);
        loadAllResults();
    }
}
