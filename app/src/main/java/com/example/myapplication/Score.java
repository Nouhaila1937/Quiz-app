package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

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
        // Code pour enregistrer le score dans Firestore

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Vérifie si l'utilisateur est connecté
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();  // récupère l'UID de l'utilisateur connecté

            // Créer un score à ajouter dans Firestore
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("score", score*100/5);  // Score de l'utilisateu

            // Ajouter ce score au tableau de scores de l'utilisateur
            db.collection("Scores")
                    .document(userId)
                    .update("scores", FieldValue.arrayUnion(scoreData))  // Ajouter le score au tableau 'scores'
                    .addOnSuccessListener(aVoid -> {
                        // Affiche un message de succès
                        Toast.makeText(getApplicationContext(), "Score ajouté ✅", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Affiche l'exception dans les logs pour mieux comprendre l'erreur
                        Log.e("Firestore Error", "Erreur lors de l'ajout du score", e);
                        Toast.makeText(getApplicationContext(), "Erreur lors de l'ajout du score ❌", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Utilisateur non connecté ❌", Toast.LENGTH_SHORT).show();
        }

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