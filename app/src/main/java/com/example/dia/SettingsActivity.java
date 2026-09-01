package com.example.dia;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ivIdCardPreview;
    private ImageView ivPassportPreview;
    
    private EditText etIdCardName, etIdCardNumber, etIdCardBirthday;
    private EditText etRnokppName, etRnokppNumber, etRnokppBirthday;
    private EditText etPassportName, etPassportNumber, etPassportBirthday;
    private EditText etBirthName, etBirthBirthday, etBirthPlace;

    private String idCardPhotoPath = "";
    private String passportPhotoPath = "";

    private final ActivityResultLauncher<String> idCardPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String path = saveImageToInternalStorage(uri, "id_card");
                    if (path != null) {
                        idCardPhotoPath = path;
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        if (bitmap != null) {
                            ivIdCardPreview.setImageBitmap(bitmap);
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> passportPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String path = saveImageToInternalStorage(uri, "passport");
                    if (path != null) {
                        passportPhotoPath = path;
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        if (bitmap != null) {
                            ivPassportPreview.setImageBitmap(bitmap);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_settings);

        // Ініціалізація View
        ImageView btnBack = findViewById(R.id.btn_back);
        
        ivIdCardPreview = findViewById(R.id.iv_id_card_preview);
        Button btnIdCardPhoto = findViewById(R.id.btn_id_card_photo);
        etIdCardName = findViewById(R.id.et_id_card_name);
        etIdCardNumber = findViewById(R.id.et_id_card_number);
        etIdCardBirthday = findViewById(R.id.et_id_card_birthday);

        etRnokppName = findViewById(R.id.et_rnokpp_name);
        etRnokppNumber = findViewById(R.id.et_rnokpp_number);
        etRnokppBirthday = findViewById(R.id.et_rnokpp_birthday);

        ivPassportPreview = findViewById(R.id.iv_passport_preview);
        Button btnPassportPhoto = findViewById(R.id.btn_passport_photo);
        etPassportName = findViewById(R.id.et_passport_name);
        etPassportNumber = findViewById(R.id.et_passport_number);
        etPassportBirthday = findViewById(R.id.et_passport_birthday);

        etBirthName = findViewById(R.id.et_birth_name);
        etBirthBirthday = findViewById(R.id.et_birth_birthday);
        etBirthPlace = findViewById(R.id.et_birth_place);

        Button btnSave = findViewById(R.id.btn_save);

        // Налаштування кнопки назад
        btnBack.setOnClickListener(v -> finish());

        // Завантаження поточних налаштувань
        loadSettings();

        // Налаштування вибору фото
        btnIdCardPhoto.setOnClickListener(v -> idCardPhotoLauncher.launch("image/*"));
        btnPassportPhoto.setOnClickListener(v -> passportPhotoLauncher.launch("image/*"));

        // Збереження
        btnSave.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, "Налаштування збережено!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("DiaSettings", Context.MODE_PRIVATE);

        // ID-картка
        etIdCardName.setText(prefs.getString("name_id_card", ""));
        etIdCardNumber.setText(prefs.getString("number_id_card", ""));
        etIdCardBirthday.setText(prefs.getString("birthday_id_card", ""));
        idCardPhotoPath = prefs.getString("photo_id_card", "");
        if (!idCardPhotoPath.isEmpty() && new File(idCardPhotoPath).exists()) {
            Bitmap b = BitmapFactory.decodeFile(idCardPhotoPath);
            if (b != null) ivIdCardPreview.setImageBitmap(b);
        } else {
            ivIdCardPreview.setImageDrawable(null);
        }

        // РНОКПП
        etRnokppName.setText(prefs.getString("name_rnokpp", ""));
        etRnokppNumber.setText(prefs.getString("number_rnokpp", ""));
        etRnokppBirthday.setText(prefs.getString("birthday_rnokpp", ""));

        // Закордонний паспорт
        etPassportName.setText(prefs.getString("name_passport", ""));
        etPassportNumber.setText(prefs.getString("number_passport", ""));
        etPassportBirthday.setText(prefs.getString("birthday_passport", ""));
        passportPhotoPath = prefs.getString("photo_passport", "");
        if (!passportPhotoPath.isEmpty() && new File(passportPhotoPath).exists()) {
            Bitmap b = BitmapFactory.decodeFile(passportPhotoPath);
            if (b != null) ivPassportPreview.setImageBitmap(b);
        } else {
            ivPassportPreview.setImageDrawable(null);
        }

        // Свідоцтво про народження
        etBirthName.setText(prefs.getString("name_birth", ""));
        etBirthBirthday.setText(prefs.getString("birthday_birth", ""));
        etBirthPlace.setText(prefs.getString("place_birth", ""));
    }

    private void saveSettings() {
        SharedPreferences prefs = getSharedPreferences("DiaSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // ID-картка
        editor.putString("name_id_card", etIdCardName.getText().toString());
        editor.putString("number_id_card", etIdCardNumber.getText().toString());
        editor.putString("birthday_id_card", etIdCardBirthday.getText().toString());
        editor.putString("photo_id_card", idCardPhotoPath);

        // РНОКПП
        editor.putString("name_rnokpp", etRnokppName.getText().toString());
        editor.putString("number_rnokpp", etRnokppNumber.getText().toString());
        editor.putString("birthday_rnokpp", etRnokppBirthday.getText().toString());

        // Закордонний паспорт
        editor.putString("name_passport", etPassportName.getText().toString());
        editor.putString("number_passport", etPassportNumber.getText().toString());
        editor.putString("birthday_passport", etPassportBirthday.getText().toString());
        editor.putString("photo_passport", passportPhotoPath);

        // Свідоцтво про народження
        editor.putString("name_birth", etBirthName.getText().toString());
        editor.putString("birthday_birth", etBirthBirthday.getText().toString());
        editor.putString("place_birth", etBirthPlace.getText().toString());

        editor.apply();
    }

    private String saveImageToInternalStorage(Uri uri, String prefix) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return null;
            File file = new File(getFilesDir(), prefix + "_avatar_" + System.currentTimeMillis() + ".jpg");
            OutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Помилка збереження фото", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
