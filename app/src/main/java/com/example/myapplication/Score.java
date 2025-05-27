package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Score extends AppCompatActivity {
    private static final String TAG = "ScoreActivity";

    Button logout, replay;
    ProgressBar progressBar;
    TextView nombrescore;
    ListView listViewScores;
    ArrayAdapter<String> adapter;
    ArrayList<String> bestScoresList = new ArrayList<>();

    int score;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Initialisation des composants
        initViews();

        // Initialisation Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Récupération du score depuis l'Intent
        Intent i1 = getIntent();
        score = i1.getIntExtra("score", 0);

        // Affichage du score
        displayScore();

        // Gestion des scores Firebase
        handleFirebaseOperations();

        // Configuration des listeners
        setupClickListeners();
    }

    private void initViews() {
        logout = findViewById(R.id.bLogout);
        replay = findViewById(R.id.btry_again);
        progressBar = findViewById(R.id.progressBar);
        nombrescore = findViewById(R.id.nombrescore);
        listViewScores = findViewById(R.id.listViewScores);

        // Configuration de l'adapter pour la ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bestScoresList);
        listViewScores.setAdapter(adapter);
    }

    private void displayScore() {
        int scorePercentage = (score * 100) / 5; // Calcul du pourcentage
        nombrescore.setText(scorePercentage + "%");
        progressBar.setProgress(scorePercentage);

        Log.d(TAG, "Score reçu: " + score + ", Pourcentage: " + scorePercentage + "%");
    }

    private void handleFirebaseOperations() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // Vérifier d'abord si le document existe, sinon le créer
            checkAndCreateUserDocument(userId);

        } else {
            Toast.makeText(getApplicationContext(), "Utilisateur non connecté ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndCreateUserDocument(String userId) {
        db.collection("Scores").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Le document existe, ajouter le score
                        addScoreToFirestore(userId);
                        loadBestScores(userId);
                    } else {
                        // Le document n'existe pas, le créer d'abord
                        createUserDocument(userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors de la vérification du document", e);
                    // Essayer de créer le document quand même
                    createUserDocument(userId);
                });
    }

    private void createUserDocument(String userId) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("scores", new ArrayList<>());

        db.collection("Scores").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Document utilisateur créé avec succès");
                    addScoreToFirestore(userId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors de la création du document utilisateur", e);
                    Toast.makeText(getApplicationContext(), "Erreur lors de l'initialisation ❌", Toast.LENGTH_SHORT).show();
                });
    }

    private void addScoreToFirestore(String userId) {
        int scorePercentage = (score * 100) / 5;

        Map<String, Object> scoreData = new HashMap<>();
        scoreData.put("score", scorePercentage);
        scoreData.put("timestamp", System.currentTimeMillis()); // Ajouter un timestamp

        db.collection("Scores")
                .document(userId)
                .update("scores", FieldValue.arrayUnion(scoreData))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Score ajouté avec succès: " + scorePercentage + "%");
                    Toast.makeText(getApplicationContext(), "Score ajouté ✅", Toast.LENGTH_SHORT).show();
                    // Recharger les scores après ajout
                    loadBestScores(userId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors de l'ajout du score", e);
                    Toast.makeText(getApplicationContext(), "Erreur lors de l'ajout du score ❌", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadBestScores(String userId) {
        db.collection("Scores").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> scores = (List<Map<String, Object>>) documentSnapshot.get("scores");
                        if (scores != null && !scores.isEmpty()) {
                            updateBestScoresList(scores);
                        } else {
                            Log.d(TAG, "Aucun score trouvé pour l'utilisateur");
                        }
                    } else {
                        Log.d(TAG, "Document utilisateur non trouvé");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur de récupération des scores", e);
                    Toast.makeText(getApplicationContext(), "Erreur lors du chargement des scores ❌", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateBestScoresList(List<Map<String, Object>> scores) {
        // Vider la liste actuelle
        bestScoresList.clear();

        // Extraire les valeurs de score
        List<Integer> scoreValues = new ArrayList<>();
        for (Map<String, Object> scoreMap : scores) {
            Object scoreObj = scoreMap.get("score");
            if (scoreObj != null) {
                int scoreValue;
                if (scoreObj instanceof Long) {
                    scoreValue = ((Long) scoreObj).intValue();
                } else if (scoreObj instanceof Integer) {
                    scoreValue = (Integer) scoreObj;
                } else {
                    continue; // Ignorer les types non supportés
                }
                scoreValues.add(scoreValue);
            }
        }

        // Trier par ordre décroissant
        Collections.sort(scoreValues, Collections.reverseOrder());

        // Prendre les 3 meilleurs scores
        int limit = Math.min(3, scoreValues.size());
        for (int i = 0; i < limit; i++) {
            bestScoresList.add((i + 1) + " - " + scoreValues.get(i) + "%");
        }

        // Notifier l'adapter
        runOnUiThread(() -> adapter.notifyDataSetChanged());

        Log.d(TAG, "Liste des meilleurs scores mise à jour: " + bestScoresList.size() + " éléments");
    }

    private void setupClickListeners() {
        logout.setOnClickListener(v -> {
            // Déconnexion de l'utilisateur
            auth.signOut();
            Toast.makeText(getApplicationContext(), "Merci pour votre participation", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Score.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        replay.setOnClickListener(v -> {
            Intent intent = new Intent(Score.this, QuizActivity.class);
            startActivity(intent);
            finish(); // Fermer l'activité actuelle pour éviter l'accumulation
        });

        // Configuration du RatingBar et du bouton de soumission
        setupRatingFunctionality();
    }

    private void setupRatingFunctionality() {
        RatingBar ratingBar = findViewById(R.id.ratingBar2);
        Button submitRating = findViewById(R.id.submitRating);

        if (ratingBar != null && submitRating != null) {
            ratingBar.setNumStars(5);

            submitRating.setOnClickListener(v -> {
                float rating = ratingBar.getRating();

                if (rating == 0) {
                    Toast.makeText(getApplicationContext(), "Veuillez sélectionner une note", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (auth.getCurrentUser() != null) {
                    String userId = auth.getCurrentUser().getUid();
                    Map<String, Object> ratingData = new HashMap<>();
                    ratingData.put("rating", rating);
                    ratingData.put("timestamp", System.currentTimeMillis());

                    db.collection("Ratings")
                            .document(userId)
                            .set(ratingData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getApplicationContext(), "Note enregistrée ✅", Toast.LENGTH_SHORT).show();
                                submitRating.setEnabled(false); // Empêcher les soumissions multiples
                                submitRating.setText("Note enregistrée");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erreur lors de l'enregistrement de la note", e);
                                Toast.makeText(getApplicationContext(), "Erreur lors de l'enregistrement de la note ❌", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Utilisateur non connecté ❌", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}