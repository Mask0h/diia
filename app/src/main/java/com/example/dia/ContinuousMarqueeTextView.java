package com.example.dia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class ContinuousMarqueeTextView extends View {

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String text = "Документ оновлено о 16:52 | 01.09.2026 • ";
    private float textWidth = 0;
    private float offsetX = 0;
    private float speed = 1.5f;
    private boolean isRunning = true;

    public ContinuousMarqueeTextView(Context context) {
        super(context);
        init(context);
    }

    public ContinuousMarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ContinuousMarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        textPaint.setColor(0xFF0F2D17);
        float density = context.getResources().getDisplayMetrics().density;
        textPaint.setTextSize(11.5f * density);

        try {
            Typeface tf = ResourcesCompat.getFont(context, R.font.e_ukraine_medium);
            if (tf != null) {
                textPaint.setTypeface(tf);
            }
        } catch (Exception ignored) {
        }

        speed = 0.9f * density;
        updateTextWidth();
    }

    public void setText(String newText) {
        if (newText == null) newText = "";
        if (!newText.endsWith(" • ")) {
            newText = newText + " • ";
        }
        this.text = newText;
        updateTextWidth();
        invalidate();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        invalidate();
    }

    public void setSpeed(float speedDp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        this.speed = speedDp * density;
    }

    private void updateTextWidth() {
        if (text != null && !text.isEmpty()) {
            textWidth = textPaint.measureText(text);
        } else {
            textWidth = 0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text == null || text.isEmpty() || textWidth <= 0) return;

        int width = getWidth();
        int height = getHeight();

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float baseline = (height - fm.bottom - fm.top) / 2f;

        float x = -offsetX;
        while (x < width) {
            canvas.drawText(text, x, baseline, textPaint);
            x += textWidth;
        }

        if (isRunning && isShown()) {
            offsetX = (offsetX + speed) % textWidth;
            postInvalidateOnAnimation();
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            postInvalidateOnAnimation();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isRunning = true;
        postInvalidateOnAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
    }
}
