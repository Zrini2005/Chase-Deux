package com.example.cheesechase;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.widget.Toast;


import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity3 extends AppCompatActivity {
    private GameView2 gameView;
    private TextView scoreTextView;
    private TextView loading;
    private static TextView collectedLettersTextView;
    private ImageButton leftPowerUpButton;
    private ImageButton rightPowerUpButton;
    private ImageView button, home;
    private int keyPoints;
    private ProgressBar loadingBar;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Queue<Obstacle> obstaclePool = new LinkedList<Obstacle>();
    private Queue<Cheese> cheesePool = new LinkedList<Cheese>();
    private Queue<Key> keyPool = new LinkedList<Key>();
    private Random random = new Random();
    private ChaseDeuxApi chaseDeuxApi;
    private static int obstacleLimit;
    private LinearLayout heartsLayout;
    private static ImageView[] hearts;
    private static String randomWord;
    public static List<Character> collectedLetters = new ArrayList<>();
    public static int ol;

    public static void decreaselife() {
        ol--;
        updateHearts(ol);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        scoreTextView = findViewById(R.id.scoreTextView);
        loading=findViewById(R.id.loading);


        loadingBar = findViewById(R.id.loadingBar);
        button = findViewById(R.id.xbutton);
        leftPowerUpButton = findViewById(R.id.leftPowerUpButton);
        rightPowerUpButton = findViewById(R.id.rightPowerUpButton);
        home = findViewById(R.id.home);

        heartsLayout = findViewById(R.id.heartsLayout);
        collectedLettersTextView = findViewById(R.id.collectedLettersTextView);
        Intent intent2 = getIntent();
        obstacleLimit = intent2.getIntExtra("obstacleLimit",2);
        ol=obstacleLimit;
        randomWord = intent2.getStringExtra("randomWord");
        initializeHearts();



        executorService.execute(() -> {
            preGenerateObstacles(20,10);

            runOnUiThread(() -> {
                gameView = findViewById(R.id.gameView2);
                gameView.initWithObstaclePool(this, obstaclePool, obstacleLimit, cheesePool, randomWord, keyPool);
                gameView.setScoreTextView(scoreTextView);
                loadingBar.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                gameView.setVisibility(View.VISIBLE);
                gameView.resume();
                if (keyPoints >= 2) {
                    showPowerUpButtons();
                } else {
                    hidePowerUpButtons();
                }

            });
        });

        button.setOnClickListener(v -> {
            if (!gameView.isPaused()) {
                pauseGame();
                button.setImageResource(R.drawable.playbutton);
            } else {
                resumeGame();
                button.setImageResource(R.drawable.pausebutton);
            }
        });

        SharedPreferences sharedPreferences2 = getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        keyPoints = sharedPreferences2.getInt("key_points", 0);



        home.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
            startActivity(intent);
        });

        leftPowerUpButton.setOnClickListener(v -> {
            if (keyPoints >= 2) {
                gameView.makeJerryImmune();
                keyPoints -= 2;
                updateKeyPoints();
                hidePowerUpButtons();
            }
        });

        rightPowerUpButton.setOnClickListener(v -> {
            if (keyPoints >= 2) {
                gameView.giveJerryCheeseAndGun();
                keyPoints -= 2;
                updateKeyPoints();
                hidePowerUpButtons();
            }
        });


    }
    private void initializeHearts() {
        int maxHearts = obstacleLimit;
        hearts = new ImageView[maxHearts];
        heartsLayout.removeAllViews();
        for (int i = 0; i < maxHearts; i++) {
            hearts[i] = new ImageView(this);
            hearts[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            hearts[i].setImageResource(R.drawable.heart);
            heartsLayout.addView(hearts[i]);
        }
    }

    public static void updateHearts(int lives) {
        for (int i = 0; i < hearts.length; i++) {
            if (i < lives) {
                hearts[i].setImageResource(R.drawable.heart);
            } else {
                hearts[i].setImageResource(R.drawable.heartless);
            }
        }
    }

    private void preGenerateObstacles(int count,int count2) {
        for (int i = 0; i < count; i++) {
            int lane = random.nextInt(3);
            obstaclePool.add(new Obstacle(this, lane, 0));
            if(i < count2){
                cheesePool.add(new Cheese(this, lane, 0));
            }
            if(i < count2){
                keyPool.add(new Key(this, lane, 0));
            }
            // Update loading bar
            final int progress = (i * 100) / count;
            runOnUiThread(() -> loadingBar.setProgress(progress));
        }
    }

    public void showPowerUpButtons() {
        leftPowerUpButton.setVisibility(View.VISIBLE);
        rightPowerUpButton.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            leftPowerUpButton.setVisibility(View.GONE);
            rightPowerUpButton.setVisibility(View.GONE);
        }, 10000);
    }

    public void hidePowerUpButtons() {
        leftPowerUpButton.setVisibility(View.GONE);
        rightPowerUpButton.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void pauseGame() {
        gameView.pause();
    }

    private void resumeGame() {
        gameView.resume();
    }

    private void updateKeyPoints() {
        SharedPreferences sharedPreferences2 = getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putInt("key_points", keyPoints);
        editor2.apply();
    }
    public static void onLetterCollected(char letter) {
        collectedLetters.add(letter);
        updateCollectedLettersTextView();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                collectedLettersTextView.setVisibility(View.VISIBLE);
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                collectedLettersTextView.setVisibility(View.GONE);
            }
        }, 3000);}

    private static void updateCollectedLettersTextView() {
        StringBuilder displayedText = new StringBuilder();
        for (int i = 0; i < randomWord.length(); i++) {
            if (i < collectedLetters.size()) {
                displayedText.append(collectedLetters.get(i));
            } else {
                displayedText.append(" _");
            }
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                collectedLettersTextView.setText(displayedText.toString());
            }
        });
    }
    public static void letterOver(){
        collectedLettersTextView.setText("YOU GET 25 CHEESES!!!");
    }

}
