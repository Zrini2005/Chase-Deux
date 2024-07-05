package com.example.cheesechase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;





import android.graphics.Canvas;
import android.graphics.Paint;

public class Jerry {
    private int x;
    private int y;
    private int track;
    private int radius;
    private Bitmap[] images;
    private Bitmap[] gunImage;
    private int currentImageIndex;
    private long lastImageSwitchTime;
    private boolean hasGun;

    public Jerry(Context context) {
        track = 1;
        radius = 150;
        y = 1700;
        updatePosition();

        images = new Bitmap[2];
        images[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jerryleftpng);
        images[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jerryright);

        images[0] = Bitmap.createScaledBitmap(images[0], radius * 2, radius * 2, false);
        images[1] = Bitmap.createScaledBitmap(images[1], radius * 2, radius * 2, false);
        gunImage=new Bitmap[2];
        gunImage[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.pistolleft);
        gunImage[0] = Bitmap.createScaledBitmap(gunImage[0], 100, 100, false);
        gunImage[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.pistolright);
        gunImage[1] = Bitmap.createScaledBitmap(gunImage[1], 100, 100, false);

        currentImageIndex = 0;
        lastImageSwitchTime = System.currentTimeMillis();
    }

    private void updatePosition() {
        if(track==2) {
            x = track * 400 + 100;
        } else if (track==1) {
            x = track * 400 + 130;
        } else{
            x = track * 400 + 170;
        }
    }
    public void setHasGun(boolean hasGun) {
        this.hasGun = hasGun;
    }

    public void draw(Canvas canvas, Paint paint) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastImageSwitchTime >= 250) {
            currentImageIndex = (currentImageIndex + 1) % 2;
            lastImageSwitchTime = currentTime;
        }

        canvas.drawBitmap(images[currentImageIndex], x - radius, y - radius, paint);
        if (hasGun) {
            int gunX = x + radius - gunImage[0].getWidth() / 2-50;
            int gunY = y + radius- 150;
            canvas.drawBitmap(gunImage[currentImageIndex], gunX, gunY, paint);
        }
    }

    public void moveRight() {
        if (track < 2) {
            track++;
            updatePosition();
        }
    }

    public void moveLeft() {
        if (track > 0) {
            track--;
            updatePosition();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public int getWidth() {
        return images[0].getWidth();
    }
    public int getHeight() {
        return images[0].getHeight();
    }
}
