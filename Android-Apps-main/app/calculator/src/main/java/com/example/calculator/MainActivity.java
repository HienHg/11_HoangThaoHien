package com.example.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {
    private TextView editTextText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextText = findViewById(R.id.textViewResult);
        setButtonClickListeners();
        updateEqualButtonState();
        saveLastCalculation();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setButtonClickListeners() {
        int[] buttonIds = {R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.buttonDot, R.id.buttonAC, R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide, R.id.buttonPercent, R.id.buttonParentheses, R.id.buttonEqual, R.id.buttonBack};
        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(view -> onButtonClick(view));
        }
    }

    private void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        switch (buttonText) {
            case "=":
                calculateResult();
                break;
            case "()":
                handleParentheses();
                break;
            case "⌫":
                removeLastInput();
                break;
            case "AC":
                clearInput();
                break;
            default:
                appendInput(buttonText);
                break;
        }
    }

    private void appendInput(String input) {
        editTextText.setText(editTextText.getText().toString() + input);
        updateEqualButtonState();
    }

    private void removeLastInput() {
        String s = editTextText.getText().toString();
        if (s.length() > 0) {
            editTextText.setText(s.substring(0, s.length() - 1));
        }
        updateEqualButtonState();
    }

    private void clearInput() {

        editTextText.setText("");
        updateEqualButtonState();
    }

    private boolean isOpenParentheses = false;
    private void handleParentheses() {
        if (isOpenParentheses) {
            appendInput(")");
            isOpenParentheses = false;
        } else {
            appendInput("(");
            isOpenParentheses = true;
        }
    }

    private void calculateResult() {
        try {
            String expression = editTextText.getText().toString();
            Expression expressionEval = new Expression(expression);
            double result = expressionEval.calculate();
            editTextText.setText(String.valueOf(result));
        } catch (Exception e) {
            editTextText.setText("Error");
        }
    }

    //Kiểm tra hợp lệ
    private boolean isExpressionValid() {
        String expression = editTextText.getText().toString();
        if (expression.isEmpty()) {
            return false;
        }
        // Kiểm tra xem biểu thức có kết thúc bằng toán tử không
        char lastChar = expression.charAt(expression.length() - 1);
        return !(lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/' || lastChar == '.');
    }

    //Cập nhật trạng thái
    private void updateEqualButtonState() {
        Button buttonEqual = findViewById(R.id.buttonEqual);
        buttonEqual.setEnabled(isExpressionValid());
    }

    // Lưu biểu thức và kết quả vào file txt
    private void saveLastCalculation() {
        try {
            FileOutputStream fos = openFileOutput("a.txt", Context.MODE_APPEND );
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(editTextText.getText().toString());
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Khôi phục biểu thức và kết quả từ file txt
    private void loadLastCalculation() {
        try {
            FileInputStream fis = openFileInput("a.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            editTextText.setText(sb.toString());
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
