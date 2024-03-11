package com.example.seii_einzelbeispiel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private Button btn_calc;
    private TextView txtViewAnswer;
    private EditText editText;

    private String server_domain = "se2-submission.aau.at";
    private int port = 20080;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn_send_mainActivity);
        btn_calc = findViewById(R.id.btn_calculate);
        txtViewAnswer = findViewById(R.id.txt_answer_mainActivity);
        editText = findViewById(R.id.editTextNumber);

        //https://stackoverflow.com/questions/10231309/android-button-onclick
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String input = editText.getText().toString();
                String regex = "^[0-9]+";

                if (input.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a numerical input", Toast.LENGTH_SHORT).show();
                } else if (input.matches(regex)) {
                    Toast.makeText(getApplicationContext(), "Button clicked", Toast.LENGTH_LONG).show();
                    //https://www.geeksforgeeks.org/what-are-threads-in-android-with-example/
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                socket = new Socket(server_domain, port);
                                out = new PrintWriter(socket.getOutputStream(), true);
                                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                                //OutPutStream
                                out.println(input);


                                String response = in.readLine();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtViewAnswer.setText(response);
                                        Toast.makeText(getApplicationContext(), "Answer received", Toast.LENGTH_LONG).show();

                                    }
                                });

                                socket.close();

                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }
        });
        btn_calc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String respo = editText.getText().toString();
                String[] inputVals = respo.split("");
                int[] numIn = new int[inputVals.length];

                for (int i = 0; i < inputVals.length; i++) {
                    numIn[i] = Integer.parseInt(inputVals[i]);
                }

                ArrayList<Integer> retList = bubbleSortAlgorithm(numIn);


                txtViewAnswer.setText(retList.toString());


            }
        });

    }

    static ArrayList<Integer> bubbleSortAlgorithm(int[] rr) {
        ArrayList<Integer> arrayList = new ArrayList<>();

        for (int i = 0; i < rr.length; i++) {
            for (int j = 0; j < rr.length; j++) {
                if (rr[i] <= rr[j]) {
                    int helper = rr[i];
                    rr[i] = rr[j];
                    rr[j] = helper;
                }
            }
        }
        for (int i = 0; i < rr.length; i++) {
            if (!isPrime(rr[i])) {

                arrayList.add(rr[i]);
            }
        }
        return arrayList;
    }
    // https://java.soeinding.de/content.php/primzahl

    static boolean isPrime(int num) {
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
