package com.example.cheesechase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;


public class Cheese {
    private int x;
    private int y;
    private int lane;
    private int speed;
    private Bitmap cheeseBitmap;
    private int width;
    private int height;
    private Bitmap bitmap1;
    private int track;


    public Cheese(Context context, int lane, int yPosition) {
        track=lane;
        if(lane==2) {
            x = lane * 400 + 100;
        } else if (lane==1) {
            x = lane * 400 + 130;
        } else{
            x = lane * 400 + 170;
        }
        y = yPosition;
        speed = 20;
        Bitmap originalBitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.cheese);

        width = 200;
        height = 200;

        cheeseBitmap = Bitmap.createScaledBitmap(originalBitmap1, width, height, false);
    }

    public void update(float v) {
        y += (int) (speed * v);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(cheeseBitmap, x - (float) cheeseBitmap.getWidth() / 2, y - (float) cheeseBitmap.getHeight() / 2, paint);
    }

    public boolean isColliding(Jerry jerry) {
        int jerryX = jerry.getX();
        int jerryY = jerry.getY();
        int jerryRadius = jerry.getRadius();

        return (Math.abs(x - jerryX) < cheeseBitmap.getWidth() / 2 + jerryRadius) && (Math.abs(y - jerryY) < cheeseBitmap.getHeight() / 2 + jerryRadius);
    }

    public int getY() {
        return y;
    }

    public int getLane() {
        return track;
    }

    public void reset() {
        y=0;
    }
}
