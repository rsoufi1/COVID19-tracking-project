package com.example.osproject2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;

public class Graph extends View {
    LinkedList<Integer> xValues;
    LinkedList<Integer> cases;
    int maxCases;
    int numberOfDays;
    int width;
    int height;

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.xValues = new LinkedList<>();
        this.cases = new LinkedList<>();
        this.maxCases = 0;
        this.numberOfDays = 0;
        this.width = 0;
        this.height = 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("width", String.valueOf(w));
        this.width = w;
        this.height = h;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //Testing: draw a circle lol
        Paint paint=new Paint();
        paint.setStrokeWidth(3);

        int padding = 32;
        if(numberOfDays != 0){
            double scaleX = (double)(width - padding)/(double)numberOfDays;
            double scaleY = (double)(height - padding*3)/(double)maxCases;
            Log.d("scales: ", String.valueOf(scaleX) + " " + String.valueOf(scaleY));

            //Draw graph lines
            paint.setColor(Color.WHITE);
            paint.setAlpha(100);
            //Major lines
            int middle = height/2;
            int min = height-16;
            int max = 32;
            canvas.drawLine(0, min, width, min, paint);
            canvas.drawLine(0, max, width, max, paint);
            canvas.drawLine(0, middle, width, middle, paint);

            //minor lines
            paint.setAlpha(55);
            int middleToMin = (min+middle)/2;
            int middleToMax = (max+middle)/2;
            canvas.drawLine(0, middleToMin, width, middleToMin, paint);
            canvas.drawLine(0, middleToMax, width, middleToMax, paint);

            //Add y-axis
            paint.setAlpha(255);
            paint.setTextSize((float) 50.0);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStrokeWidth(5);
            canvas.drawText("0", 24, min, paint);
            canvas.drawText(String.valueOf(maxCases/2), 24, middle+25, paint);
            canvas.drawText(String.valueOf(maxCases), 24, max+25, paint);

            //Draw graph
            paint.setColor(Color.parseColor("#52aeff"));
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            Path p = new Path();
            Paint p2 = new Paint();
            p2.setColor(Color.parseColor("#52aeff"));
            p2.setStrokeWidth(5);
            p2.setDither(true);
            p2.setStrokeCap(Paint.Cap.ROUND);
            p2.setStyle(Paint.Style.STROKE);
            p2.setStrokeJoin(Paint.Join.ROUND);
            p2.setPathEffect(new CornerPathEffect(30) );
            p2.setAntiAlias(true);
            p.moveTo(0, height - padding);
            for(int i = 0; i < xValues.size(); i++){
                int x = (int)(xValues.get(i)*scaleX);
                int y = height - (int)(padding+(cases.get(i)*scaleY));
                p.lineTo(x, y);
                //Log.d("line", String.valueOf(x) + " " + String.valueOf(y));
            }
            canvas.drawPath(p, p2);

            paint.setStyle(Paint.Style.FILL);

            //Display current number of cases
            paint.setTextSize(100);
            paint.setAlpha(255);
            paint.setColor(Color.WHITE);
            canvas.drawText(String.valueOf(cases.getLast()) + " Cases", 50, 175, paint);
            Log.d("hewwow", "uwu2");
        } else{
            paint.setStyle(Paint.Style.FILL);

            //Display current number of cases
            paint.setTextSize(100);
            paint.setAlpha(255);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Loading...", width/2, height/2, paint);
        }


    }

    public void hewwow(){Log.d("hewwow", "uwu");}

    public void addValues(LinkedList<Integer> xValue, LinkedList<Integer> yValue){
        for(int i = 0; i < xValue.size(); i++){
            this.xValues.add(xValue.get(i));
            this.cases.add(yValue.get(i));

            if(this.cases.getLast() > this.maxCases){
                this.maxCases = this.cases.getLast();
            }
        }

        this.numberOfDays = this.xValues.getLast();
        Log.d("addValues", "done" + String.valueOf(maxCases));
    }

    @Override
    public String toString(){
        String s = "";
        for(int i = 0; i < this.cases.size(); i++){
            s += String.valueOf(this.cases.get(i)) + " ";
        }

        return s;
    }
}
