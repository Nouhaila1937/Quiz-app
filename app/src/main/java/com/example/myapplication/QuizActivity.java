package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    // Classe pour représenter une question
    public static class Question {
        private String questionText;
        private int imageResource;
        private String[] options;
        private String correctAnswer;

        public Question(String questionText, int imageResource, String[] options, String correctAnswer) {
            this.questionText = questionText;
            this.imageResource = imageResource;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        // Getters
        public String getQuestionText() { return questionText; }
        public int getImageResource() { return imageResource; }
        public String[] getOptions() { return options; }
        public String getCorrectAnswer() { return correctAnswer; }
    }

    // Variables UI
    private Button bNext;
    private RadioGroup rg;
    private RadioButton rb;
    private TextView timerTextView;
    private TextView questionNumberTextView;
    private TextView questionTextView;
    private ImageView questionImageView;
    private CountDownTimer countDownTimer;

    // Variables du quiz
    private static final long TIME_LIMIT = 30000; // 30 secondes
    private int score = 0;
    private int currentQuestionIndex = 0;
    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizdynamique);

        // Initialisation des vues
        initViews();

        // Initialisation des questions
        initQuestions();

        // Afficher la première question
        displayCurrentQuestion();

        // Démarrer le timer
        startTimer();

        // Listener pour le bouton suivant
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNextButtonClick();
            }
        });
    }

    private void initViews() {
        bNext = findViewById(R.id.next_button);
        rg = findViewById(R.id.answers);
        timerTextView = findViewById(R.id.timerTextView);
        questionNumberTextView = findViewById(R.id.question_number);
        questionTextView = findViewById(R.id.question_text);
        questionImageView = findViewById(R.id.imageView);
    }

    private void initQuestions() {
        questions = new ArrayList<>();

        // Question 1
        questions.add(new Question(
                "Qui est l'inventeur de dynamite?",
                R.drawable.dynamite,
                new String[]{"Thomas Alva Edison", "Alfred Bernard Nobel", "Nicola Tesla"},
                "Thomas Alva Edison"
        ));

        // Question 2
        questions.add(new Question(
                "Quel est le premier homme à avoir marché sur la Lune ?",
                R.drawable.astronaut,
                new String[]{"Neil Armstrong", "Buzz Aldrin", "Yuri Gagarin"},
                "Neil Armstrong"
        ));

        // Question 3
        questions.add(new Question(
                "Quel est l'organe principal du système nerveux central ?",
                R.drawable.organ,
                new String[]{"Le cœur", "La foie", "Le cerveau"},
                "Le cerveau"
        ));

        // Question 4
        questions.add(new Question(
                "Qui a peint le célèbre tableau La Joconde ?",
                R.drawable.joconde,
                new String[]{"Léonard de Vinci", "Michel-Ange", "Raphaël"},
                "Léonard de Vinci"
        ));

        // Question 5
        questions.add(new Question(
                "Quelle est la capitale administrative du Maroc ?",
                R.drawable.maroc,
                new String[]{"Rabat", "Casablanca", "Marrakech"},
                "Rabat"
        ));
    }

    private void displayCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);

            // Mettre à jour le numéro de question
            questionNumberTextView.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.size());

            // Mettre à jour le texte de la question
            questionTextView.setText(currentQuestion.getQuestionText());

            // Mettre à jour l'image
            questionImageView.setImageResource(currentQuestion.getImageResource());

            // Mettre à jour les options de réponse
            String[] options = currentQuestion.getOptions();
            RadioButton option1 = findViewById(R.id.option1);
            RadioButton option2 = findViewById(R.id.option2);
            RadioButton option3 = findViewById(R.id.option3);

            option1.setText(options[0]);
            option2.setText(options[1]);
            option3.setText(options[2]);

            // Réinitialiser la sélection
            rg.clearCheck();

            // Mettre à jour le texte du bouton
            if (currentQuestionIndex == questions.size() - 1) {
                bNext.setText("TERMINÉ");
            } else {
                bNext.setText("SUIVANT");
            }
        }
    }

    private void handleNextButtonClick() {
        if (rg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Veuillez choisir une réponse SVP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Arrêter le timer actuel
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Vérifier la réponse
        checkAnswer();

        // Passer à la question suivante ou terminer le quiz
        currentQuestionIndex++;

        if (currentQuestionIndex < questions.size()) {
            // Il y a encore des questions
            displayCurrentQuestion();
            startTimer();
        } else {
            // Quiz terminé, aller à l'écran de score
            goToScoreActivity();
        }
    }

    private void checkAnswer() {
        rb = findViewById(rg.getCheckedRadioButtonId());
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (rb != null && rb.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
            score += 1;
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Temps restant : " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                // Temps écoulé, passer à la question suivante automatiquement
                if (rg.getCheckedRadioButtonId() != -1) {
                    checkAnswer();
                }

                currentQuestionIndex++;

                if (currentQuestionIndex < questions.size()) {
                    displayCurrentQuestion();
                    startTimer();
                } else {
                    goToScoreActivity();
                }
            }
        }.start();
    }

    private void goToScoreActivity() {
        Intent intent = new Intent(QuizActivity.this, Score.class);
        intent.putExtra("score", score);
        startActivity(intent);
        overridePendingTransition(R.anim.exit, R.anim.entry);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}