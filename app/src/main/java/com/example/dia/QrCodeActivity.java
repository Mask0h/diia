package com.example.dia;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class QrCodeActivity extends AppCompatActivity {

    private static final int QR_SIZE = 600;
    private CountDownTimer countDownTimer;
    private TextView tvTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_qr_code);

        tvTimer = findViewById(R.id.tv_timer);
        ImageView ivQr = findViewById(R.id.iv_qr_code);
        ImageView btnClose = findViewById(R.id.btn_close);
        LinearLayout btnQr = findViewById(R.id.btn_qr);
        LinearLayout btnBarcode = findViewById(R.id.btn_barcode);

        // Generate QR bitmap (simple pattern placeholder)
        Bitmap qrBitmap = generateFakeQr(QR_SIZE);
        ivQr.setImageBitmap(qrBitmap);

        // Timer 3:00
        startTimer(180000);

        btnClose.setOnClickListener(v -> finish());

        btnQr.setOnClickListener(v -> {
            ivQr.setImageBitmap(generateFakeQr(QR_SIZE));
        });

        btnBarcode.setOnClickListener(v -> {
            ivQr.setImageBitmap(generateFakeBarcode(QR_SIZE, 200));
        });
    }

    private void startTimer(long millis) {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long mins = millisUntilFinished / 60000;
                long secs = (millisUntilFinished % 60000) / 1000;
                tvTimer.setText(String.format("Код діятиме ще %d:%02d хв", mins, secs));
            }
            @Override
            public void onFinish() {
                tvTimer.setText("Код застарів. Оновіть сторінку");
                startTimer(180000); // restart
            }
        }.start();
    }

    private Bitmap generateFakeQr(int size) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        int black = 0xFF000000;
        int white = 0xFFFFFFFF;

        // fill white
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                bmp.setPixel(x, y, white);

        // scale factor
        int cell = size / 25;

        // Simple QR-like pattern
        int[] pattern = {
            1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1,1,1,1,0,0,0,0,
            1,0,0,0,0,0,1,0,0,1,0,1,0,0,1,0,0,0,0,0,1,0,1,1,0,
            1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1,0,0,1,1,
            1,0,1,1,1,0,1,0,0,1,1,0,0,0,1,0,1,1,1,0,1,0,1,0,0,
            1,0,1,1,1,0,1,0,1,1,0,1,1,0,1,0,1,1,1,0,1,0,0,1,1,
            1,0,0,0,0,0,1,0,0,0,1,1,0,0,1,0,0,0,0,0,1,0,1,1,0,
            1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1,1,1,1,0,0,0,0,
            0,0,0,0,0,0,0,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,
            1,0,1,1,0,1,1,1,0,0,1,0,1,1,1,0,1,1,0,1,1,1,0,1,0,
            0,1,0,0,1,0,0,0,1,0,0,1,0,0,0,1,0,0,1,0,0,0,1,0,1,
            1,0,1,0,1,1,1,0,1,1,0,0,1,0,1,1,0,1,0,1,1,0,0,1,0,
            0,1,0,1,0,0,0,1,0,0,1,1,0,1,0,0,1,0,1,0,0,1,0,0,1,
            1,1,0,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,
            0,0,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,
            1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,
            0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,
            1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,
            0,0,0,0,0,0,0,0,1,0,1,1,0,0,1,0,0,0,1,0,0,1,0,0,1,
            1,1,1,1,1,1,1,1,0,1,0,0,1,1,0,1,1,1,0,1,1,0,1,1,0,
            0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,
            1,1,1,1,1,1,1,0,1,1,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,
            1,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,0,1,0,0,0,1,0,0,0,
            1,0,1,1,1,0,1,0,1,0,0,1,0,0,1,0,1,0,0,1,0,0,1,0,1,
            1,0,0,0,0,0,1,0,0,1,1,0,1,0,0,1,0,1,1,0,1,0,0,1,0,
            1,1,1,1,1,1,1,0,1,0,0,1,0,1,1,0,0,0,1,1,0,0,1,0,1
        };

        for (int row = 0; row < 25; row++) {
            for (int col = 0; col < 25; col++) {
                int color = pattern[row * 25 + col] == 1 ? black : white;
                for (int dy = 0; dy < cell; dy++)
                    for (int dx = 0; dx < cell; dx++)
                        bmp.setPixel(col * cell + dx, row * cell + dy, color);
            }
        }
        return bmp;
    }

    private Bitmap generateFakeBarcode(int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int black = 0xFF000000;
        int white = 0xFFFFFFFF;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                bmp.setPixel(x, y, white);
        // Draw bars
        int[] barWidths = {3,2,3,1,2,3,1,2,1,3,2,1,3,2,1,2,3,2,1,3,2,1,2,1,3,2,3,1,2,3};
        int x = 10;
        boolean isBlack = true;
        for (int w : barWidths) {
            int color = isBlack ? black : white;
            for (int bx = 0; bx < w * 8; bx++) {
                if (x + bx >= width - 10) break;
                for (int y = 10; y < height - 10; y++) {
                    bmp.setPixel(x + bx, y, color);
                }
            }
            x += w * 8;
            isBlack = !isBlack;
        }
        return bmp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
