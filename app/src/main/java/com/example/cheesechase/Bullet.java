package com.example.cheesechase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;



public class Bullet {
    private int x;
    private int y;
    private int speed;
    private Bitmap bulletBitmap;

    public Bullet(Context context, int x, int y) {
        this.x = x;
        this.y = y;
        this.speed = 20;
        bulletBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);
        this.bulletBitmap = Bitmap.createScaledBitmap(bulletBitmap, 25, 50, false);
    }

    public void update() {
        y -= speed;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (bulletBitmap != null) {
            canvas.drawBitmap(bulletBitmap, x - (float) bulletBitmap.getWidth() / 2, y - (float) bulletBitmap.getHeight() / 2, paint);
        }
    }

    public boolean isColliding(Obstacle obstacle) {
        return (Math.abs(x - obstacle.getX()) < bulletBitmap.getWidth() / 2 + obstacle.getWidth() / 2) &&
                (Math.abs(y - obstacle.getY()) < bulletBitmap.getHeight() / 2 + obstacle.getHeight() / 2);
    }

    public int getY() {
        return y;
    }
}

