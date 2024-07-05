package com.example.cheesechase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;


public class Explosion {
    private Bitmap[] frames;
    private int currentFrame;
    private long frameDuration;
    private long lastFrameChangeTime;
    private float x, y;
    private boolean finished;

    public Explosion(Context context, float x, float y) {
        frames = new Bitmap[4];
        frames[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode0);
        frames[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode1);
        frames[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode2);
        frames[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode3);

        currentFrame = 0;
        frameDuration = 100;
        lastFrameChangeTime = System.currentTimeMillis();
        this.x = x;
        this.y = y;
        finished = false;
    }

    public void update() {
        if (System.currentTimeMillis() - lastFrameChangeTime > frameDuration) {
            currentFrame++;
            if (currentFrame >= frames.length) {
                finished = true;
            } else {
                lastFrameChangeTime = System.currentTimeMillis();
            }
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!finished) {
            canvas.drawBitmap(frames[currentFrame], x, y, paint);
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
