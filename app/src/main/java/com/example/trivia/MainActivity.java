package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.graphics.drawable.AnimatedImageDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView cardText;
    private TextView questionCounterText;
    private Button trueButton;
    private Button falseButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private TextView scoreText;
    private TextView highScoreText;
    private int curreQueIndex=0;
    private List<Question> questionList;
    private int scoreCounter = 0;

    private Score score;

    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        score = new Score();
        prefs = new Prefs(MainActivity.this);
        Log.d("high score", "onCreate: " + prefs.getHighScore());

        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        scoreText = findViewById(R.id.score_text);
        questionCounterText = findViewById(R.id.counter_text);
        cardText = findViewById(R.id.question_text_view);
        highScoreText = findViewById(R.id.high_score_text);


        scoreText.setText("Current Score: " + String.valueOf(score.getScore()));

        highScoreText.setText("Higest Score: " + String.valueOf(prefs.getHighScore()));

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);


        questionList = new QuestionBank().getQuestion(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                cardText.setText(questionArrayList.get(curreQueIndex).getAnswer());
                questionCounterText.setText(curreQueIndex + "/" + questionArrayList.size());

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.prev_button:
                if (curreQueIndex > 0) {
                    curreQueIndex = (curreQueIndex - 1) % questionList.size();
                    updateQuestion();
                }

                break;
            case R.id.next_button:

                curreQueIndex = (curreQueIndex+1) % questionList.size();
                updateQuestion();

                //Log.d("pref", "onClick: " + prefs.getHighScore());
                break;

            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;

        }
    }

    private void updateQuestion(){

        cardText.setText(questionList.get(curreQueIndex).getAnswer());
        questionCounterText.setText(curreQueIndex + "/" + questionList.size());
    }
    private void checkAnswer(boolean user_input){

        boolean answer = questionList.get(curreQueIndex).isTrue();

        if(answer == user_input) {
            Toast.makeText(MainActivity.this, "Correct Answer", Toast.LENGTH_SHORT).show();
            addPoints();

            fadeView();
        }
        else {
            shakeAnimation();
            deductPoint();

            Toast.makeText(MainActivity.this,
                    "Wrong Answer", Toast.LENGTH_SHORT).
                    show();

        }
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                curreQueIndex = (curreQueIndex+1) % questionList.size();
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                    cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                curreQueIndex = (curreQueIndex+1) % questionList.size();
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void    addPoints(){

        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreText.setText(String.valueOf(score.getScore()));
        Log.d("ADD SCORE ", "addPointss: " + score.getScore() );
    }
    private void deductPoint(){

        scoreCounter -= 100;
        if(scoreCounter > 0) {
            score.setScore(scoreCounter );

        }

        else{
            scoreCounter = 0;
            score.setScore(scoreCounter );

        }
        scoreText.setText(String.valueOf(score.getScore()));

        Log.d("Deduct SCORE ", "dedPointss: " + score.getScore() );

    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.saveHighScore(score.getScore());
    }
}
