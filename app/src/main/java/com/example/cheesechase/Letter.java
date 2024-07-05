package com.example.cheesechase;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;


public class Letter {
    private char letter;
    private int x;
    private int y;
    private int lane;
    private int speed;
    private Bitmap bitmap;

    public Letter(Context context, char letter, int lane, int speed) {
        this.letter = letter;
        this.lane = lane;
        this.speed = speed;
        getLaneX(lane);
        this.y = -50;
        this.bitmap = getLetterBitmap(context, letter);
    }

    public void update(float speedMultiplier) {
        y += (int) (speed * speedMultiplier);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public boolean isColliding(Jerry jerry) {
        int jerryLeft = jerry.getX();
        int jerryTop = jerry.getY();
        int jerryRight = jerryLeft + jerry.getWidth();
        int jerryBottom = jerryTop + jerry.getHeight();

        int letterLeft = x;
        int letterTop = y;
        int letterRight = letterLeft + bitmap.getWidth();
        int letterBottom = letterTop + bitmap.getHeight();

        return letterRight > jerryLeft && letterLeft < jerryRight &&
                letterBottom > jerryTop && letterTop < jerryBottom;
    }

    public char getLetter() {
        return letter;
    }

    public int getY() {
        return y;
    }

    public int getLane() {
        return lane;
    }

    private void getLaneX(int lane) {
        if (lane == 2) {
            x = lane * 400 + 50;
        } else if (lane == 1) {
            x = lane * 400 + 90;
        } else {
            x = lane * 400 + 130;
        }
    }

    private Bitmap getLetterBitmap(Context context, char letter) {
        int diameter = 100;
        int diameter2 = 80;
        Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.YELLOW);
        circlePaint.setAntiAlias(true);
        canvas.drawCircle((float) diameter / 2, (float) diameter / 2, (float) diameter / 2, circlePaint);
        Paint circlePaint2 = new Paint();
        circlePaint2.setColor(Color.GRAY);
        circlePaint2.setAntiAlias(true);
        canvas.drawCircle((float) diameter / 2, (float) diameter / 2, (float) diameter2 / 2, circlePaint2);

        TextView textView = new TextView(context);
        textView.setText(String.valueOf(letter));
        textView.setTextSize(40);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());

        canvas.save();
        canvas.translate((float) (diameter - textView.getMeasuredWidth()) / 2, (float) (diameter - textView.getMeasuredHeight()) / 2);
        textView.draw(canvas);
        canvas.restore();

        return bitmap;
    }
}
