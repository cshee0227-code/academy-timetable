# 학원 시간표

선생님 10명 · 학생 30명 규모의 1:1 학원 시간표 관리 앱 (월~금, 10:00~20:00, 50분 교시)

- 주간 보드: 드래그 앤 드롭으로 수업 이동·교환, 자동 배정
- 연강·공강 현황: 학생 2~4연강 끊김, 선생님 공강 확인
- 개인별 시간표, 선생님·학생 정보 관리, 백업(.json)

## 설치 (안드로이드 APK)

1. 휴대폰에서 아래 주소로 `academy-timetable.apk`를 내려받습니다.
   https://github.com/cshee0227-code/academy-timetable/releases/latest/download/academy-timetable.apk
2. 내려받은 파일을 열고, "출처를 알 수 없는 앱 설치"를 허용한 뒤 설치합니다.

- 안드로이드 8.0 이상에서 동작합니다.
- 인터넷 권한이 없습니다. 입력한 내용은 휴대폰 안에만 저장됩니다.
- APK를 직접 빌드하려면 JDK 17과 Android SDK(build-tools 35.0.0, platform android-35)를 준비한 뒤 Git Bash에서 `sh android/build.sh`를 실행합니다.

## 설치 (웹앱)

https://cshee0227-code.github.io/academy-timetable/ 를 휴대폰에서 열고 '홈 화면에 추가'(아이폰 Safari) 또는 '앱 설치'(안드로이드 Chrome)를 누릅니다. 아이폰은 이 방법을 사용하세요.

## GitHub Pages로 공개하기
1. 이 폴더의 파일 전체를 새 저장소(repository) 최상위에 업로드
2. Settings > Pages > Source: `Deploy from a branch`, Branch: `main` / `(root)` > Save
3. 1~2분 뒤 `https://<아이디>.github.io/<저장소이름>/` 주소로 접속
4. 휴대폰에서 그 주소를 열고 '홈 화면에 추가'(아이폰 Safari) 또는 '앱 설치'(안드로이드 Chrome)

## 데이터 저장 방식
- 데이터는 각 기기의 브라우저에 저장됩니다(기기 간 자동 동기화 없음).
- 기기 이동·보관은 '선생님·학생 정보' 탭 하단의 백업 내보내기/불러오기를 사용하세요.
- 주의: 공개 저장소의 index.html 안에 들어 있는 초기 데이터는 누구나 볼 수 있습니다. 실제 학생 이름은 앱에서 입력하세요(입력한 데이터는 그 기기에만 저장되고 GitHub에는 올라가지 않습니다).
