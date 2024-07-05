package com.example.cheesechase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;



import android.graphics.Canvas;
import android.graphics.Paint;


import android.graphics.Canvas;
import android.graphics.Paint;

public class Tom {
    private int x;
    private int y;
    private int radius;
    public boolean isVisible;
    private boolean isout;
    private Bitmap[] images;
    private int currentImageIndex;
    private long lastImageSwitchTime;
    private Bitmap out;

    public Tom(Context context) {
        radius = 150;
        x = -radius;
        y = -radius;
        isVisible = true;
        isout=false;
        images = new Bitmap[2];
        images[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tomleft);
        images[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tomright);
        out=BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion);

        out=Bitmap.createScaledBitmap(out, radius * 5, radius * 5, false);
        images[0] = Bitmap.createScaledBitmap(images[0], radius * 2, radius * 2, false);
        images[1] = Bitmap.createScaledBitmap(images[1], radius * 2, radius * 2, false);

        currentImageIndex = 0;
        lastImageSwitchTime = System.currentTimeMillis();
    }
    public void reset() {
        radius = 150;
        x = -radius;
        y = -radius;
        isVisible = true;
        isout= false;
        currentImageIndex = 0;
        lastImageSwitchTime = System.currentTimeMillis();
    }


    public void draw(Canvas canvas, Paint paint) {
        if (isVisible) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastImageSwitchTime >= 500) {
                currentImageIndex = (currentImageIndex + 1) % 2;
                lastImageSwitchTime = currentTime;
            }
            if(isout){
                canvas.drawBitmap(out, x - (float) out.getWidth() / 2, 1700 - (float) out.getHeight() / 2, paint);
            }

            canvas.drawBitmap(images[currentImageIndex], x - radius, y - radius, paint);

        }
    }

    public void followJerry(Jerry jerry) {
        if (isVisible) {
            x = jerry.getX();
            y = jerry.getY() +200;
        }
    }

    public void retreat() {
        isVisible = false;
    }

    public void appearBottom(int jerryx) {
        y = 1900;
        x=jerryx;
        isVisible = true;
        isout=true;
    }

    public boolean isColliding(Jerry jerry) {

        int dx = x - jerry.getX();
        int dy = y - jerry.getY();
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        return distance < radius + jerry.getRadius();
    }

    public void updatePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
