package com.example.calculator1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView solutionTextView;
    private TextView resultTextView;
    private StringBuilder input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure R class is correctly generated

        solutionTextView = findViewById(R.id.result_tv);
        resultTextView = findViewById(R.id.solution_tv);
        input = new StringBuilder();

        int[] buttonIDs = {
                R.id.button_c, R.id.button_open_bracket, R.id.button_close_bracket, R.id.button_divide,
                R.id.button_7, R.id.button_8, R.id.button_9, R.id.button_multiply,
                R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_plus,
                R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_minus,
                R.id.button_ac, R.id.button_0, R.id.button_dot, R.id.button_equal
        };

        for (int id : buttonIDs) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();

        switch (buttonText) {
            case "C":
                if (input.length() > 0) {
                    input.deleteCharAt(input.length() - 1);
                }
                break;
            case "AC":
                input.setLength(0);
                break;
            case "=":
                calculateResult();
                return; // Do not update solutionTextView with "="
            default:
                input.append(buttonText);
                break;
        }
        solutionTextView.setText(input.toString());
    }

    private void calculateResult() {
        try {
            double result = evaluateExpression(input.toString());
            resultTextView.setText(String.valueOf(result));
        } catch (Exception e) {
            resultTextView.setText("Error");
        }
    }

    private double evaluateExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();
        char[] tokens = expression.toCharArray();
        int i = 0;

        while (i < tokens.length) {
            if (tokens[i] == ' ') {
                i++;
                continue;
            }

            if (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.') {
                StringBuilder sbuf = new StringBuilder();
                while (i < tokens.length && (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.')) {
                    sbuf.append(tokens[i++]);
                }
                values.push(Double.parseDouble(sbuf.toString()));
                i--;
            } else if (tokens[i] == '(') {
                ops.push(tokens[i]);
            } else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(tokens[i]);
            }
            i++;
        }

        while (!ops.empty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }
}
