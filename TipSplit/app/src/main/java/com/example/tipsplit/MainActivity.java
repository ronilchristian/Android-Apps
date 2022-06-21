package com.example.tipsplit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    private EditText totalBill;
    private EditText tipAmount;
    private EditText tipTotal;
    private EditText numPeople;
    private EditText total;
    private EditText overage;
    private RadioGroup rg;

    double bill_with_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalBill = findViewById(R.id.totalBill);
        tipAmount = findViewById(R.id.tipAmount);
        tipTotal = findViewById(R.id.tipTotal);
        numPeople = findViewById(R.id.numPeople);
        total = findViewById(R.id.total);
        overage = findViewById(R.id.overage);
        rg = findViewById(R.id.radioGroup);
    }

    public void radioSelect(View v) {
        String s = totalBill.getText().toString();
        if(s.trim().isEmpty()) {
            rg.clearCheck();
            return;
        }

        double tip = 0;
        double totBill = Double.parseDouble(s);
        if (v.getId() == R.id.radio12) {
            // do something
            tip = totBill * 0.12;
        } else if (v.getId() == R.id.radio15) {
            // do something
            tip = totBill * 0.15;
        } else if (v.getId() == R.id.radio18) {
            // do something
            tip = totBill * 0.18;
        } else if (v.getId() == R.id.radio20) {
            // do something
            tip = totBill * 0.20;
        }

        bill_with_tip = Double.parseDouble(String.format("%.2f", totBill + tip));

        tipAmount.setText(String.format("$%.2f", tip));
        tipTotal.setText(String.format("$%.2f", bill_with_tip));
    }

    public void onGo(View v) {
        String s = numPeople.getText().toString();
        if(s.trim().isEmpty())
            return;

        double num_people = Double.parseDouble(s);
        if(num_people == 0)
            return;

        double total_per_person = (double) Math.ceil(bill_with_tip/num_people * 100) / 100;
        double ovrg = total_per_person*num_people - bill_with_tip;

        total.setText(String.format("$%.2f", total_per_person));
        overage.setText(String.format("$%.2f", ovrg));
    }

    public void onClear(View v) {
        totalBill.setText("");
        tipAmount.setText("");
        tipTotal.setText("");
        numPeople.setText("");
        total.setText("");
        overage.setText("");
        rg.clearCheck();
    }
}