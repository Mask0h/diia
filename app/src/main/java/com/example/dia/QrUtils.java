package com.example.dia;

import android.graphics.Bitmap;

public class QrUtils {

    public static Bitmap generateFakeQr(int size) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        int black = 0xFF000000;
        int bgWhite = 0xFFFFFFFF;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                bmp.setPixel(x, y, bgWhite);
            }
        }

        int cell = size / 25;

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
                int color = pattern[row * 25 + col] == 1 ? black : bgWhite;
                for (int dy = 0; dy < cell; dy++) {
                    for (int dx = 0; dx < cell; dx++) {
                        bmp.setPixel(col * cell + dx, row * cell + dy, color);
                    }
                }
            }
        }
        return bmp;
    }

    public static Bitmap generateFakeBarcode(int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int black = 0xFF000000;
        int white = 0xFFFFFFFF;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bmp.setPixel(x, y, white);
            }
        }
        int[] barWidths = {3,2,3,1,2,3,1,2,1,3,2,1,3,2,1,2,3,2,1,3,2,1,2,1,3,2,3,1,2,3};
        int x = 10;
        boolean isBlack = true;
        for (int w : barWidths) {
            int color = isBlack ? black : white;
            for (int bx = 0; bx < w * 6; bx++) {
                if (x + bx >= width - 10) break;
                for (int y = 10; y < height - 10; y++) {
                    bmp.setPixel(x + bx, y, color);
                }
            }
            x += w * 6;
            isBlack = !isBlack;
        }
        return bmp;
    }
}
