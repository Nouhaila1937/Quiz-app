package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Quiz5 extends AppCompatActivity {
    Button bNext;
    RadioGroup rg;
    RadioButton rb;
    int score;
    String CorrectResp="Rabat";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz5);
        // Récupérer le score passé depuis l'activité précédente
        if (getIntent().hasExtra("score")) {
            score = getIntent().getIntExtra("score", 0);
        }
        bNext=findViewById(R.id.next_button);
        rg=findViewById(R.id.answers);
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rg.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(), "Veuillez choisir une réponse SVP", Toast.LENGTH_SHORT).show();
                }else{
                    rb=findViewById(rg.getCheckedRadioButtonId());
                    if(rb.getText().toString().equals(CorrectResp))
                        score+=1;

                Intent i1=new Intent(Quiz5.this, Score.class);
                i1.putExtra("score",score);
                startActivity(i1);
            }
            }
        });
    }
}