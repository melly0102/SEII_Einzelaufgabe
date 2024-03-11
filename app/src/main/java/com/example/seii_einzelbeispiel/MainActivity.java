package com.example.seii_einzelbeispiel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button sendButton;
    private Button calculateButton;
    private TextView answerTextView;
    private EditText inputEditText;

    private final String serverDomain = "se2-submission.aau.at";
    private final int port = 20080;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defineViews();
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String input = inputEditText.getText().toString();
                String regex = "^[0-9]+";

                if (input.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a numerical input", Toast.LENGTH_LONG).show();
                } else if (input.matches(regex)) {
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                socket = new Socket(serverDomain, port);
                                out = new PrintWriter(socket.getOutputStream(), true);
                                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                                //OutPutStream
                                out.println(input);
                                String response = in.readLine();
                                runningUI(response);
                            } catch (IOException ex) {
                                Log.e("MainActivity", "An error occured during socket communication");
                            } finally {
                                try {
                                    if (socket != null) {
                                        socket.close();
                                    }
                                    if (out != null) {
                                        out.close();
                                    }
                                    if (in != null) {
                                        in.close();
                                    }
                                } catch (IOException ex) {
                                    Log.e("MainActivity", "An error occured during socket communication");
                                }
                            }
                        }
                    });
                    thread.start();
                }
            }
        });
        calculateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inputResponse = inputEditText.getText().toString();
                String[] inputValues = inputResponse.split("");
                int[] inputNumbers = new int[inputValues.length];

                for (int i = 0; i < inputValues.length; i++) {
                    inputNumbers[i] = Integer.parseInt(inputValues[i]);
                }
                ArrayList<Integer> sortedNonPrimeList = bubbleSortNonPrimeNumbers(inputNumbers);
                answerTextView.setText(sortedNonPrimeList.toString());
            }
        });
    }
    private void runningUI(String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                answerTextView.setText(response);
            }
        });
    }
    private void defineViews() {
        sendButton = findViewById(R.id.btn_send_mainActivity);
        calculateButton = findViewById(R.id.btn_calculate);
        answerTextView = findViewById(R.id.txt_answer_mainActivity);
        inputEditText = findViewById(R.id.editTextNumber);
    }
    private static ArrayList<Integer> bubbleSortNonPrimeNumbers(int[] array) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (array[i] <= array[j]) {
                    int helper = array[i];
                    array[i] = array[j];
                    array[j] = helper;
                }
            }
        }
        for (int j : array) {
            if (!isPrimeNumber(j)) {
                arrayList.add(j);
            }
        }
        return arrayList;
    }
    private static boolean isPrimeNumber(int num) {
        if (num < 2) {
            return false;
        } else {
            for (int i = 2; i < num; i++) {
                if (num % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
