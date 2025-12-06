# 농담몬 (JokeMon)
AI 기반 농담 생성 & 음성 재생 Android 앱  
Jetpack Compose + MVI + Firebase Functions + OpenAI + ElevenLabs


<img width="1024" height="500" alt="1024-500" src="https://github.com/user-attachments/assets/d94fe1ea-96ee-450f-92a6-3eec78044211" />

## 참여 인원
2명 - 앱 개발 1명 , 디자인 1명


## 소개
농담몬은 OpenAI API를 이용해 농담을 생성하고,  
ElevenLabs TTS API를 통해 생성된 농담을 음성으로 재생할 수 있는 Android 앱입니다.

UI는 Jetpack Compose 기반으로 구현했으며,  
아키텍처는 MVI 패턴을 기반으로 설계했습니다.  
또한 API 호출은 App → Firebase Functions → OpenAI/ElevenLabs 구조로 구성해  
보안 및 안정성을 강화했습니다.

---

## 주요 기능
- AI 농담 생성 (OpenAI API)
- 생성된 농담을 음성으로 재생 (ElevenLabs API)
- 닉네임 변경 , 캐릭터 선택 기능
- 즐겨찾기 / 최근 농담 저장 (Room)
- 클립보드 복사, 스낵바, 애니메이션 제공
- Jetpack Compose 기반 최신 UI 구조 적용
---

## 기술 스택

### **Android / UI**
- Kotlin
- Jetpack Compose
- Material3
- MVI 아키텍처

### **Data / Service**
- Retrofit, OkHttp
- Firebase Functions (Serverless API Gateway)
- OpenAI API (Text Generation)
- ElevenLabs API (TTS)
- Room
- Data Stroe , Shared Preference

### **Architecture / State**
- Coroutine + Flow (State Flow , Shared Flow)
- Hilt DI
- Repository Pattern

### **ETC**
- Firebase Crashlytics
- Firebase Analytics

---

## 🏗 아키텍처 구조
presentation (Compose UI, Intent, State)

└── viewmodel (MVI)

data

└── repository

└── source (remote/local)


---

## 주요 화면
![joke_mon_screen_shot_1](https://github.com/user-attachments/assets/fb2cf242-b58e-4006-ad88-57781f830330) &nbsp; ![joke_mon_screen_shot_2](https://github.com/user-attachments/assets/4952160e-3122-4c71-b6dd-1cd1e23b43ee)
![joke_mon_screen_shot_3](https://github.com/user-attachments/assets/9d7a46bd-1f95-4c92-877e-ac9f3d62d40d) &nbsp; ![joke_mon_screen_shot_4](https://github.com/user-attachments/assets/0236e148-d2cd-4e05-a3f6-ada7c24d9c2b) 
![joke_mon_screen_shot_5](https://github.com/user-attachments/assets/9ef8dcbf-7604-4718-9b8c-4c2c510cf9d2) &nbsp; ![joke_mon_screen_shot_6](https://github.com/user-attachments/assets/bb2c8af1-d597-440e-8030-b06135cbe060)






## 개발 포인트

### 1) **Jetpack Compose + MVI 아키텍처 적용**
- 모든 화면은 단일 상태(State)로 구성
- Intent → Reducer → State 업데이트 구조
- Preview 기반 빠른 UI 빌드

### 2) **서버리스 중계 구조**
- Firebase Functions를 사용해서  
- 앱에서 직접 OpenAI/ElevenLabs 호출 X  
- → Token 보호 + 네트워크 안정성 확보

### 3) **Room 기반 히스토리/즐겨찾기 구현**
- 최근 생성된 농담 저장
- key 기반 중복 방지 로직 적용

---

## 향후 계획
- 위젯 기능 추가 예정
---

## 다운로드
https://play.google.com/store/apps/details?id=com.joke.mon

---

## 개발자
나진석  
Android Developer  
email : na0736@gmail.com / github : https://github.com/najin1027

