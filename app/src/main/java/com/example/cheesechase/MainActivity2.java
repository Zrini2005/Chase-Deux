package com.example.cheesechase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity2 extends AppCompatActivity {
    private TextView textView;
    private TextView keyPointsTextView;
    //private ChaseDeuxApi chaseDeuxApi;
    //private ImageView fetchedImageView;
    private static String randomWord;
    private ChaseDeuxApi chaseDeuxApi;
    private static int obstacleLimit;
    private ImageView themeview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button button=findViewById(R.id.startButton);
        themeview=findViewById(R.id.theme);
        button.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.game_over_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            TextView scoreTextView = dialog.findViewById(R.id.score_text_view);
            TextView highScoreTextView = dialog.findViewById(R.id.high_score_text_view);
            scoreTextView.setVisibility(View.GONE);
            highScoreTextView.setVisibility(View.GONE);
            Button playAgainButton = dialog.findViewById(R.id.play_again_button);
            playAgainButton.setText("Swipe Control");
            Button exitButton = dialog.findViewById(R.id.exit_button);
            exitButton.setText("Gesture Control");
            playAgainButton.setOnClickListener(v1 -> {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                intent.putExtra("obstacleLimit",obstacleLimit);
                intent.putExtra("randomWord",randomWord);
                startActivity(intent);
                dialog.dismiss();

            });

            exitButton.setOnClickListener(v1 -> {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                intent.putExtra("obstacleLimit",obstacleLimit);
                intent.putExtra("randomWord",randomWord);
                startActivity(intent);
                dialog.dismiss();

                dialog.dismiss();
            });

            dialog.show();

        });

        /*fetchedImageView = findViewById(R.id.fetchedImageView);
        chaseDeuxApi = ApiClient.getRetrofitInstance().create(ChaseDeuxApi.class);
        fetchImage("obstacle");*/



        chaseDeuxApi = ApiClient.getRetrofitInstance().create(ChaseDeuxApi.class);

        chaseDeuxApi.getObstacleLimit().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject.has("obstacleLimit")) {
                        obstacleLimit = jsonObject.get("obstacleLimit").getAsInt();

                        Toast.makeText(MainActivity2.this, "Obstacle limit: " + obstacleLimit, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity2.this, "Failed to fetch obstacle limit", Toast.LENGTH_SHORT).show();
                            obstacleLimit=2;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("API_CALL", "Failed to fetch obstacle limit: " + t.getMessage());
                t.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity2.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                        obstacleLimit=2;
                    }
                });
            }
        });
        Random rand = new Random();
        int randomNumber = rand.nextInt(11) + 5;
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "{\"length\": " + randomNumber + "}");
        chaseDeuxApi.getRandomWord(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body()!= null) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject.has("word")) {
                        randomWord = jsonObject.get("word").getAsString();
                        Toast.makeText(MainActivity2.this, "Random word: " + randomWord, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity2.this, "Failed to fetch random word", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("API_CALL", "Failed to fetch random word: " + t.getMessage());
                t.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity2.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        RequestBody requestBody2 = RequestBody.create(MediaType.parse("application/json"), "{\"date\": \"" + currentDate + "\", \"time\": \"" + currentTime + "\"}");

         chaseDeuxApi.getTheme(requestBody2).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject.has("theme")) {
                        String theme = jsonObject.get("theme").getAsString();
                        Toast.makeText(MainActivity2.this, "Theme: " + theme, Toast.LENGTH_SHORT).show();
                        if (theme.equals("day")) {
                            themeview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_brightness_high_24, null));
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else if (theme.equals("night")) {
                            themeview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_brightness_3_24, null));
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity2.this, "Failed to fetch theme", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("API_CALL", "Failed to fetch theme: " + t.getMessage());
                t.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity2.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });





        Button button1 = findViewById(R.id.rulesButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity2.this);
                dialog.setContentView(R.layout.ruledialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.setCancelable(false);
                if (R.style.animation!= 0) {
                    dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                }
                Button dialogButton = dialog.findViewById(R.id.button1);
                if (dialogButton!= null) {
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
                dialog.show();
            }
        });
        textView=findViewById(R.id.highScoreTextView);
        loadHighScore();
        keyPointsTextView = findViewById(R.id.keyPointsTextView);

        SharedPreferences sharedPreferences = getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        int keyPoints = sharedPreferences.getInt("key_points", 0);
        keyPointsTextView.setText("Keys: " + keyPoints);


    }
    /*private void fetchImage(String character) {
        chaseDeuxApi.getImage(character).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    fetchedImageView.setImageBitmap(bitmap);
                } else {
                    Log.e("API_CALL", "Failed to fetch image. Response was not successful.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API_CALL", "Failed to fetch image: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void loadFetchedImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(fetchedImageView);
    }*/
    private void loadHighScore() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        int highScore = sharedPreferences.getInt("HighScore", 0);
        textView.setText("High Score: " + highScore);
    }

}