package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
public class Score extends AppCompatActivity {
    Button logout,replay;
    ProgressBar progressBar;
    TextView nombrescore;
    int score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);
        logout=findViewById(R.id.bLogout);
        replay=findViewById(R.id.btry_again);
        progressBar = findViewById(R.id.progressBar);
        nombrescore=findViewById(R.id.nombrescore);

        Intent i1=getIntent();
        score= i1.getIntExtra("score",0);
        nombrescore.setText(score*100/5+"%");
        progressBar.setProgress(score*100/5);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Merci pour votre participation",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Score.this,MainActivity.class));
                finish();
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Score.this, Quiz1.class));
            }
        });
    }
}