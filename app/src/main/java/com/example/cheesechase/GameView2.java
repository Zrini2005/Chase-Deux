package com.example.cheesechase;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameView2 extends SurfaceView implements Runnable {
    private Thread gameThread;
    private boolean isPlaying;
    private Paint paint;
    private SurfaceHolder surfaceHolder;
    private Jerry jerry;
    private Tom tom;

    private long tomAppearTime;
    private boolean tomFollowing;
    private List<Obstacle> obstacles;
    private List<Cheese> cheeses;
    private List<Key> keys;
    private List<Bullet> bullets;
    private Random random;
    private int cheesePoints;
    private boolean hasGun;
    private int score;
    private int highScore;
    private int obstaclesHit;
    private long lastObstacleTime;
    private long lastKeyTime;
    private long lastCheeseTime;
    private long ObstacleTime=2000;
    private long CheeseTime=5000;
    private float initialX;
    private float initialY;
    private int laneWidth;
    private int separatorWidth;
    private int separatorColor = Color.YELLOW;
    private TextView scoreTextView;
    private MediaPlayer mediaPlayer;
    private Bitmap laneImage;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private long lastTapTime = 0;
    private List<Explosion> explosions;
    private Bitmap[] roadSegments;
    private int roadSegment;
    private long lastRoadTime;
    private boolean isPaused = false;
    private int lastScoreIncrement = 0;
    private float speedMultiplier = 1.0f;

    private Queue<Obstacle> obstaclePool;

    private int keyPoints;
    private boolean isJerryImmune;
    private long immunityEndTime;
    private SoundPool soundPool;
    private int pows;
    private int swipe;
    private int explo;
    private int obstacleLimit;
    private Queue<Cheese> cheesePool;
    private Queue<Key> keyPool;
    private List<Letter> letters;
    private String currentWord;
    private int currentLetterIndex;
    private long lastLetterTime;
    private final long LetterTime = 10000;
    public GameView2(Context context) {
        super(context);
        init(context);
    }

    public GameView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public void initWithObstaclePool(Context context, Queue<Obstacle> obstaclePool,int ob,Queue<Cheese> cheesePool,String s,Queue<Key> keyPool) {
        this.obstaclePool = obstaclePool;
        this.cheesePool=cheesePool;
        this.keyPool=keyPool;
        this.obstacleLimit=ob;
        this.currentWord=s;
        init(context);
    }

    private void init(Context context) {
        surfaceHolder = getHolder();
        paint = new Paint();
        jerry = new Jerry(getContext());
        tom = new Tom(getContext());
        obstacles = new ArrayList<>();



        random = new Random();
        score = 0;
        highScore = 0;
        obstaclesHit = 0;
        lastObstacleTime = System.currentTimeMillis();
        tomAppearTime = 0;
        tomFollowing = false;

        laneWidth = getResources().getDisplayMetrics().widthPixels / 3;
        separatorWidth = 20;
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();
        swipe = soundPool.load(this.getContext(), R.raw.jerryjmp, 1);
        pows = soundPool.load(this.getContext(), R.raw.powerup, 1);

        explo=soundPool.load(this.getContext(), R.raw.explosionsound, 1);

        roadSegments = new Bitmap[4];
        roadSegments[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.road1);
        roadSegments[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.road2);
        roadSegments[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.road3);
        roadSegments[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.road4);
        roadSegments[0] = Bitmap.createScaledBitmap(roadSegments[0], laneWidth, getResources().getDisplayMetrics().heightPixels, false);
        roadSegments[1] = Bitmap.createScaledBitmap(roadSegments[1], laneWidth, getResources().getDisplayMetrics().heightPixels, false);
        roadSegments[2] = Bitmap.createScaledBitmap(roadSegments[2], laneWidth, getResources().getDisplayMetrics().heightPixels, false);
        roadSegments[3] = Bitmap.createScaledBitmap(roadSegments[3], laneWidth, getResources().getDisplayMetrics().heightPixels, false);
        roadSegment=0;


        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
        cheesePoints = 0;
        hasGun = false;
        cheeses = new ArrayList<>();
        keyPoints=0;

        bullets = new ArrayList<>();
        lastCheeseTime = System.currentTimeMillis();
        keys = new ArrayList<>();
        lastKeyTime = System.currentTimeMillis();
        lastRoadTime = System.currentTimeMillis();
        explosions = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("HighScore", 0);
        SharedPreferences sharedPreferences2 = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        keyPoints = sharedPreferences2.getInt("key_points", 0);
        isJerryImmune = false;
        immunityEndTime = 0;
        letters = new ArrayList<>();

        currentLetterIndex = 0;
        lastLetterTime = System.currentTimeMillis();

    }

    @Override
    public void run() {
        while (isPlaying) {
            long startTime = System.nanoTime();
            update();
            draw();
            control();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            long targetFrameTime = 16;
            long sleepTime = targetFrameTime - duration;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {

                }
            }
            if (duration > 80) {
                Log.d("GameLoop", "Frame time: " + duration + " ms");
            }
        }
    }
    private void saveHighScore(int highScore) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("HighScore", highScore);
        editor.apply();
    }
    public void makeJerryImmune() {
        isJerryImmune = true;
        immunityEndTime = System.currentTimeMillis() + 10000;
        soundPool.play(pows, 1, 1, 0, 0, 1);

    }
    public void giveJerryCheeseAndGun() {
        cheesePoints += 25;
        hasGun = true;
        jerry.setHasGun(true);
        soundPool.play(pows, 1, 1, 0, 0, 1);
    }

    public void setScoreTextView(TextView scoreTextView) {
        this.scoreTextView = scoreTextView;
    }


    private void update() {
        long currentTime = System.currentTimeMillis();

        if (isJerryImmune && currentTime > immunityEndTime) {
            isJerryImmune = false;
        }

        if (score >= 10 && score / 10 > lastScoreIncrement) {
            lastScoreIncrement = score / 10;
            speedMultiplier += 0.1f;
            ObstacleTime -= 100;
            CheeseTime -= 100;
        }

        if (currentTime - lastRoadTime > 200) {
            roadSegment = (roadSegment + 1) % 4;
            lastRoadTime = currentTime;
        }

        if (currentTime - lastKeyTime > 30000) {
            generateKey();
            lastKeyTime = currentTime;
        }

        long obstacleDelay = isJerryImmune && currentTime < immunityEndTime - 2000 ? 250 : ObstacleTime;
        if (currentTime - lastObstacleTime > obstacleDelay) {
            generateObstacle();
            lastObstacleTime = currentTime;
        }

        long cheeseDelay = isJerryImmune && currentTime < immunityEndTime - 2000 ? 1000 : CheeseTime;
        if (currentTime - lastCheeseTime > cheeseDelay) {
            generateCheese();
            lastCheeseTime = currentTime;
        }
        if ( currentTime - lastLetterTime > LetterTime) {
            generateLetter();
            lastLetterTime = currentTime;
        }
        updateLetters(currentTime);


        updateKeys(currentTime);
        updateObstacles(currentTime);
        updateCheeses(currentTime);
        updateBullets();
        updateExplosions();

        if (tomFollowing) {
            if (currentTime - tomAppearTime <= 5000) {
                tom.followJerry(jerry);
            } else {
                tom.retreat();
                tomFollowing = false;
            }
        }

        if (score > highScore) {
            highScore = score;
            saveHighScore(highScore);
        }

        if (scoreTextView != null) {
            post(() -> scoreTextView.setText("Score: " + score + "      Cheese: " + cheesePoints));
        }
    }
    private void generateLetter() {
        if (currentLetterIndex < currentWord.length()) {
            char letter = currentWord.charAt(currentLetterIndex);
            int lane = random.nextInt(3);
            Letter newLetter = new Letter(getContext(), letter, lane, 20);
            letters.add(newLetter);
        }
    }

    private void updateLetters(long currentTime) {
        Iterator<Letter> iterator = letters.iterator();
        while (iterator.hasNext()) {
            Letter letter = iterator.next();
            letter.update(speedMultiplier);
            if (letter.getY() > 2000) {
                iterator.remove();
            } else if (letter.isColliding(jerry)) {
                iterator.remove();
                MainActivity3.onLetterCollected(letter.getLetter());
                currentLetterIndex++;

                if (currentLetterIndex >= currentWord.length()) {
                    cheesePoints += 25;
                    MainActivity3.letterOver();
                    currentWord = "";
                    currentLetterIndex = 0;
                }
            }
        }
    }




    private void generateKey() {
        int lane;
        boolean isValidLane;
        Key key = null;
        do {
            lane = random.nextInt(3);
            isValidLane = isLaneValid(lane);
            if (isValidLane) {
                key = getKeyFromPool();
            }
        } while (!isValidLane);
        keys.add(key);
    }
    private Obstacle getObstacleFromPool() {
        if (obstaclePool == null) {
            obstaclePool = new LinkedList<>();
        }
        if (obstaclePool.isEmpty()) {
            int lane = random.nextInt(3);
            return new Obstacle(getContext(),lane,0);
        } else {
            return obstaclePool.poll();
        }
    }
    private void returnKeyToPool(Key key) {
        key.reset();
        keyPool.add(key);
    }
    private Key getKeyFromPool() {
        if (keyPool == null) {
            keyPool = new LinkedList<>();
        }
        if (keyPool.isEmpty()) {
            int lane = random.nextInt(3);
            return new Key(getContext(),lane,0);
        } else {
            return keyPool.poll();
        }
    }
    private void returnObstacleToPool(Obstacle obstacle) {
        obstacle.reset();
        obstaclePool.add(obstacle);
    }
    private Cheese getCheeseFromPool() {
        if (cheesePool == null) {
            cheesePool = new LinkedList<>();
        }
        if (cheesePool.isEmpty()) {
            int lane = random.nextInt(3);
            return new Cheese(getContext(),lane,0);
        } else {
            return cheesePool.poll();
        }
    }
    private void returnCheeseToPool(Cheese cheese) {
        cheese.reset();
        cheesePool.add(cheese);
    }

    private void generateObstacle() {
        int lane = random.nextInt(3);
        Obstacle obstacle = getObstacleFromPool();
        obstacles.add(obstacle);
    }

    private void generateCheese() {
        int lane;
        boolean isValidLane;
        Cheese cheese = null;
        do {
            lane = random.nextInt(3);
            isValidLane = isLaneValid(lane);
            if (isValidLane) {
                cheese = getCheeseFromPool();
            }
        } while (!isValidLane);
        cheeses.add(cheese);
    }
    private boolean isLaneValid(int lane) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getLane() == lane && Math.abs(obstacle.getY()) < 500) {
                return false;
            }
        }
        return true;
    }

    private void updateKeys(long currentTime) {
        Iterator<Key> keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            Key key = keyIterator.next();
            key.update(speedMultiplier);
            if (key.getY() > 2000) {
                keyIterator.remove();
                returnKeyToPool(key);
            } else if (key.isColliding(jerry)) {
                keyPoints++;
                returnKeyToPool(key);
                keyIterator.remove();
                saveKeyPoints();
            }
        }
    }

    private void saveKeyPoints() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("key_points", keyPoints);
        editor.apply();
    }

    private void updateObstacles(long currentTime) {
        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            obstacle.update(isJerryImmune && currentTime < immunityEndTime - 2000 ? 5 : speedMultiplier);
            if (obstacle.getY() > 2000) {
                iterator.remove();

                returnObstacleToPool(obstacle);
                score++;
            } else if (obstacle.isColliding(jerry)) {
                returnObstacleToPool(obstacle);

                handleObstacleCollision(iterator, obstacle, currentTime);
            }
        }
    }

    private void handleObstacleCollision(Iterator<Obstacle> iterator, Obstacle obstacle, long currentTime) {
        if (!isJerryImmune) {
            obstaclesHit++;
            iterator.remove();
            MainActivity3.decreaselife();

            if (obstaclesHit < obstacleLimit) {
                tomAppearTime = currentTime;
                tom.isVisible = true;
                tomFollowing = true;
            } else {
                tom.appearBottom(jerry.getX());
                tomFollowing = false;
                isPlaying = false;
                gameOver();
            }
        } else {
            score++;
            iterator.remove();
        }
    }


    private void updateCheeses(long currentTime) {
        Iterator<Cheese> cheeseIterator = cheeses.iterator();
        while (cheeseIterator.hasNext()) {
            Cheese cheese = cheeseIterator.next();
            cheese.update(isJerryImmune && currentTime < immunityEndTime - 2000 ? 5 : speedMultiplier);
            if (cheese.getY() > 2000) {
                cheeseIterator.remove();
                returnCheeseToPool(cheese);
            } else if (cheese.isColliding(jerry)) {
                cheesePoints++;
                returnCheeseToPool(cheese);
                cheeseIterator.remove();
                if (cheesePoints >= 5) {
                    hasGun = true;
                    jerry.setHasGun(true);
                }
            }
        }
    }

    private void updateBullets() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();

            boolean hit = false;
            Iterator<Obstacle> obstacleItr = obstacles.iterator();
            while (obstacleItr.hasNext()) {
                Obstacle obstacle = obstacleItr.next();
                if (bullet.isColliding(obstacle)) {
                    obstacleItr.remove();
                    bulletIterator.remove();
                    explosions.add(new Explosion(getContext(), obstacle.getX() - 120, obstacle.getY()));
                    soundPool.play(explo, 1, 1, 0, 0, 1);
                    hit = true;
                    break;
                }
            }

            if (!hit && bullet.getY() < 0) {
                bulletIterator.remove();
            }
        }
    }

    private void updateExplosions() {
        Iterator<Explosion> explosionIterator = explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            explosion.update();
            if (explosion.isFinished()) {
                explosionIterator.remove();
            }
        }
    }


    private void draw() {
        if (surfaceHolder != null && surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                for (int i = 0; i < 3; i++) {
                    canvas.drawBitmap(roadSegments[roadSegment], i * laneWidth, 0, paint);
                    if (i < 2) {
                        paint.setColor(separatorColor);
                        canvas.drawRect((i + 1) * laneWidth - separatorWidth, 0, (i + 1) * laneWidth, getHeight(), paint);
                    }
                }

                paint.setColor(Color.BLUE);
                jerry.draw(canvas, paint);

                paint.setColor(Color.BLACK);
                for (Obstacle obstacle : obstacles) {
                    obstacle.draw(canvas, paint);
                }
                for (Cheese cheese : cheeses) {
                    cheese.draw(canvas, paint);
                }
                for (Key key : keys) {
                    key.draw(canvas, paint);
                }

                for (Bullet bullet : bullets) {
                    bullet.draw(canvas, paint);
                }
                for (Explosion explosion : explosions) {
                    explosion.draw(canvas, paint);
                }
                for (Letter letter : letters) {
                    letter.draw(canvas, paint);
                }

                paint.setColor(Color.CYAN);
                tom.draw(canvas, paint);

                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void gameOver() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.game_over_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            TextView scoreTextView = dialog.findViewById(R.id.score_text_view);
            TextView highScoreTextView = dialog.findViewById(R.id.high_score_text_view);
            scoreTextView.setText("Your score: " + score);
            highScoreTextView.setText("High score: " + highScore);
            if(score == highScore){
                scoreTextView.setVisibility(GONE);
                highScoreTextView.setTextSize(60);
                highScoreTextView.setText("New HighScore - " + score);
            }

            Button playAgainButton = dialog.findViewById(R.id.play_again_button);
            Button exitButton = dialog.findViewById(R.id.exit_button);
            exitButton.setText("Home");
            Button revive=dialog.findViewById(R.id.revive);
            if(cheesePoints>=5){
                revive.setVisibility(View.VISIBLE);
            }
            revive.setOnClickListener(v -> {
                isPlaying=true;
                tom.reset();
                cheesePoints-=5;
                jerry.setHasGun(cheesePoints >= 5);
                MainActivity3.ol=obstacleLimit;
                MainActivity3.updateHearts(obstacleLimit);

                tomAppearTime = 0;
                obstaclesHit = 0;
                obstacles.clear();
                cheeses.clear();
                keys.clear();
                gameThread = new Thread(GameView2.this);
                gameThread.start();
                dialog.dismiss();

            });

            playAgainButton.setOnClickListener(v -> {

                score = 0;
                cheesePoints=0;
                hasGun=false;
                MainActivity3.ol=obstacleLimit;
                MainActivity3.updateHearts(obstacleLimit);
                speedMultiplier=1f;
                MainActivity3.collectedLetters= new ArrayList<>();
                currentLetterIndex=0;
                jerry.setHasGun(false);
                ObstacleTime=2000;
                CheeseTime=5000;
                tomAppearTime = 0;
                obstaclesHit = 0;
                obstacles.clear();
                cheeses.clear();
                keys.clear();
                tom.reset();
                tomFollowing = false;
                isPlaying = true;
                gameThread = new Thread(GameView2.this);
                gameThread.start();
                dialog.dismiss();
                if (getContext() instanceof MainActivity3) {
                    if(keyPoints>=2) {
                        ((MainActivity3) getContext()).showPowerUpButtons();
                    }
                }
            });

            exitButton.setOnClickListener(v -> {

                Intent intent = new Intent((Activity) getContext(), MainActivity2.class);
                ((Activity) getContext()).startActivity(intent);

                dialog.dismiss();
            });

            dialog.show();
        });
    }

    private void control() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTapTime < 300) {
                    if ( cheesePoints>=5 &&  hasGun) {
                        Bullet bullet = new Bullet(getContext(), jerry.getX(), jerry.getY());
                        bullets.add(bullet);
                        cheesePoints-=5;
                        if(cheesePoints<5){
                            hasGun=false;
                            jerry.setHasGun(false);
                        }
                    }
                    break;
                } else {
                    initialX = event.getX();
                    lastTapTime = currentTime;
                }
            case MotionEvent.ACTION_UP:

                float finalX = event.getX();
                if (initialX+50 < finalX) {
                    jerry.moveRight();
                    soundPool.play(swipe, 1, 1, 0, 0, 1);
                } else if (initialX > finalX+50) {
                    jerry.moveLeft();
                    soundPool.play(swipe, 1, 1, 0, 0, 1);
                }
                break;

        }
        return true;
    }


    public void pause() {
        isPlaying = false;
        isPaused = true;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }

    public void resume() {
        isPlaying = true;
        isPaused = false;
        gameThread = new Thread(this);
        gameThread.start();


    }
    public boolean isPaused() {
        return isPaused;
    }
}
