package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Quiz1 extends AppCompatActivity {
    private Button bNext;
    private RadioGroup rg;
    private RadioButton rb;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private static final long TIME_LIMIT = 30000; // 30 secondes
    private int score = 0;
    private final String CorrectResp = "Thomas Alva Edison";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);

        bNext = findViewById(R.id.next_button);
        rg = findViewById(R.id.answers);
        timerTextView = findViewById(R.id.timerTextView);

        startTimer();

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Veuillez choisir une réponse SVP", Toast.LENGTH_SHORT).show();
                } else {
                    rb = findViewById(rg.getCheckedRadioButtonId());
                    if (rb.getText().toString().equals(CorrectResp)) {
                        score += 1;
                    }

//                    if (countDownTimer != null) {
//                        countDownTimer.cancel();
//                    }

                    Intent i1 = new Intent(Quiz1.this, Quiz2.class);
                    i1.putExtra("score", score);
                    startActivity(i1);
                    overridePendingTransition(R.anim.exit, R.anim.entry);
                    finish();
                }
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Temps restant : " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                if (rg.getCheckedRadioButtonId() == -1) {
                    // Aucune réponse sélectionnée, passez à la question suivante
                    Intent i1 = new Intent(Quiz1.this, Quiz2.class);
                    i1.putExtra("score", score);
                    startActivity(i1);
                    overridePendingTransition(R.anim.exit, R.anim.entry);
                    finish();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
