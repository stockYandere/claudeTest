package com.example.claudetest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

/** 공 하나가 중력, 점프, 천장/바닥 반동에 따라 움직이는 화면. */
public class GameView extends View {
    // 화면 높이에 비례한 값이라 기기 크기와 상관없이 비슷하게 움직인다.
    private static final float GRAVITY = 2.4f;          // 화면 높이 / 초^2
    private static final float JUMP_SPEED = 1.1f;       // 버튼 한 번에 더해지는 위쪽 속도 (화면 높이 / 초)
    private static final float MAX_UP_SPEED = 2.6f;     // 연타해도 넘지 않는 최대 상승 속도
    private static final float CEILING_BOUNCE = 0.7f;   // 천장에 부딪힌 뒤 남는 속도 비율
    private static final float FLOOR_BOUNCE = 0.35f;    // 바닥에 떨어진 뒤 튀어 오르는 속도 비율
    private static final float REST_SPEED = 0.08f;      // 이보다 느리게 바닥에 닿으면 멈춘다
    private static final long FLASH_MS = 150;

    private final Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint floorPaint = new Paint();
    private final Paint ceilingPaint = new Paint();

    private float ballY = Float.NaN;   // 공 중심의 y 좌표 (px)
    private float velocityY;           // px / 초, 양수가 아래쪽
    private boolean moving;
    private long lastFrameMs;
    private long ceilingHitMs;

    public GameView(Context context) {
        super(context);
        setBackgroundColor(Color.rgb(240, 244, 255));
        ballPaint.setColor(Color.rgb(217, 119, 87));
        floorPaint.setColor(Color.rgb(90, 90, 90));
    }

    /** 버튼이 눌릴 때마다 호출된다. 공중에서도 위쪽 속도가 더해진다. */
    public void jump() {
        float h = getHeight();
        if (h == 0) return;
        // 떨어지는 중이면 낙하 속도를 없애고, 올라가는 중이면 그 위에 더한다.
        velocityY = Math.max(Math.min(velocityY, 0) - JUMP_SPEED * h, -MAX_UP_SPEED * h);
        if (!moving) {
            moving = true;
            lastFrameMs = SystemClock.uptimeMillis();
        }
        postInvalidateOnAnimation();
    }

    private float radius() {
        return Math.min(getWidth(), getHeight()) * 0.07f;
    }

    private float floorThickness() {
        return getHeight() * 0.02f;
    }

    private float ceilingThickness() {
        return getHeight() * 0.015f;
    }

    private void step() {
        long now = SystemClock.uptimeMillis();
        float dt = Math.min((now - lastFrameMs) / 1000f, 0.05f);
        lastFrameMs = now;

        float h = getHeight();
        float r = radius();
        float top = ceilingThickness() + r;
        float bottom = h - floorThickness() - r;

        velocityY += GRAVITY * h * dt;
        ballY += velocityY * dt;

        if (ballY < top) {
            // 천장 충돌: 위치를 되돌리고 속도를 반대로 (반작용)
            ballY = top;
            velocityY = -velocityY * CEILING_BOUNCE;
            ceilingHitMs = now;
        } else if (ballY > bottom) {
            ballY = bottom;
            if (velocityY > REST_SPEED * h) {
                velocityY = -velocityY * FLOOR_BOUNCE;
            } else {
                velocityY = 0;
                moving = false;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        float r = radius();

        if (Float.isNaN(ballY)) {
            ballY = h - floorThickness() - r;
        }
        if (moving) {
            step();
        }

        boolean flashing = SystemClock.uptimeMillis() - ceilingHitMs < FLASH_MS;
        ceilingPaint.setColor(flashing ? Color.rgb(230, 60, 60) : Color.rgb(90, 90, 90));
        canvas.drawRect(0, 0, w, ceilingThickness(), ceilingPaint);
        canvas.drawRect(0, h - floorThickness(), w, h, floorPaint);
        canvas.drawCircle(w / 2f, ballY, r, ballPaint);

        if (moving || flashing) {
            postInvalidateOnAnimation();
        }
    }
}
