#!/bin/sh
# APK 만들기 (Git Bash에서 실행): sh build.sh
# 화면(index.html)을 고친 뒤 업데이트할 때는 VERSION 숫자를 올린다.
set -e
VERSION=1

SRC="$(cd "$(dirname "$0")" && pwd)"
B="$HOME/android-build"
export JAVA_HOME="$(cygpath -w "$B/jdk/jdk-17.0.21.1+2")"
export PATH="$B/jdk/jdk-17.0.21.1+2/bin:$PATH"
TOOLS="$B/sdk/build-tools/35.0.0"
JAR="$(cygpath -m "$B/sdk/platforms/android-35/android.jar")"
W="$B/work"

# 서명 키: 같은 키로 서명해야 데이터를 지우지 않고 업데이트할 수 있다
KEY="$B/keystore/release.jks"
PASS="$B/keystore/password.txt"
if [ ! -f "$KEY" ]; then
  mkdir -p "$B/keystore"
  head -c 24 /dev/urandom | base64 | tr -d '+/=\n' > "$PASS"
  keytool -genkeypair -keystore "$(cygpath -m "$KEY")" -alias app -keyalg RSA -keysize 2048 -validity 10000 \
    -storepass:file "$(cygpath -m "$PASS")" -dname "CN=Support Calendar"
fi

rm -rf "$W"
mkdir -p "$W/res/drawable" "$W/assets" "$W/classes" "$W/dex"
cp "$SRC/../icon-192.png" "$W/res/drawable/icon.png"
cp "$SRC/AndroidManifest.xml" "$SRC/MainActivity.java" "$W/"
cp "$SRC/../index.html" "$W/assets/"
cd "$W"

"$TOOLS/aapt2.exe" compile --dir res -o res.zip
"$TOOLS/aapt2.exe" link -o base.apk -I "$JAR" --manifest AndroidManifest.xml \
  --min-sdk-version 26 --target-sdk-version 35 --version-code $VERSION --version-name 1.$VERSION -A assets res.zip
javac -nowarn -encoding UTF-8 -source 8 -target 8 -classpath "$JAR" -d classes MainActivity.java
jar cf classes.jar -C classes .
"$TOOLS/d8.bat" --release --min-api 26 --lib "$JAR" --output dex classes.jar
jar uf base.apk -C dex classes.dex
"$TOOLS/zipalign.exe" -f 4 base.apk aligned.apk
"$TOOLS/apksigner.bat" sign --ks "$(cygpath -m "$KEY")" --ks-pass "file:$(cygpath -m "$PASS")" --out app.apk aligned.apk
"$TOOLS/apksigner.bat" verify app.apk
cp app.apk "$SRC/../학원시간표.apk"
echo "완료: 학원시간표.apk"
