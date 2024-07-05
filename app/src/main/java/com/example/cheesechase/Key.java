package com.example.cheesechase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class Key {
    private Context context;
    private int lane;
    private int y;
    private Bitmap bitmap;
    private int width;
    private int x;
    private int height;
    private  int speed;

    public Key(Context context, int lane, int y1) {

        if(lane==2) {
            x = lane * 400 + 100;
        } else if (lane==1) {
            x = lane * 400 + 130;
        } else{
            x = lane * 400 + 170;
        }
        y=y1;
        speed=20;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.key);
        width = 200;
        height = 200;

        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public int getLane() {
        return lane;
    }

    public int getY() {
        return y;
    }

    public void update(float v) {
        y += (int) (speed *v);
    }

    public boolean isColliding(Jerry jerry) {
        int jerryX = jerry.getX();
        int jerryY = jerry.getY();
        int jerryRadius = jerry.getRadius();

        return (Math.abs(x - jerryX) < bitmap.getWidth() / 2 + jerryRadius) && (Math.abs(y - jerryY) < bitmap.getHeight() / 2 + jerryRadius);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, x - (float) bitmap.getWidth() / 2, y - (float) bitmap.getHeight() / 2, paint);
    }

    public void reset() {
        y=0;
    }
}
