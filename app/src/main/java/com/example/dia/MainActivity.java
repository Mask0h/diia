package com.example.dia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dia.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // СКРЫВАЕМ ВЕРХНЮЮ ПЛАШКУ ТУТ:
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Задерживаем стартовый экран на 2 секунды, а затем переходим на HomeActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Закрываем этот экран, чтобы нельзя было вернуться назад
        }, 2000);
    }
}