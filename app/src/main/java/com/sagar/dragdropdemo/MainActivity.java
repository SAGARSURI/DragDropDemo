package com.sagar.dragdropdemo;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this,PuzzleActivity.class));
        findViewById(R.id.myimage1).setOnTouchListener(new MyTouchListener());
        findViewById(R.id.topleft).setOnDragListener(new MyDragListener());
        findViewById(R.id.topright).setOnDragListener(new MyDragListener());
        findViewById(R.id.bottomleft).setOnDragListener(new MyDragListener());
        findViewById(R.id.bottomright).setOnDragListener(new MyDragListener());
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {

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
                    v.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.layout_background));
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    //ViewGroup owner = (ViewGroup) view.getParent(); // Removed
                    //owner.removeView(view);                         // Removed
                    LinearLayout container = (LinearLayout) v;

                    if(container.getChildCount()>0){
                        container.removeAllViews();
                    }

                    // Added the following to copy the old view's bitmap to a new ImageView:
                    ImageView oldView = (ImageView) view;
                    ImageView newView = new ImageView(getApplicationContext());
                    newView.setImageBitmap(((BitmapDrawable) oldView.getDrawable()).getBitmap());

                    container.addView(newView);                       // Changed to add new view instead
                    view.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.layout_background));
                default:
                    break;
            }
            return true;
        }
    }
}
