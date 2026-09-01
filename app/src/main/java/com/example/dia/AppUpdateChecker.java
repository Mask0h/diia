package com.example.dia;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppUpdateChecker {

    // GitHub логін та назва репозиторію
    public static String GITHUB_OWNER = "Mask0h";
    public static String GITHUB_REPO = "diia";

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public AppUpdateChecker(Context context) {
        this.context = context;
    }

    public static class UpdateInfo {
        public boolean hasUpdate;
        public String latestVersion;
        public String currentVersion;
        public String releaseNotes;
        public String downloadUrl;
        public String releaseUrl;
    }

    public interface CheckCallback {
        void onSuccess(UpdateInfo info);
        void onError(String errorMessage);
    }

    public void check(boolean showProgress, CheckCallback callback) {
        AlertDialog progressDialog = null;
        if (showProgress) {
            progressDialog = new MaterialAlertDialogBuilder(context)
                    .setTitle("Перевірка оновлень")
                    .setMessage("Перевіряємо наявність нової версії на GitHub...")
                    .setCancelable(false)
                    .show();
        }

        final AlertDialog finalProgressDialog = progressDialog;

        executor.execute(() -> {
            try {
                String apiUrl = "https://api.github.com/repos/" + GITHUB_OWNER + "/" + GITHUB_REPO + "/releases/latest";
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
                conn.setRequestProperty("User-Agent", "Dia-Android-App");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(sb.toString());
                    String tagName = json.optString("tag_name", "");
                    String body = json.optString("body", "");
                    String htmlUrl = json.optString("html_url", "");
                    String downloadUrl = htmlUrl;

                    JSONArray assets = json.optJSONArray("assets");
                    if (assets != null && assets.length() > 0) {
                        for (int i = 0; i < assets.length(); i++) {
                            JSONObject asset = assets.getJSONObject(i);
                            String assetName = asset.optString("name", "");
                            if (assetName.endsWith(".apk")) {
                                downloadUrl = asset.optString("browser_download_url", downloadUrl);
                                break;
                            }
                        }
                    }

                    String currentVersion = getCurrentVersionName();
                    boolean isNewer = isVersionNewer(tagName, currentVersion);

                    UpdateInfo info = new UpdateInfo();
                    info.hasUpdate = isNewer;
                    info.latestVersion = tagName;
                    info.currentVersion = currentVersion;
                    info.releaseNotes = body;
                    info.downloadUrl = downloadUrl;
                    info.releaseUrl = htmlUrl;

                    mainHandler.post(() -> {
                        if (finalProgressDialog != null && finalProgressDialog.isShowing()) {
                            finalProgressDialog.dismiss();
                        }
                        if (callback != null) {
                            callback.onSuccess(info);
                        } else {
                            showDefaultDialog(info);
                        }
                    });

                } else if (responseCode == 404) {
                    // Репозиторій або релізи ще не створені на GitHub -> вважаємо що поточної версії достатньо
                    mainHandler.post(() -> {
                        if (finalProgressDialog != null && finalProgressDialog.isShowing()) {
                            finalProgressDialog.dismiss();
                        }
                        UpdateInfo info = new UpdateInfo();
                        info.hasUpdate = false;
                        info.currentVersion = getCurrentVersionName();
                        info.latestVersion = info.currentVersion;
                        if (callback != null) {
                            callback.onSuccess(info);
                        } else {
                            showNoUpdatesDialog(info.currentVersion);
                        }
                    });
                } else {
                    throw new Exception("Код відповіді сервера: " + responseCode);
                }

            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (finalProgressDialog != null && finalProgressDialog.isShowing()) {
                        finalProgressDialog.dismiss();
                    }
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    } else {
                        showErrorDialog("Не вдалося перевірити оновлення. Перевірте підключення до інтернету.");
                    }
                });
            }
        });
    }

    public void showDefaultDialog(UpdateInfo info) {
        if (info.hasUpdate) {
            String message = "Доступна нова версія: " + info.latestVersion + "\nВстановлена версія: " + info.currentVersion;
            if (info.releaseNotes != null && !info.releaseNotes.trim().isEmpty()) {
                message += "\n\nЩо нового:\n" + info.releaseNotes;
            }

            new MaterialAlertDialogBuilder(context)
                    .setTitle("Доступне оновлення")
                    .setMessage(message)
                    .setPositiveButton("Оновити", (dialog, which) -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.downloadUrl));
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(context, "Не вдалося відкрити посилання на завантаження", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Пізніше", null)
                    .show();
        } else {
            showNoUpdatesDialog(info.currentVersion);
        }
    }

    private void showNoUpdatesDialog(String currentVersion) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Оновлення відсутні")
                .setMessage("У вас встановлено найновішу версію застосунку (" + currentVersion + ").")
                .setPositiveButton("Зрозуміло", null)
                .show();
    }

    private void showErrorDialog(String errorMsg) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Помилка")
                .setMessage(errorMsg)
                .setPositiveButton("Зрозуміло", null)
                .show();
    }

    private String getCurrentVersionName() {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0";
        }
    }

    public static boolean isVersionNewer(String remoteVersion, String localVersion) {
        if (remoteVersion == null || localVersion == null) return false;

        String cleanRemote = remoteVersion.replaceAll("^[vV]", "").trim();
        String cleanLocal = localVersion.replaceAll("^[vV]", "").trim();

        String[] remoteParts = cleanRemote.split("[.-]");
        String[] localParts = cleanLocal.split("[.-]");

        int length = Math.max(remoteParts.length, localParts.length);
        for (int i = 0; i < length; i++) {
            int r = i < remoteParts.length ? parseNumber(remoteParts[i]) : 0;
            int l = i < localParts.length ? parseNumber(localParts[i]) : 0;

            if (r > l) return true;
            if (r < l) return false;
        }
        return false;
    }

    private static int parseNumber(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
