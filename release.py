#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Скрипт для автоматичної збірки APK та публікації релізу на GitHub.

Використання:
    python release.py
    або
    python release.py v1.1 "Опис змін у релізі"
"""

import os
import sys
import re
import json
import subprocess
import urllib.request
import urllib.error
from pathlib import Path

# ==================== НАЛАШТУВАННЯ GITHUB ====================
GITHUB_OWNER = "Mask0h"         # Ваш логін GitHub
GITHUB_REPO = "diia"            # Назва репозиторію на GitHub

# GitHub Personal Access Token (з правами 'repo' або 'contents:write')
# Можна задати тут напряму АБО через змінну оточення GITHUB_TOKEN
GITHUB_TOKEN = os.environ.get("GITHUB_TOKEN", "ВАШ_GITHUB_TOKEN_ТУТ")

# Режим збірки: 'debug' або 'release'
BUILD_VARIANT = "debug"
# =============================================================

PROJECT_DIR = Path(__file__).resolve().parent
GRADLEW = PROJECT_DIR / ("gradlew.bat" if os.name == "nt" else "gradlew")
GRADLE_FILE = PROJECT_DIR / "app" / "build.gradle.kts"


def get_current_version_from_gradle() -> str:
    """Зчитує поточну versionName з app/build.gradle.kts"""
    if not GRADLE_FILE.exists():
        return "1.0"
    content = GRADLE_FILE.read_text(encoding="utf-8")
    match = re.search(r'versionName\s*=\s*"([^"]+)"', content)
    if match:
        return match.group(1)
    return "1.0"


def update_version_in_gradle(new_version: str):
    """Оновлює versionName у app/build.gradle.kts"""
    if not GRADLE_FILE.exists():
        return
    content = GRADLE_FILE.read_text(encoding="utf-8")
    updated = re.sub(r'versionName\s*=\s*"[^"]+"', f'versionName = "{new_version}"', content)
    
    # Також можна збільшити versionCode на 1
    def inc_version_code(m):
        code = int(m.group(1)) + 1
        return f"versionCode = {code}"
    
    updated = re.sub(r'versionCode\s*=\s*(\d+)', inc_version_code, updated)
    GRADLE_FILE.write_text(updated, encoding="utf-8")
    print(f"✅ Оновлено app/build.gradle.kts -> versionName = '{new_version}'")


def build_apk(variant: str) -> Path:
    """Компілює Android проєкт через Gradle"""
    task = f"assemble{variant.capitalize()}"
    print(f"\n🔨 Запуск компіляції: {GRADLEW.name} {task} ...")

    cmd = [str(GRADLEW), task]
    result = subprocess.run(cmd, cwd=PROJECT_DIR)

    if result.returncode != 0:
        print("\n❌ Помилка збірки проєкту! Перевірте помилки Gradle вище.")
        sys.exit(1)

    apk_path = PROJECT_DIR / "app" / "build" / "outputs" / "apk" / variant / f"app-{variant}.apk"
    if not apk_path.exists():
        # Спробуємо знайти будь-який apk у вихідній папці
        apk_dir = PROJECT_DIR / "app" / "build" / "outputs" / "apk" / variant
        apks = list(apk_dir.glob("*.apk"))
        if apks:
            apk_path = apks[0]
        else:
            print(f"\n❌ Згенерований APK файл не знайдено за шляхом: {apk_path}")
            sys.exit(1)

    print(f"✅ APK успішно зібрано: {apk_path} ({apk_path.stat().st_size / (1024*1024):.2f} MB)")
    return apk_path


def create_github_release(tag: str, release_name: str, body: str) -> dict:
    """Створює новий Release на GitHub"""
    url = f"https://api.github.com/repos/{GITHUB_OWNER}/{GITHUB_REPO}/releases"
    headers = {
        "Authorization": f"Bearer {GITHUB_TOKEN}",
        "Accept": "application/vnd.github.v3+json",
        "User-Agent": "Dia-Release-Script",
        "Content-Type": "application/json"
    }
    payload = {
        "tag_name": tag,
        "name": release_name,
        "body": body,
        "draft": False,
        "prerelease": False
    }

    req = urllib.request.Request(url, data=json.dumps(payload).encode("utf-8"), headers=headers, method="POST")
    try:
        with urllib.request.urlopen(req) as response:
            return json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        err_msg = e.read().decode("utf-8")
        print(f"\n❌ Помилка створення релізу на GitHub (HTTP {e.code}):\n{err_msg}")
        if e.code == 401:
            print("👉 Перевірте правильність вашого GITHUB_TOKEN.")
        elif e.code == 404:
            print(f"👉 Репозиторій '{GITHUB_OWNER}/{GITHUB_REPO}' не знайдено або немає прав доступу.")
        sys.exit(1)


def upload_release_asset(upload_url_template: str, apk_path: Path, asset_name: str):
    """Завантажує APK файл у створений реліз на GitHub"""
    upload_url = upload_url_template.split("{")[0] + f"?name={asset_name}"
    
    file_size = apk_path.stat().st_size
    print(f"\n⬆️  Завантаження {asset_name} на GitHub ({file_size / (1024*1024):.2f} MB)...")

    with open(apk_path, "rb") as f:
        data = f.read()

    headers = {
        "Authorization": f"Bearer {GITHUB_TOKEN}",
        "Accept": "application/vnd.github.v3+json",
        "User-Agent": "Dia-Release-Script",
        "Content-Type": "application/vnd.android.package-archive",
        "Content-Length": str(file_size)
    }

    req = urllib.request.Request(upload_url, data=data, headers=headers, method="POST")
    try:
        with urllib.request.urlopen(req) as response:
            res_json = json.loads(response.read().decode("utf-8"))
            download_url = res_json.get("browser_download_url", "")
            print(f"✅ Файл успішно завантажено!")
            print(f"🔗 Пряме посилання на APK: {download_url}")
            return download_url
    except urllib.error.HTTPError as e:
        err_msg = e.read().decode("utf-8")
        print(f"\n❌ Помилка завантаження файлу на GitHub (HTTP {e.code}):\n{err_msg}")
        sys.exit(1)


def main():
    print("=" * 60)
    print("🚀 Автоматична збірка та публікація релізу Дія на GitHub")
    print("=" * 60)

    # 1. Перевірка токена
    if not GITHUB_TOKEN or GITHUB_TOKEN == "ВАШ_GITHUB_TOKEN_ТУТ":
        print("\n⚠️  УВАГА: Не вказано GITHUB_TOKEN!")
        print("Вкажіть токен у файлі release.py (рядок GITHUB_TOKEN = '...')")
        print("або встановіть змінну оточення: set GITHUB_TOKEN=ghp_xxxx\n")
        token_input = input("Введіть ваш GitHub Personal Access Token (або Enter для виходу): ").strip()
        if not token_input:
            sys.exit(1)
        globals()["GITHUB_TOKEN"] = token_input

    # 2. Визначення версії
    current_ver = get_current_version_from_gradle()
    
    tag = None
    body = "Оновлення застосунку Дія"

    if len(sys.argv) > 1:
        tag = sys.argv[1]
    if len(sys.argv) > 2:
        body = sys.argv[2]

    if not tag:
        print(f"\nПоточна версія у проєкті: {current_ver}")
        suggested_tag = f"v{current_ver}"
        tag_input = input(f"Введіть тег нової версії (за замовчуванням '{suggested_tag}'): ").strip()
        tag = tag_input if tag_input else suggested_tag

    clean_ver = tag.lstrip("vV")
    if clean_ver != current_ver:
        update_version_in_gradle(clean_ver)

    if len(sys.argv) <= 2:
        body_input = input("Введіть опис змін (Release Notes, або Enter для стандартного): ").strip()
        if body_input:
            body = body_input

    release_title = f"Реліз {tag}"

    # 3. Компіляція APK
    apk_path = build_apk(BUILD_VARIANT)

    # 4. Створення релізу на GitHub
    print(f"\n📦 Створення релізу '{tag}' у репозиторії '{GITHUB_OWNER}/{GITHUB_REPO}'...")
    release_data = create_github_release(tag, release_title, body)
    html_url = release_data.get("html_url", "")
    upload_url = release_data.get("upload_url", "")

    print(f"✅ Реліз створено: {html_url}")

    # 5. Завантаження APK
    asset_name = f"dia-{tag}.apk"
    upload_release_asset(upload_url, apk_path, asset_name)

    print("\n" + "=" * 60)
    print(f"🎉 УСПІХ! Нову версію {tag} опубліковано на GitHub.")
    print(f"🌐 Сторінка релізу: {html_url}")
    print("Тепер кнопка 'Оновити застосунок' у додатку автоматично знайде це оновлення!")
    print("=" * 60)


if __name__ == "__main__":
    main()
