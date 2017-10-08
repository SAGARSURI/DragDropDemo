package com.sagar.dragdropdemo;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by sagarsuri on 02/10/17.
 */

public class PuzzleActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_layout);

        findViewById(R.id.a0).setOnTouchListener(new MyTouchListener());
        findViewById(R.id.b0).setOnTouchListener(new MyTouchListener());
        findViewById(R.id.c0).setOnTouchListener(new MyTouchListener());
        findViewById(R.id.d0).setOnTouchListener(new MyTouchListener());
        findViewById(R.id.e0).setOnTouchListener(new MyTouchListener());
        GridLayout layout = (GridLayout) findViewById(R.id.gridLayout);
        layout.setRowCount(4);
        layout.setColumnCount(4);
        for (int i = 0; i < 4; i++) {
            GridLayout.Spec rowSpec = GridLayout.spec(i, 1, GridLayout.FILL, 1f);
            for (int j = 0; j < 4; j++) {
                GridLayout.Spec colSpec = GridLayout.spec(j, 1, GridLayout.FILL, 1f);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setId(R.id.row + i + R.id.col + j);
                linearLayout.setGravity(Gravity.FILL_HORIZONTAL);
                linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_background));
                linearLayout.setOnDragListener(new MyDragListener());
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                linearLayout.addView(imageView);
                GridLayout.LayoutParams myGLP = new GridLayout.LayoutParams();
                myGLP.rowSpec = rowSpec;
                myGLP.columnSpec = colSpec;
                layout.addView(linearLayout, myGLP);
            }
        }
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0);
                } else {
                    view.startDrag(data, shadowBuilder, view, 0);
                }
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {

        private View.OnClickListener myListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Image name", view.getContentDescription() + "");
                ImageView newImage = (ImageView) view;
                newImage.setImageBitmap(rotateBitmap(((BitmapDrawable) newImage.getDrawable()).getBitmap(), 90));
                //view.setRotation(view.getRotation()+90);
            }
        };

        private Bitmap rotateBitmap(Bitmap bitmap, int rotationAngleDegree) {

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int newW = w, newH = h;
            if (rotationAngleDegree == 90 || rotationAngleDegree == 270) {
                newW = h;
                newH = w;
            }
            Bitmap rotatedBitmap = Bitmap.createBitmap(newW, newH, bitmap.getConfig());
            Canvas canvas = new Canvas(rotatedBitmap);

            Rect rect = new Rect(0, 0, newW, newH);
            Matrix matrix = new Matrix();
            float px = rect.exactCenterX();
            float py = rect.exactCenterY();
            matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
            matrix.postRotate(rotationAngleDegree);
            matrix.postTranslate(px, py);
            canvas.drawBitmap(bitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();

            return rotatedBitmap;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    /**
                     * Change background of the layout where item is entering
                     */
                    v.setBackgroundColor(Color.parseColor("#ECECEC"));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    /**
                     * Change background of the layout back to normal once item is moved out of it
                     */
                    v.setBackground(ContextCompat.getDrawable(PuzzleActivity.this, R.drawable.layout_background));
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    LinearLayout container = (LinearLayout) v;

                    // Added the following to copy the old view's bitmap to a new ImageView:
                    ImageView oldView = (ImageView) view;
                    ImageView newView = (ImageView) container.getChildAt(0);
                    newView.setId(oldView.getId());
                    newView.setContentDescription(oldView.getContentDescription());
                    newView.setOnClickListener(myListener);
                    newView.setImageBitmap(((BitmapDrawable) oldView.getDrawable()).getBitmap());

                    view.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    View currentView = (View) event.getLocalState();
                    currentView.setVisibility(View.VISIBLE);
                    v.setBackground(ContextCompat.getDrawable(PuzzleActivity.this, R.drawable.layout_background));
                default:
                    break;
            }
            return true;
        }
    }
}
