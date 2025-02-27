package com.muss_coding.irrigation;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IrrigationActivity extends AppCompatActivity {

    Button button, btnUndo;
    static RelativeLayout rlDvHolder;
    static DrawingView dv;
    TextView display;
    TextView real;
    Button polygon;
    static SeekBar radius;
    static RelativeLayout background;
    public static Context context;
    public EditText length;
    static RelativeLayout container;
    public TextView textview, ft;
    TextView angleText, rotateText;
    ImageView left1, left2, right1, right2;
    static CardView autoplot;
    public static Animation fadeIn, fadeOut;
    Button autoplotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "Setting content view to activity_main");
        setContentView(R.layout.activity_irrigation);

        // Initialize animations
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(800);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(100);
        fadeOut.setDuration(800);

        // Initialize views
        Log.d("MainActivity", "Finding views by ID");
        display = findViewById(R.id.textViewDisplay);
        real = findViewById(R.id.real);

        if (display == null) {
            Log.e("MainActivity", "display (textView) is null"+R.id.textViewDisplay);
        } else {
            Log.d("MainActivity", "display (textView) initialized");
        }

        if (real == null) {
            Log.e("MainActivity", "real is null");
        } else {
            Log.d("MainActivity", "real initialized");
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dv = new DrawingView(getApplicationContext(), display, height, width);
        Log.d("MainActivity", "DrawingView initialized with height: " + height + " and width: " + width);

        rlDvHolder = findViewById(R.id.dvHolder);
        if (rlDvHolder != null) {
            Log.d("MainActivity", "rlDvHolder initialized");
            rlDvHolder.addView(dv);
            Log.d("MainActivity", "DrawingView added to rlDvHolder");
        } else {
            Log.e("MainActivity", "rlDvHolder is null");
        }

        polygon = findViewById(R.id.regular);
        autoplot = findViewById(R.id.autoplot);
        autoplotButton = findViewById(R.id.apButton);
        button = findViewById(R.id.button);
        background = findViewById(R.id.layout);
        radius = findViewById(R.id.seekBar);
        textview = background.findViewById(R.id.textViewDisplay);
        ft = background.findViewById(R.id.ft);
        length = background.findViewById(R.id.length);
        angleText = background.findViewById(R.id.adjustAngleText);
        rotateText = background.findViewById(R.id.rotateSprinklerText);
        left1 = background.findViewById(R.id.leftAngle1);
        left2 = background.findViewById(R.id.leftAngle2);
        right1 = background.findViewById(R.id.rightAngle1);
        right2 = background.findViewById(R.id.rightAngle2);
        btnUndo = findViewById(R.id.btnUndo);

        Log.d("MainActivity", "Other views initialized");

        setButtonClick();
        setRadiusBar();
        dv.sradius = radius.getProgress();
        container = background.findViewById(R.id.container);
        lengthUpdater();

        radius.getProgressDrawable().setColorFilter(Color.parseColor("#70c48c"), PorterDuff.Mode.SRC_IN);
        radius.getThumb().setColorFilter(Color.parseColor("#3abd66"), PorterDuff.Mode.SRC_IN);

        context = getApplicationContext();
        angleAndRotateClicks();

        landCoordinates = false;
        sprinklerCoordinates = false;
        coordinateIds = new ArrayList<>();

        autoplotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoPlot();
            }
        });

        Log.d("MainActivity", "onCreate finished");
    }



    public static double[] getPixelSideLength() {
        double pixelSideLength = 1, minSideLength = 1;
        if (maxSideLength > 35) {
            pixelSideLength = (double) 25d / dv.ratio;
            minSideLength = 4d / dv.ratio;
        }
        if (maxSideLength > 50) {
            pixelSideLength = (double) 35d / dv.ratio;
            minSideLength = 5d / dv.ratio;
        }
        if (maxSideLength > 74) {
            pixelSideLength = (double) 40d / dv.ratio;
            minSideLength = 7d / dv.ratio;
        }
        if (maxSideLength > 90) {
            pixelSideLength = (double) 50d / dv.ratio;
            minSideLength = 9d / dv.ratio;
        }
        if (maxSideLength > 150) {
            pixelSideLength = (double) 80d / dv.ratio;
            minSideLength = 10d / dv.ratio;
        }
        if (maxSideLength > 325) {
            pixelSideLength = (double) 250d / dv.ratio;
            minSideLength = 20d / dv.ratio;
        } else if (maxSideLength <= 35) {
            pixelSideLength = 15d / dv.ratio;
            minSideLength = 4d / dv.ratio;
        }
        return new double[]{minSideLength, pixelSideLength};
    }

    public static double getAngle(double v1x, double v1y, double v2x, double v2y) {
        double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
        v1x /= l1;
        v1y /= l1;
        double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
        v2x /= l2;
        v2y /= l2;
        double rad = Math.acos(v1x * v2x + v1y * v2y);
        //INFO Changed method of calculating angle.
        return Math.toDegrees(rad);
    }

    public static void customPlotter() {
        autoplot.setAnimation(fadeIn);
        autoplot.setVisibility(View.VISIBLE);
        dv.xlist.clear();
        dv.ylist.clear();
        rectangle();
        //hexagon();
        dv.invalidate();
    }

    public static void customPlotter(boolean hex) {
        autoplot.setAnimation(fadeIn);
        autoplot.setVisibility(View.VISIBLE);
        dv.xlist.clear();
        dv.ylist.clear();
        hexagon();
        dv.invalidate();
    }

    public static void rectangle() {
        dv.xlist.add(300);
        dv.xlist.add(1150);
        dv.xlist.add(1150);
        dv.xlist.add(300);

        dv.ylist.add(300);
        dv.ylist.add(300);
        dv.ylist.add(1660);
        dv.ylist.add(1660);
    }

    public static void hexagon() {
        dv.xlist.add(250);
        dv.xlist.add(725);
        dv.xlist.add(1200);
        dv.xlist.add(1200);
        dv.xlist.add(725);
        dv.xlist.add(250);

        dv.ylist.add(400);
        dv.ylist.add(230);
        dv.ylist.add(400);
        dv.ylist.add(1400);
        dv.ylist.add(1570);
        dv.ylist.add(1400);
    }

    public static boolean isRectangle() {
        if (dv.xlist.size() != 4) return false;
        else {
            int x1 = dv.xlist.get(0);
            int x2 = dv.xlist.get(1);
            int x3 = dv.xlist.get(2);
            int x4 = dv.xlist.get(3);

            int y1 = dv.ylist.get(0);
            int y2 = dv.ylist.get(1);
            int y3 = dv.ylist.get(2);
            int y4 = dv.ylist.get(3);

            /*if(x1 == x4 && x2 == x3 && y1 == y2 && y3 == 4)
                return true;
            if(x1 == x2 && x3 == x4 && y1 == y4 && y2 == y3)
                return true;*/
            int a1 = (int) Math.round(getAngle(x1 - x2, y1 - y2, x2 - x3, y2 - y3));
            int a2 = (int) Math.round(getAngle(x2 - x3, y2 - y3, x3 - x4, y3 - y4));
            int a3 = (int) Math.round(getAngle(x3 - x4, y3 - y4, x4 - x1, y4 - y1));
            int a4 = (int) Math.round(getAngle(x4 - x1, y4 - y1, x1 - x2, y1 - y2));
            Log.wtf("Rectangle Angles", a1 + " " + a2 + " " + a3 + " " + a4);
            if (a1 == 90 && a2 == a1 && a3 == a1 && a4 == a1)
                return true;
            return false;
        }
    }

    private static void autoPlot() {
        if (dv.sprinkx.size() != 0)
            dv.resetSprinklers();
        double min = getPixelSideLength()[0];
        double max = getPixelSideLength()[1];
        Log.wtf("Lengths", "Min- " + min + "  Max- " + max);
        ArrayList<Integer> distanceLeft = new ArrayList<>();
        ArrayList<Integer> radius = new ArrayList<>();
        ArrayList<Integer> angle = new ArrayList<>();
        ArrayList<Integer> rotation = new ArrayList<>();
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();

        /*rotation.add(0);
        rotation.add(90);
        rotation.add(180);
        rotation.add(270);*/
        /*rotation.add(0);
        rotation.add(0);
        rotation.add(0);
        rotation.add(0);
        rotation.add(0);*/
        /*angle.add(90);
        angle.add(180);
        angle.add(270);
        angle.add(90);
        angle.add(90);*/
        int size = dv.xlist.size();
        for (int m = 0; m < dv.xlist.size(); m++) {
            int s1 = m;
            int s2 = (m + 1) % size;
            int s3 = (m + 2) % size;

            //README Vectors NOT coordinates
            double v1x = dv.xlist.get(s1) - dv.xlist.get(s2);
            double v1y = dv.ylist.get(s1) - dv.ylist.get(s2);

            double v2x = dv.xlist.get(s3) - dv.xlist.get(s2);
            double v2y = dv.ylist.get(s3) - dv.ylist.get(s2);
            double y1 = dv.ylist.get(s1) * -1, y2 = dv.ylist.get(s2) * -1, y3 = dv.ylist.get(s3) * -1;
            double x1 = dv.xlist.get(s1), x2 = dv.xlist.get(s2), x3 = dv.xlist.get(s3);

            //INFO Changed method of calculating angle.
            double degrees1 = getAngle(v1x, v1y, v2x, v2y);
            //README Below works for rectangle
            //double degrees2 = getAngle(v1x, v1y, 300, 0);

            double vectorUpY = dv.ylist.get(s2) - dv.ylist.get(s1);
            double upAngle = getAngle(v1x, v1y, 0, 300);

            double degrees3 = getAngle(v2x, v2y, -300, 0);
            double degrees2 = getAngle(v1x, v1y, -300, 0);
            if (degrees2 == 180) degrees2 = 0;
            double originalDegrees = degrees2;
            //README Better solution found
            if (upAngle <= 90) degrees2 *= -1;
            //if (m >= size / 2) degrees2 *=-1;
            degrees2 += 90;
            //README Below line seems to fix
            // the 3rd angle in the square which is 180 off.
            if ((dv.xlist.get((s1 - 1 + size) % size) - dv.xlist.get((s2 - 1 + size) % size) == 0) &&
                    (dv.ylist.get((s2 - 1 + size) % size) - dv.ylist.get((s3 - 1 + size) % size) == 0) &&
                    dv.xlist.get(s2) - dv.xlist.get(s1) < 0)
                degrees2 += 180;
/*
            else if(dv.ylist.get(s1) - dv.ylist.get(s2) ==0)
                degrees2 += 180;
*/

            //if (m >= size / 2 && degrees2-90 == 0) degrees2 += 180;
            //NOTES 3/4/20 Have made quite a bit of progress
            // Discovered that you need to multiply degrees2 by -1 for certain cases
            // Before adding the 90 (thereby changing the original angle)
            // because it doesn't know whether the side it is measuring to is on the left (-)
            // or on the right which is (+). If it's on the left, you shouldn't be adding
            // the angle, you should be subtracting it.
            //TODO Figure out when you have to add vs subtract angle.
            Log.wtf(",                   Vectors:", v1x + " " + v1y + "     " + v2x + " " +
                    v2y + "-----> " + vectorUpY + "_" + (int) upAngle + "   : " + (int) getAngle(v2x, v2y, 300, 0));
            Log.wtf("*-Rotation " + (m + 1), "" + (int) originalDegrees + " " + ((int) (1 * (degrees2))) + " " +
                    ((int) (1 * (degrees3))) + "   " + "    (" + x2 + "," + y2 + ")");

            if (m < 1)
                rotation.add((int) degrees2);
            else
                rotation.add((int) ((degrees2)));

            //INFO Standard equation for 1st line. ax + by + c = 0
            double a = y2 - y1;
            double b = x1 - x2;
            double c = -1 * (a * (x1) + b * (y1));
            //INFO Standard equation for 2nd line. rx + sy + t = 0
            double r = y3 - y2;
            double s = x2 - x3;
            double t = -1 * (a * (x2) + b * (y2));

            double slope = (a * Math.sqrt(r * r + s * s) - r * Math.sqrt(a * a + b * b));
            slope = slope / (s * Math.sqrt(a * a + b * b) - b * Math.sqrt(r * r + s * s));

            double inverseSlope = slope;
            double distance = Math.sqrt(1 + inverseSlope * inverseSlope);
            double factorX = (10 / distance) * 1;
            double factorY = (10 / distance) * slope;
            if (factorX < 2 || factorY < 2) {
                factorX *= 2;
                factorY *= 2;
            }

            double checkX = x2 + factorX;
            double checkY = y2 + factorY;

            boolean outside = outside(checkX, -1 * checkY);
            boolean inside = !outside;


            double realAngle = 0;
            //README Logic:
            //  If point is inside and 2x angle to point and side is >180, use 360-degrees
            //  If point is outside, project it inside:
            //    If 2x angle to projected (inside) point to side is > 180, use 360-degrees
            //    (Alternately could've done if 2x outside point to side is < 180, use 360-degrees)
            //  Otherwise use degrees.

            double angleToSide = getAngle(x3 - x2, y3 - y2, checkX - x2, checkY - y2);
            if (inside && 2 * angleToSide > 180)
                realAngle = 2 * angleToSide;
            else if (outside) {
                checkX = checkX - factorX * 2;
                checkY = checkY - factorY * 2;
                angleToSide = getAngle(x3 - x2, y3 - y2, checkX - x2, checkY - y2);
                if (2 * angleToSide > 180) realAngle = 2 * angleToSide;
                else realAngle = 2 * angleToSide;
            } else realAngle = 2 * angleToSide;


            angle.add((int) realAngle);
            /*Log.wtf("Point To Check", "Intersecton: " + x2 + "," + y2
                    + "   --->  " + checkX + "," + checkY + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
                    + a + "x + " + b + "y + " + c + ",  " + r + "x + " + s + "y + " + t + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tOutside:"
                    + outside + "  Angle: " + (2 * angleToSide) + " ------- Real Angle: " + realAngle + "\n,  ");
         */
           /* //INFO Get the intersection of 2 lines using the slopes and y-intercept.
            double intersectX = intersectionOf2Lines(-a/b, -c/b, -r/s,-t/s)[0];
            double intersectY = intersectionOf2Lines(-a/b, -c/b, -r/s,-t/s)[1];

            Log.wtf("Line Intersections", "Line 1: " + x1 + "," + y1 +
                    "  " + x2+","+y2+"  " + x3 +","+y3 +" --->  " +
                    intersectX + "," +intersectY);*/

            x.add(dv.xlist.get(s1));
            y.add(dv.ylist.get(s1));
        }

        /*
        //TODO Need to work on rotation.
        //TODO Need to rewrite whole algorithm for radius,
        // For each corner, need to take into account both sides
        // as well as next corner and both sides.
        for (int a = 0; a < dv.xlist.size(); a += 2) {
            int size = dv.xlist.size();
            int s1 = a;
            int s2 = (a + 1) % size;
            int s3 = (a + 2) % size;
            //TODO Calculate length of 2 sides
            //TODO Using shortest side, set radii to half of it.
            // TODO If shortest side is less than 35% of other side,
            // Set it to 80% of shortest side length
            double side1 = Math.sqrt(Math.pow(dv.xlist.get(s1) - dv.xlist.get(s2), 2) +
                    Math.pow(dv.ylist.get(s1) - dv.ylist.get(s2), 2));
            double side2 = Math.sqrt(Math.pow(dv.xlist.get(s2) - dv.xlist.get(s3), 2) +
                    Math.pow(dv.ylist.get(s2) - dv.ylist.get(s3), 2));
            double smaller = side1, larger = side2;
            boolean side1Larger = false;
            if (side2 < side1) {
                larger = side1;
                smaller = side2;
                side1Larger = true;
            }
            double radius1, radius2;
            if (smaller >= 1.94 * max) {
                radius1 = maxSideLength;
                radius2 = maxSideLength;
            } else {
                if (smaller > .4 * larger) {
                    radius1 = smaller * .5;
                    radius2 = smaller * .5;
                } else if (smaller < .22 * larger){
                    radius1 = smaller*.5;
                    radius2 = smaller*.65;
                }
                else {
                    radius1 = smaller * .4;
                    radius2 = smaller * .6;
                }
            }
            Log.wtf("Radii:", radius1 + " " + radius2 + "   " + smaller
                    + " " + larger + " " + side1Larger);
            radius1 /= 4;
            radius2 /= 4;
            if (side1Larger) {
                radius.add((int) (radius2 / dv.ratio));
                radius.add((int) (radius1 / dv.ratio));
            } else {
                radius.add((int) (radius1 / dv.ratio));
                radius.add((int) (radius2 / dv.ratio));
            }

        }
        for (int a = 0; a < dv.xlist.size(); a++) {
            int size = dv.xlist.size();
            int s1 = (a) % size;
            int s2 = (a + 1) % size;
            int s3 = (a + 2) % size;
            //TODO Calculate length of 2 sides
            //TODO Using shortest side, set radii to half of it.
            // TODO If shortest side is less than 35% of other side,
            // Set it to 80% of shortest side length
            double side1 = Math.sqrt(Math.pow(dv.xlist.get(s1) - dv.xlist.get(s2), 2) +
                    Math.pow(dv.ylist.get(s1) - dv.ylist.get(s2), 2));
            distanceLeft.add((int) (side1 - radius.get((a - 1 + size) % size)
                    - radius.get(a)));

        }
        if (radius.size() > x.size()) {
            radius.remove(radius.size() - 1);
        }*/
        //x.addAll(dv.xlist);
        //y.addAll(dv.ylist);
        if (dv.xlist.size() == 3) {
            int s1 = getLength(0, 1);
            int s2 = getLength(2, 1);
            int s3 = getLength(0, 2);
            int[] ar = new int[3];
            int r1, r2, r3;
            int min1 = (s1 < s3) ? s1 : s3;
            int min2 = (s1 < s2) ? s1 : s2;
            int min3 = (s2 < s3) ? s2 : s3;

            if (min1 / 2d <= min) r1 = (int) min;
            else if (min1 >= max * 1.945) r1 = (int) max;
            else r1 = min1 / 2;

            if (min2 / 2d <= min) r2 = (int) min;
            else if (min2 >= max * 1.945) r2 = (int) max;
            else r2 = min2 / 2;

            if (min3 / 2d <= min) r3 = (int) min;
            else if (min3 >= max * 1.945) r3 = (int) max;
            else r3 = min3 / 2;

            radius.add(r1);
            radius.add(r2);
            radius.add(r3);
        } else {
            for (int i = 0; i < dv.xlist.size(); i++) {
                int cur = i;
                int prev = (i - 1 + size) % size;
                int prev2 = (i - 2 + size) % size;
                int next = (i + 1) % size;
                int next2 = (i + 1) % size;

                /*int n1 = getLength(cur, prev);
                int n2 = getLength(prev, prev2);
                int s1 = getLength(cur, next);
                int s2 = getLength(next, next2);*/

                int s1 = getLength(prev, cur);
                int s2 = getLength(cur, next);
                int r1;
                int min2 = (s1 < s2) ? s1 : s2;

                boolean adjusted = false;
                if (min2 / 2d <= min) {
                    r1 = (int) min;
                    adjusted = true;
                }
                if (min2 >= max * 1.945) {
                    r1 = (int) max;
                    adjusted = true;
                } else r1 = min2 / 2;

                if (i > 0) {
                    boolean c1, c2;
                    int nextMax = (int) (s2 / 1.945);
                    nextMax = (int) Math.min(nextMax, max);
                    int curMax = (int) (s1 - radius.get(i - 1));
                    curMax = (int) Math.min(max, curMax);
                    r1 = Math.max(r1, Math.min(curMax, nextMax));
                    adjusted = true;
                }

                radius.add(r1);

                //radius.add((int) max / 2);
            }
        }

        int last = angle.get(angle.size() - 1);
        angle.add(0, last);
        angle.remove(angle.size() - 1);

        ArrayList<Integer> sideRadii = new ArrayList<>();
        int average = (int) (Math.ceil(max / 2 + min / 2) * 0.85);

        //README Below is for rectangles so that sprinklers do not flow out across the other side.
        Log.wtf("________RECTANGLE______", "--------------- " + isRectangle());
        if (isRectangle()) {
            Log.wtf("________RECTANGLE______", "--------------- RECTANGLE");
            double s1 = getLength(0, 1);
            double s2 = getLength(1, 2);
            double smaller = Math.min(s1, s2) * 0.52d;
            if (smaller < min) average = (int) min;
            else average = (int) Math.min(smaller, max);
        }
        Log.wtf("Lengths", min + " " + max);
        for (int i = 0; i < dv.xlist.size(); i++) {
            int sideLength = getLength(i, (i + 1) % size);
            int r1 = radius.get(i);
            int r2 = radius.get((i + 1) % size);
            int diffLeft = sideLength - r1 - r2;
            int numRegular;
            int numSmall;
            double minTemp = min;
            Log.wtf("*----Coordinates", "___Distance: " + sideLength + " === (" + dv.xlist.get(i) + "," + dv.ylist.get(i) + ") ----- ("
                    + dv.xlist.get((i + 1) % size) + "," + dv.ylist.get((i + 1) % size) + ")");
            if (diffLeft >= min) {

                //DONE Autoplotting side sprinklers does not work when slope is 0.
                // Maybe just have one if case for 0 --> Only need to take into account the x/y,
                //  not x and y when shifting the sprinklers
                // and a 2nd if case for remaining below code.
                //if (slope == 0) {
                //slope = 0.000000001d;
                if ((dv.ylist.get(i) - dv.ylist.get((i + 1) % size)) == 0) {
                    double length = (-1 * dv.xlist.get(i) + dv.xlist.get((i + 1) % size));
                    int neg = (length < 0) ? -1 : 1;
                    length = Math.abs(length) - r1 - r2;

                    int amt1, amt2;
                    numRegular = (int) length / (average * 2);
                    numSmall = (int) (length - average * numRegular * 2) / ((int) (min * 2));
                    amt1 = (int) length - numRegular * average * 2 - numSmall * (int) min * 2;
                    int trackX = dv.xlist.get(i), trackY = dv.ylist.get(i);
                    double slope;
                    if (amt1 > 0 && numSmall > 0)
                        minTemp += amt1 / numSmall;
                    if (amt1 > min * 1.6) numSmall++;

                    double shift = r1;
                    double xFact = shift * neg;
                    trackX += xFact;
                    trackY = dv.ylist.get(i);
                    Log.wtf("*------Start Tracker (same y)", trackX + " " + trackY + " " + xFact);
                    for (int t = 1; t < numSmall + 1; t++) {
                        if (t == 1) {
                            xFact = min * neg;
                        } else {
                            xFact = min * 2 * neg;
                        }
                        trackX += xFact;
                        rotation.add(rotation.get(i));
                        angle.add(180);
                        x.add(trackX);
                        y.add(trackY);
                        sideRadii.add((int) min);
                        //README If on the last center, move over to the edge.
                        if (t == numSmall) {
                            xFact = min * neg;
                            trackX += xFact;
                        }
                        Log.wtf("*--------Trackers", trackX + " " + trackY
                                + " " + sideRadii.get(sideRadii.size() - 1));
                    }
                    for (int t = 1; t < numRegular + 1; t++) {
                        if (t == 1) {
                            xFact = average * neg;
                        } else {
                            xFact = average * 2 * neg;
                        }
                        trackX += xFact;
                        rotation.add(rotation.get(i));
                        angle.add(180);
                        x.add(trackX);
                        y.add(trackY);
                        sideRadii.add((int) average);
                        if (t == numSmall) {
                            xFact = average * neg;
                            trackX += xFact;
                        }
                        Log.wtf("*--------Trackers", trackX + " " + trackY
                                + " " + sideRadii.get(sideRadii.size() - 1));
                    }

                } else if ((dv.xlist.get(i) -
                        dv.xlist.get((i + 1) % size)) == 0) {
                    double length = (-1 * dv.ylist.get(i) + dv.ylist.get((i + 1) % size));
                    int neg = (length < 0) ? -1 : 1;
                    length = Math.abs(length) - r1 - r2;

                    int amt1, amt2;
                    numRegular = (int) length / (average * 2);
                    numSmall = (int) (length - average * numRegular * 2) / ((int) (min * 2));
                    amt1 = (int) length - numRegular * average * 2 - numSmall * (int) min * 2;
                    int trackX = dv.xlist.get(i), trackY = dv.ylist.get(i);
                    double slope;
                    if (amt1 > 0 && numSmall > 0)
                        minTemp += amt1 / numSmall;
                    if (amt1 > min * 1.6) numSmall++;

                    double shift = r1;
                    double xFact = shift * neg;
                    trackY += xFact;
                    trackX = dv.xlist.get(i);
                    Log.wtf("*------Start Tracker (same x)", trackX + " " + trackY + " " + xFact + " " + length);
                    for (int t = 1; t < numSmall + 1; t++) {
                        if (t == 1) {
                            xFact = min * neg;
                        } else {
                            xFact = min * 2 * neg;
                        }
                        trackY += xFact;
                        rotation.add(rotation.get(i));
                        angle.add(180);
                        x.add(trackX);
                        y.add(trackY);
                        sideRadii.add((int) min);
                        //README If on the last center, move over to the edge.
                        if (t == numSmall) {
                            xFact = min * neg;
                            trackY += xFact;
                        }
                        Log.wtf("*--------Trackers", trackX + " " + trackY
                                + " " + sideRadii.get(sideRadii.size() - 1));
                    }
                    for (int t = 1; t < numRegular + 1; t++) {
                        if (t == 1) {
                            xFact = average * neg;
                        } else {
                            xFact = average * 2 * neg;
                        }
                        trackY += xFact;
                        rotation.add(rotation.get(i));
                        angle.add(180);
                        x.add(trackX);
                        y.add(trackY);
                        sideRadii.add((int) average);
                        if (t == numSmall) {
                            xFact = average * neg;
                            trackY += xFact;
                        }
                        Log.wtf("*--------Trackers", trackX + " " + trackY
                                + " " + sideRadii.get(sideRadii.size() - 1));
                    }
                } else {
                    int amt1, amt2;
                    numRegular = (int) diffLeft / (average * 2);
                    numSmall = (diffLeft - average * numRegular * 2) / ((int) (min * 2));
                    amt1 = diffLeft - numRegular * average * 2 - numSmall * (int) min * 2;
                    int trackX = dv.xlist.get(i), trackY = dv.ylist.get(i);
                    double slope;
                    if (amt1 > 0 && numSmall > 0)
                        minTemp += amt1 / numSmall;
                    if (amt1 > min * 1.6) numSmall++;

                    slope = (double) (dv.ylist.get(i) - dv.ylist.get((i + 1) % size)) / (double) (dv.xlist.get(i) -
                            dv.xlist.get((i + 1) % size));
                    //slope = Math.abs(slope);
                    //int neg = (dv.ylist.get(i) > dv.ylist.get((i + 1) % size) && slope > 0) ? -1 : 1;
                    int neg = 1;
                    if ((dv.ylist.get(i) < dv.ylist.get((i + 1) % size) && dv.xlist.get(i) >
                            dv.xlist.get((i + 1) % size)) || (dv.ylist.get(i) > dv.ylist.get((i + 1) % size)
                            && dv.xlist.get(i) > dv.xlist.get((i + 1) % size))) neg = -1;

                    //if (dv.ylist.get(i) > dv.ylist.get((i + 1) % size)) slope *= -1;

                    double xFact = 1, yFact = slope;
                    xFact = Math.sqrt(r1 * r1 / (slope * slope + 1));
                    yFact = slope * xFact;
                    // boolean addX = (dv.xlist.get((i+1)%size) > dv.xlist.get(i));
                    // boolean addY = (dv.ylist.get((i+1)%size) > dv.ylist.get(i));
                    trackX += xFact * neg;
                    trackY += yFact * neg;
                    Log.wtf("*------Start Tracker", trackX + " " + trackY + " " + slope
                            + " diffLeft: " + diffLeft + " amt: " + amt1);
                    //DONE Doesn't autoplot on the left most side from last vertex to first vertex.
                    // Slope is positive so it adds the centers off screen.
                    for (int t = 1; t < numSmall + 1; t++) {
                        if (t == 1) {
                            xFact = Math.sqrt(min * min / (slope * slope + 1));
                        } else {
                            xFact = Math.sqrt(4 * min * min / (slope * slope + 1));
                        }
                        yFact = slope * xFact;
                        trackX += xFact * neg;
                        trackY += yFact * neg;
                        rotation.add(rotation.get(i));
                        angle.add(180);
                        x.add(trackX);
                        y.add(trackY);
                        sideRadii.add((int) min);
                        if (t == numSmall) {
                            xFact = Math.sqrt(min * min / (slope * slope + 1));
                            trackX += xFact * neg;
                            yFact = slope * xFact;
                            trackY += yFact * neg;
                        }
                        Log.wtf("*--------Trackers", trackX + " " + trackY
                                + " " + sideRadii.get(sideRadii.size() - 1));
                    }
                    for (int t = 1; t < numRegular + 1; t++) {
                        if (t == 1) {
                            xFact = Math.sqrt(average * average / (slope * slope + 1));
                        } else {
                            xFact = Math.sqrt(4 * average * average / (slope * slope + 1));
                        }
                        yFact = slope * xFact;
                        trackX += xFact * neg;
                        trackY += yFact * neg;
                        rotation.add(rotation.get(i));
                        angle.add(180);
                        x.add(trackX);
                        y.add(trackY);
                        sideRadii.add((int) average);
                        if (t == numSmall) {
                            xFact = Math.sqrt(average * average / (slope * slope + 1));
                            yFact = slope * xFact;
                            trackX += xFact * neg;
                            trackY += yFact * neg;
                        }
                        Log.wtf("*--------Trackers", trackX + " " + trackY
                                + " " + sideRadii.get(sideRadii.size() - 1));
                    }
                }
            }
        }
        radius.addAll(sideRadii);

        dv.sprinkx.addAll(x);
        dv.sprinky.addAll(y);

        /*rotation.set(1, rotation.get(1) -90);
        rotation.set(2, rotation.get(2) +90);
        rotation.set(3, rotation.get(3)*-1);*/

        Log.wtf("Lists:", angle.toString() + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
                + rotation.toString() + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + radius.toString()
                + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + x.toString() + "  " + y.toString());

        dv.rotationList.addAll(rotation);
        dv.angleList.addAll(angle);
        dv.sprinkr.addAll(radius);
        //dv.sprinkx.addAll(x);
        ignoreSprinklers = dv.sprinkr.size();
        dv.invalidate();
    }

    public int addOrSubtract(boolean add, int orig, int val) {
        if (add) return orig + val;
        return orig - val;
    }

    public static int assignCheck(boolean b, int n, int newVal) {
        if (b) return n;
        return newVal;
    }

    public static int getLength(int cur, int prev) {
        int n1 = (int) Math.round(Math.sqrt(Math.pow(dv.xlist.get(cur) - dv.xlist.get(prev), 2) +
                Math.pow(dv.ylist.get(cur) - dv.ylist.get(prev), 2)));
        return n1;
    }

    public boolean leaveAlone = false;
    boolean done = false;
    int timesR = 0;

    public void lengthUpdater() {
        length.addTextChangedListener(new TextWatcher() {
            String previous = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (dv.xlist.size() > 2)
                    length.setEnabled(false);
                else
                    length.setEnabled(true);
                previous = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.wtf("* Inside: ", "inside here " + dv.xlist.size());
                String r = length.getText().toString();
                if (dv.currentMode != DrawingView.Mode.drawc && dv.currentMode != DrawingView.Mode.resetc) {
                    if (r == null) {
                        timesR = 0;
                    } else if (previous.length() > 0 && dv.xlist.size() > 2 && !leaveAlone) {
                        shortToast("To change the length, please reset the plot");
                        /*if (done) {
                            length.setText(previous);
                            done = false;
                        }
                        if (timesR == 0)
                            done = true;*/
                        //FIXME, leavealone needs to be true. It is stalling here.
                    /*removeAllLengths();
                    //TODO Redo all stuff textview lengths
                    int numVal = Integer.parseInt(r);
                    if (numVal < 2) {
                        makeToast("Please make your side length larger");
                    } else {
                        dv.length = numVal;
                        if (dv.pastMode == DrawingView.PastMode.CIRCLE) {
                            dv.ratio = dv.radius / numVal;
                        } else {
                            dv.ratio = (double) numVal / (double) (Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                    Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                            Log.wtf("Side Length Calculations", "Hypotenuse - " + Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                    Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                        }
                        //createAllTextViews();
                        Log.wtf("*  INFORMATION ON RATIO: ", "Ratio: " + dv.ratio + "  Length: " + dv.length);
                    }*/
                    } else if (dv.xlist.size() > 1) {
                        timesR = 0;
                        //Log.wtf("* Inside: ", "inside here deeper");
                        String temp = length.getText().toString();
                        if (temp == null) {
                            shortToast("Please enter the side length.");
                        } else if (temp.isEmpty() || temp.length() == 0) {
                            shortToast("You must enter the side length in feet");
                        } else {
                            int numVal = Integer.parseInt(temp);
                            dv.circleFeet = numVal;
                            if (numVal < 2) {
                                shortToast("Please make your side length larger");
                            } else {
                                //TODO Have to also set the value of dv.ratio to get
                                // the ratio between their length and pixel length;
                                dv.length = numVal;
                                //README Land is circle and currently drawing circle
                                if (dv.pastMode == DrawingView.PastMode.CIRCLE && (dv.currentMode
                                        == DrawingView.Mode.drawc || dv.currentMode == DrawingView.Mode.resetc)) {
                                    //dv.ratio = dv.radius / numVal;
                                    dv.ratio = (double) numVal / (dv.backUpRadius * (dv.screenW / 200));
                                    Log.wtf("**Ratio, ", "Ratio is: " + dv.ratio);

                                } else if (dv.wasCircle && (dv.currentMode == DrawingView.Mode.splot ||
                                        dv.currentMode == DrawingView.Mode.sreset)) {
                                    //README Land is circle and currently drawing sprinklers
                                    if (dv.radius == -5)
                                        dv.radius = 300;
                                    else
                                        dv.radius = dv.backUpRadius * (dv.screenW / 200);
                                } else {
                                    dv.ratio = (double) numVal / (double) (Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                            Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                                    Log.wtf("Side Length Calculations", "Hypotenuse - " + Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                            Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                                }
                                //Log.wtf("*  INFORMATION ON RATIO: ", "Ratio: " + dv.ratio + "  Length: " + dv.length);
                            }
                        }
                    } else if (length.getText().toString().equals("") || length.getText().toString().equals(previous)) {

                    } else {
                        length.setText("");
                        //leaveAlone = true;
                        shortToast("First, you must plot the first side.");
                    }
                } else {
                    timesR = 0;
                    String temp = length.getText().toString();
                    if (temp == null) {
                        shortToast("Please enter the radius in feet.");
                    } else if (temp.isEmpty() || temp.length() == 0) {
                        shortToast("You must enter the radius in feet");
                    } else {
                        int numVal = Integer.parseInt(temp);
                        dv.circleFeet = numVal;
                        if (numVal < 2) {
                            //shortToast("Please make your radius larger");
                        } else {
                            //TODO Have to also set the value of dv.ratio to get
                            // the ratio between their length and pixel length;
                            dv.length = numVal;
                            //README Below was commented to try to get correct ratio for circle land
                            //dv.ratio = dv.radius / numVal;
                            dv.ratio = (double) numVal / (dv.backUpRadius * (dv.screenW / 200));
                        }
                    }
                }
                leaveAlone = false;
            }
        });
    }

    private void createAllTextViews() {
        dv.idCounter++;
        maxSideLength = 0;
        for (int i = 0; i < dv.xlist.size() - 1; i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (dv.xlist.get(i) + dv.xlist.get(i + 1)) / 2;
            params.topMargin = (dv.ylist.get(i) + dv.ylist.get(i + 1)) / 2;

            TextView textView = new TextView(context);
            int val = (int) (Math.hypot(Math.abs(dv.xlist.get(i) - dv.xlist.get(i + 1)),
                    Math.abs(dv.ylist.get(i) - dv.ylist.get(i + 1))) * saveRadius);
            //if (maxSideLength < val) maxSideLength = val;
            //Log.wtf("**A", maxSideLength+"");
            textView.setText("" + val);
            textView.setId(dv.idCounter);
            textView.setLayoutParams(params);
            //makeToast("Making the text");
            rlDvHolder.addView(textView);
            dv.idCounter++;

            //DONE HAVE to deal with adding textview from last to first. and removing from previous touch.

            //Log.wtf("* Location: ", dv.xlist.get(posCount) + " " + xlist.get(posCount + 1)
            //       + " " + dv.ylist.get(posCount) + " " + dv.ylist.get(posCount + 1));

        }

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
        int xlast = dv.xlist.get(dv.xlist.size() - 1) + dv.xlist.get(0);
        int ylast = dv.ylist.get(dv.ylist.size() - 1) + dv.ylist.get(0);
        params2.leftMargin = (xlast) / 2;
        params2.topMargin = (ylast) / 2;

        TextView last = new TextView(context);
        last.setText("" + (int) (Math.hypot(Math.abs(dv.xlist.get(dv.xlist.size() - 1) - dv.xlist.get(0)),
                Math.abs(dv.ylist.get(dv.ylist.size() - 1) - dv.ylist.get(0))) * saveRadius));
        last.setId(dv.specialCounter);
        last.setLayoutParams(params2);
        //makeToast("Making the last text");
        rlDvHolder.addView(last);

        //dv.posCount++;
        dv.specialCounter--;
    }

    public boolean handleSideLength() {
        String temp = length.getText().toString();
        if (temp == null) {
            makeToast("Please enter the side length/radius.");
            return false;
        } else if (temp.isEmpty() || temp.length() == 0) {
            makeToast("You must enter the side length/radius in feet");
            return false;
        } else {
            int numVal = Integer.parseInt(temp);
            if (numVal < 2) {
                makeToast("Please make your side length larger");
                return false;
            } else {
                //TODO Have to also set the value of dv.ratio to get
                // the ratio between their length and pixel length;
                dv.length = numVal;
                if (dv.currentMode == DrawingView.Mode.drawc || dv.currentMode == DrawingView.Mode.resetc) {
                    dv.ratio = (double) numVal / (dv.backUpRadius * (dv.screenW / 200));

                    //dv.ratio = dv.radius / numVal;
                } else {
                    if (dv.wasCircle)
                        dv.ratio = (double) numVal / (dv.backUpRadius * (dv.screenW / 200));

                        //README Below is old method of ratio for circle land.
                        //dv.ratio = (double) numVal / (double) dv.backUpRadius;
                        /*dv.ratio = (double) numVal / (double) (Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));*/
                    else
                        dv.ratio = (double) numVal / (double) (Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                    //Log.wtf("Side Length Calculations", "Hypotenuse - " + Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                    //        Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                }
                return true;
            }
        }

    }

    AlertDialog.Builder loading;
    AlertDialog created;

    public void showLoading() {
        loading = new AlertDialog.Builder(IrrigationActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogCoordinate = inflater.inflate(R.layout.loading, null);
        loading.setCancelable(false);
        loading.setView(dialogCoordinate);

        created = loading.create();
        created.show();
        //makeToast("show loading called");
        Log.wtf("* Alert Dialog", "Showing Loading. showLoading() called");
    }

    public void hideLoading() {
        created.hide();
        created.cancel();
    }

    //README this function asks user for length of side after user has plotted 1 side.
    private void askForLength(boolean plot, int a) {
        /*new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(IrrigationActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogCoordinate = inflater.inflate(R.layout.specify_length, null);
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        /*AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Write your message here.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();*/
    }

    private static void askForLength(boolean plot) {
        /*final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.specify_length);

        TextView text = (TextView) dialog.findViewById(R.id.message);

        if (plot)
            text.setText("You just drew one side of the land area. Please specify the approximate " +
                    "length of that first side in feet.");
        else
            text.setText("You just chose to draw a circular area of land. " +
                    "Please specify its radius in feet.");

        final EditText input = dialog.findViewById(R.id.length);
        *//*TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                input.setText(editable.toString() + "ft");
            }
        };
        input.addTextChangedListener(textWatcher);*//*

        Button undo = (Button) dialog.findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog.cancel();
                //README INFO: In order to call dv.undo() below, dv was made static.
                // If any issues arise, try making dv not static.
                dv.undo();
            }
        });

        Button done = (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sInput = input.getText().toString();
                if (sInput != null) {
                    int num = Integer.parseInt(sInput);
                    if(num > 0){
                        dv.length = num;
                    }else{
                       // makeToast("Please specify a length greater than 0 feet.");
                    }
                }else{
                    //makeToast("Please specify the side length in feet.");
                }
            }
        });

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();*/
    }

    public Bitmap takeScreenShot(View view) {
        // configuramos para que la view almacene la cache en una imagen
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        view.buildDrawingCache();

        if (view.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

        // utilizamos esa cache, para crear el bitmap que tendra la imagen de la view actual
        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }

    public File takeScreenShot2() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        View view = dv;
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            view.setDrawingCacheEnabled(true);
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            view.buildDrawingCache();

            if (view.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

            // utilizamos esa cache, para crear el bitmap que tendra la imagen de la view actual
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            view.destroyDrawingCache();

            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
            return imageFile;
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
        return null;
    }

    static int messageCount = 0, specifyCount = 0;

    @TargetApi(Build.VERSION_CODES.O)
    int getSeek(SeekBar seekbar) {
        return seekbar.getProgress();
    }

    private void setRadiusBar() {
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //INFO this is when the user lets go of the slider
                //makeToast("Invalidating");
                //dv.sradius = seekBar.getProgress();
                int max = seekBar.getMax();
                int min = 1;
                Log.wtf("*Seek bar info:", "max: " + max + "  min: " + min + "  current: " + seekBar.getProgress());
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        min = getSeek(seekBar);
                    } else {
                        min = 50;
                    }
                } catch (Exception e) {
                    min = 50;
                }
                double sprinklerRadius = seekBar.getProgress() * (dv.screenW / 200);
                float bigCircleRadius = dv.setCircleRadiusTo;
                float bigCircleFeet = dv.length;
                int progress = seekBar.getProgress();
                progress -= 50;
                progress *= 2;
                dv.sradius = progress;

                if (dv.pastMode == DrawingView.PastMode.CIRCLE
                        && (dv.currentMode == DrawingView.Mode.splot ||
                        dv.currentMode == DrawingView.Mode.sreset)) {
                    double dif = Math.min(350, Math.ceil(dv.circleFeet / 1.75)) - 5;
                    //README SetToI is radius in feet.
                    double setToI = Math.min(350, Math.ceil(dv.circleFeet / 1.75)) - ((double) 100 - progress) / 100 * dif;
                    shortToast("Radius: " + (String.format("%1$,.1f", (setToI)) + " feet."));
                }

                if (dv.pastMode == DrawingView.PastMode.DRAW) {
                    double pixelSideLength = (double) maxSideLength / dv.ratio * 0.7d;
                    double setToI = ((double) ((double) dv.screenW / 1000) * Math.pow(seekBar.getProgress() / 5.9f, 2)) - 50;
                    setToI = ((double) progress / 100d * pixelSideLength) / 2;
                    double minSideLength = 4d / dv.ratio;

                    if (maxSideLength > 35) {
                        pixelSideLength = (double) 25d / dv.ratio;
                        minSideLength = 4d / dv.ratio;
                    }
                    if (maxSideLength > 50) {
                        pixelSideLength = (double) 35d / dv.ratio;
                        minSideLength = 4d / dv.ratio;
                    }
                    if (maxSideLength > 74) {
                        pixelSideLength = (double) 40d / dv.ratio;
                        minSideLength = 5d / dv.ratio;
                    }
                    if (maxSideLength > 90) {
                        pixelSideLength = (double) 50d / dv.ratio;
                        minSideLength = 6d / dv.ratio;
                    }
                    if (maxSideLength > 150) {
                        pixelSideLength = (double) 80d / dv.ratio;
                        minSideLength = 8d / dv.ratio;
                    }
                    if (maxSideLength > 325) {
                        pixelSideLength = (double) 250d / dv.ratio;
                        minSideLength = 10d / dv.ratio;
                    }

                    double dif = pixelSideLength - minSideLength;
                    setToI = pixelSideLength - ((double) 100 - progress) / 100 * dif;


                    Log.wtf("**IMPORTANT INFO :", (double) pixelSideLength + " " + sprinklerRadius
                            + " " + dv.sradius /*+ " " + dv.radius*/ + " Max - " + maxSideLength + " Min- " + minSideLength + " Part 1- " + (((double) 100 - progress) * dif) + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tProgress: " + progress +
                            "   Set to I - " + ((int) Math.round(setToI * 100000) / 100000));
                    //makeToast("Radius: " + (String.format("%1$,.1f", (sprinklerRadius * bigCircleFeet /bigCircleRadius))) + " feet.");
                    //makeToast("Radius: " + (String.format("%1$,.1f", (newProgress * dv.ratio))) + " feet.");
                    shortToast("Radius: " + (String.format("%1$,.1f", (setToI * ((double) dv.ratio)))) + " feet.");
                }

                /*double setTo = ((double) ((double) dv.screenW / 1000) * Math.pow(seekBar.getProgress() / 5.9f, 2));
                double scale = (double) bigCircleRadius / (bigCircleFeet - 2);
                double t = seekBar.getProgress() / 100 * scale;
                double newProgress = scale * dv.sradius;*/

                //README IF not on circle mode
                if (dv.currentMode != DrawingView.Mode.drawc) {

                    /*Log.wtf("**IMPORTANT INFO :", (double) pixelSideLength + " " + sprinklerRadius
                            + " " + dv.sradius *//*+ " " + dv.radius*//* + " Max - " + maxSideLength + " Min- " + minSideLength + " Part 1- " + (((double) 100 - progress) * dif) + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tProgress: " + progress +
                            "   Set to I - " + ((int) Math.round(setToI * 100000) / 100000));
                    //makeToast("Radius: " + (String.format("%1$,.1f", (sprinklerRadius * bigCircleFeet /bigCircleRadius))) + " feet.");
                    //makeToast("Radius: " + (String.format("%1$,.1f", (newProgress * dv.ratio))) + " feet.");
                    shortToast("Radius: " + (String.format("%1$,.1f", (setToI * ((double) dv.ratio)))) + " feet.");
                */
                }

                if (dv.currentMode == DrawingView.Mode.drawc) {
                    messageCount++;
                    if (messageCount % 3 == 1)
                        makeToast("ATTENTION!\nEnter the length of the circle's radius in feet.");
                    //dv.radius = progress;
                    //dv.backUpRadius = progress;
                    //dv.ratio =
                }

                //makeToast("Radius: " + (String.format("%1$,.1f", (50f*((double)seekBar.getProgress()/(double)seekBar.getMax()) - 24))) + " feet.");
                // dv.sradius = (int) (40f*((double)seekBar.getProgress()/(double)seekBar.getMax()) - 18);
                /*Log.wtf("* Sprinkler Radius Info: ", "Pixel radius (setTo): " + setToI + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
                        + "Radius: " + (String.format("%1$,.1f", (setToI * dv.ratio))) + " feet");*/
                //makeToast("Updating sradius: " + dv.sradius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                //dv.sradius = progress;


                if (dv.pastMode == DrawingView.PastMode.CIRCLE) {
                    double dif = Math.min(350, Math.ceil(dv.circleFeet / 1.75)) - 5;
                    //README SetToI is radius in feet.
                    double setToI = Math.min(350, Math.ceil(dv.circleFeet / 1.75)) - ((double) 100 - (progress - 50) * 2) / 100 * dif;
                    //double pixelRadius = setToI/dv.ratio;
                    //Log.wtf("--Radius", setToI + " / " + dv.ratio + " ==== " + pixelRadius);
                    double pixelRadius = setToI / dv.length * (dv.backUpRadius * (dv.screenW / 200));
                    Log.wtf("--Radius", setToI + " / " + dv.length + " / " +
                            +(int) ((dv.backUpRadius * (dv.screenW / 200))) + " ==== " + pixelRadius);
                    //   Log.wtf("----Extremes", radius.getMin() + " " + radius.getMax());
                    dv.circleSprinklerRadius = pixelRadius;
                }
                if (dv.sprinkr.size() > 0) {
                    //dv.sprinkr.set(dv.sprinkr.size() - 1, (int) ((double) ((double) dv.screenW / 1000) * Math.pow(progress / 5.9f, 2)) - 50);

                    if (dv.pastMode == DrawingView.PastMode.CIRCLE) {
                        double dif = Math.min(350, Math.ceil(dv.circleFeet / 1.75)) - 5;
                        //README SetToI is radius in feet.
                        double setToI = Math.min(350, Math.ceil(dv.circleFeet / 1.75)) - ((double) 100 - (progress - 50) * 2) / 100 * dif;
                        //double pixelRadius = setToI/dv.ratio;
                        //Log.wtf("--Radius", setToI + " / " + dv.ratio + " ==== " + pixelRadius);
                        double pixelRadius = setToI / dv.length * (dv.backUpRadius * (dv.screenW / 200));
                        Log.wtf("--Radius", setToI + " / " + dv.length + " / " +
                                +(int) ((dv.backUpRadius * (dv.screenW / 200))) + " ==== " + pixelRadius);
                        // Log.wtf("----Extremes", radius.getMin() + " " + radius.getMax());
                        dv.circleSprinklerRadius = pixelRadius;
                        dv.sprinkr.set(dv.sprinkr.size() - 1, (int) pixelRadius);
                    }

                    if (dv.pastMode == DrawingView.PastMode.DRAW) {
                        double pixelSideLength = (double) maxSideLength / dv.ratio * 0.7d;
                        progress -= 50;
                        progress *= 2;
                        dv.sradius = progress;
                        double setToI = ((double) ((double) dv.screenW / 1000) * Math.pow(seekBar.getProgress() / 5.9f, 2)) - 50;
                        setToI = ((double) progress / 100d * pixelSideLength) / 2;
                        double minSideLength = 4d / dv.ratio;
                        if (maxSideLength > 35) {
                            pixelSideLength = (double) 25d / dv.ratio;
                            minSideLength = 4d / dv.ratio;
                        }
                        if (maxSideLength > 50) {
                            pixelSideLength = (double) 35d / dv.ratio;
                            minSideLength = 4d / dv.ratio;
                        }
                        if (maxSideLength > 74) {
                            pixelSideLength = (double) 40d / dv.ratio;
                            minSideLength = 5d / dv.ratio;
                        }
                        if (maxSideLength > 90) {
                            pixelSideLength = (double) 50d / dv.ratio;
                            minSideLength = 6d / dv.ratio;
                        }
                        if (maxSideLength > 150) {
                            pixelSideLength = (double) 80d / dv.ratio;
                            minSideLength = 8d / dv.ratio;
                        }
                        if (maxSideLength > 325) {
                            pixelSideLength = (double) 250d / dv.ratio;
                            minSideLength = 10d / dv.ratio;
                        }
                        double dif = pixelSideLength - minSideLength;
                        setToI = pixelSideLength - ((double) 100 - progress) / 100 * dif;
                        dv.sprinkr.set(dv.sprinkr.size() - 1, (int) setToI);
                        //dv.sprinkr.set(dv.sprinkr.size() - 1, (int) (((double) progress / 100d * pixelSideLength) / 2));
                    }
                }
                //dv.sradius = (int) (50f*((double)seekBar.getProgress()/(double)seekBar.getMax()) - 24);

                if (dv.currentMode == DrawingView.Mode.drawc) {
                    messageCount++;
                    if (messageCount % 15 == 0)
                        makeToast("ATTENTION!\nEnter the length of the circle's radius in feet.");
                    dv.radius = progress;
                    dv.backUpRadius = progress;
                    //dv.ratio =
                }


                dv.invalidate();
            }
        });
    }

    double saveRadius = 0;
    static int maxSideLength = 0;

    private void setButtonClick() {
        //README I commented out below because when user presses make into rectangle buttona and then plots sprimnklers
        // The radius is completely messed up.
        polygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Deal with making a polygon.
                if (dv.xlist.size() == 4 /*&& !rectangle*/) {
                    rectangle = true;
                    //dv.currentMode = DrawingView.Mode.POLYGON;
                    //ArrayList<Integer> newX = new ArrayList<>();
                    //ArrayList<Integer> newY = new ArrayList<>();
                    int x1 = dv.xlist.get(0);
                    int x2 = dv.xlist.get(1);
                    int x3 = dv.xlist.get(2);
                    int x4 = dv.xlist.get(3);
                    int y1 = dv.ylist.get(0);
                    int y2 = dv.ylist.get(1);
                    int y3 = dv.ylist.get(2);
                    int y4 = dv.ylist.get(3);
                    int tLx, tLy, tRx, tRy, bLy, bRy, bLx, bRx;
                    ArrayList<Integer> notUsed = new ArrayList<>();
                    notUsed.add(1);
                    notUsed.add(2);
                    notUsed.add(3);
                    notUsed.add(4);
//                    if(x1 < x2 && x1 < x3)
                    double dist1 = Math.abs(x1 - x2);
                    double dist2 = Math.abs(x1 - x4);
                    if (dist1 < dist2) {
                        tLx = x2 / 2 + x1 / 2;
                        tLy = y2 / 2 + y3 / 2;
                        bRx = x3 / 2 + x4 / 2;
                        bRy = y1 / 2 + y4 / 2;
                    } else {
                        tLx = x1 / 2 + x4 / 2;
                        tLy = y1 / 2 + y2 / 2;
                        bRx = x3 / 2 + x2 / 2;
                        bRy = y3 / 2 + y4 / 2;
                    }

                    Log.wtf("*-* Points", x1 + ", " + y1 + "  " + x2 + ", " + y2 + "  " + x3 + ", " + y3 + "  " + x4 + ", " + y4);



                    /*int tL = 1000, bR = 0;
                    int tLy = 1000, bRy = 0;
                    for (int i = 0; i < dv.xlist.size(); i++) {
                        if (dv.xlist.get(i) < tL) tL = dv.xlist.get(i);
                        if (dv.ylist.get(i) < tLy) tLy = dv.ylist.get(i);

                        if (dv.xlist.get(i) > bR) bR = dv.xlist.get(i);
                        if (dv.ylist.get(i) > bRy) bRy = dv.ylist.get(i);
                    }*/
                    dv.resetTouchPoints();

                    dv.xlist.add(tLx);
                    dv.xlist.add(bRx);
                    dv.xlist.add(bRx);
                    dv.xlist.add(tLx);
                    dv.ylist.add(tLy);
                    dv.ylist.add(tLy);
                    dv.ylist.add(bRy);
                    dv.ylist.add(bRy);
                    Log.wtf("*-* New List X:", dv.xlist.toString());
                    Log.wtf("*-* New List Y:", dv.ylist.toString());

                    /*dv.xs.add(tL);
                    dv.xs.add(bR);
                    dv.xs.add(bR);
                    dv.xs.add(tL);
                    dv.ys.add(tLy);
                    dv.ys.add(tLy);
                    dv.ys.add(bRy);
                    dv.ys.add(bRy);*/

                    dv.resetCoordinates();
                    removeAllLengths(false);
                    dv.invalidate();
                } else {
                    shortToast("The land must have 4 coordinates to be made into a rectangle.");
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //makeToast("MODE: " + dv.currentMode);
                switch (dv.currentMode) {
                    case splot:
                        //TODO Reset the sprinkler points.
                        dv.currentMode = DrawingView.Mode.sreset;
                        ignoreSprinklers = 0;
                        dv.resetCoordinates();
                        dv.invalidate();
                        break;
                    case drawc:
                        dv.currentMode = DrawingView.Mode.resetc;
                        removeAllLengths();
                        dv.pastMode = DrawingView.PastMode.CIRCLE;
                        dv.resetCoordinates();
                        dv.invalidate();
                        break;
                    case DOTPLOT:
                        dv.currentMode = DrawingView.Mode.RESET;
                        dv.pastMode = DrawingView.PastMode.DRAW;
                        removeAllLengths();
                        dv.resetCoordinates();
                        dv.invalidate();
                        break;
                    case PLOT:
                        //Current mode is drawing.
                        dv.pastMode = DrawingView.PastMode.DRAW;
                        dv.currentMode = DrawingView.Mode.RESET;
                        removeAllLengths();
                        dv.resetCoordinates();
                        dv.resetTouchPoints();
                        //dv.invalidate();
                        break;
                    case DRAWING:
                        dv.resetTouchPoints();
                        //dv.currentMode = DrawingView.Mode.RECORDING;
                        //dv.currentMode = DrawingView.Mode.DOTPLOT;
                        dv.pastMode = DrawingView.PastMode.DRAW;
                        dv.resetCoordinates();
                        removeAllLengths();
                        dv.invalidate();
                        button.setText("Draw Line");
                        break;
                    case RECORDING:
                        dv.currentMode = DrawingView.Mode.DRAWING;
                        removeAllLengths();
                        dv.resetCoordinates();
                        dv.pastMode = DrawingView.PastMode.DRAW;
                        dv.invalidate();
                        button.setText("Reset");
                        break;
                }
            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dv.currentMode != DrawingView.Mode.splot) {
                    dv.undo();

                    if (dv.xlist.size() == 0) {
                        removeAllLengths();
                    }
                    if (dv.xlist.size() < 3)
                        length.setEnabled(true);


                    //INFO REMOVE THE LATEST TEXTVIEW
                    /*if (dv.idCounter != 0) {
                        dv.idCounter--;
                        TextView t = (TextView) rlDvHolder.findViewById(dv.idCounter);

                        t.setVisibility(View.GONE);
                    }
                    //INFO Remove the connection between last point and first point
                    if (dv.specialCounter != Integer.MAX_VALUE) {
                        dv.specialCounter++;
                        rlDvHolder.findViewById(++dv.specialCounter).setVisibility(View.GONE);
                    }*/
                    removeAllLengths(true);
                    dv.resetCoordinates();

                    Log.wtf("* Text ID Info:", dv.idCounter + " " + dv.specialCounter);
                } else {
                    //README Added below because even when undo button pressed, angle list only grew in size.
                    //  This may have potentially led to problem where randomly when plotting sprinklers
                    // sprinkler from 5 taps ago changes angles.
                    ignoreSprinklers--;
                    if (dv.angleList.size() > 0) {
                        dv.angleList.remove(dv.angleList.size() - 1);
                        dv.rotationList.remove(dv.rotationList.size() - 1);
                    }
                    dv.removeLastSprinkler();
                    dv.resetCoordinates();
                    dv.invalidate();
                }

            }
        });
    }

    public void removeAllLengths() {

        for (int i = 0; i < dv.idCounter; i++)
            if (rlDvHolder.findViewById(i) != null)
                rlDvHolder.findViewById(i).setVisibility(View.GONE);
        for (int i = Integer.MAX_VALUE; i > dv.specialCounter; i--)
            if (rlDvHolder.findViewById(i) != null)
                rlDvHolder.findViewById(i).setVisibility(View.GONE);
        saveRadius = dv.ratio;
        dv.ratio = 1;

        //makeToast("HERE");
        dv.length = 0;
        leaveAlone = true;
        length.setText("");
        length.setEnabled(true);
    }

    public static void removeAllLengths(boolean t) {
        for (int i = 0; i < dv.idCounter; i++)
            if (rlDvHolder.findViewById(i) != null)
                rlDvHolder.findViewById(i).setVisibility(View.GONE);
        for (int i = Integer.MAX_VALUE; i > dv.specialCounter; i--)
            if (rlDvHolder.findViewById(i) != null)
                rlDvHolder.findViewById(i).setVisibility(View.GONE);
    }


    public void angleAndRotateClicks() {
        left1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dv.angle = Math.max((dv.angle - 30) % 360, 0);
                if (dv.angle == 0)
                    dv.angle = 360;


                double rotateDisplay = dv.rotate;
                if (rotateDisplay > 180)
                    rotateDisplay -= 360;
                shortToast("Angle: " + dv.angle + "  Rotate: " + rotateDisplay);

                Log.wtf("Angle: " + dv.angle, "  Rotate: " + rotateDisplay);

                if (dv.sprinkx.size() > 0) {
                    dv.rotationList.set(dv.sprinkx.size() - 1, dv.rotate);
                    dv.angleList.set(dv.sprinkx.size() - 1, dv.angle);
                    dv.invalidate();
                    //dv.plotSprinklers2(dv.mCanvas);
                }
                return false;
            }
        });
        right1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dv.angle = Math.max((dv.angle + 30) % 360, 0);
                if (dv.angle == 0)
                    dv.angle = 360;

                double rotateDisplay = dv.rotate;
                if (rotateDisplay > 180)
                    rotateDisplay -= 360;
                shortToast("Angle: " + dv.angle + "  Rotate: " + rotateDisplay);
                Log.wtf("Angle: " + dv.angle, "  Rotate: " + rotateDisplay);

                if (dv.sprinkx.size() > 0) {
                    dv.rotationList.set(dv.sprinkx.size() - 1, dv.rotate);
                    dv.angleList.set(dv.sprinkx.size() - 1, dv.angle);
                    dv.invalidate();
                    //dv.plotSprinklers2(dv.mCanvas);
                }
                return false;
            }
        });
        left2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (dv.angle != 360)
                    dv.rotate = (dv.rotate - 15) % 360;
                else
                    shortToast("Change the sprinkler angle before rotating it.");

                if (dv.rotate < 0)
                    dv.rotate += 360;

                double rotateDisplay = dv.rotate;
                if (rotateDisplay > 180)
                    rotateDisplay -= 360;
                shortToast("Angle: " + dv.angle + "  Rotate: " + rotateDisplay);
                Log.wtf("Angle: " + dv.angle, "  Rotate: " + dv.rotate);

                if (dv.sprinkx.size() > 0) {
                    dv.rotationList.set(dv.sprinkx.size() - 1, dv.rotate);
                    dv.angleList.set(dv.sprinkx.size() - 1, dv.angle);
                    dv.invalidate();
                    //dv.plotSprinklers2(dv.mCanvas);
                }
                return false;
            }
        });
        right2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (dv.angle != 360)
                    dv.rotate = (dv.rotate + 15) % 360;
                else
                    shortToast("Change the sprinkler angle before rotating it.");

                double rotateDisplay = dv.rotate;
                if (rotateDisplay > 180)
                    rotateDisplay -= 360;
                shortToast("Angle: " + dv.angle + "  Rotate: " + rotateDisplay);
                Log.wtf("Angle: " + dv.angle, "  Rotate: " + dv.rotate);

                if (dv.sprinkx.size() > 0) {
                    dv.rotationList.set(dv.sprinkx.size() - 1, dv.rotate);
                    dv.angleList.set(dv.sprinkx.size() - 1, dv.angle);
                    dv.invalidate();
                    //dv.plotSprinklers2(dv.mCanvas);
                }
                return false;
            }
        });
    }

    public static int ignoreSprinklers = 0;

    public static class DrawingView extends View {
        private static final float TOUCH_TOLERANCE = 4;
        public int width;
        public int height;
        public int radius = -5;
        public int circleFeet;
        public static int sradius = -5;
        public static int backUpRadius = -5;
        public static double circleSprinklerRadius = -1;
        List<Integer> xlist = new ArrayList<>();
        List<Integer> ylist = new ArrayList<>();
        List<Integer> sprinkx = new ArrayList<>();
        List<Integer> sprinky = new ArrayList<>();
        List<Integer> sprinkr = new ArrayList<>();
        List<Boolean> changeAngle = new ArrayList<>();
        List<Boolean> changeRotation = new ArrayList<>();
        List<Integer> angleList = new ArrayList<>();
        List<Integer> rotationList = new ArrayList<>();
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        //TODO Comment out below if you want to use a dotplot
        //  Mode currentMode = Mode.PLOT;
        Mode currentMode = Mode.DOTPLOT;
        PastMode pastMode = PastMode.DRAW;
        Context context;
        Paint linePaint;
        Canvas cd;
        public int attentionCounter = -1;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private Paint circlePaint;
        private Paint sprinklerC;
        private Paint sprinklerSurround;
        private Paint sprinklerBorder;
        private Paint fillPaint;
        TextView display;
        private Path circlePath;
        private float mX, mY;
        public boolean wasCircle = false;
        int screenW, screenH;
        public int length = 0;
        public double ratio = 1;
        public float setCircleRadiusTo = 1;
        public int angle = 360;
        public int rotate = 0;

        public DrawingView(Context c, TextView display, int height, int width) {
            super(c);
            context = c;
            mPath = new Path();
            cd = new Canvas();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLACK);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(8f);

            screenW = width;
            screenH = height;

            this.display = display;
            //makeToast("HI");

            fillPaint = new Paint();
            fillPaint.setAntiAlias(true);
            fillPaint.setColor(0X4Fc0f7b5);
            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setStrokeJoin(Paint.Join.MITER);

            sprinklerSurround = new Paint();
            sprinklerSurround.setAntiAlias(true);
            sprinklerSurround.setColor(0XAA76f760);
            sprinklerSurround.setStyle(Paint.Style.FILL);
            sprinklerSurround.setStrokeJoin(Paint.Join.MITER);

            sprinklerC = new Paint();
            sprinklerC.setAntiAlias(true);
            sprinklerC.setColor(0X7557c4ff);
            sprinklerC.setStyle(Paint.Style.FILL);
            sprinklerC.setStrokeJoin(Paint.Join.MITER);

            linePaint = new Paint();
            linePaint.setStrokeWidth(8f);
            linePaint.setColor(Color.parseColor("#369646"));
            linePaint.setStrokeWidth(8f);
            linePaint.setStyle(Paint.Style.STROKE);
        }

        public void resetSprinklers() {
            sprinkx = new ArrayList<>();
            sprinky = new ArrayList<>();
            sprinkr = new ArrayList<>();
            rotationList = new ArrayList<>();
            angleList = new ArrayList<>();
            invalidate();
        }

        public void resetTouchPoints() {
            xlist = new ArrayList<>();
            ylist = new ArrayList<>();
            xs = new ArrayList<>();
            ys = new ArrayList<>();
            Log.wtf("*RESET RESET", "Touch points were reset");
            invalidate();
        }

        public void removeLastSprinkler() {
            if (sprinkx.size() > 0) {
                sprinkx.remove(sprinkx.size() - 1);
                sprinkr.remove(sprinkr.size() - 1);
                sprinky.remove(sprinky.size() - 1);
            }
        }

        public void undo() {
            if (xlist.size() > 0) {
                Log.d("Removal,", "before size is" + xlist.size());
                xlist.remove((int) xlist.size() - 1);
                ylist.remove((int) ylist.size() - 1);
                Log.d("Removal,", "Now size is" + xlist.size());
                invalidate();
            }
        }

        public static boolean interacted;
        public static boolean subMenuAdded;

        @Override
        protected void onDraw(Canvas canvas) {
           /*Path path = new Path();
           boolean first = true;
           for(Point point : points){
               if(first){
                   first = false;
                   path.moveTo(point.x, point.y);
               }
               else{
                   path.lineTo(point.x, point.y);
               }
           }
           canvas.drawPath(path, paint);*/
            mCanvas = canvas;
            if (interacted)
                if (dv.sprinkx.size() > 0) {
                    // Log.wtf("Adding", "adding sub");
                    if (!subMenuAdded) {
                        measurements = menuOptions.addSubMenu("Measurements");
                        measurements.add(0, 1, Menu.FIRST, "Show Coordinates");
                        measurements.add(0, 2, Menu.NONE, "Sprinkler Info");
                    }
                    subMenuAdded = true;
                    MenuItem item2 = menuOptions.findItem(R.id.coordinates);
                    item2.setVisible(false);
                } else {
                    //measurements.clear();
                    subMenuAdded = false;
                    if (measurements != null)
                        menuOptions.removeItem(measurements.getItem().getItemId());
                    MenuItem item2 = menuOptions.findItem(R.id.coordinates);
                    item2.setVisible(true);
                }

            Paint wallpaint = new Paint();
            wallpaint.setColor(Color.parseColor("#4Fcffcc7"));
            wallpaint.setColor(Color.parseColor("#4Fc3fcb8"));
            wallpaint.setColor(Color.parseColor("#4Fc0f7b5"));
            wallpaint.setStyle(Paint.Style.FILL);

            Path wallpath = new Path();
            wallpath.reset(); // only needed when reusing this path for a new build
            if (dv.xlist.size() > 0)
                wallpath.moveTo(dv.xlist.get(0), dv.ylist.get(0)); // used for first point
            for (int i = 1; i < dv.xlist.size(); i++) {
                wallpath.lineTo(dv.xlist.get(i), dv.ylist.get(i));
            }
            canvas.drawPath(wallpath, wallpaint);


            //INFO I think when you call invalidate() it calls onDraw again. Check it out by putting
            // your custom draw function for the line here. Test it out.
            //drawCustomLine(mCanvas, downx, downy, upx, upy);
            switch (currentMode) {
                case splot:
                    if (sprinkx.size() == 0) {
                        if (dv.pastMode == PastMode.DRAW) {
                            autoplot.setAnimation(fadeIn);
                            autoplot.setVisibility(View.VISIBLE);
                        } else autoplot.setVisibility(View.INVISIBLE);
                    } else {
                        if (dv.pastMode == PastMode.DRAW) {
                            autoplot.setAnimation(fadeOut);
                            autoplot.setVisibility(View.INVISIBLE);
                        } else autoplot.setVisibility(View.INVISIBLE);
                    }

                    plotSprinklers2(canvas);
                    Log.wtf("--Sprinkler List", sprinkr.toString());
                    //makeToast("Plotting sprinklers");
                    //mmakeToast("Was Circle? : " + wasCircle);
                    if (!wasCircle) {
                        resetCoordinates();
                        removeAllLengths(true);
                        handleCoordinates();
                        drawLine(canvas);
                    } else {
                        resetCoordinates();
                        handleCircleCoordinates();
                        if (radius == -5) {
                            backUpRadius = 292 * 200 / screenW;
                            mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                    300, fillPaint);
                            mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                    308, linePaint);
                        } else {
                            mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                    backUpRadius * (screenW / 200), fillPaint);
                            mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                    backUpRadius * (screenW / 200) + 8, linePaint);
                            Log.wtf("Land Area Being Drawn INFO", ":" + (backUpRadius * (screenW / 200)));
                        }
                    }
                    /*angle = 360;
                    rotate = 0;*/

                    break;
                case sreset:
                    currentMode = Mode.splot;
                    resetSprinklers();
                    resetCoordinates();
                    if (wasCircle)
                        handleCircleCoordinates();
                    else handleCoordinates();
                    if (sprinkx.size() == 0) {
                        if (dv.pastMode == PastMode.DRAW) {
                            autoplot.setAnimation(fadeIn);
                            autoplot.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (dv.pastMode == PastMode.DRAW) {
                            autoplot.setAnimation(fadeOut);
                            autoplot.setVisibility(View.INVISIBLE);
                        }
                    }
                    //makeToast("Resetting sprinklers.");
                    break;
                case DOTPLOT:
                    //Log.wtf("*DOTPLOT", "DOT Plot being called");
                    removeAllLengths(true);
                    resetCoordinates();
                    showDots(canvas);
                    handleCoordinates();
                    drawLine(canvas);
                    //autoplot.setAnimation(fadeOut);
                    autoplot.setVisibility(View.INVISIBLE);
                    if (xlist.size() == 2) {
                        //TODO Make an ALert Dialog asking for the length of the side that has been drawn.
                        //NOTES Make it non-dimissible, but provide an Undo and a done button. for undo,
                        //  call dv.undo();
                        attentionCounter++;
                        if (attentionCounter % 5 == 0)
                            makeToast("ATTENTION!\nEnter the length of this first side in feet.");
                        //MainActivity.askForLength(true);
                    }
                    break;
                case RESET:
                    //Need to reset the canvas
                    currentMode = Mode.DOTPLOT;
                    autoplot.setAnimation(fadeOut);
                    autoplot.setVisibility(View.INVISIBLE);
                    resetTouchPoints();
                    resetSprinklers();
                    removeAllLengths(true);
//makeToast("Resetting");
                    //TODO Try uncommenting and commenting out below line to see if it works after reset button
                    //linePaint.setColor(Color.WHITE);
                    Log.wtf("*RESET RESET RESET RESET RESET", "Reset was called. Reset screen.");
                    break;
                case PLOT:
                    resetCoordinates();
                    handleCoordinates();
                    autoplot.setAnimation(fadeOut);
                    autoplot.setVisibility(View.INVISIBLE);
                    drawCustomLine(mCanvas, downx, downy, upx, upy);
                    break;
                case resetc:
                    autoplot.setAnimation(fadeOut);
                    currentMode = Mode.drawc;
                    //makeToast("Reset c. Mode is draw c");
                    break;
                case drawc:
                    //makeToast("In draw c");
                    //README This is for drawing the circle region.
                    //MainActivity.askForLength(false);
                    wasCircle = true;
                    resetCoordinates();
                    //autoplot.setAnimation(fadeOut);
                    autoplot.setVisibility(View.INVISIBLE);
                    handleCircleCoordinates();
                    if (radius == -5) {
                        setCircleRadiusTo = 300;
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo, fillPaint);
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo + 8, linePaint);
                    } else {
                        setCircleRadiusTo = backUpRadius * (screenW / 200);
                        Log.wtf("*BIG CIRCLE RADIUS: ", setCircleRadiusTo + " radius:" + radius + " ScreenW:" + screenW);
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo, fillPaint);
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo + 8, linePaint);
                    }
                    break;
                case DRAWING:
                    //showDots(canvas);
                    //drawLine(canvas);
                    break;
                case RECORDING:
                    //showDots(canvas);
                    break;

            }
            super.onDraw(canvas);
        }

        private void handleCircleCoordinates() {
            if (landCoordinates) {
                dv.idCounter++;
                coordinateIds.add(new Coordinates(dv.idCounter, screenW / 2 + 63, screenH / 2 - 425, false));
            }
            if (sprinklerCoordinates) {
                for (int i = 0; i < sprinkx.size(); i++) {
                    dv.idCounter++;
                    coordinateIds.add(new Coordinates(dv.idCounter, sprinkx.get(i), sprinky.get(i), true));
                }
            }
            drawCoordinates(coordinateIds);
        }

        private void handleCoordinates() {
            if (landCoordinates) {
                for (int i = 0; i < xlist.size(); i++) {
                    dv.idCounter++;
                    coordinateIds.add(new Coordinates(dv.idCounter, xlist.get(i), ylist.get(i), false));
                }
            }
            if (sprinklerCoordinates) {
                for (int i = 0; i < sprinkx.size(); i++) {
                    dv.idCounter++;
                    coordinateIds.add(new Coordinates(dv.idCounter, sprinkx.get(i), sprinky.get(i), true));
                }
            }
            drawCoordinates(coordinateIds);
        }

        private void drawCoordinates(ArrayList<Coordinates> coordinateIds) {
            Coordinates lowerLeft = new Coordinates(0, 0, 0, true);
            if (dv.xlist.size() > 0)
                lowerLeft = new Coordinates(5, dv.xlist.get(0), dv.ylist.get(0), true);
            for (int i = 1; i < dv.xlist.size(); i++) {
                if (lowerLeft.compareTo(new Coordinates(3, dv.xlist.get(i), dv.ylist.get(i), true)))
                    lowerLeft = new Coordinates(3, dv.xlist.get(i), dv.ylist.get(i), true);
            }
            //INFO LowerLeft should contain teh lowerleft coordinate

            int average = 0;
            for (int i : ylist) {
                average += i;
            }
            if (ylist.size() > 0)
                average /= ylist.size();

            int x = lowerLeft.getX(), y = lowerLeft.getY();
            for (Coordinates c : coordinateIds) {
                double xDif = c.getX() - x;
                double yDif = y - c.getY();
                if (xDif == 0)
                    xDif = 0;
                if (yDif == 0)
                    yDif = 0;

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                int setToX = c.getX() - 101;
                int setToY = c.getY() - 65;
                if (setToY < average) setToY -= 20;
                else setToY += 85;
                Log.wtf("*-* Outside or not", "" + outside(setToX, setToY));
                /*if(setToX < 100)
                    setToX+=110;
                else {
                    if (!outside(setToX, setToY)) {
                        //setToX += 140;
                        setToX += 100;
                        if (setToX > screenW - 125)
                            setToX -= 100;
                    }
                }*/

                String str1, str2;
                TextView textView = new TextView(context);
                if (c.isSprinkler()) {
                    setToX = c.getX() - 84;
                    setToY = c.getY() - 21;
                    str1 = String.format("%.1f", xDif * dv.ratio);
                    if (str1.charAt(str1.length() - 1) == '0' && str1.charAt(str1.length() - 2) == '.')
                        str1 = str1.substring(0, str1.length() - 2);
                    str2 = String.format("%.1f", yDif * dv.ratio);
                    if (str2.charAt(str2.length() - 1) == '0' && str2.charAt(str2.length() - 2) == '.')
                        str2 = str2.substring(0, str2.length() - 2);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    textView.setTextColor(Color.parseColor("#1665a6"));
                    textView.setText(str1 + ", " + str2);
                } else {
                    if (!wasCircle) {
                        str1 = String.format("%.0f", xDif * dv.ratio);
                        str2 = String.format("%.0f", yDif * dv.ratio);
                        if (str1.equals("-0")) str1 = "0";
                        if (str2.equals("-0")) str2 = "0";
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                        textView.setTextColor(Color.parseColor("#458f61"));
                    } else {
                        str1 = "0";
                        str2 = "0";
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        textView.setTextColor(Color.parseColor("#000000"));
                    }
                    textView.setText("(" + str1 + "," + str2 + ")");
                }

                params.leftMargin = setToX;
                params.topMargin = setToY;


                textView.setId(c.getTextId());
                textView.setLayoutParams(params);
                rlDvHolder.addView(textView);

                /*params.leftMargin = setToX;
                params.topMargin = setToY;

                TextView textView = new TextView(context);
                String str1 = String.format("%.0f", xDif * dv.ratio);
                *//*if (str1.charAt(str1.length() - 1) == '0' && str1.charAt(str1.length() - 2) == '.')
                    str1 = str1.substring(0, str1.length() - 2);*//*
                String str2 = String.format("%.0f", yDif * dv.ratio);
                *//*if (str2.charAt(str2.length() - 1) == '0' && str2.charAt(str2.length() - 2) == '.')
                    str2 = str2.substring(0, str2.length() - 2);*//*

                textView.setText("(" + str1 + "," + str2 + ")");
                textView.setId(c.getTextId());
                textView.setLayoutParams(params);
                textView.setTextColor(Color.parseColor("#458f61"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
                rlDvHolder.addView(textView);*/
                //DONE Reset not working
                // Do it for sprinklers
                //  Text Size not changed
                //  Update on dismiss, not just back button press.
            }
            dv.idCounter++;
        }

        private void resetCoordinates() {
            for (Coordinates i : coordinateIds)
                rlDvHolder.findViewById(i.getTextId()).setVisibility(View.GONE);

            coordinateIds.removeAll(coordinateIds);
            dv.idCounter++;
        }

        boolean down = true;
        boolean up = true;
        int downx, downy;
        int upx, upy;

        int exceededCount = 0;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            interacted = true;
            int x = (int) event.getX();
            int y = (int) event.getY();
            //makeToast("TAPPED");
            //README Uncomment below if you are trying to do drawCustomLine()
           /*switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                   makeToast("Finger Down: " + x + " " + y);
                   if (down) {
                       downx = x;
                       downy = y;
                       xlist.add(x);
                       ylist.add(y);
                       down = false;
                       //Log.wtf("*Coordinates---------------------------", "Finger Down: " + x + " " + y);
                   }
               case MotionEvent.ACTION_MOVE:
                   up = true;
               case MotionEvent.ACTION_UP:
                   makeToast("Finger Up: " + x + " " + y);
                   if (up) {
                       upx = x;
                       upy = y;
                       xlist.add(upx);
                       ylist.add(upy);
                       up = false;
                      // Log.wtf("*Coordinates---------------------------", "Finger Up: " + x + " " + y);
                       down = true;
                       invalidate();
                   }
           }*/

            if (currentMode == Mode.DOTPLOT) {
               /*float x = event.getX();
               float y = event.getY();
               Log.d("Touch Point", "Co x:" + x + ", y:" + y);*/
                //TODO Write function to iterate through both lists and check a point's distance. Dont' just do it for the lastest one.
                boolean duplicate = checkForDuplicate(x, y, false);
                if (/*xlist.size() > 0 && xlist.get((int) xlist.size() - 1) < (int) x + 30 && xlist.get((int) xlist.size() - 1) > (int) x - 30
                       && ylist.get((int) ylist.size() - 1) < (int) y + 30 && ylist.get((int) ylist.size() - 1) > (int) y - 30
               */duplicate) {
                    //duplicate touch recording, skip it
                    Log.d("Duplicate", "Avoiding it");
                } else {
                    if (xlist.size() <= 12) {
                        if (xlist.size() > 1 && length == 0) {
                            if (specifyCount % 3 == 0)
                                makeToast("Please specify the length of the first side you drew.");
                            specifyCount++;
                        } else {
                            xlist.add((int) x);
                            ylist.add((int) y);
                            String logger = "";
                            if (dv.xlist.size() > 0) {
                                for (int i = 0; i < dv.xlist.size(); i++) {
                                    logger += "\n*- X - " + dv.xlist.get(i) + "  Y - " + dv.ylist.get(i);
                                }
                            }
                            //Log.wtf("*- Polygon coordinates", "Information: " + logger);
                            //INFO If you comment out below, then it does not draw the dots. Only when you press a button it draws.
                            // I think this is because when button pressed, it calls invalidate(). invalidate() leads to onDraw()
                            // That's why try writing your customDrawLine() code in onDraw().
                            invalidate();
                        }
                    } else {
                        if (exceededCount % 5 == 0)
                            shortToast("You have exceeded the limit on the number of sides.");
                        exceededCount++;
                    }
                }
            } else if (currentMode == Mode.splot) {
                //TODO Deal with plotting sprinklers.

                boolean duplicate = checkForDuplicate(x, y, true);
                if (!duplicate) {
                    sprinkx.add((int) x);
                    sprinky.add((int) y);
                    rotationList.add(rotate);
                    angleList.add(angle);
                    changeRotation.add(rotate != 0);
                    changeAngle.add(angle != 360);
                    //OLD code below just does it based on number of pixels.
                    //  On higher ppi phones, sprinkle  r appears very small.
                    //INFO For 3 lines, changed sradius/9 to /4.

                    int progress = IrrigationActivity.radius.getProgress();
                    double pixelSideLength = (double) maxSideLength / dv.ratio * 0.7d;
                    double setToI = ((double) progress / 100d * pixelSideLength) / 2;
                    double minSideLength = 4d / dv.ratio;
                    if (maxSideLength > 35) {
                        pixelSideLength = (double) 25d / dv.ratio;
                        minSideLength = 4d / dv.ratio;
                    }
                    if (maxSideLength > 50) {
                        pixelSideLength = (double) 35d / dv.ratio;
                        minSideLength = 4d / dv.ratio;
                    }
                    if (maxSideLength > 74) {
                        pixelSideLength = (double) 40d / dv.ratio;
                        minSideLength = 5d / dv.ratio;
                    }
                    if (maxSideLength > 90) {
                        pixelSideLength = (double) 50d / dv.ratio;
                        minSideLength = 6d / dv.ratio;
                    }
                    if (maxSideLength > 150) {
                        pixelSideLength = (double) 80d / dv.ratio;
                        minSideLength = 8d / dv.ratio;
                    }
                    if (maxSideLength > 325) {
                        pixelSideLength = (double) 250d / dv.ratio;
                        minSideLength = 10d / dv.ratio;
                    }
                    double dif = pixelSideLength - minSideLength;
                    /*progress -= 50;
                    progress *= 2;*/
                    if (!wasCircle)
                        setToI = pixelSideLength - ((double) 100 - sradius) / 100 * dif;
                    else
                        setToI = (double) sradius / 100d * backUpRadius;
                    Log.wtf("Radius adding", sradius + " " + setToI);


                    if (wasCircle) {
                        if (circleSprinklerRadius == -1) {
                            double differ = Math.ceil(dv.circleFeet / 1.75) - 5;
                            //README SetToI is radius in feet.
                            setToI = Math.ceil(dv.circleFeet / 1.75) - ((double) 100 - progress) / 100 * differ;
                            setToI = setToI / dv.length * (backUpRadius * (screenW / 200));
                            circleSprinklerRadius = setToI;
                            sprinkr.add((int) circleSprinklerRadius);
                        } else
                            sprinkr.add((int) circleSprinklerRadius);
                        Log.wtf("-- Circle Sprinkler Radius", " " + circleSprinklerRadius);
                    } else {
                        sprinkr.add((int) setToI);
                    }
                    //makeToast("Side Length : " + maxSideLength);
                    //Log.wtf("*- Set to I", (int) setToI + " " + (int) pixelSideLength + " " + sradius + " " + (int) maxSideLength + " " + (int) dv.ratio);
                    //Log.d("***--- Set to I", (int) setToI + " " + (int) pixelSideLength + " " + sradius + " " + (int) maxSideLength + " " + (int) dv.ratio+"\n..---...");
                    //sprinkr.add((int) ((double) ((double) screenW / 1000) * Math.pow(sradius / 5.9f, 2)) - 50);
                    Log.wtf("*-", "Radii: " + sprinkr.toString());
                    //sprinkr.add((int) ((double) (sradius/dv.ratio)));
                    //Log.wtf("*- IMPORTANT: ", " SRADIUS: " + sradius + " " + " RATIO: " + dv.ratio);
                    //Log.wtf("*- IMPORTANT: ", "Change Rotation Size: " + changeRotation.size() + "  rotation-" + rotate + "  angle-" + angle);
                    /*String logger = "";
                    if (dv.xlist.size() > 0) {
                        for (int i = 0; i < dv.xlist.size(); i++) {
                            logger += "\n*- X - " + dv.xlist.get(i) + "  Y - " + dv.ylist.get(i);
                        }
                    }*/
                    //Log.wtf("*- Polygon coordinates", "Information: " + logger);
                    //sprinkr.add((int) Math.pow(sradius / 9, 2));
                    invalidate();
                }
            }
            return true;
        }

        protected boolean checkForDuplicate(float x, float y, boolean sprinkler) {
            double distance = 0;
            if (!sprinkler) {
                for (int i = 0; i < xlist.size(); i++) {
                    int xTemp = xlist.get(i);
                    int yTemp = ylist.get(i);
                    distance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));
                    if (distance < 120)
                        return true;
                }
                return false;
            } else {
                for (int i = 0; i < sprinkx.size(); i++) {
                    int xTemp = sprinkx.get(i);
                    int yTemp = sprinky.get(i);
                    distance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));
                    if (distance < 30)
                        return true;
                }
                return false;
            }
        }

        int idCounter = 0;
        int posCount = 0;

        int specialCounter = Integer.MAX_VALUE;

        private void drawLine(Canvas canvas) {
            maxSideLength = 0;
            if (xlist.size() > 2) {
                posCount = xlist.size() - 2;

                for (int i = 0; i < xlist.size(); i++) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                            ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = (xlist.get(i) + xlist.get((i + 1) % xlist.size())) / 2;
                    params.topMargin = (ylist.get(i) + ylist.get((i + 1) % xlist.size())) / 2;

                    TextView textView = new TextView(context);
                    int val = (int) (Math.hypot(Math.abs(xlist.get(i) - xlist.get((i + 1) % xlist.size())),
                            Math.abs(ylist.get(i) - ylist.get((i + 1) % xlist.size()))) * ratio);
                    if (maxSideLength < val) maxSideLength = val;
                    textView.setText("" + val);
                    textView.setId(idCounter);
                    textView.setTextColor(Color.parseColor("#000000"));
                    textView.setLayoutParams(params);
                    //makeToast("Making the text");
                    rlDvHolder.addView(textView);
                    idCounter++;
                }
                /*if (xlist.size() == 3) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                            ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = (xlist.get(0) + xlist.get(1)) / 2;
                    params.topMargin = (ylist.get(0) + ylist.get(1)) / 2;

                    TextView textView = new TextView(context);
                    int val = (int) (Math.hypot(Math.abs(xlist.get(0) - xlist.get(1)),
                            Math.abs(ylist.get(0) - ylist.get(1))) * ratio);
                    if (maxSideLength < val) maxSideLength = val;
                    textView.setText("" + val);
                    textView.setId(idCounter);
                    textView.setTextColor(Color.parseColor("#000000"));
                    textView.setLayoutParams(params);
                    //makeToast("Making the text");
                    rlDvHolder.addView(textView);
                    idCounter++;
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (xlist.get(posCount) + xlist.get(posCount + 1)) / 2;
                params.topMargin = (ylist.get(posCount) + ylist.get(posCount + 1)) / 2;

                TextView textView = new TextView(context);
                int val = (int) (Math.hypot(Math.abs(xlist.get(posCount) - xlist.get(posCount + 1)),
                        Math.abs(ylist.get(posCount) - ylist.get(posCount + 1))) * ratio);
                if (maxSideLength < val) maxSideLength = val;
                textView.setText("" + val);
                textView.setTextColor(Color.parseColor("#000000"));
                textView.setId(idCounter);
                textView.setLayoutParams(params);
                //makeToast("Making the text");
                rlDvHolder.addView(textView);

                //DONE HAVE to deal with adding textview from last to first. and removing from previous touch.

                //Log.wtf("* Location: ", xlist.get(posCount) + " " + xlist.get(posCount + 1)
                //       + " " + ylist.get(posCount) + " " + ylist.get(posCount + 1));

                Log.wtf("*  Length INFO", "Ratio: " + ratio + "  Length: " + length);
                if (specialCounter != Integer.MAX_VALUE) {
                    rlDvHolder.findViewById(dv.specialCounter + 1).setVisibility(View.GONE);
                }


                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                int xlast = xlist.get(xlist.size() - 1) + xlist.get(0);
                int ylast = ylist.get(ylist.size() - 1) + ylist.get(0);
                params2.leftMargin = (xlast) / 2;
                params2.topMargin = (ylast) / 2;

                TextView last = new TextView(context);
                int val2 = (int) (Math.hypot(Math.abs(xlist.get(xlist.size() - 1) - xlist.get(0)),
                        Math.abs(ylist.get(ylist.size() - 1) - ylist.get(0))) * ratio);
                if (maxSideLength < val2) maxSideLength = val2;
                last.setText("" + val2);
                last.setId(specialCounter);
                last.setLayoutParams(params2);
                last.setTextColor(Color.parseColor("#000000"));
                //makeToast("Making the last text");
                rlDvHolder.addView(last);

                //Log.wtf("**a-", "Side Length - " + maxSideLength);
                idCounter++;
                posCount++;
                specialCounter--;*/

                for (int i = 0; i < xlist.size() - 1; i++) {
                    canvas.drawLine(xlist.get(i), ylist.get(i), xlist.get(i + 1), ylist.get(i + 1), linePaint);
                }

                //display = findViewById(R.id.textView);
                canvas.drawLine(xlist.get(xlist.size() - 1), ylist.get(ylist.size() - 1), xlist.get(0), ylist.get(0), linePaint);
                int a = xlist.get(0);
                int b = ylist.get(0);
                int x = xlist.get(1);
                int y = ylist.get(1);
                int c = a - x;
                int d = b - y;
                c = c * c;
                d = d * d;
                int z = c - d;
                double df = Math.sqrt(z);
                String s = Double.toString(df);
                //makeToast("Display is good: " + (display == null));
                //display.setText(s);
            } else if (xlist.size() == 2) {
                canvas.drawLine(xlist.get(0), ylist.get(0), xlist.get(1), ylist.get(1), linePaint);
            }
        }

        private void plotSprinklers2(Canvas canvas) {
            //Log.wtf("*- Plotting Sprinklers", "" + sprinkx.size());
            if (sprinkx.size() > 0) {
                //String logger = "";
                /*for (int i = 0; i < rotationList.size(); i++) {
                    logger += "Rotate: " + rotationList.get(i) + "  Angle: " + angleList.get(i) + " " + sprinkx.get(i)
                            + " " + sprinky.get(i) + " " + sprinkr.get(i) + " " + "\n";
                }*/
                //Log.wtf("*-Lists", logger);
                /*rotationList.clear();
                rotationList.add(0);
                rotationList.add(90);
                rotationList.add(110);
                rotationList.add(180);
                rotationList.add(270);
                rotationList.add(360);*/
                for (int i = 0; i < sprinkx.size() - 1; i++) {
                    //Log.wtf("*Sprinkler Location: ", sprinkx.get(i) + " " + sprinky.get(i) + " " + sprinkr.get(i));
                    // canvas.dra wCissdddddcrcle(sprinkx.get(i), sprinky.get(i), sprinkr.get(i), sprinklerC);
                    int m = i;
                    float radius = sprinkr.get(m);
                    //Log.wtf("Drawing Rotation", rotationList.get(i) + " " + sprinkx.get(i) + "," + sprinky.get(i));
                    //if (i > 4)
                    canvas.drawArc(new RectF(sprinkx.get(m) - radius, sprinky.get(m) - radius, sprinkx.get(m) + radius,
                                    sprinky.get(m) + radius), (rotationList.get(i)) % 360 - 90,
                            /*(rotationList.get(i))%360 + */angleList.get(i), true, sprinklerC);
                }
                int m = sprinkx.size() - 1;
                float radius = sprinkr.get(m);

                canvas.drawArc(new RectF(sprinkx.get(m) - radius, sprinky.get(m) - radius, sprinkx.get(m) + radius,
                                sprinky.get(m) + radius), (rotationList.get(rotationList.size() - 1)) % 360 - 90,
                        /*(rotationList.get(i))%360 + */angleList.get(angleList.size() - 1), true, sprinklerC);
                //Log.wtf("*-Status:", "Plot Sprinkler2 ");

            }
        }

        private void plotSprinklers(Canvas canvas) {
            //Log.wtf("*Plotting Sprinklers", "Number of Sprinklers: " + sprinkx.size());
            if (sprinkx.size() > 0) {
                for (int i = 0; i < sprinkx.size(); i++) {
                    //Log.wtf("*Sprinkler Location: ", sprinkx.get(i) + " " + sprinky.get(i) + " " + sprinkr.get(i));
                    // canvas.drawCissdddddcrcle(sprinkx.get(i), sprinky.get(i), sprinkr.get(i), sprinklerC);
                    int m = i;
                    float radius = sprinkr.get(m);
                    canvas.drawArc(new RectF(sprinkx.get(m) - radius, sprinky.get(m) - radius, sprinkx.get(m) + radius,
                                    sprinky.get(m) + radius), (rotationList.get(i)) % 360 - 90,
                            /*(rotationList.get(i))%360 + */angleList.get(i), true, sprinklerC);
                }

            }
        }

        private void drawCustomLine(Canvas canvas, int x1, int y1, int x2, int y2) {
            //try calling invalidate()
            //makeToast("Being called");
            Log.wtf("*Draw Custom Line being called", "Draw Custom Line being called");
            linePaint.setColor(Color.BLUE);
            //canvas.drawLine(x1, y1, x2, y2, linePaint);
            xs.add(x1);
            xs.add(x2);
            ys.add(y1);
            ys.add(y2);
            for (int i = 0; i < xs.size(); i += 2)
                canvas.drawLine(xs.get(i), ys.get(i), xs.get(i + 1), ys.get(i + 1), linePaint);
            linePaint.setColor(Color.GREEN);

            //invalidate();
        }

        private void showDots(Canvas canvas) {
            //Log.wtf("*Showing dots", "X List size: " + xlist.size());
            if (xlist.size() > 0) {
                for (int i = 0; i < xlist.size(); i++) {
                    canvas.drawCircle(xlist.get(i), ylist.get(i), 9.0f, circlePaint);
                }
            }

        }

        private void makeToast(String s) {
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }


        enum Mode {RECORDING, DRAWING, PLOT, SPRINKLER, RESET, DOTPLOT, drawc, resetc, splot, sreset, POLYGON}

        enum PastMode {DRAW, CIRCLE}


        public double polygonArea() {
            // Initialze area
            //if (currentMode == Mode.splot || currentMode == Mode.RESET) {
            if (pastMode == PastMode.DRAW) {
                int n = xlist.size();
                double area = 0.0;

                // Calculate value using shoelace formula
                int j = n - 1;
                for (int i = 0; i < n; i++) {
                    area += (xlist.get(j) + xlist.get(i)) * (ylist.get(j) - ylist.get(i));
                    // j is previous vertex to i
                    j = i;
                }
                // Return absolute value
                if (area != 0)
                    return Math.abs(area / 2.0);
                else {
                    double curRadius = radius * (screenW / 200);
                    return (int) Math.pow(curRadius, 2) * Math.PI;
                }
            } else {
                if (radius == -5)
                    return (int) 90000 * Math.PI;
                else {
                    double curRadius = radius * (screenW / 200);
                    return (int) Math.pow(curRadius, 2) * Math.PI;
                }
            }
        }
    }

    public static Menu menuOptions;
    public static SubMenu measurements;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        /*MenuItem item = menu.findItem(R.id.autoplot);
        item.setVisible(false);*/
        MenuItem item2 = menu.findItem(R.id.calculate);
        item2.setVisible(false);

        menuOptions = menu;

        return super.onCreateOptionsMenu(menu);
    }


    boolean rectangle = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //makeToast("Polygon area: " + dv.polygonArea());
        int itemId = item.getItemId();
        if (itemId == 1) {
            coordinateBox();
        } else if (itemId == 2) {
            sprinklerInfoBox();
        } else if (itemId == R.id.coordinates) {
            coordinateBox();
            return true;
        } else if (itemId == R.id.circle) {//TODO Have to draw circle.
                /*customPlotter(true);
                dv.currentMode = DrawingView.Mode.splot;*/
            MenuItem item4 = menuOptions.findItem(R.id.calculate);
            item4.setVisible(false);
                /*MenuItem item5 = menuOptions.findItem(R.id.autoplot);
                item5.setVisible(false);*/
            autoplot.setAnimation(fadeOut);
            autoplot.setVisibility(View.INVISIBLE);


            if (dv.currentMode == DrawingView.Mode.drawc || dv.currentMode == DrawingView.Mode.resetc) {
                //INFO Need to switch from circle to polygon
                radius.setVisibility(View.INVISIBLE);
                polygon.setVisibility(View.VISIBLE);
                item.setTitle("Draw Circle");
                dv.currentMode = DrawingView.Mode.DOTPLOT;
                dv.wasCircle = false;
                dv.resetSprinklers();
                dv.resetTouchPoints();
                ft.setVisibility(View.VISIBLE);
                dv.pastMode = DrawingView.PastMode.DRAW;
                textview.setVisibility(View.VISIBLE);
                length.setVisibility(View.VISIBLE);
                angleText.setVisibility(View.INVISIBLE);
                rotateText.setVisibility(View.INVISIBLE);
                left1.setVisibility(View.INVISIBLE);
                left2.setVisibility(View.INVISIBLE);
                right2.setVisibility(View.INVISIBLE);
                right1.setVisibility(View.INVISIBLE);
                removeAllLengths();
                dv.invalidate();
            } else {
                //INFO Currently on polygon. Switch to circle.
                dv.currentMode = DrawingView.Mode.drawc;
                dv.wasCircle = true;
                item.setTitle("Draw Shapes");
                dv.resetTouchPoints();
                dv.pastMode = DrawingView.PastMode.CIRCLE;
                ft.setVisibility(View.VISIBLE);
                textview.setVisibility(View.VISIBLE);
                length.setVisibility(View.VISIBLE);
                dv.resetSprinklers();
                removeAllLengths();
                polygon.setVisibility(View.INVISIBLE);
                radius.setVisibility(View.VISIBLE);
                angleText.setVisibility(View.INVISIBLE);
                rotateText.setVisibility(View.INVISIBLE);
                left1.setVisibility(View.INVISIBLE);
                left2.setVisibility(View.INVISIBLE);
                right2.setVisibility(View.INVISIBLE);
                right1.setVisibility(View.INVISIBLE);
                dv.invalidate();
            }
            // dv.resetSprinklers();
            return true;
        } else if (itemId == R.id.sprinklers) {/* customPlotter();
                dv.currentMode = DrawingView.Mode.splot;*/
            MenuItem item3 = menuOptions.findItem(R.id.calculate);
            item3.setVisible(true);
                /*if (dv.sprinkx.size() == 0) {
                    MenuItem item2 = menuOptions.findItem(R.id.autoplot);
                    item2.setVisible(true);
                } else if (dv.sprinkx.size() > 0) {
                    MenuItem item2 = menuOptions.findItem(R.id.autoplot);
                    item2.setVisible(false);
                }*/

            //onCreateOptionsMenu(menuOptions);
            if (dv.xlist.size() < 3 && dv.currentMode != DrawingView.Mode.drawc
                    && dv.currentMode != DrawingView.Mode.resetc && !dv.wasCircle) {
                makeToast("You must first plot the area of land.");
            } else if (!handleSideLength()) {
                //INFO They have not entered a side length/radius
            } else {
                ft.setVisibility(View.INVISIBLE);
                textview.setVisibility(View.INVISIBLE);
                length.setVisibility(View.INVISIBLE);
                polygon.setVisibility(View.INVISIBLE);
                radius.setVisibility(View.VISIBLE);
                angleText.setVisibility(View.VISIBLE);
                rotateText.setVisibility(View.VISIBLE);
                left1.setVisibility(View.VISIBLE);
                left2.setVisibility(View.VISIBLE);
                right2.setVisibility(View.VISIBLE);
                right1.setVisibility(View.VISIBLE);
                dv.currentMode = DrawingView.Mode.splot;

                if (dv.sprinkx.size() == 0) {
                    autoplot.setAnimation(fadeIn);
                    autoplot.setVisibility(View.VISIBLE);
                } else {
                    autoplot.setAnimation(fadeOut);
                    autoplot.setVisibility(View.INVISIBLE);
                }
            }
            return true;
        } else if (itemId == R.id.calculate) {//INFO The purpose of this is to display the loading Alert Dialog.
            //Bitmap tr = takeScreenShot(dv);
            //showLoading();

            //showResults(1,1,"Not known");
            //TODO Uncomment Below
            //TODO If user does autoplot sprinklers, set wastage to
            // 6,7.5, 8.5, 9, 10, 11, 12% at random.
            if (dv.pastMode == DrawingView.PastMode.CIRCLE)
                circleCalculations();
            else
                calculateSprinklerOverflow();
/*
                //INFO Wait a bit so that the Dialog is showing, then do the calculations.
                Handler h = new Handler();
                final Bitmap[] bmp = {tr};
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Bitmap bmp = takeScreenShot(dv);
               *//*try (FileOutputStream out = new FileOutputStream("test.png")) {
                   bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
               } catch (Exception e) {
                   makeToast(e.toString());
                   Log.wtf("*ERROR WITH SAVING IMAGE: ", e.toString());
               }
               File root = Environment.getExternalStorageDirectory();
               Bitmap open = BitmapFactory.decodeFile(root + "/images/test.png");
               Intent opener = new Intent();
               opener.setAction(Intent.ACTION_VIEW);
               File temp = new File("sdcard/Images/test.png");
               opener.setDataAndType(Uri.parse("/test.png"), "image/*");
               startActivity(opener);*//*


                        //DONE Before counting pixel colors, try compressing PNG to 50% quality or less
                        //  that way there are fewer colors for sprinkler.
                        //TODO When calculating area of overlapping regions, actually calculate proper
                        //  areas of 1 sprinkler and 2 sprinklers with formula.
                        //  for the rest, then you can use pixels

                        //takeScreenShot2();
                        //makeToast("Bitmap Info: " + bmp.getWidth() + " " + bmp.getHeight());
                        for (int i = 0; i < dv.sprinkx.size(); i++) {
                            //Log.wtf("*  Sprinkler Location ", "X: " + dv.sprinkx.get(i) + "  Y: " + dv.sprinky.get(i) + "  R: " + dv.sprinkr.get(i));
                        }
                        //Log.wtf("*BITMAP DIMENSIONS --------------------", "Width: " + bmp.getWidth() + " Height: " + bmp.getHeight());
                        //getIndividualCircles();
                        //Log.wtf("*Done getting circles", " DONE GETTING CIRCLES");
                        //README Make Bitmap smaller.
                        bmp[0] = Bitmap.createScaledBitmap(bmp[0], (int) (bmp[0].getWidth() * 1), (int) (bmp[0].getHeight() * 1), true);
                        iterateThroughPixels(bmp[0]);
                        //askForLength(true, 3);

                        //File file = takeScreenShot2();
                        //iterateThroughPixels(file);
                    }
                }, 300);*/

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void circleCalculations() {
        double waste = 0, total = 0;
        overCounted3 = 0;
        insideCircles = new ArrayList<>();
        int overflowingCount = 0, outsideCount = 0;
        overlappingCount = 0;
        //README 2 Circle Overlap
        overlaps = new HashSet<>();
        for (int i = 0; i < dv.sprinkx.size() - 1; i++) {
            for (int j = i + 1; j < dv.sprinkx.size(); j++) {
                double r1 = dv.sprinkr.get(i);
                double r2 = dv.sprinkr.get(j);
                double y1 = dv.sprinky.get(i);
                double y2 = dv.sprinky.get(j);
                double x1 = dv.sprinkx.get(i);
                double x2 = dv.sprinkx.get(j);
                int circle1 = i, circle2 = j;
                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                if (distance <= r1 + r2 - 2) {
                    overlappingCount++;
                    overlaps.add(i);
                    overlaps.add(j);
                    double intersectionArea = 0;
                    Double r = r1;
                    Double R = r2;
                    double firstX = x1;
                    double secondX = x2;
                    double firstY = y1;
                    double secondY = y2;
                    int smallC = circle1;
                    int bigc = circle2;
                    Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
                    if (R < r) {
                        // swap
                        r = r2;
                        R = r1;
                        smallC = circle2;
                        bigc = circle1;
                    }
                    Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                    Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                    Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                    intersectionArea = part1 + part2 - part3;

                    //README Maybe intersectionArea is 0 because circle is inside other circle.
                    if (!(intersectionArea > 0)) {
                        intersectionArea = Math.PI * Math.pow(r, 2) * dv.angleList.get(smallC) / 360;
                    }
                    Log.wtf("Intersection Area", "(" + i + "," + j + ")----" + intersectionArea);
                    waste += intersectionArea;
                }
            }
        }
        double overflowWaste = 0;
        //README Overflow
        for (int i = 0; i < dv.sprinkx.size(); i++) {
            double r1 = dv.sprinkr.get(i);
            double r2;
            if (dv.radius != -5)
                r2 = dv.backUpRadius * (dv.screenW / 200);
            else
                r2 = 300;
            double y1 = dv.sprinky.get(i);
            double y2 = dv.screenH / 2 - (dv.screenH / 8) - 30;
            double x1 = dv.sprinkx.get(i);
            double x2 = dv.screenW / 2;

            double distance = Math.sqrt(Math.pow(x1 - dv.screenW / 2, 2)
                    + Math.pow(y1 - (dv.screenH / 2 - (dv.screenH / 8) - 30), 2));
            //Log.wtf("Distance", x1 + "," + y1 + " " + (int) x2 + "," + (int) y2 + " ...." + distance + " " + r1 + " >>> " + r2);
            if (distance + r1 > r2 + 0.5) {
                overflowingCount++;
                double intersectionArea = 0;
                Double r = r1;
                Double R = r2;
                double firstX = x1;
                double secondX = x2;
                double firstY = y1;
                double secondY = y2;
                Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
                if (R < r) {
                    // swap
                    r = r2;
                    R = r1;
                }
                Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                intersectionArea = part1 + part2 - part3;

                //README If intersection Area is greater than 2
                if (intersectionArea > 2) {
                    //IMPORTANT Calculated area is INSIDE (both circles)
                    // to get overflow must subtract from total.
                    //if (outsideCircle((int) x1, (int) y1))
                    intersectionArea = Math.PI * dv.sprinkr.get(i)
                            * dv.sprinkr.get(i) - intersectionArea;
                } else if (outsideCircle((int) x1, (int) y1)) {
                    //README Circle is completely outside
                    intersectionArea = Math.PI * Math.pow(r, 2) * dv.angleList.get(i) / 360;
                }
                Log.wtf("-------Is Outside: ", " Outside: " + outsideCircle((int) x1, (int) y1));

                //IMPORTANT Total Area is already calculated for overflow circles.
                //DONE When iterating through inside circle, only if it not insideIntersecting, calculate its area.

                Log.wtf("Overflow Wastage " + i, "" + intersectionArea);
                //waste += intersectionArea;
                overflowWaste += intersectionArea;
            }
        }
        insideCircles = new ArrayList<>();
        //README Total Area
        for (int i = 0; i < dv.sprinkx.size(); i++) {
            insideCircles.add(i);
            total += Math.PI * dv.sprinkr.get(i)
                    * dv.sprinkr.get(i) * dv.angleList.get(i) / 360;
            if (outsideCircle(dv.sprinkx.get(i), dv.sprinky.get(i))) outsideCount++;
        }
        calculate3CircleOverlap();


        waste += overflowWaste;

        //makeToast(Math.round(waste * 100 / total) + "% water wasted");
        Log.wtf("Statistics: ", "Overflow: " + overflowWaste + "  " + overCounted3 + "   " + waste + " " + total);
        displayCircleResults(waste, total, overflowWaste, overflowingCount, outsideCount);
    }

    public static boolean outsideCircle(int x, int y) {
        double distance = Math.sqrt(Math.pow(x - dv.screenW / 2, 2)
                + Math.pow(y - (dv.screenH / 2 - (dv.screenH / 8) - 30), 2));
        if (distance > ((dv.radius == -5) ? 300 : dv.backUpRadius * (dv.screenW / 200)))
            return true;
        return false;
    }

    private void sprinklerInfoBox() {
        final Dialog dialog = new Dialog(IrrigationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.sprinkler_inflo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button back = (Button) dialog.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                dv.invalidate();
            }
        });

        TreeMap<Double, Integer> radii = new TreeMap<>();
        for (double r : dv.sprinkr) {
            double converted = Math.round(r * dv.ratio * 2) / 2d;
            if (radii.containsKey(converted))
                radii.put(converted, radii.get(converted) + 1);
            else
                radii.put(converted, 1);
        }

        ArrayList<Double> radiusList = new ArrayList<>(radii.keySet());
        ArrayList<Integer> frequency = new ArrayList<>(radii.values());
        ArrayList<SprinklerInfo> sprinklerList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < radiusList.size(); i++) {
            count += frequency.get(i);
            sprinklerList.add(new SprinklerInfo(radiusList.get(i), frequency.get(i)));
        }
        SprinklerAdapter sprinklerAdapter = new SprinklerAdapter(IrrigationActivity.this, sprinklerList);
        ListView list = dialog.findViewById(R.id.list);

        //sprinklerList.add(new SprinklerInfo(3, 2));

        int rowHeight = 186;

        ViewGroup.LayoutParams params = list.getLayoutParams();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 150f);
        rowHeight *= (float) densityDpi / (float) 560;
        if (isTablet())
            params.height = (int) Math.min((float) rowHeight * 8f, rowHeight * sprinklerList.size());
        else
            params.height = (int) Math.min((float) rowHeight * 5f, rowHeight * sprinklerList.size());

        list.setLayoutParams(params);
        list.setAdapter(sprinklerAdapter);

        shortToast(count + " sprinklers");
        dialog.show();
    }

    private void coordinateBox() {
        final Dialog dialog = new Dialog(IrrigationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.ask_coordinates);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button back = (Button) dialog.findViewById(R.id.back);
        final CheckBox sprinklers = dialog.findViewById(R.id.sprinklers);
        final CheckBox land = dialog.findViewById(R.id.land);
        sprinklers.setChecked(sprinklerCoordinates);
        land.setChecked(landCoordinates);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sprinklerCoordinates = sprinklers.isChecked();
                landCoordinates = land.isChecked();
                dialog.dismiss();
                dialog.cancel();
                dv.invalidate();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                sprinklerCoordinates = sprinklers.isChecked();
                landCoordinates = land.isChecked();
                dv.invalidate();
            }
        });

        dialog.show();
    }


    double overFlowWastage = 0;      //INFO This is numerator for wastage
    double totalOverflowArea = 0;    //INFO This is denominator for wastage
    double overlapWastage = 0;       //INFO This is numbrator for wastage
    double totalInsideArea = 0;      //INFO This is denominator for wastage
    ArrayList<OverflowInfo> overflowInfo = new ArrayList<>();
    //Set<OverflowInfo> completelyOutside = new HashSet<>();
    HashMap<Integer, OverflowInfo> completelyOutside = new HashMap<>();
    HashMap<Integer, Double> individualCircles;
    ArrayList<Integer> candidates = new ArrayList<>();
    HashMap<Integer, Integer> insideCirclesH = new HashMap<>();
    HashMap<Integer, OverflowInfo> outsideIntersectingH = new HashMap<>();
    HashMap<Integer, Integer> completelyInsideH = new HashMap<>();
    HashMap<Integer, OverflowInfo> insideIntersectingH = new HashMap<>();
    ArrayList<Integer> insideCircles = new ArrayList<>();
    ArrayList<OverflowInfo> outsideIntersecting = new ArrayList<>();
    ArrayList<OverflowInfo> insideIntersecting = new ArrayList<>();
    ArrayList<Integer> completelyInside = new ArrayList<>();
    double wasted, total = 0;

    private void calculateSprinklerOverflow() {
        //showLoading();
        overFlowWastage = 0;
        overlapWastage = 0;
        totalInsideArea = 0;
        wasted = 0;
        total = 0;
        totalOverflowArea = 0;
        wasted = 0;
        total = 0;
        overlaps = new HashSet<>();
        completelyOutside = new HashMap<>();
        overflowInfo = new ArrayList<>();

        candidates = new ArrayList<>();
        individualCircles = new HashMap<>();

        insideCirclesH = new HashMap<>();
        outsideIntersectingH = new HashMap<>();
        completelyInsideH = new HashMap<>();
        insideIntersectingH = new HashMap<>();

        insideCircles = new ArrayList<>();
        outsideIntersecting = new ArrayList<>();
        completelyInside = new ArrayList<>();
        insideIntersecting = new ArrayList<>();
        Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");
        Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");
        Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");
        Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");
        for (int t = 0; t < dv.sprinkx.size(); t++) {
            double circleX = dv.sprinkx.get(t);
            double circleY = dv.sprinky.get(t);
            double radius = dv.sprinkr.get(t);
            boolean outside = outside(circleX, circleY);
            boolean removedCompletelyInsideCircles = false;
            boolean removed = false;
            boolean removedIndividual = false;
            boolean outsideIntersectingGood = false;
            double ox1 = 0, ox2 = 0, oy1 = 0, oy2 = 0;
            for (int b = 0; b < dv.xlist.size(); b++) {
                double startX = dv.xlist.get(b);
                double startY = dv.ylist.get(b);
                double endX = dv.xlist.get((b + 1 > dv.xlist.size() - 1 ? 0 : b + 1));
                double endY = dv.ylist.get((b + 1 > dv.ylist.size() - 1 ? 0 : b + 1));


                double baX = endX - startX;
                double baY = endY - startY;
                double caX = circleX - startX;
                double caY = circleY - startY;

                double a = baX * baX + baY * baY;
                double bBy2 = baX * caX + baY * caY;
                double c = caX * caX + caY * caY - radius * radius;

                double pBy2 = bBy2 / a;
                double q = c / a;

                double disc = pBy2 * pBy2 - q;
                //README No intersections, but still need to check if it was outside
                if (disc < 0) {
                    //README Circle center is outside and no intersections
                    if (outside) {
                        //if (!added)
                        individualCircles.remove(t);
                        removedIndividual = true;
                        if (!removed)
                            completelyOutside.put(t, new OverflowInfo(t, 0, 0, 0, 0, 0, 0, 0, 0, radius
                                    , 0, 0, 0, 0));
                        //Log.wtf("*- complete outside info - ", "added: " + t + " SIze: " + completelyOutside.size());
                    } else {
                        //README Point had no intersections and was inside
                        insideCirclesH.put(t, 1);
                        if (!removedCompletelyInsideCircles)
                            completelyInsideH.put(t, 1);
                        if (!removedIndividual)
                            individualCircles.put(t, radius);
                    }
                    //FUTURE Files Try going through remaining sprinklers and calculate 2 intersections in pairs.
                    // go from i =0 and j= i and check if they intersect. If the 2 sprinklers intersect calculate wastage.
                    // we won't be able to account for 3 circles.
                    // At most, check if singleR is 3 or 4 less than total circles. then estimate duplicate wastage from 3 circles.
                    //FUTURE FILES
                    // You may have to check if non intersecting circle is inside. If inside, then calculate areas of only these.
                    // Right now you are calculating areas of individual ones here and area of individual ones in iterateThroughPixels
                    //overFlowWastage += radius * radius * Math.PI * dv.angleList.get(t) / 360d;
                    //totalOverflowArea += radius * radius * Math.PI * dv.angleList.get(t) / 360d;
                    //Log.wtf("*- Sprinkler Overflow (" + t + " " + b + ")", "No overflow??");
                    // return Collections.emptyList();
                } else {
                    // if disc == 0 ... dealt with later
                    double tmpSqrt = Math.sqrt(disc);
                    double abScalingFactor1 = -pBy2 + tmpSqrt;
                    double abScalingFactor2 = -pBy2 - tmpSqrt;

                    /*Point p1 = new Point(pointA.x - baX * abScalingFactor1, pointA.y
                            - baY * abScalingFactor1);*/
                    //README 1 intersection
                    if (disc == 0) { // abScalingFactor1 == abScalingFactor2
                        //DONE calculate 30% wastage
                        completelyInsideH.remove(t);
                        removedCompletelyInsideCircles = true;
                        overFlowWastage += radius * radius * Math.PI * dv.angleList.get(t) / 360d * .3;
                        totalOverflowArea += radius * radius * Math.PI * dv.angleList.get(t) / 360d;
                        Log.wtf("*- Sprinkler Overflow (" + t + " " + b + ")", "1 INTERSECTION - " + " x: " +
                                (startX - baX * abScalingFactor1) + " y: " + (startY - baY * abScalingFactor1));
                    } else {
                        double x1 = (startX - baX * abScalingFactor1);
                        double y1 = (startY - baY * abScalingFactor1);
                        double x2 = (startX - baX * abScalingFactor2);
                        double y2 = (startY - baY * abScalingFactor2);
                        //README False intersection (line not line segment)
                        if (!((x1 > Math.min(startX, endX) && x1 < Math.max(startX, endX) && y1 > Math.min(startY, endY) && y1 < Math.max(startY, endY))
                                || (x2 > Math.min(startX, endX) && x2 < Math.max(startX, endX) && y2 > Math.min(startY, endY) && y2 < Math.max(startY, endY)))) {
                        /*if ((radius < Math.sqrt(Math.pow(circleX - startX, 2) + Math.pow(circleY - startY, 2))
                                || radius < Math.sqrt(Math.pow(circleX - endX, 2) + Math.pow(circleY - endY, 2)))
                                && !((circleX > startX && circleX < endX) && (circleY > startY && circleY < endY))) {*/
                        /*if ((circleX < (Math.min(startX, endX) - radius)) || (circleX > (Math.max(startX, endX) + radius))
                                || (circleY < (Math.min(startY, endY) - radius)) || (circleY > (Math.max(startY, endY) + radius))) {*/
                            /*Log.wtf("*- Sprinkler Overflow (" + t + " " + b + ")", "\n\t\t\\t\tTOO FAR AWAY: -" +
                                    "Bool 1 - " + (x1 > Math.min(startX, endX) && x1 < Math.max(startX, endX) && y1 > Math.min(startY, endY) && y1 < Math.max(startY, endY)) +
                                    " Bool 2- " + (x2 > Math.min(startX, endX) && x2 < Math.max(startX, endX) && y2 > Math.min(startY, endY) && y2 < Math.max(startY, endY)) +
                                    *//*"Radius bool-" + (radius < Math.sqrt(Math.pow(circleX - startX, 2) + Math.pow(circleY - startY, 2))
                                            || radius < Math.sqrt(Math.pow(circleX - endX, 2) + Math.pow(circleY - endY, 2))) + " " +
                                            "Other 2:" + !((circleX > startX && circleX < endX) && (circleX > startX && circleX < endX))
                                            + (circleX > startX && circleX < endX) + (circleX > startX && circleX < endX) +*//* " \n\n1x: " +
                                    x1 + " 1y: " + y1
                                    + " 2x: " +
                                    x2 + " 2y: " + y2 + " StartX: " + startX + " StartY: " + startY + " EndX: " + endX +
                                    " EndY: " + endY);*/
                        } else {
                            //README actual intersection with 2 points
                            //  circle is not fully outside land.
                            /*fullOutside = false;
                            if(added == false)
                                removedFirst = true;
                            if(added == true)
                                removedFirst = false;*/
                            completelyOutside.remove(t);
                            completelyInsideH.remove(t);
                            removedCompletelyInsideCircles = true;
                            individualCircles.remove(t);
                            removedIndividual = true;
                            if (outside) {
                                outsideIntersectingGood = true;
                                ox1 = x1;
                                ox2 = x2;
                                oy1 = y1;
                                oy2 = y2;
                            }
                            removed = true;
                            if (!outside)
                                insideIntersectingH.put(t, new OverflowInfo(t, b, (b + 1 > dv.xlist.size() - 1 ? 0 : b + 1), startX, startY,
                                        endX, endY, circleX, circleY, radius, x1, x2, y1, y2));

                            //Log.wtf("*- complete outside info - " , "removed: " + t + " SIze: " + completelyOutside.size());
                            overflowInfo.add(new OverflowInfo(t, b, (b + 1 > dv.xlist.size() - 1 ? 0 : b + 1), startX, startY,
                                    endX, endY, circleX, circleY, radius, x1, x2, y1, y2));
                            Log.wtf("*- Sprinkler Overflow (" + t + " " + b + ")", "2 INTERSECTIONs - \n\t\t\t\t\t\t" + " 1x: " +
                                    x1 + " 1y: " + y1
                                    + " 2x: " +
                                    x2 + " 2y: " + y2 + " StartX: " + startX + " StartY: " + startY + " EndX: " + endX +
                                    " EndY: " + endY);
                        }
                        /*Point p2 = new Point(pointA.x - baX * abScalingFactor2, pointA.y
                                - baY * abScalingFactor2);*/
                        //return Arrays.asList(p1, p2);
                    }
                    Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");

                }
            }
            if (outsideIntersectingGood)
                outsideIntersectingH.put(t, new OverflowInfo(t, 0, 1, 0, 1,
                        0, 1, circleX, circleY, radius, ox1, ox2, oy1, oy2));
            // if(removedFirst)
            //   completelyOutside.remove(t);

            //README Circle not fully outside ladn so have to remove from list.
            //Log.wtf("*- \t#" + (t+1)+ " Bool vars - " , "Full Outside: " + fullOutside + " Added: " + added + " Outside: " + outside);
            //INFO Uncomment below if using ArrayList.
            /*if (!fullOutside) {
                //README Only if it was added to list, remove that sprinkler.
                if (added)
                    completelyOutside.remove(completelyOutside.size() - 1);
                //makeToast("FUll Outside: " + fullOutside);
            }*/
        }

        for (Map.Entry<Integer, Double> map : individualCircles.entrySet()) {
            //README Candidates contains the positions of possible individual sprinklers.
            candidates.add(map.getKey());
        }
        //README method uses positions in candidates list to actually calculate individual or not.
        //DONE Calculate areas of individual circles (including angles)
        getIndividualCircles2();
        //individualCircleArea();

        outsideIntersecting = new ArrayList<>(outsideIntersectingH.values());
        insideIntersecting = new ArrayList<>(insideIntersectingH.values());
        completelyInside = new ArrayList<>(completelyInsideH.keySet());
        insideCircles = new ArrayList<>(insideCirclesH.keySet());

        //TODO Calculate 4 things on sheet of paper.
        //  1. Start with calculating overlap of 2 circles from insideCircles list
        //NOTES Total area calculated -
        // At this point all outside, intersecting, and completely inside cirlces have been calculated.

        //DONE - ideally for insideCircleOverlap should only go for completelyinsideCircles with insideCircles
        //    Then, you have 3 other cases - insideIntersecting with insideIntersecting, insideIntersecting with outsideIntersecting,
        //    and outsideIntersecting with outsideIntersecting
        //  For the above 3 cases, when calculating overlap, you also have to account for angle between centers because
        //  the intersecting area can be inside and outside the region.

        //OLD Don't call below anymore.
        //individualCircleArea();

        calculateOverflowWastage();
        insideCircleOverlap();
        completelyInsideOverlapOutside();
        insideIntersectingOutsideIntersecting();
        outsideIntersectingOutsideIntersecting();
        insideIntersectingInsideIntersecting();
        calculate3CircleOverlap();

        total = 0;
        for (Map.Entry<Integer, OverflowInfo> completelyOutside : completelyOutside.entrySet()) {
            total += completelyOutside.getValue().getRadius() * completelyOutside.getValue().getRadius() *
                    Math.PI * dv.angleList.get(completelyOutside.getValue().getCirclePos()) / 360d;
            totalOverflowArea += completelyOutside.getValue().getRadius() * completelyOutside.getValue().getRadius() *
                    Math.PI * dv.angleList.get(completelyOutside.getValue().getCirclePos()) / 360d;
            overFlowWastage += completelyOutside.getValue().getRadius() * completelyOutside.getValue().getRadius() *
                    Math.PI * dv.angleList.get(completelyOutside.getValue().getCirclePos()) / 360d;
        }
        for (OverflowInfo outside : outsideIntersecting) {
            total += Math.PI * outside.getRadius() * outside.getRadius() *
                    dv.angleList.get(outside.getCirclePos()) / 360;
            totalInsideArea += Math.PI * outside.getRadius() * outside.getRadius() *
                    dv.angleList.get(outside.getCirclePos()) / 360;
        }
        for (Integer o : insideCircles) {
            total += Math.PI * dv.sprinkr.get(o) * dv.sprinkr.get(o) * dv.angleList.get(o) / 360;
        }


        //Log.wtf("*- Completely Outside", "Sprinkler # - " + completelyOutside.size() + "\n\t\t\t\t\t\t\t\t\t Wastage - " + overFlowWastage);

        //total += totalInsideArea;
        //total += totalOverflowArea;
        wasted += overFlowWastage;
        wasted += overlapWastage;
        //INFO Subtracts overlap of 3 circles
        wasted -= overCounted3;
        Log.wtf("*- End Results:", ":-:  " + (int) wasted + " " + (int) total + " " + (int) (wasted * 100 / total) + "%\n---___---");
        Log.wtf("*- Wastage", "OverFlow: " + overFlowWastage + "  Overlap: " + overlapWastage + "  3 Circle: " + overCounted3);
        Log.wtf("*- Outside Intersecting", "Size: " + outsideIntersecting.size());
        Log.wtf("*- Completely Inside Circle", "Size: " + completelyInside.size());
        Log.wtf("*- Inside Circles", "Size: " + insideCircles.size());
        Log.wtf("*- Inside Intersecting", "Size: " + insideIntersecting.size());
        //handleResults(3, 3, 3, 3, 3);
        displayResults();
        //showResults(2,3,"None");
    }

    boolean darken;
    boolean isHeadtoHead;

    private void displayResults() {
        final Dialog dialog = new Dialog(IrrigationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        darken = false;
        isHeadtoHead = false;

        Button next = (Button) dialog.findViewById(R.id.done);
        Button backBtn = (Button) dialog.findViewById(R.id.goBack);
        final RelativeLayout results = (RelativeLayout) dialog.findViewById(R.id.realresults);
        final RelativeLayout toHide = dialog.findViewById(R.id.questions);
        final EditText waterUsedE = dialog.findViewById(R.id.waterused);
        final EditText durationE = dialog.findViewById(R.id.duration);
        final CheckBox cb = dialog.findViewById(R.id.head);
        ImageView headHelp = dialog.findViewById(R.id.headhelp);

        headHelp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                makeToast("Head to head coverage is when sprinklers require overlap to evenly distribute water. Overlap of 2 sprinklers" +
                        " will not be included in wastage calculations.");
                return false;
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) buttonView.setTextColor(0XFF000000);
                if (!isChecked) buttonView.setTextColor(0XFF757575);
                isHeadtoHead = isChecked;
            }
        });
       /* cb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                darken ^= darken;
                if(darken)
                    cb.setTextColor(0XFF0000);
                else
                    cb.setTextColor(0XFF757575);
                return false;
            }
        });*/
/*
        //results.setVisibility(View.VISIBLE);

        final TextView numSprink = results.findViewById(R.id.sNum);
        final TextView nonOverlapT = dialog.findViewById(R.id.noA);
        final TextView overlapA = dialog.findViewById(R.id.oA);
        final TextView totalA = dialog.findViewById(R.id.tA);
        final TextView landCovered = dialog.findViewById(R.id.lcA);
        final TextView totalLandA = dialog.findViewById(R.id.tlA);
        final TextView percentCoveredA = dialog.findViewById(R.id.pcA);
        final TextView numIntersect = dialog.findViewById(R.id.plc);
        final TextView wasted = dialog.findViewById(R.id.ww);
        final TextView totalWaterOutput = dialog.findViewById(R.id.two);
        final TextView percentWasted = dialog.findViewById(R.id.pww);
        final TextView perMonth = dialog.findViewById(R.id.permonth);
        final TextView perYear = dialog.findViewById(R.id.peryear);*/

        //TODO rephrase question for how much water sprinkler uses to
        // How much water system uses (System always outputs same amount of water)
        // Then, change calculations (it will be easier now)
        //hideLoading();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String wU = waterUsedE.getText().toString();
                boolean fgood = false;
                boolean sgood = false;
                double waterUsed = 0;
                double duration = 0;
                if (wU != null) {
                    if (wU.length() > 0) {
                        waterUsed = Double.parseDouble(wU);
                        fgood = true;
                    }
                }

                String d = durationE.getText().toString();
                if (d != null) {
                    if (d.length() > 0) {
                        duration = Double.parseDouble(d);
                        sgood = true;
                    }
                }
                Log.wtf("*  Progress", fgood + " " + sgood);

                if (!(fgood && sgood))
                    shortToast("Please fill in the information.");
                else {
                    //DONE IT is good to contineu ahead.
                    Spinner soilType = dialog.findViewById(R.id.soilType);
                    String choice = soilType.getSelectedItem().toString();
                    dialog.cancel();
                    dialog.dismiss();

                    showResults(waterUsed, duration, choice);
                    //handleResults(3, 3, 3, 3, 3);
                    /*toHide.setVisibility(View.INVISIBLE);
                    results.setVisibility(View.VISIBLE);

                    Button goBack = dialog.findViewById(R.id.goBack);
                    Button done = dialog.findViewById(R.id.done);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });

                    goBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            toHide.setVisibility(View.VISIBLE);
                            results.setVisibility(View.INVISIBLE);
                        }
                    });*/
/*

                    //DONE Just do the calculations to display the actual stuff.

                    //INFO README
                    // I think a better method of calculating coverage is simply totalWaterOutput *(1-percentWastage)
                    double percentWastage = ((double) (area)) / ((double) (non + counter + overlappingButOnly1SprinklerRegion));
                    double coverage = ((double) (non + overlappingButOnly1SprinklerRegion + counter - area) / (v));
                    if (coverage > 1) {
                        coverage = 0;
                        for (int r : dv.sprinkr) {
                            coverage += Math.PI * Math.pow(r, 2);
                        }
                        coverage -= coverage * percentWastage;
                        coverage = coverage / v;
                        if (coverage > 1)
                            coverage = 0.7678;
                    }
                    //double percentWastage = area / (non + counter + overlappingButOnly1SprinklerRegion);

                    //README Accounting for soil type
                    String choice = soilType.getSelectedItem().toString();
                    if (choice.equals("Sandy")) {
                        percentWastage *= 0.95f;
                    } else if (choice.equals("Loam")) {
                        percentWastage *= 0.9f;
                    } else if (choice.equals("Clay")) {
                        percentWastage *= 1.15f;
                    }

                    numSprink.setText(dv.sprinkx.size() + "");
                    Log.wtf("* Stats Nums: ", waterUsed + " " + duration + " " + coverage + " " + percentWastage);
                    numIntersect.setText("" + (dv.sprinkx.size() - singleX.size()));
                    totalLandA.setText(String.format("%1$,.2f", (v * Math.pow(dv.ratio, 2))) + " sq. ft");
                    Log.wtf("* INFO", coverage + " " + v + " " + dv.ratio);
                    landCovered.setText(String.format("%1$,.2f", (coverage * v * Math.pow(dv.ratio, 2))) + " sq. ft");
                    percentCoveredA.setText(String.format("%1$,.1f", coverage * 100) + "%");
                    percentWasted.setText(String.format("%1$,.1f", percentWastage * 100) + "%");
                    perMonth.setText(String.format("%1$,.0f", 4 * duration * waterUsed * dv.sprinky.size() * percentWastage) + " gal");
                    perYear.setText(String.format("%1$,.0f", 52 * duration * waterUsed * dv.sprinky.size() * percentWastage) + " gal");
                    totalWaterOutput.setText(String.format("%1$,.1f", duration * waterUsed * dv.sprinky.size()) + " gal/wk");
                    totalA.setText(String.format("%1$,.1f", duration * waterUsed * dv.sprinky.size()) + " gal/wk");
                    wasted.setText(String.format("%1$,.2f",
                            duration * waterUsed * dv.sprinky.size() * percentWastage) + " gal/wk");


                    nonOverlapT.setText(String.format("%1$,.2f", duration * waterUsed * singleX.size()) + " gal/wk");
                    //nonOverlapT.setText(String.format("%1$,.2f", (pixToGallon(non, waterUsed) * duration)) + " gal/wk");
                    overlapA.setText(String.format("%1$,.2f", duration * waterUsed * (dv.sprinkx.size() - singleX.size())) + " gal/wk");
                    //overlapA.setText(String.format("%1$,.2f", duration * (pixToGallon(non + counter + overlappingButOnly1SprinklerRegion, waterUsed) - pixToGallon(non, waterUsed))) + " gal/wk");
*/

                    //TODO Shift left and shift right

                    //wasted.setText(String.format("%1$,.1f", duration * pixToGallon(area, waterUsed)) + " gal/wk");
                    //totalWaterOutput.setText(String.format("%1$,.2f", duration * (pixToGallon(non + counter + overlappingButOnly1SprinklerRegion, waterUsed))) + " gal/wk");

                }

            }
        });

        dialog.show();
    }

    int overlappingCount = 0;
    Set<Integer> overlaps;

    private void displayCircleResults(final double waste, final double total, final double overflowWaste
            , final int overflowingCount, final int outsideCount) {
        final Dialog dialog = new Dialog(IrrigationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        darken = false;
        isHeadtoHead = false;

        Button next = (Button) dialog.findViewById(R.id.done);
        Button backBtn = (Button) dialog.findViewById(R.id.goBack);
        final RelativeLayout results = (RelativeLayout) dialog.findViewById(R.id.realresults);
        final RelativeLayout toHide = dialog.findViewById(R.id.questions);
        final EditText waterUsedE = dialog.findViewById(R.id.waterused);
        final EditText durationE = dialog.findViewById(R.id.duration);
        final Spinner soilType = dialog.findViewById(R.id.soilType);
        final CheckBox cb = dialog.findViewById(R.id.head);
        ImageView headHelp = dialog.findViewById(R.id.headhelp);

        headHelp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                makeToast("Head to head coverage is when sprinklers require overlap to evenly distribute water. Overlap of 2 sprinklers" +
                        " will not be included in wastage.");
                return false;
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) buttonView.setTextColor(0XFF000000);
                if (!isChecked) buttonView.setTextColor(0XFF757575);
                isHeadtoHead = isChecked;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String wU = waterUsedE.getText().toString();
                boolean fgood = false;
                boolean sgood = false;
                double waterUsed = 0;
                double duration = 0;
                if (wU != null) {
                    if (wU.length() > 0) {
                        waterUsed = Double.parseDouble(wU);
                        fgood = true;
                    }
                }

                String d = durationE.getText().toString();
                if (d != null) {
                    if (d.length() > 0) {
                        duration = Double.parseDouble(d);
                        sgood = true;
                    }
                }
                Log.wtf("*  Progress", fgood + " " + sgood);

                if (!(fgood && sgood))
                    shortToast("Please fill in the information.");
                else {
                    //DONE IT is good to contineu ahead.
                    dialog.cancel();
                    dialog.dismiss();

                    String choice = soilType.getSelectedItem().toString();
                    showCircleResults(waste, total, overflowWaste
                            , overflowingCount, outsideCount, waterUsed, duration, choice);
                }

            }
        });

        dialog.show();
    }

    public boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void showCircleResults(final double waste, final double total, final double overflowWaste
            , final int overflowingCount, final int outsideCount, double waterUsed, double duration, String choice) {
        final Dialog dialog = new Dialog(IrrigationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.real_result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!isTablet())
            dialog.getWindow().setLayout(dialog.getWindow().getAttributes().width,
                    (int) (dv.screenH * 0.9d));
        else {
            RelativeLayout holder = dialog.findViewById(R.id.holder);
            RelativeLayout reller = dialog.findViewById(R.id.reller);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.reller);
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            holder.setLayoutParams(params);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reller.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ABOVE);
            reller.setLayoutParams(layoutParams);
            ViewGroup.LayoutParams paramser = dialog.findViewById(R.id.carder).getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            paramser.width = (int) (paramser.width * 1.18f);
        }
        final ImageView excessHelp = dialog.findViewById(R.id.excessHelp);
        final ImageView overflowHelp = dialog.findViewById(R.id.overflowHelp);
        final ImageView insideHelp = dialog.findViewById(R.id.insideHelp);
        final ImageView outsideHelp = dialog.findViewById(R.id.outsideHelp);


        final Button back = dialog.findViewById(R.id.goBack);
        Button done = dialog.findViewById(R.id.done);
        View.OnClickListener clicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                if (view.getId() == back.getId()) displayCircleResults(waste, total,
                        overflowWaste, overflowingCount, outsideCount);
            }
        };
        back.setOnClickListener(clicker);
        done.setOnClickListener(clicker);

        double soilFactor = 1;
        if (choice.equals("Sandy")) {
            soilFactor *= 0.95f;
        } else if (choice.equals("Loam")) {
            soilFactor *= 0.9f;
        } else if (choice.equals("Clay")) {
            soilFactor *= 1.15f;
        }

        wasted = waste;
        if (isHeadtoHead) {
            wasted = overflowWaste + overCounted3;
        }

        wasted *= soilFactor;
        if (wasted < 0 || wasted > total)
            wasted = total * .95;
        Log.wtf("*- End Results:", "- " + (int) wasted + " " + (int) total + " " + (int) (wasted * 100 / total) + "%\n---___---");
        /*Log.wtf("*- Individual Circles", "Size: " + singleR.size());
        Log.wtf("*- Outside Intersecting", "Size: " + outsideIntersecting.size());
        Log.wtf("*- Completely Inside Circle", "Size: " + completelyInside.size());
        Log.wtf("*- Inside Circles", "Size: " + insideCircles.size());
        Log.wtf("*- Inside Intersecting", "Size: " + insideIntersecting.size());*/


        TextView numSprink = (TextView) dialog.findViewById(R.id.sprinks);
        numSprink.setText(Integer.toString(dv.sprinkx.size()));
        TextView outsideSprink = dialog.findViewById(R.id.outsideNum);
        TextView insideSprink = dialog.findViewById(R.id.insideNum);
        outsideSprink.setText(Integer.toString(outsideCount));
        insideSprink.setText(Integer.toString(dv.sprinkx.size() - outsideCount));
        TextView overlappingSprink = dialog.findViewById(R.id.overlapNum);
        TextView overflowSprink = dialog.findViewById(R.id.overflowNum);
        overflowSprink.setText(Integer.toString(overflowingCount));
        //overlappingSprink.setText(Integer.toString(Math.max(overlappingCount, dv.sprinkx.size())));
        /*overlappingSprink.setText(Integer.toString(dv.sprinkx.size() - singleX.size()
        -completelyOutside.size()));*/
        overlappingSprink.setText(Integer.toString(overlaps.size()));
        /*final TextView nonOverlapT = dialog.findViewById(R.id.noA);
        final TextView overlapA = dialog.findViewById(R.id.oA);*/
        final TextView totalA = dialog.findViewById(R.id.tA);
        final TextView landCovered = dialog.findViewById(R.id.landCovered);
        final TextView totalLandA = dialog.findViewById(R.id.totalLand);
        final TextView percentCoveredA = dialog.findViewById(R.id.pcA);
        final TextView wastedT = dialog.findViewById(R.id.ww);
        final TextView totalWaterOutput = dialog.findViewById(R.id.two);
        final TextView percentWasted = dialog.findViewById(R.id.pww);
        final TextView perMonth = dialog.findViewById(R.id.permonth);
        final TextView perYear = dialog.findViewById(R.id.peryear);
        TextView overflowWaterOutput = dialog.findViewById(R.id.overflow);
        TextView excessiveWaterOutput = dialog.findViewById(R.id.excess);

        double landArea = Math.PI * dv.circleFeet * dv.circleFeet;
        Log.wtf("Land Area", landArea + " " + dv.backUpRadius + " " + dv.ratio + " ---> " +
                (dv.backUpRadius / dv.ratio));
        int num = dv.sprinkx.size();
        double totalWater = num * duration * waterUsed;
        totalLandA.setText(format(landArea) + " sq. ft");
        totalWaterOutput.setText(format(totalWater) + " gal/wk");
        totalA.setText(Math.round(totalWater * 10) / 10 + " gal/wk");
        final double overflowWater = totalWater * overflowWaste / total;
        overflowWaterOutput.setText(format(overflowWater * soilFactor) + " gal/wk");
        final double excessive;
        if (isHeadtoHead)
            excessive = totalWater * (overCounted3) / total;
        else
            excessive = totalWater * (overlapWastage - overCounted3) / total;excessiveWaterOutput.setText(format(excessive * soilFactor) + " gal/wk");

        double waterWasted = totalWater * wasted / total;
        wastedT.setText(format(waterWasted) + " gal/wk");

        double percentageWasted = wasted / total;
        percentWasted.setText(format(percentageWasted * 100) + "%");

        perMonth.setText((format2(waterWasted * 4)) + " gal");
        perYear.setText((format2(waterWasted * 52)) + " gal");

        //double notWastedWater = 1 - percentageWasted;
        //double notWastedWater = 1 - percentageWasted + overflowWater/total + overCounted3 / total;
        double notWastedWater = 1 - percentageWasted +
                overflowWaste / total;

        double landCoveredArea = (total - overflowWaste
                - (waste - overflowWaste) + overCounted3) * landArea / (Math.PI * (dv.backUpRadius * (dv.screenW / 200))
                * (dv.backUpRadius * (dv.screenW / 200)));
        // double landCoveredArea = notWastedWater * total / (Math.PI * dv.backUpRadius * dv.backUpRadius) * landArea;
        double percentLandCovered = landCoveredArea / landArea;
        if (percentLandCovered > 1) {
            percentLandCovered = 0.992;
            landCoveredArea = landArea * percentLandCovered;
        }
        Log.wtf("Land Covered", landCoveredArea + " = " + total + " - " + overflowWaste
                + " - " + (waste - overflowWaste) + " + " + overCounted3 + ") / " + (Math.PI * (dv.backUpRadius * (dv.screenW / 200))
                * (dv.backUpRadius * (dv.screenW / 200))) + "  * " + landArea);
        /*Log.wtf("Percent Land", " " + Math.round(notWastedWater * 100) + " " +
                Math.round(percentageWasted * 100) + " " + Math.round(overflowWater * 100));
        Log.wtf("\"-_Land Covered", " " + Math.round(notWastedWater * 100) + " " +
                Math.round(percentageWasted * 100) + " " + Math.round(overflowWater * 100));
        Log.wtf("*- Land Coverage - ", dv.polygonArea() + " " + notWastedWater + " " + landCoveredArea
                + " " + percentLandCovered);*/
        landCovered.setText(format(landCoveredArea) + " sq. ft");
        double variable = notWastedWater * total / (Math.PI * dv.backUpRadius * dv.backUpRadius);
        /*totalLandA.setText(Math.round(variable * 100) + " " + Math.round(notWastedWater * 100) + " == " +
                Math.round(percentageWasted * 100) + " " + Math.round(overflowWater * 100));*/
        percentCoveredA.setText(format(percentLandCovered * 100) + "%");
        final double finalSoilFactor = soilFactor;
        View.OnTouchListener toucher = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() == excessHelp.getId()) {
                    if (excessive == 0)
                        if (!isHeadtoHead)
                            makeToast("Your sprinkler design does not waste any water by excessive watering (overlapping sprinklers)!");
                        else
                            makeToast("Your sprinkler design does not excessively water your land!");
                    else
                        makeToast(format(excessive * finalSoilFactor) + " gallons per week will be wasted by overwatering" +
                                " the same region repeatedly.");
                }
                if (view.getId() == insideHelp.getId()) {
                    if (dv.sprinkr.size() - (outsideIntersecting.size() + completelyOutside.size()) == dv.sprinkr.size())
                        makeToast("All your sprinklers are placed inside the land plot.");
                    else
                        makeToast("You have " + (dv.sprinkr.size() - (outsideIntersecting.size() + completelyOutside.size())) + " sprinklers placed inside the land plot.");
                }
                if (view.getId() == outsideHelp.getId())
                    makeToast("You have " + (outsideIntersecting.size() + completelyOutside.size()) + " sprinklers placed outside your land plot.");
                else if (view.getId() == overflowHelp.getId()) {
                    if (overflowWater == 0)
                        makeToast("Your sprinkler design does not waste any water by overflowing outside the land plot!");
                    else
                        makeToast(format(overflowWater * finalSoilFactor) + " gallons per week will be wasted by overflowing out of the specified land plot.");

                }
                return false;
            }
        };
        excessHelp.setOnTouchListener(toucher);
        overflowHelp.setOnTouchListener(toucher);
        insideHelp.setOnTouchListener(toucher);
        outsideHelp.setOnTouchListener(toucher);
        dialog.show();
    }


    //FUTURE FILES see how app looks with plotting sprinkler center as a black dot.
    //FUTURE FILES Give actual feedback as to how they can improve the design plan.
    //  Have if statements. Maybe say you have 3 sprinklers outside land plot, reposition them inside to
    //  save x amount of water. You have x overlapping sprinklers
    //  and only 60% coverage, reposition them to cover more land.
    private void showResults(double waterUsed, double duration, String choice) {
        final Dialog dialog = new Dialog(IrrigationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.real_result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!isTablet())
            dialog.getWindow().setLayout(dialog.getWindow().getAttributes().width,
                    (int) (dv.screenH * 0.9d));
        else {
            RelativeLayout holder = dialog.findViewById(R.id.holder);
            RelativeLayout reller = dialog.findViewById(R.id.reller);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.reller);
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            holder.setLayoutParams(params);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reller.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ABOVE);
            reller.setLayoutParams(layoutParams);
            ViewGroup.LayoutParams paramser = dialog.findViewById(R.id.carder).getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            paramser.width = (int) (paramser.width * 1.18f);
            //dialog.findViewById(R.id.carder).setLayoutParams(params);

           /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = (int)(dialog.getWindow().getAttributes().width * 1.35f);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                      dialog.getWindow().setAttributes(lp);
 makeToast("I am a tablet: " + isTablet());*/
            /*dialog.getWindow().setLayout((int) (dialog.getWindow().getAttributes().width * 1.35f),
                    dialog.getWindow().getDecorView().getHeight());*/
        }

        final ImageView excessHelp = dialog.findViewById(R.id.excessHelp);
        final ImageView overflowHelp = dialog.findViewById(R.id.overflowHelp);
        final ImageView insideHelp = dialog.findViewById(R.id.insideHelp);
        final ImageView outsideHelp = dialog.findViewById(R.id.outsideHelp);

        final Button back = dialog.findViewById(R.id.goBack);
        Button done = dialog.findViewById(R.id.done);
        View.OnClickListener clicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                if (view.getId() == back.getId()) displayResults();
            }
        };
        back.setOnClickListener(clicker);
        done.setOnClickListener(clicker);

        double soilFactor = 1;
        if (choice.equals("Sandy")) {
            soilFactor *= 0.95f;
        } else if (choice.equals("Loam")) {
            soilFactor *= 0.9f;
        } else if (choice.equals("Clay")) {
            soilFactor *= 1.1f;
        }

        //total += totalInsideArea;
        //total += totalOverflowArea;
        /*wasted += overFlowWastage;
        wasted += overlapWastage;
        //INFO Subtracts overlap of 3 circles
        wasted -= overCounted3;*/

        if (isHeadtoHead) {
            wasted = overFlowWastage + overCounted3;
            Log.wtf("HEADTOHEAD", wasted + " = " + overFlowWastage + " + " + overCounted3);
        }

        wasted *= soilFactor;
        if (wasted < 0 || wasted > total)
            wasted = total * .95;
        Log.wtf("*- End Results:", "- " + (int) wasted + " " + (int) total + " " + (int) (wasted * 100 / total) + "%\n---___---");
        Log.wtf("*- Individual Circles", "Size: " + singleR.size());
        Log.wtf("*- Outside Intersecting", "Size: " + outsideIntersecting.size());
        Log.wtf("*- Completely Inside Circle", "Size: " + completelyInside.size());
        Log.wtf("*- Inside Circles", "Size: " + insideCircles.size());
        Log.wtf("*- Inside Intersecting", "Size: " + insideIntersecting.size());

        //TODO Land Coverage is not always working accurately
        //TODO Extra button -
        //  1. Try to do "Make into Rectangle" button again. This time assign TextViews properly
        //  2.If it doesn't work remove the button completely and shift down side length asker
        //TODO IMPORTANT Wastage calculation is wrong for: big circle intersect big circle with
        //  small circle intersecting small circle. Wastage region jumps from 87 (big only)
        // to 140 (big and small overlaps). Check it out (may be linked to land coverage)

        TextView numSprink = (TextView) dialog.findViewById(R.id.sprinks);
        numSprink.setText(Integer.toString(dv.sprinkx.size()));
        TextView outsideSprink = dialog.findViewById(R.id.outsideNum);
        TextView insideSprink = dialog.findViewById(R.id.insideNum);
        outsideSprink.setText(Integer.toString(outsideIntersecting.size() + completelyOutside.size()));
        insideSprink.setText(Integer.toString(insideCircles.size()));
        TextView overlappingSprink = dialog.findViewById(R.id.overlapNum);
        TextView overflowSprink = dialog.findViewById(R.id.overflowNum);
        overflowSprink.setText(Integer.toString(outsideIntersecting.size() + completelyOutside.size()
                + insideIntersecting.size()));
        //overlappingSprink.setText(Integer.toString(Math.max(overlappingCount, dv.sprinkx.size())));
        /*overlappingSprink.setText(Integer.toString(dv.sprinkx.size() - singleX.size()
        -completelyOutside.size()));*/
        overlappingSprink.setText(Integer.toString(overlaps.size()));
        /*final TextView nonOverlapT = dialog.findViewById(R.id.noA);
        final TextView overlapA = dialog.findViewById(R.id.oA);*/
        final TextView totalA = dialog.findViewById(R.id.tA);
        final TextView landCovered = dialog.findViewById(R.id.landCovered);
        final TextView totalLandA = dialog.findViewById(R.id.totalLand);
        final TextView percentCoveredA = dialog.findViewById(R.id.pcA);
        final TextView wastedT = dialog.findViewById(R.id.ww);
        final TextView totalWaterOutput = dialog.findViewById(R.id.two);
        final TextView percentWasted = dialog.findViewById(R.id.pww);
        final TextView perMonth = dialog.findViewById(R.id.permonth);
        final TextView perYear = dialog.findViewById(R.id.peryear);
        TextView overflowWaterOutput = dialog.findViewById(R.id.overflow);
        TextView excessiveWaterOutput = dialog.findViewById(R.id.excess);

        double landArea = dv.polygonArea() * dv.ratio * dv.ratio;
        int num = dv.sprinkx.size();
        double totalWater = num * duration * waterUsed;
        totalLandA.setText(format(landArea) + " sq. ft");
        totalWaterOutput.setText(format(totalWater) + " gal/wk");
        totalA.setText(Math.round(totalWater * 10) / 10 + " gal/wk");
        final double overflowWater = totalWater * overFlowWastage / total;
        overflowWaterOutput.setText(format(overflowWater * soilFactor) + " gal/wk");
        final double excessive;
        if (isHeadtoHead)
            excessive = totalWater * (overCounted3) / total;
        else
            excessive = totalWater * (overlapWastage - overCounted3) / total;
        excessiveWaterOutput.setText(format(excessive * soilFactor) + " gal/wk");

        double waterWasted = totalWater * wasted / total;
        wastedT.setText(format(waterWasted) + " gal/wk");

        double percentageWasted = wasted / total;
        percentWasted.setText(format(percentageWasted * 100) + "%");

        perMonth.setText((format2(waterWasted * 4)) + " gal");
        perYear.setText((format2(waterWasted * 52)) + " gal");

        //double notWastedWater = 1 - percentageWasted;
        //double notWastedWater = 1 - percentageWasted + overflowWater/total + overCounted3 / total;
        double notWastedWater = 1 - percentageWasted +
                overflowWater / total;
        //double landCoveredArea = notWastedWater * total / dv.polygonArea() * landArea;
        double landCoveredArea = (total - wasted / soilFactor + overCounted3) / dv.polygonArea()
                * landArea;
        //notWastedWater * total / dv.polygonArea() * landArea;
        double percentLandCovered = landCoveredArea / landArea;
        if (percentLandCovered > 1) {
            percentLandCovered = 0.992;
            landCoveredArea = landArea * percentLandCovered;
        }
        Log.wtf("----Covered Important", landCoveredArea + " = " +
                total + " - " + wasted / soilFactor + " + " + overCounted3 + " / " + dv.polygonArea());
        Log.wtf("Percent Land", " " + Math.round(notWastedWater * 100) + " " +
                Math.round(percentageWasted * 100) + " " + Math.round(overflowWater * 100));
        Log.wtf("\"-_Land Covered", " " + Math.round(notWastedWater * 100) + " " +
                Math.round(percentageWasted * 100) + " " + Math.round(overflowWater * 100));
        Log.wtf("*- Land Coverage - ", dv.polygonArea() + " " + notWastedWater + " " + landCoveredArea
                + " " + percentLandCovered);
        landCovered.setText(format(landCoveredArea) + " sq. ft");
        double variable = notWastedWater * total / dv.polygonArea();
        /*totalLandA.setText(Math.round(variable * 100) + " " + Math.round(notWastedWater * 100) + " == " +
                Math.round(percentageWasted * 100) + " " + Math.round(overflowWater * 100));*/
        percentCoveredA.setText(format(percentLandCovered * 100) + "%");

        final double finalSoilFactor = soilFactor;
        View.OnTouchListener toucher = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() == excessHelp.getId()) {
                    if (excessive == 0)
                        if (!isHeadtoHead)
                            makeToast("Your sprinkler design does not waste any water by excessive watering (overlapping sprinklers)!");
                        else
                            makeToast("Your sprinkler design does not excessively water your land!");
                    else
                        makeToast(format(excessive * finalSoilFactor) + " gallons per week will be wasted by overwatering" +
                                " the same region repeatedly.");
                }
                if (view.getId() == insideHelp.getId()) {
                    if (dv.sprinkr.size() - (outsideIntersecting.size() + completelyOutside.size()) == dv.sprinkr.size())
                        makeToast("All your sprinklers are placed inside the land plot.");
                    else
                        makeToast("You have " + (dv.sprinkr.size() - (outsideIntersecting.size() + completelyOutside.size())) + " sprinklers placed inside the land plot.");
                }
                if (view.getId() == outsideHelp.getId())
                    makeToast("You have " + (outsideIntersecting.size() + completelyOutside.size()) + " sprinklers placed outside your land plot.");
                else if (view.getId() == overflowHelp.getId()) {
                    if (overflowWater == 0)
                        makeToast("Your sprinkler design does not waste any water by overflowing outside the land plot!");
                    else
                        makeToast(format(overflowWater * finalSoilFactor) + " gallons per week will be wasted by overflowing out of the specified land plot.");

                }
                return false;
            }
        };
        excessHelp.setOnTouchListener(toucher);
        overflowHelp.setOnTouchListener(toucher);
        insideHelp.setOnTouchListener(toucher);
        outsideHelp.setOnTouchListener(toucher);
        dialog.show();

    }

    private String format2(double v) {
        return String.format("%1$,.0f", (int) Math.round(v * 10) / 10d);
    }

    private String format(double overflowWater) {
        return String.format("%1$,.1f", Math.round(overflowWater * 10) / 10d);
    }


    double overCounted3 = 0;

    public static double[] intersectionOf2Lines(double m1, double b1, double m2, double b2) {
        double x = (b1 - b2) / (m2 - m1);
        double y = m2 * x + b2;

        return new double[]{x, y};
    }

    private void calculate3CircleOverlap() {
        Log.wtf("*- 3 Overlapping Circles", "_______________________________________");
        double area = 0;
        for (int i = 0; i < insideCircles.size() - 2; i++) {
            for (int j = i + 1; j < insideCircles.size() - 1; j++) {
                int circle1 = insideCircles.get(i);
                int circle2 = insideCircles.get(j);

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                //README The 2 circles overlap
                if (distance <= r1 + r2) {
                    for (int k = j + 1; k < insideCircles.size(); k++) {
                        int circle3 = insideCircles.get(k);
                        double x3 = dv.sprinkx.get(circle3);
                        double y3 = dv.sprinky.get(circle3);
                        double r3 = dv.sprinkr.get(circle3);
                        double angle3 = dv.angleList.get(circle3);
                        overlaps.add(circle1);
                        overlaps.add(circle2);
                        double dist1 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2));
                        double dist2 = Math.sqrt(Math.pow(x2 - x3, 2) + Math.pow(y2 - y3, 2));
                        if ((dist1 <= r1 + r3) && (dist2 <= r2 + r3)) {
                            overlaps.add(circle3);
                            Log.wtf("*- 3 intersects: ", circle1 + " " + circle2 + " " + circle3);
                            //README 3rd circle overlaps with other 2.
                            //TODO Have to determine the intersection points of c1-c2, c1-c3, c2-c3 that are inside the other circle.
                            double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                            double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                            double h = Math.sqrt(r1 * r1 - a * a);

                            double x4 = x1 + a * (x2 - x1) / d;
                            double y4 = y1 + a * (y2 - y1) / d;
                            double x5 = x4 + h * (y2 - y1) / d;     // also x3=x2-h*(y1-y0)/d
                            double y5 = y4 - h * (x2 - x1) / d;
                            x4 = x4 - h * (y2 - y1) / d;
                            y4 = y4 + h * (x2 - x1) / d;

                            double d2 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2));
                            double a2 = (r1 * r1 - r3 * r3 + d2 * d2) / (2 * d2);
                            double h2 = Math.sqrt(r1 * r1 - a2 * a2);

                            double x6 = x1 + a2 * (x3 - x1) / d2;
                            double y6 = y1 + a2 * (y3 - y1) / d2;
                            double x7 = x6 + h2 * (y3 - y1) / d2;     // also x3=x2-h*(y1-y0)/d
                            double y7 = y6 - h2 * (x3 - x1) / d2;
                            x6 = x6 - h2 * (y3 - y1) / d2;
                            y6 = y6 + h2 * (x3 - x1) / d2;

                            double d3 = Math.sqrt(Math.pow(x2 - x3, 2) + Math.pow(y2 - y3, 2));
                            double a3 = (r2 * r2 - r3 * r3 + d3 * d3) / (2 * d3);
                            double h3 = Math.sqrt(r2 * r2 - a3 * a3);

                            double x8 = x2 + a3 * (x3 - x2) / d3;
                            double y8 = y2 + a3 * (y3 - y2) / d3;
                            double x9 = x8 + h3 * (y3 - y2) / d3;     // also x3=x2-h*(y1-y0)/d
                            double y9 = y8 - h3 * (x3 - x2) / d3;
                            x8 = x8 - h3 * (y3 - y2) / d3;
                            y8 = y8 + h3 * (x3 - x2) / d3;

                            double i1x, i1y, i2x, i2y, i3x, i3y;


                            boolean chain1 = false, chain2 = false, chain3 = false;
                            double[] intersection1 = circleCircleIntersectionisInOtherCircle(x4, y4, x5, y5, x3, y3, r3);
                            double[] intersection2 = circleCircleIntersectionisInOtherCircle(x6, y6, x7, y7, x2, y2, r2);
                            double[] intersection3 = circleCircleIntersectionisInOtherCircle(x8, y8, x9, y9, x1, y1, r1);
                            chain3 = intersection1[2] == 1;
                            chain2 = intersection2[2] == 1;
                            chain1 = intersection3[2] == 1;
                            //README There is a chain
                            if (chain1 || chain2 || chain3) {
                                //TODO IF chain need to subtract area of overlap of 2 circles.
                                //INFO Below is the info for the circles that we need to calculate the overlap of.
                                double xc1 = x1, xc2 = x2, yc1 = y1, yc2 = y2, rc1 = r1, rc2 = r2;
                                if (chain3) {
                                    //Overlap of 1 and 2
                                } else if (chain2) {
                                    //Overlap of 1 and 3
                                    xc2 = x3;
                                    yc2 = y3;
                                    rc2 = r3;
                                } else {
                                    //Overlap of 2 and 3
                                    xc1 = x2;
                                    yc1 = y2;
                                    rc1 = r2;
                                    xc2 = x3;
                                    yc2 = y3;
                                    rc2 = r3;
                                }
                                //INFO calculate the overlap
                                Double r = rc1;
                                Double R = rc2;
                                double firstX = xc1;
                                double secondX = xc2;
                                double firstY = yc1;
                                double secondY = yc2;
                                Double dm = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
                                if (R < r) {
                                    // swap
                                    r = rc2;
                                    R = rc1;
                                }
                                Double part1 = r * r * Math.acos((dm * dm + r * r - R * R) / (2 * dm * r));
                                Double part2 = R * R * Math.acos((dm * dm + R * R - r * r) / (2 * dm * R));
                                Double part3 = 0.5f * Math.sqrt((-dm + r + R) * (dm + r - R) * (dm - r + R) * (dm + r + R));

                                double intersectionArea = part1 + part2 - part3;

                                //README Maybe intersectionArea is 0 because circle is inside other circle.
                                if (!(intersectionArea > 0)) {
                                    intersectionArea = Math.PI * Math.pow(r, 2) * 345 / 360;
                                }
                                area += intersectionArea;
                                Log.wtf("*- Chain Circles", "Overlap: " + intersectionArea + " Total Area: " + area);
                            } else {
                                i1x = intersection3[0];
                                i1y = intersection3[1];
                                i2x = intersection2[0];
                                i2y = intersection2[1];
                                i3x = intersection1[0];
                                i3y = intersection1[1];

                                double length1 = Math.sqrt(Math.pow(i1x - i2x, 2) + Math.pow(i1y - i2y, 2));
                                double length2 = Math.sqrt(Math.pow(i2x - i3x, 2) + Math.pow(i2y - i3y, 2));
                                double length3 = Math.sqrt(Math.pow(i1x - i3x, 2) + Math.pow(i1y - i3y, 2));
                                double s = (length1 + length2 + length3) / 2;

                                //INFO Area of the triangle formed by the three points
                                double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                                double arc1, arc2, arc3;
                                arc1 = arcArea(r1, i2x, i2y, i3x, i3y, x1, y1);
                                arc2 = arcArea(r2, i1x, i1y, i3x, i3y, x2, y2);
                                arc3 = arcArea(r3, i1x, i1y, i2x, i2y, x3, y3);
                                double intersectionArea = arc1 + arc2 + arc3 + triangleArea;
                                area += intersectionArea;
                                Log.wtf("*- Areas", "Overlap: " + intersectionArea + " Total Area: " + area + " Triangle: "
                                        + triangleArea + " Arc1:" + arc1 + " Arc2:" + arc2 + " Arc3:" + arc3 + " Circles: " + (Math.PI * r1 * r1 * 3));
                            }

                        }
                    }
                }
            }
        }
        overCounted3 = area;
        Log.wtf("*- 3 Circle Overlap Results", overCounted3 + "");
    }

    //README Give it the intersections of 2 circles (x1, y1) and (x2, y2) then the center/radius of the other circle.
    private double[] circleCircleIntersectionisInOtherCircle(double x1, double y1, double x2, double y2, double centerX, double centerY, double radius) {
        double distance1 = Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));
        double distance2 = Math.sqrt(Math.pow(x2 - centerX, 2) + Math.pow(y2 - centerY, 2));
        boolean b1 = false;
        boolean b2 = false;
        //README Chainis important becasue we can have a chain of 3 circles, side by side
        //  and 1 circle intersects with other 2.
        boolean chain = false;
        b1 = distance1 <= radius;
        b2 = distance2 <= radius;
        chain = b1 && b2;
        return new double[]{b1 ? x1 : x2, b1 ? y1 : y2, chain ? 1 : 0};
    }

    public static boolean landCoordinates, sprinklerCoordinates;
    public static ArrayList<Coordinates> coordinateIds;

    private void insideIntersectingOutsideIntersecting() {
        Log.wtf("*- Inside and Outside Intersecting", "_______________________________________");
        double overlap2 = 0;
        for (int i = 0; i < insideIntersecting.size(); i++) {
            for (int j = 0; j < outsideIntersecting.size(); j++) {
                int circle1 = insideIntersecting.get(i).getCirclePos();
                int circle2 = outsideIntersecting.get(j).getCirclePos();

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

                double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                double h = Math.sqrt(r1 * r1 - a * a);
                double x3 = x1 + a * (x2 - x1) / d;
                double y3 = y1 + a * (y2 - y1) / d;
                double x4 = x3 + h * (y2 - y1) / d;     // also x3=x2-h*(y1-y0)/d
                double y4 = y3 - h * (x2 - x1) / d;
                x3 = x3 - h * (y2 - y1) / d;
                y3 = y3 + h * (x2 - x1) / d;

                /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (int) x3;
                params.topMargin = (int) y3;
                TextView textView = new TextView(context);
                textView.setText("(" + (int) x3 + "," + (int) y3 + ")");
                textView.setId(dv.idCounter);
                textView.setTextSize(7);
                textView.setLayoutParams(params);
                //makeToast("Making the text");
                rlDvHolder.addView(textView);
                dv.idCounter++;
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.leftMargin = (int) x4;
                params2.topMargin = (int) y4;
                TextView textView2 = new TextView(context);
                textView2.setText("(" + (int) x4 + "," + (int) y4 + ")");
                textView2.setId(dv.idCounter);
                textView2.setLayoutParams(params2);
                textView2.setTextSize(7);
                //makeToast("Making the text");
                rlDvHolder.addView(textView2);
                dv.idCounter++;*/

                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
                        //README Both intersection points are inside the land meaning that it is simply overlap wastage
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
                        double intersectionArea = 0;
                        Double r = r1;
                        Double R = r2;
                        double firstX = x1;
                        double secondX = x2;
                        double firstY = y1;
                        double secondY = y2;
                        int smallC = circle1;
                        int bigc = circle2;
                        if (R < r) {
                            // swap
                            r = r2;
                            R = r1;
                            smallC = circle2;
                            bigc = circle1;
                        }
                        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                        intersectionArea = part1 + part2 - part3;

                        //README Maybe intersectionArea is 0 because circle is inside other circle.
                        if (!(intersectionArea > 0)) {
                            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
                        }

                        overlap2 += intersectionArea;
                    } else {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
//README Intersection area is inside and outside
                        //INFO You need the circle-circle intersection that is inside.
                        //README Below is for the circle-circle intersection
                        double circleX = 0, circleY = 0;
                        if (out1) {
                            circleX = x4;
                            circleY = y4;
                        } else if (out2) {
                            circleX = x3;
                            circleY = y3;
                        }
                        //TODO Calculate circle-line intersection for both circles.
                        //INFO Below is for the circle-line intersections.
                        double ix1, ix2, iy1, iy2;
                        ix1 = insideIntersecting.get(i).getX1();
                        iy1 = insideIntersecting.get(i).getY1();
                        double dist = Math.sqrt(Math.pow(ix1 - outsideIntersecting.get(j).circleX, 2) +
                                Math.pow(iy1 - outsideIntersecting.get(j).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > outsideIntersecting.get(j).getRadius()) {
                            ix1 = insideIntersecting.get(i).getX2();
                            iy1 = insideIntersecting.get(i).getY2();
                        }

                        ix2 = outsideIntersecting.get(j).getX1();
                        iy2 = outsideIntersecting.get(j).getY1();
                        dist = Math.sqrt(Math.pow(ix2 - insideIntersecting.get(i).circleX, 2) +
                                Math.pow(iy2 - insideIntersecting.get(i).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > insideIntersecting.get(i).getRadius()) {
                            ix2 = outsideIntersecting.get(j).getX2();
                            iy2 = outsideIntersecting.get(j).getY2();
                        }

                        double length1 = Math.sqrt(Math.pow(ix1 - ix2, 2) + Math.pow(iy1 - iy2, 2));
                        double length2 = Math.sqrt(Math.pow(ix2 - circleX, 2) + Math.pow(iy2 - circleY, 2));
                        double length3 = Math.sqrt(Math.pow(ix1 - circleX, 2) + Math.pow(iy1 - circleY, 2));
                        double s = (length1 + length2 + length3) / 2;

                        /*double l = ix1 * (iy2 - circleY);
                        double m = ix2 * (circleY - iy1);
                        double n = circleX * (iy1 - iy2);

                        double triangleArea = Math.abs((l + m + n) - 50) % (Math.PI * Math.max(r1, r2) * Math.max(r1, r2));*/
                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;
                        Log.wtf("*- Information: ", "Wastage - " + (int) overlap2 + "  Full - " +
                                overlap(r1, r2, x1, x2, y1, y2, circle1, circle2) + " ix1-" + (int) ix1 + " ix2-" + (int) ix2
                                + " iy1-" + (int) iy1 + " iy2-" + (int) iy2 + " c-cX:" + (int) circleX + " c-cY:" + (int) circleY
                                + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t1 circle- " + (int) (r1 * r1 * Math.PI) + "  X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " +
                                (int) x2 + " Y2: " + (int) y2 + " Arc1-" + (int) arc1 + " Arc2- " + (int) arc2 + " Triangle-" + (int) triangleArea);

                        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = (int) ix1 - 3;
                        params.topMargin = (int) iy1;
                        TextView textView = new TextView(context);
                        textView.setText("o");
                        textView.setId(dv.idCounter);
                        textView.setTextSize(7);
                        textView.setLayoutParams(params);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView);
                        dv.idCounter++;
                        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.leftMargin = (int) ix2 - 5;
                        params2.topMargin = (int) iy2;
                        TextView textView2 = new TextView(context);
                        textView2.setText("o");
                        textView2.setId(dv.idCounter);
                        textView2.setLayoutParams(params2);
                        textView2.setTextSize(7);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView2);
                        dv.idCounter++;
                        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params3.leftMargin = (int) circleX - 6;
                        params3.topMargin = (int) circleY;
                        TextView textView3 = new TextView(context);
                        textView3.setText("o");
                        textView3.setId(dv.idCounter);
                        textView3.setLayoutParams(params3);
                        textView3.setTextSize(7);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView3);
                        dv.idCounter++;*/
                    }

                    /* Log.wtf("*- Information: ", "X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " + (int) x2 + " Y2: " + (int) y2
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t Radius - " + (int) r1 + " X3: " + (int) x3 + " Y3: " + (int) y3 + " X4: " + (int) x4 + " Y4: " + (int) y4
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWastage - " + overlap2 +"  Total - " +
                            overlap(r1, r2, x1, x2, y1, y2, circle1, circle2));*/

                }
            }
        }
        overlapWastage += overlap2;
        Log.wtf("*- Results insideIntersecting-outsideIntersecting:", (int) overlap2 + ".\n-");
    }

    private void outsideIntersectingOutsideIntersecting() {
        Log.wtf("*- Outside and Outside Intersecting", "_______________________________________");
        double overlap2 = 0;
        for (int i = 0; i < outsideIntersecting.size() - 1; i++) {
            for (int j = i + 1; j < outsideIntersecting.size(); j++) {
                int circle1 = outsideIntersecting.get(i).getCirclePos();
                int circle2 = outsideIntersecting.get(j).getCirclePos();

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

                double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                double h = Math.sqrt(r1 * r1 - a * a);
                double x3 = x1 + a * (x2 - x1) / d;
                double y3 = y1 + a * (y2 - y1) / d;
                double x4 = x3 + h * (y2 - y1) / d;     // also x3=x2-h*(y1-y0)/d
                double y4 = y3 - h * (x2 - x1) / d;
                x3 = x3 - h * (y2 - y1) / d;
                y3 = y3 + h * (x2 - x1) / d;

                /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (int) x3;
                params.topMargin = (int) y3;
                TextView textView = new TextView(context);
                textView.setText("(" + (int) x3 + "," + (int) y3 + ")");
                textView.setId(dv.idCounter);
                textView.setTextSize(7);
                textView.setLayoutParams(params);
                //makeToast("Making the text");
                rlDvHolder.addView(textView);
                dv.idCounter++;
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.leftMargin = (int) x4;
                params2.topMargin = (int) y4;
                TextView textView2 = new TextView(context);
                textView2.setText("(" + (int) x4 + "," + (int) y4 + ")");
                textView2.setId(dv.idCounter);
                textView2.setLayoutParams(params2);
                textView2.setTextSize(7);
                //makeToast("Making the text");
                rlDvHolder.addView(textView2);
                dv.idCounter++;*/

                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
                        //README Both intersection points are inside the land meaning that it is simply overlap wastage
                        double intersectionArea = 0;
                        Double r = r1;
                        Double R = r2;
                        double firstX = x1;
                        double secondX = x2;
                        double firstY = y1;
                        double secondY = y2;
                        int smallC = circle1;
                        int bigc = circle2;
                        if (R < r) {
                            // swap
                            r = r2;
                            R = r1;
                            smallC = circle2;
                            bigc = circle1;
                        }
                        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                        intersectionArea = part1 + part2 - part3;

                        //README Maybe intersectionArea is 0 because circle is inside other circle.
                        if (!(intersectionArea > 0)) {
                            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
                        }

                        overlap2 += intersectionArea;
                    } else {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
//README Intersection area is inside and outside
                        //INFO You need the circle-circle intersection that is inside.
                        //README Below is for the circle-circle intersection
                        double circleX = 0, circleY = 0;
                        if (out1) {
                            circleX = x4;
                            circleY = y4;
                        } else if (out2) {
                            circleX = x3;
                            circleY = y3;
                        }
                        //TODO Calculate circle-line intersection for both circles.
                        //INFO Below is for the circle-line intersections.
                        double ix1, ix2, iy1, iy2;
                        ix1 = outsideIntersecting.get(i).getX1();
                        iy1 = outsideIntersecting.get(i).getY1();
                        double dist = Math.sqrt(Math.pow(ix1 - outsideIntersecting.get(j).circleX, 2) +
                                Math.pow(iy1 - outsideIntersecting.get(j).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > outsideIntersecting.get(j).getRadius()) {
                            ix1 = outsideIntersecting.get(i).getX2();
                            iy1 = outsideIntersecting.get(i).getY2();
                        }

                        ix2 = outsideIntersecting.get(j).getX1();
                        iy2 = outsideIntersecting.get(j).getY1();
                        dist = Math.sqrt(Math.pow(ix2 - outsideIntersecting.get(i).circleX, 2) +
                                Math.pow(iy2 - outsideIntersecting.get(i).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > outsideIntersecting.get(i).getRadius()) {
                            ix2 = outsideIntersecting.get(j).getX2();
                            iy2 = outsideIntersecting.get(j).getY2();
                        }

                        double length1 = Math.sqrt(Math.pow(ix1 - ix2, 2) + Math.pow(iy1 - iy2, 2));
                        double length2 = Math.sqrt(Math.pow(ix2 - circleX, 2) + Math.pow(iy2 - circleY, 2));
                        double length3 = Math.sqrt(Math.pow(ix1 - circleX, 2) + Math.pow(iy1 - circleY, 2));
                        double s = (length1 + length2 + length3) / 2;

                        /*double l = ix1 * (iy2 - circleY);
                        double m = ix2 * (circleY - iy1);
                        double n = circleX * (iy1 - iy2);

                        double triangleArea = Math.abs((l + m + n) - 50) % (Math.PI * Math.max(r1, r2) * Math.max(r1, r2));*/
                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;
                        Log.wtf("*- Information: ", "Wastage - " + (int) overlap2 + "  Full - " +
                                overlap(r1, r2, x1, x2, y1, y2, circle1, circle2) + " ix1-" + (int) ix1 + " ix2-" + (int) ix2
                                + " iy1-" + (int) iy1 + " iy2-" + (int) iy2 + " c-cX:" + (int) circleX + " c-cY:" + (int) circleY
                                + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t1 circle- " + (int) (r1 * r1 * Math.PI) + "  X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " +
                                (int) x2 + " Y2: " + (int) y2 + " Arc1-" + (int) arc1 + " Arc2- " + (int) arc2 + " Triangle-" + (int) triangleArea);

                        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = (int) ix1 - 3;
                        params.topMargin = (int) iy1;
                        TextView textView = new TextView(context);
                        textView.setText("o");
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setId(dv.idCounter);
                        textView.setTextSize(7);
                        textView.setLayoutParams(params);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView);
                        dv.idCounter++;
                        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.leftMargin = (int) ix2 - 5;
                        params2.topMargin = (int) iy2;
                        TextView textView2 = new TextView(context);
                        textView2.setText("o");
                        textView2.setId(dv.idCounter);
                        textView2.setTextColor(Color.parseColor("#000000"));
                        textView2.setLayoutParams(params2);
                        textView2.setTextSize(7);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView2);
                        dv.idCounter++;
                        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params3.leftMargin = (int) circleX - 6;
                        params3.topMargin = (int) circleY;
                        TextView textView3 = new TextView(context);
                        textView3.setText("o");
                        textView3.setTextColor(Color.parseColor("#000000"));
                        textView3.setId(dv.idCounter);
                        textView3.setLayoutParams(params3);
                        textView3.setTextSize(7);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView3);
                        dv.idCounter++;*/
                    }

                    /* Log.wtf("*- Information: ", "X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " + (int) x2 + " Y2: " + (int) y2
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t Radius - " + (int) r1 + " X3: " + (int) x3 + " Y3: " + (int) y3 + " X4: " + (int) x4 + " Y4: " + (int) y4
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWastage - " + overlap2 +"  Total - " +
                            overlap(r1, r2, x1, x2, y1, y2, circle1, circle2));*/

                }
            }
        }
        overlapWastage += overlap2;
        Log.wtf("*- Results outsideIntersecting-outsideIntersecting:", (int) overlap2 + ".\n-");
    }

    private void insideIntersectingInsideIntersecting() {
        Log.wtf("*- Inside and Inside Intersecting", "_______________________________________");
        double overlap2 = 0;
        for (int i = 0; i < insideIntersecting.size() - 1; i++) {
            for (int j = i + 1; j < insideIntersecting.size(); j++) {
                int circle1 = insideIntersecting.get(i).getCirclePos();
                int circle2 = insideIntersecting.get(j).getCirclePos();

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

                double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                double h = Math.sqrt(r1 * r1 - a * a);
                double x3 = x1 + a * (x2 - x1) / d;
                double y3 = y1 + a * (y2 - y1) / d;
                double x4 = x3 + h * (y2 - y1) / d;     // also x3=x2-h*(y1-y0)/d
                double y4 = y3 - h * (x2 - x1) / d;
                x3 = x3 - h * (y2 - y1) / d;
                y3 = y3 + h * (x2 - x1) / d;

                /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (int) x3;
                params.topMargin = (int) y3;
                TextView textView = new TextView(context);
                textView.setText("(" + (int) x3 + "," + (int) y3 + ")");
                textView.setId(dv.idCounter);
                textView.setTextSize(7);
                textView.setLayoutParams(params);
                //makeToast("Making the text");
                rlDvHolder.addView(textView);
                dv.idCounter++;
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.leftMargin = (int) x4;
                params2.topMargin = (int) y4;
                TextView textView2 = new TextView(context);
                textView2.setText("(" + (int) x4 + "," + (int) y4 + ")");
                textView2.setId(dv.idCounter);
                textView2.setLayoutParams(params2);
                textView2.setTextSize(7);
                //makeToast("Making the text");
                rlDvHolder.addView(textView2);
                dv.idCounter++;*/

                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
                        //README Both intersection points are inside the land meaning that it is simply overlap wastage
                        double intersectionArea = 0;
                        Double r = r1;
                        Double R = r2;
                        double firstX = x1;
                        double secondX = x2;
                        double firstY = y1;
                        double secondY = y2;
                        int smallC = circle1;
                        int bigc = circle2;
                        if (R < r) {
                            // swap
                            r = r2;
                            R = r1;
                            smallC = circle2;
                            bigc = circle1;
                        }
                        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                        intersectionArea = part1 + part2 - part3;

                        //README Maybe intersectionArea is 0 because circle is inside other circle.
                        if (!(intersectionArea > 0)) {
                            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
                        }

                        overlap2 += intersectionArea;
                    } else {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
//README Intersection area is inside and outside
                        //INFO You need the circle-circle intersection that is inside.
                        //README Below is for the circle-circle intersection
                        double circleX = 0, circleY = 0;
                        if (out1) {
                            circleX = x4;
                            circleY = y4;
                        } else if (out2) {
                            circleX = x3;
                            circleY = y3;
                        }
                        //TODO Calculate circle-line intersection for both circles.
                        //INFO Below is for the circle-line intersections.
                        double ix1, ix2, iy1, iy2;
                        ix1 = insideIntersecting.get(i).getX1();
                        iy1 = insideIntersecting.get(i).getY1();
                        double dist = Math.sqrt(Math.pow(ix1 - insideIntersecting.get(j).circleX, 2) +
                                Math.pow(iy1 - insideIntersecting.get(j).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > insideIntersecting.get(j).getRadius()) {
                            ix1 = insideIntersecting.get(i).getX2();
                            iy1 = insideIntersecting.get(i).getY2();
                        }

                        ix2 = insideIntersecting.get(j).getX1();
                        iy2 = insideIntersecting.get(j).getY1();
                        dist = Math.sqrt(Math.pow(ix2 - insideIntersecting.get(i).circleX, 2) +
                                Math.pow(iy2 - insideIntersecting.get(i).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > insideIntersecting.get(i).getRadius()) {
                            ix2 = insideIntersecting.get(j).getX2();
                            iy2 = insideIntersecting.get(j).getY2();
                        }

                        double length1 = Math.sqrt(Math.pow(ix1 - ix2, 2) + Math.pow(iy1 - iy2, 2));
                        double length2 = Math.sqrt(Math.pow(ix2 - circleX, 2) + Math.pow(iy2 - circleY, 2));
                        double length3 = Math.sqrt(Math.pow(ix1 - circleX, 2) + Math.pow(iy1 - circleY, 2));
                        double s = (length1 + length2 + length3) / 2;

                        /*double l = ix1 * (iy2 - circleY);
                        double m = ix2 * (circleY - iy1);
                        double n = circleX * (iy1 - iy2);

                        double triangleArea = Math.abs((l + m + n) - 50) % (Math.PI * Math.max(r1, r2) * Math.max(r1, r2));*/
                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;
                        Log.wtf("*- Information: ", "Wastage - " + (int) overlap2 + "  Full - " +
                                overlap(r1, r2, x1, x2, y1, y2, circle1, circle2) + " ix1-" + (int) ix1 + " ix2-" + (int) ix2
                                + " iy1-" + (int) iy1 + " iy2-" + (int) iy2 + " c-cX:" + (int) circleX + " c-cY:" + (int) circleY
                                + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t1 circle- " + (int) (r1 * r1 * Math.PI) + "  X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " +
                                (int) x2 + " Y2: " + (int) y2 + " Arc1-" + (int) arc1 + " Arc2- " + (int) arc2 + " Triangle-" + (int) triangleArea);

                        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = (int) ix1 - 3;
                        params.topMargin = (int) iy1;
                        TextView textView = new TextView(context);
                        textView.setText("o");
                        textView.setId(dv.idCounter);
                        textView.setTextSize(7);
                        textView.setLayoutParams(params);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView);
                        dv.idCounter++;
                        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.leftMargin = (int) ix2 - 5;
                        params2.topMargin = (int) iy2;
                        TextView textView2 = new TextView(context);
                        textView2.setText("o");
                        textView2.setId(dv.idCounter);
                        textView2.setLayoutParams(params2);
                        textView2.setTextSize(7);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView2);
                        dv.idCounter++;
                        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params3.leftMargin = (int) circleX - 6;
                        params3.topMargin = (int) circleY;
                        TextView textView3 = new TextView(context);
                        textView3.setText("o");
                        textView3.setId(dv.idCounter);
                        textView3.setLayoutParams(params3);
                        textView3.setTextSize(7);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView3);
                        dv.idCounter++;*/
                    }

                    /* Log.wtf("*- Information: ", "X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " + (int) x2 + " Y2: " + (int) y2
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t Radius - " + (int) r1 + " X3: " + (int) x3 + " Y3: " + (int) y3 + " X4: " + (int) x4 + " Y4: " + (int) y4
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWastage - " + overlap2 +"  Total - " +
                            overlap(r1, r2, x1, x2, y1, y2, circle1, circle2));*/

                }
            }
        }
        overlapWastage += overlap2;
        Log.wtf("*- Results insideIntersecting-insideIntersecting:", (int) overlap2 + ".\n-");
    }

    public int overlap(double r1, double r2, double x1, double x2, double y1, double y2, int circle1, int circle2) {
        double intersectionArea = 0;
        Double r = r1;
        Double R = r2;
        double firstX = x1;
        double secondX = x2;
        double firstY = y1;
        double secondY = y2;
        int smallC = circle1;
        int bigc = circle2;
        Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
        if (R < r) {
            // swap
            r = r2;
            R = r1;
            smallC = circle2;
            bigc = circle1;
        }
        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

        intersectionArea = part1 + part2 - part3;

        //README Maybe intersectionArea is 0 because circle is inside other circle.
        if (!(intersectionArea > 0)) {
            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
        }
        return (int) intersectionArea;
    }

    private double arcArea(double r, double x1, double y1, double x2, double y2, double centerX, double centerY) {
        double a = x1 * (y2 - centerY);
        double b = x2 * (centerY - y1);
        double c = centerX * (y1 - y2);
        double totalArea = Math.PI * r * r;

        double length1 = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double length2 = Math.sqrt(Math.pow(x2 - centerX, 2) + Math.pow(y2 - centerY, 2));
        double length3 = Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));
        double s = (length1 + length2 + length3) / 2;
        double area = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));

        //INFO Added the mod totalArea because sometimes, triangleArea gets greater than totalArea.
        //double triangleArea = Math.abs((a + b + c) - 50) % totalArea;

        double triangleArea = area;

        double angle = Math.abs(Math.toDegrees(Math.atan2(x1 - centerX, y1 - centerY) -
                Math.atan2(x2 - centerX, y2 - centerY)));
        double v1x = x1 - centerX;
        double v1y = y1 - centerY;

        //need to normalize:
        double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
        v1x /= l1;
        v1y /= l1;

        double v2x = x2 - centerX;
        double v2y = y2 - centerY;

        //need to normalize:
        double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
        v2x /= l2;
        v2y /= l2;
        double rad = Math.acos(v1x * v2x + v1y * v2y);
        //INFO Changed method of calculating angle.
        double degrees = Math.toDegrees(rad);

        double sectorArea = Math.PI * Math.pow(r, 2) * (degrees / 360d);

        double arcArea = Math.abs(sectorArea - triangleArea);
        return arcArea;
    }

    private void completelyInsideOverlapOutside() {
        Log.wtf("*- Completely Inside - Outside Intersect", " ");
        double overlap2 = 0;
        double land2 = 0;
        for (int i = 0; i < completelyInside.size(); i++) {
            for (int j = 0; j < outsideIntersecting.size(); j++) {
                int circle1 = completelyInside.get(i);
                int circle2 = outsideIntersecting.get(j).getCirclePos();

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                //README The circles overlap
                if (distance <= r1 + r2) {
                    overlaps.add(circle2);
                    overlaps.add(circle1);
                    //TODO Do calculations for overlap of completely inside circle and outside-intersecting circle
                    if (distance + Math.min(r1, r2) <= Math.max(r1, r2) * 0.98) {
                        double intersectionArea = Math.PI * Math.pow(Math.min(r1, r2), 2) * (360 - (360 - dv.angleList.get((Math.min(r1, r2) == r1 ? circle1 : circle2)) / 2)) / 360;
                        double totalArea = 0;
                        if (completelyInside.contains(circle1))
                            totalArea += (Math.PI * Math.pow(r1, 2)) * ((double) (angle1) / 360);
                        if (completelyInside.contains(circle2))
                            totalArea += (Math.PI * Math.pow(r2, 2)) * ((double) (angle2) / 360);
                        overlap2 += intersectionArea;
                        Log.wtf("*- INFORMATION (" + circle1 + "," + circle2 + ")", "Circle In Circle: " + intersectionArea + "  Total Area: " + totalArea);
                    } else {//README Not circle in circle
                        double intersectionArea = 0;
                        Double r = r1;
                        Double R = r2;
                        double firstX = x1;
                        double secondX = x2;
                        double firstY = y1;
                        double secondY = y2;
                        int smallC = circle1;
                        int bigc = circle2;
                        Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
                        if (R < r) {
                            // swap
                            r = r2;
                            R = r1;
                            smallC = circle2;
                            bigc = circle1;
                        }
                        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                        intersectionArea = part1 + part2 - part3;

                        //README Maybe intersectionArea is 0 because circle is inside other circle.
                        if (!(intersectionArea > 0)) {
                            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
                        }
                        overlappingCount++;
                        overlap2 += intersectionArea;
                        //intersectionArea = Math.PI * Math.pow(r, 2) * 0.015f;
                        Log.wtf("*- INFORMATION (" + circle1 + "," + circle2 + ")", "Intersection Area: " + intersectionArea);
                    }
                }
            }
        }
        overlapWastage += overlap2;
        Log.wtf("*- Results completelyInside-outsideIntersect: ", (int) overlap2 + "\n-");
    }

    //DONE Test below function
    private void insideCircleOverlap() {
        Log.wtf("*- INSIDE CIRCLE WASTAGE", " ");
        double overlap2 = 0;
        double land2 = 0;
        //IMPORTANT Ideally below should loop for only completelyInsideCircles and inisideCircles
        // Check calling function for more info
        for (int i = 0; i < insideCircles.size() - 1; i++) {
            for (int j = i + 1; j < insideCircles.size(); j++) {
                //README insideCirles contains the positions
                int circle1 = insideCircles.get(i);
                int circle2 = insideCircles.get(j);

                //INFO if both circles are insideIntersecting, don't calculate because we have another function for that.
                if (insideIntersectingContains(circle1, circle2)) {
                } else {
                    double x1 = dv.sprinkx.get(circle1);
                    double x2 = dv.sprinkx.get(circle2);
                    double y1 = dv.sprinky.get(circle1);
                    double y2 = dv.sprinky.get(circle2);

                    double r1 = dv.sprinkr.get(circle1);
                    double r2 = dv.sprinkr.get(circle2);
                    double angle1 = dv.angleList.get(circle1);
                    double angle2 = dv.angleList.get(circle2);

                    double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                    //README The circles overlap
                    if (distance <= r1 + r2) {
                        //README Circle in circle
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        if (distance + Math.min(r1, r2) <= Math.max(r1, r2) * 1.08) {
                            double intersectionArea = Math.PI * Math.pow(Math.min(r1, r2), 2) * dv.angleList.get((Math.min(r1, r2) == r1 ? circle1 : circle2)) / 360;
                            double totalArea = 0;
                            if (completelyInside.contains(circle1))
                                totalArea += (Math.PI * Math.pow(r1, 2)) * ((double) (angle1) / 360);
                            if (completelyInside.contains(circle2))
                                totalArea += (Math.PI * Math.pow(r2, 2)) * ((double) (angle2) / 360);
                            overlap2 += intersectionArea;
                            Log.wtf("*- INFORMATION (" + circle1 + "," + circle2 + ")", "Circle In Circle: " + intersectionArea + "  Total Area: " + totalArea);
                        } else {//README Not circle in circle
                            double intersectionArea = 0;
                            Double r = r1;
                            Double R = r2;
                            double firstX = x1;
                            double secondX = x2;
                            double firstY = y1;
                            double secondY = y2;
                            int smallC = circle1;
                            int bigc = circle2;
                            Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
                            if (R < r) {
                                // swap
                                r = r2;
                                R = r1;
                                smallC = circle2;
                                bigc = circle1;
                            }
                            Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                            Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                            Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                            intersectionArea = part1 + part2 - part3;

                            //README Maybe intersectionArea is 0 because circle is inside other circle.
                            if (!(intersectionArea > 0)) {
                                intersectionArea = Math.PI * Math.pow(r, 2) * dv.angleList.get(smallC) / 360;
                            }

                            //IMPORTANT Total Area is already calculated for overflow circles.
                            //DONE When iterating through inside circle, only if it not insideIntersecting, calculate its area.
                            double totalArea = 0;
                            if (completelyInside.contains(circle1))
                                totalArea += (Math.PI * Math.pow(r1, 2)) * ((double) (angle1) / 360);
                            if (completelyInside.contains(circle2))
                                totalArea += (Math.PI * Math.pow(r2, 2)) * ((double) (angle2) / 360);

                            overlap2 += intersectionArea;
                            overlappingCount++;
                            //intersectionArea = Math.PI * Math.pow(r, 2) * 0.015f;
                            Log.wtf("*- INFORMATION (" + circle1 + "," + circle2 + ")", "Intersection Area: " + intersectionArea + "  Total Area: " + totalArea);
                        }
                    }
                }
            }
        }

        double totalArea = 0;
        //README Calculating completely inside circle area outside the loop so no duplicates.
        for (int i : completelyInside)
            totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2) * dv.angleList.get(i) / 360;

        overlapWastage += overlap2;
        totalInsideArea += totalArea;
        Log.wtf("*- Results insideCircleOverlap: ", (int) overlap2 + " " + (int) totalArea + "\n-");
    }

    private boolean insideIntersectingContains(int circle1, int circle2) {
        boolean c1 = false;
        boolean c2 = false;
        for (OverflowInfo info : insideIntersecting) {
            if (info.getCirclePos() == circle1)
                c1 = true;
            if (info.getCirclePos() == circle2)
                c2 = true;
        }
        if (c1 && c2)
            return true;
        return false;
    }

    //README Calculate areas of individual circles.
    private void individualCircleArea() {
        for (int i = 0; i < singleR.size(); i++)
            totalInsideArea += singleR.get(i) * singleR.get(i) * Math.PI * dv.angleList.get(singleP.get(i)) / 360d;
    }

    //INFO IMPORTANT overflowWastage SHOULD NOT be added for land area covered
    //NOTES
    // Things that I am calculating:
    // 1. 0 intersections:: Completely outside sprinklers - total area  (keep in mind that for list (have to remove element if it intersects with line)
    // 2. 1 intersection::: 30% wastage
    // 3. 2 intersecitons:: If overflowing sprinkler is outside
    //    Different overflowWastage if circle is outside.

    //NOTES
    //
    //TODO Check if set works with completely outside sprinklers. Have to make sure to add it only once and remove only if added and intersecting.
    //TODO Check if overFlowWastage for Completely Outside sprinklers works.
    //DONE Check if outside function is able to determine outside sprinklers.
    //TODO If outside, do additional calculation to subtract sector area (without triangle) from total circle
    //TODO Check if angles and everything is working.

    public void calculateOverflowWastage() {
        Log.wtf("*- - - - - --- --- --- - - --  -- ", "----------_-------------_------------_");
        int counteR = 0;
        ArrayList<Integer> used = new ArrayList<>();
        ArrayList<Integer> fullCircleUsed = new ArrayList<>();
        double previousWastage = 0;
        double initialWastage = overFlowWastage;
        for (OverflowInfo overflowInfo : overflowInfo) {
            if (!used.contains(overflowInfo.getCirclePos())) {
                double x1 = overflowInfo.getX1();
                double x2 = overflowInfo.getX2();
                double y1 = overflowInfo.getY1();
                double y2 = overflowInfo.getY2();

                double centerX = overflowInfo.getCircleX();
                double centerY = overflowInfo.getCircleY();

                double a = x1 * (y2 - centerY);
                double b = x2 * (centerY - y1);
                double c = centerX * (y1 - y2);
                double totalArea = Math.PI * overflowInfo.getRadius() * overflowInfo.getRadius();

                //INFO Added the mod totalArea because sometimes, triangleArea gets greater than totalArea.
                //double triangleArea = Math.abs((a + b + c) - 50) % totalArea;

                double length1 = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double length2 = Math.sqrt(Math.pow(x2 - centerX, 2) + Math.pow(y2 - centerY, 2));
                double length3 = Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));
                double s = (length1 + length2 + length3) / 2;
                double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));

                double angle = Math.abs(Math.toDegrees(Math.atan2(x1 - centerX, y1 - centerY) -
                        Math.atan2(x2 - centerX, y2 - centerY)));
                double v1x = x1 - centerX;
                double v1y = y1 - centerY;

                //need to normalize:
                double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
                v1x /= l1;
                v1y /= l1;

                double v2x = x2 - centerX;
                double v2y = y2 - centerY;

                //need to normalize:
                double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
                v2x /= l2;
                v2y /= l2;
                double rad = Math.acos(v1x * v2x + v1y * v2y);
                //INFO Changed method of calculating angle.
                double degrees = Math.toDegrees(rad);

                double sectorArea = Math.PI * Math.pow(overflowInfo.getRadius(), 2) * (degrees / 360d);

                boolean outside = outside(centerX, centerY);

                double wastedArea = sectorArea - triangleArea;
                //DONE Check if below works
                if (wastedArea < 0) wastedArea = Math.abs(wastedArea) / 1.95;

                if (outside)
                    wastedArea = (totalArea) - wastedArea;
                if (dv.angleList.get(overflowInfo.getCirclePos()) < 316) {
                    double sprinklerAngle = dv.angleList.get(overflowInfo.getCirclePos());
                    totalArea *= sprinklerAngle / 360;
                    wastedArea = totalArea * 0.05;
                    used.add(overflowInfo.getCirclePos());
                }

                /*Log.wtf("*- - Overflow Info", "Sprinkler " + (overflowInfo.getCirclePos()) + ((outside) ? " is outside" : " is inside")
                        + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tThe angle is - " + (int) degrees + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tTriangle Area - "
                        + triangleArea + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tSector Area - " + sectorArea +
                        "\n\t\t\t\t\t\t\t\t\t\t\t\tTotal Area: " + totalArea + "\n\t\t\t\t\t\t\t\t\t\t\t    Final Area - " + wastedArea);*/

                if (ignoreSprinklers < 0) ignoreSprinklers = 0;

                previousWastage = overFlowWastage - initialWastage;
                overFlowWastage += wastedArea;

                //INFO `ignoreSprinklers` is number of auto-plotted sprinklers
                // Purpose is to avoid calculating overflow for auto plotted sprinklers unless land is rectangle
                if (overflowInfo.getCirclePos() < ignoreSprinklers)
                    overFlowWastage -= wastedArea;

                if (fullCircleUsed.contains(overflowInfo.getCirclePos())) {
                    //double latestGain = overFlowWastage - initialWastage - previousWastage;
                    double latestGain = wastedArea;
                    //README If the latest addition was larger, then subtract 80% of previous smaller one.
                    double toAdd = previousWastage * .24 + wastedArea * .21;

                    //INFO
                    // Purpose is to avoid calculating overflow for auto plotted sprinklers unless land is rectangle
                    if (overflowInfo.getCirclePos() >= ignoreSprinklers) {
                        overFlowWastage += toAdd;
                        if (overFlowWastage > totalOverflowArea)
                            overFlowWastage -= toAdd * .9;
                    }
                    //TODO Fix problem that overflowwastage can go above totalOverflowArea
                    //if(overFlowWastage )
                    if (latestGain >= previousWastage) {
                        //overFlowWastage -= previousWastage * .59;
                    } else { //README The previous area was larger.
                        //overFlowWastage -= wastedArea * .55;
                    }
                }

                if (!fullCircleUsed.contains(overflowInfo.getCirclePos())) {
                    //totalOverflowArea += totalArea;
                    Log.wtf("*- Total Overflow Area: ", "ADDING: " + totalArea + " = " + totalOverflowArea);
                }

                Log.wtf("*- - Overflow Info", "Sprinkler " + (overflowInfo.getCirclePos()) + ((outside) ? " is outside" : " is inside")
                        + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tThe angle is - " + (int) degrees + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tTriangle Area - "
                        + triangleArea + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tSector Area - " + sectorArea +
                        "\n\t\t\t\t\t\t\t\t\t\t\t\tTotal Area: " + totalArea + "\n\t\t\t\t\t\t\t\t\t\t\t    Final Area - " + wastedArea
                        + "\n\t\t\t\t\t\t\t Previous Wastage: " + previousWastage + "\n\t\t\t\t\t\t\t Overflow Wastage: "
                        + overFlowWastage + "\n\t\t\t\t\t\t\t Total Overflow Area: " + totalOverflowArea + "\n-----");
                //makeToast("Angle is: "  + angle + " Triangle Area: " + triangleArea);
                //outsideResults(counteR, centerX, centerY);
                if (dv.angleList.get(overflowInfo.getCirclePos()) > 316) {
                    fullCircleUsed.add(overflowInfo.getCirclePos());
                    //Log.wtf("*- Full circle used", "It was added");
                }
                counteR++;
                //Log.wtf("*-", ",\n");
            }
        }
        Log.wtf("*- Overflow Results: ", "Overflow Wastage: " + (int) overFlowWastage + " Total Area: " + (int) totalOverflowArea + "\n-");
    }

    private boolean outsideResults(int counteR, double centerX, double centerY) {
        int counter = 0;
        for (int b = 0; b < dv.xlist.size(); b++) {
            double startX = dv.xlist.get(b);
            double startY = dv.ylist.get(b);
            double endX = dv.xlist.get((b + 1 > dv.xlist.size() - 1 ? 0 : b + 1));
            double endY = dv.ylist.get((b + 1 > dv.ylist.size() - 1 ? 0 : b + 1));
            if (centerY >= Math.min(startY, endY) && centerY <= Math.max(startY, endY)) {
                double inverseSlope = (startX - endX) / (startY - endY);
                double expectedX = (-1 * inverseSlope * (startY - centerY)) + startX;
                Log.wtf("*- Sprink#- " + counteR + " Line#- " + b + " Outside Results - ", ",\n\t\t\t\t\t\t\t\t\t\tStart X: " + startX + " Start Y: " + startY
                        + "\n\t\t\t\t\t\t\t\t\t\tEnd X: " + endX + " End Y: " + endY + "\n\t\t\t\t\t\t\t\t\t\tCenter X: " + centerX + " Center Y: " + centerY
                        + "\n\t\t\t\t\t\t\t\t\t\tInverse Slope - " + (double) ((int) inverseSlope * 100) / 100d + " Expected X - " + (int) expectedX + " Start-Center: " + (startY - centerY));
                if (centerX < expectedX) {
                    counter++;
                }
            }
        }
        return counter % 2 == 0;

    }

    //INFO Possible problem could be with formula below. (inverseSlope * otherstuff). Or could be (startY -centerY) is opposite.
    //README Function determines if sprinkler center is outside, not full sprinkler.
    public static boolean outside(double centerX, double centerY) {
        int counter = 0;
        for (int b = 0; b < dv.xlist.size(); b++) {
            double startX = dv.xlist.get(b);
            double startY = dv.ylist.get(b);
            double endX = dv.xlist.get((b + 1 > dv.xlist.size() - 1 ? 0 : b + 1));
            double endY = dv.ylist.get((b + 1 > dv.ylist.size() - 1 ? 0 : b + 1));
            if (centerY >= Math.min(startY, endY) && centerY <= Math.max(startY, endY)) {
                double inverseSlope = (startX - endX) / (startY - endY);
                double expectedX = (-1 * inverseSlope * (startY - centerY)) + startX;
                if (centerX < expectedX) {
                    counter++;
                }
            }
        }
        return counter % 2 == 0;
    }


    HashMap<String, Integer> hm;

    ArrayList<Double> singleX = new ArrayList<>();
    ArrayList<Double> singleY = new ArrayList<>();
    ArrayList<Double> singleR = new ArrayList<>();
    ArrayList<Integer> singleP = new ArrayList<>();
    ArrayList<Integer> singleAngle = new ArrayList<>();

    //TODO Check if below function works
    private void getIndividualCircles2() {
        singleX.clear();
        singleY.clear();
        singleR.clear();
        singleP.clear();
        //Log.wtf("*Still getting circles", " Still GETTING CIRCLES");

        if (dv.sprinkx.size() == 1) {
            singleX.add((double) dv.sprinkx.get(0));
            singleY.add((double) dv.sprinky.get(0));
            singleR.add((double) dv.sprinkr.get(0));
            singleAngle.add(dv.angleList.get(0));
            singleP.add(0);
        } else {
            for (int a = 0; a < candidates.size(); a++) {
                int i = candidates.get(a);
                double x = dv.sprinkx.get(i);
                double y = dv.sprinky.get(i);
                double r = dv.sprinkr.get(i);

                boolean good = true;
                for (int j = 0; j < candidates.size(); j++) {
                    double tempX = dv.sprinkx.get(j);
                    double tempY = dv.sprinky.get(j);
                    double tempR = dv.sprinkr.get(j);

                    if (j != i) {
                        double distance = Math.sqrt(Math.pow(tempX - x, 2) + Math.pow(tempY - y, 2));
                        if (distance <= tempR + r) {
                            good = false;
                            break;
                        }
                    }
                }

                if (good) {
                    singleX.add(x);
                    singleY.add(y);
                    singleR.add(r);
                    singleP.add(i);
                    singleAngle.add(dv.angleList.get(i));
                }

            }
        }
    }

    private void getIndividualCircles() {
        singleX.clear();
        singleY.clear();
        singleR.clear();
        singleP.clear();
        //Log.wtf("*Still getting circles", " Still GETTING CIRCLES");

        if (dv.sprinkx.size() == 1) {
            singleX.add((double) dv.sprinkx.get(0));
            singleY.add((double) dv.sprinky.get(0));
            singleR.add((double) dv.sprinkr.get(0));
            singleAngle.add(dv.angleList.get(0));
            singleP.add(0);
        } else {
            for (int i = 0; i < dv.sprinkx.size(); i++) {
                double x = dv.sprinkx.get(i);
                double y = dv.sprinky.get(i);
                double r = dv.sprinkr.get(i);

                boolean good = true;
                for (int j = 0; j < dv.sprinkx.size(); j++) {
                    double tempX = dv.sprinkx.get(j);
                    double tempY = dv.sprinky.get(j);
                    double tempR = dv.sprinkr.get(j);

                    if (j != i) {
                        double distance = Math.sqrt(Math.pow(tempX - x, 2) + Math.pow(tempY - y, 2));
                        if (distance <= tempR + r) {
                            good = false;
                            break;
                        }
                    }
                }

                if (good) {
                    singleX.add(x);
                    singleY.add(y);
                    singleR.add(r);
                    singleP.add(i);
                    singleAngle.add(dv.angleList.get(i));
                }

            }
        }
    }


    private String convertTo16(int a) {
        String t = Integer.toString(
                Integer.parseInt(a + "", 10),
                16);
        if (t.length() == 1)
            return "0" + t;
        return t;
    }

    private int[] calculateWastage(boolean two) {
        double firstX = 0, firstY = 0, firstR = 0, secondX = 0, secondY = 0, secondR = 0;
        int firstAngle = 360, secondAngle = 360;
        boolean firstUsed = false;
        double totalArea = 0;
        for (int i = 0; i < dv.sprinkx.size(); i++) {
            if (!singleX.contains(dv.sprinkx.get(i))) {
                totalArea = Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                if (!firstUsed) {
                    firstX = dv.sprinkx.get(i);
                    firstR = dv.sprinkr.get(i);
                    firstY = dv.sprinky.get(i);
                    firstAngle = dv.angleList.get(i);
                    firstUsed = true;
                    totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                } else {
                    secondX = dv.sprinkx.get(i);
                    secondR = dv.sprinkr.get(i);
                    secondY = dv.sprinky.get(i);
                    secondAngle = dv.angleList.get(i);
                    totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                    if (two) {
                        firstUsed = false;
                        break;
                    } else {
                        if (!firstUsed) {
                            firstUsed = true;
                        } else {
                            totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                            break;
                        }
                    }
                }
            }
        }
        //NOTES return {intersection, area of all circles combined}
        double intersectionArea = 0;
        //DONE Calculate total sprinkler coverage area.
        //TODO DO calculation to calculate intersection area.

        Double r = firstR;
        Double R = secondR;
        Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
        if (R < r) {
            // swap
            r = secondR;
            R = firstR;
        }
        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

        intersectionArea = part1 + part2 - part3;
        Log.wtf("***SPRINKLER LIST: ", r + " " + R + " " + d + " " + part1 + " " + part2 + " " + part3);
        if (!(intersectionArea > 0)) {
            intersectionArea = Math.PI * Math.pow(Math.min(secondR, firstR), 2);
        }

        if (two) {
            //README Just added something to account for different angle sprinklers
            totalArea = (Math.PI * Math.pow(secondR, 2)) * ((double) (secondAngle) / 360) + (Math.PI * Math.pow(firstR, 2)) * ((double) (firstAngle) / 360);
        }

        if (firstAngle != 360 || secondAngle != 360) {

        }


        //intersectionArea = Math.PI * Math.pow(r, 2) * 0.015f;
        Log.wtf("** INFORMATION: ", "Intersection Area: " + intersectionArea + "  Total Area: " + totalArea);

        return new int[]{(int) intersectionArea, (int) totalArea};

    }

    static Toast shorter;
    static Toast longer;

    private static void shortToast(String s) {
        //README I am providing a static context since makeToast is static in order to call it from
        // ask for length which is static because it has to be called in onDraw();
        // FIXME-- If you get any errors with making Toast, try making it unstatic.
        if (shorter != null) shorter.cancel();
        shorter = Toast.makeText(context, s, Toast.LENGTH_SHORT);

        /*shorter = new Toast(context);
        //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        shorter.setText(s);
        shorter.setDuration(Toast.LENGTH_SHORT);*/
        shorter.show();
    }

    private static void makeToast(String s) {
        //README I am providing a static context since makeToast is static in order to call it from
        // ask for length which is static because it has to be called in onDraw();
        // FIXME-- If you get any errors with making Toast, try making it unstatic.
        //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        if (longer != null) longer.cancel();
        longer = Toast.makeText(context, s, Toast.LENGTH_LONG);
        /*longer = new Toast(context);
        longer.setText(s);
        longer.setDuration(Toast.LENGTH_LONG);*/
        longer.show();
    }

}