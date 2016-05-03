package com.example.l1va.credittest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.example.l1va.credittest.utils.BitmapUtils;

public class ActivityImage extends Activity {

    private ImageView imageView;

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.f;
    private float lastTouchX;
    private float lastTouchY;
    private int activePointerId;
    private float totalDiffX;
    private float totalDiffY;
    private static final int INVALID_POINTER_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.imageView);

        Bundle bundle = getIntent().getExtras();
        Bitmap bitmap = BitmapUtils.getBitmap(getResources(), bundle.getInt(getString(R.string.thumbnail_id_key)));

        imageView.setImageBitmap(bitmap);
        scaleDetector = new ScaleGestureDetector(this, new ScaleListener());

        imageView.setOnTouchListener(new View.OnTouchListener() {
            boolean secondTouch = false;
            boolean scaledTwice = false;
            long time = System.currentTimeMillis();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (secondTouch && (System.currentTimeMillis() - time) <= 300) {
                        secondTouch = false;
                        if (scaledTwice) {
                            scaledTwice = false;
                            imageView.setScaleX(1);
                            imageView.setScaleY(1);
                        } else {
                            scaledTwice = true;
                            imageView.setScaleX(2);
                            imageView.setScaleY(2);
                        }
                    } else {
                        secondTouch = true;
                        time = System.currentTimeMillis();
                        return false;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);

        if (ev.getPointerCount() > 1) {
            return true;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                lastTouchX = MotionEventCompat.getX(ev, pointerIndex);
                lastTouchY = MotionEventCompat.getY(ev, pointerIndex);
                activePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x, y;
                try {
                    x = MotionEventCompat.getX(ev, pointerIndex);
                    y = MotionEventCompat.getY(ev, pointerIndex);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Pointer index out of range: " + ex);
                    return true;
                }
                totalDiffX += x - lastTouchX;
                totalDiffY += y - lastTouchY;
                imageView.setTranslationX(totalDiffX);
                imageView.setTranslationY(totalDiffY);
                lastTouchX = x;
                lastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                activePointerId = INVALID_POINTER_ID;
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int[] loc = new int[2];
                imageView.getLocationOnScreen(loc);

                float scaleX = imageView.getScaleX();
                float scaleY = imageView.getScaleY();
                int viewW = imageView.getWidth();
                int viewH = imageView.getHeight();
                int imageW = imageView.getDrawable().getIntrinsicWidth();
                int imageH = imageView.getDrawable().getIntrinsicHeight();


                double fitScale = Math.max(1.0, Math.max(imageH / (1.0 * viewH), imageW / (1.0 * viewW)));

                double x0 = loc[0] + viewW * scaleX / 2 - imageW * scaleX / 2 / fitScale;
                double x1 = loc[0] + viewW * scaleX / 2 + imageW * scaleX / 2 / fitScale;
                double y0 = loc[1] + viewH * scaleY / 2 - imageH * scaleY / 2 / fitScale;
                double y1 = loc[1] + viewH * scaleY / 2 + imageH * scaleY / 2 / fitScale;

                boolean isInside = x >= x0 && x < x1 && y >= y0 && y < y1;
                if (!isInside) {
                    onBackPressed();
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                activePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == activePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    lastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }
    }
}
