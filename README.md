# android_side_SMStoMAPProject
When company send to Messages, My App catches the context and shows a Map of where things go.

- 회사에서 일정 형식의 문자 메시지가 오면, 위치 정보를 추출합니다.
- 추출된 위치 정보와 이전 위치를 비교하여, 같으면 무시, 다르면 Push 알림 ``` NotificationCompat.Builder ``` 을 보낸다.
- 새로운 위치의 경우 ``` getSharedPreferneced ``` 를 이용하여 저장한다.
- Push 알림 클릭 시 MapActivity를 실행한다.
