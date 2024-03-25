package com.example.contractorcalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextLaborCost, editTextMaterialCost;
    private TextView textViewSubTotal, textViewTotal, textViewTax, textViewTaxRateValue;

    private static final double DEFAULT_TAX_RATE = 0.05;
    private static final String PREFS_FILE_NAME = "MyPrefs";
    private static final String TAX_RATE_KEY = "taxRate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextLaborCost = findViewById(R.id.editTextLaborCost);
        editTextMaterialCost = findViewById(R.id.editTextMaterialCost);
        textViewSubTotal = findViewById(R.id.textViewSubTotal);
        textViewTax = findViewById(R.id.textViewTax);
        textViewTotal = findViewById(R.id.textViewTotal);
        textViewTaxRateValue = findViewById(R.id.textViewTaxRateValue);

        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotal();
            }
        });

        Button buttonChangeRate = findViewById(R.id.buttonChangeRate);
        buttonChangeRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeRateDialog();
            }
        });

        float savedTaxRate = loadTaxRate();
        textViewTaxRateValue.setText(String.format(Locale.getDefault(), "%.2f%%", savedTaxRate * 100));
    }

    private void calculateTotal() {
        double laborCost = Double.parseDouble(editTextLaborCost.getText().toString());
        double materialCost = Double.parseDouble(editTextMaterialCost.getText().toString());

        double subTotal = laborCost + materialCost;
        textViewSubTotal.setText(String.format("%.2f", subTotal));

        double tax = subTotal * loadTaxRate();
        textViewTax.setText(String.format("%.2f", tax));

        double total = subTotal + tax;
        textViewTotal.setText(String.format("%.2f", total));
    }

    private void showChangeRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Tax Rate");

        View view = getLayoutInflater().inflate(R.layout.dialog_change_rate, null);
        final EditText editTextNewTaxRate = view.findViewById(R.id.editTextNewTaxRate);
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTaxRateStr = editTextNewTaxRate.getText().toString();
                if (!newTaxRateStr.isEmpty()) {
                    float newTaxRate = Float.parseFloat(newTaxRateStr) / 100; // Convert to decimal
                    saveTaxRate(newTaxRate);
                    textViewTaxRateValue.setText(String.format(Locale.getDefault(), "%.2f%%", newTaxRate * 100));
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void saveTaxRate(float taxRate) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).edit();
        editor.putFloat(TAX_RATE_KEY, taxRate);
        editor.apply();
    }

    private float loadTaxRate() {
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        return prefs.getFloat(TAX_RATE_KEY, (float) DEFAULT_TAX_RATE);
    }
}
