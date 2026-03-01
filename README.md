# Java Console Todo App

Java 콘솔 기반 Todo(할 일) 관리 프로그램입니다.  
메뉴 방식으로 **CRUD(추가/조회/수정(완료)/삭제)** 흐름을 구현했고, **입력 검증**과 **진행 요약(완료율)**을 포함했습니다.  
**Version:** v1.1 (edit/search/sort)

## Features
- Create: 할 일 추가 (id 자동 증가)
- Read: 할 일 목록 출력
- Update: 완료 처리
  - 이미 완료된 항목 재처리 시 안내 메시지 출력
- Delete: id 기반 삭제
- Input Validation
  - 숫자 입력 예외 처리(try-catch)로 비정상 입력 방지
  - 제목 빈 값 입력 방지(trim)
- Summary
  - 총/완료/남은/진행률(%) 요약 출력
- Update: 제목 수정 기능(id로 찾아 제목 변경)
- Search: 키워드 검색(제목에 포함되면 출력, 대소문자 무시)
- View: 목록에서 미완료 Todo를 먼저 출력(정렬)

## Tech Stack
- Java
- ArrayList, Scanner
- try-catch (NumberFormatException)

## Project Structure
```text
src/
 ├─ Main.java   # 메뉴 루프 + 입력 처리 + 기능 메서드
 └─ Todo.java   # Todo 모델(id/title/done) + 출력/완료 메서드
```
How to Run

IntelliJ에서 Main.java 실행

Notes (Implementation Highlights)

입력은 nextLine() 기반으로 통일하고 숫자는 Integer.parseInt()로 변환하여 입력 안정성을 높였습니다.

Todo 클래스로 데이터/동작을 분리해 객체 단위로 상태를 관리합니다.

findById() / removeById()로 id 기반 작업(완료/삭제)을 처리합니다.

Sample Output (Format)

1. [미완료] 공부하기

2. [완료] 운동하기

총 3개 / 완료 1개 / 남은 2개 (33%)

Next Improvements

제목 수정 기능

키워드 검색 기능

필터/정렬(미완료 먼저, 완료만 보기)

파일 저장/불러오기(프로그램 재시작 후에도 데이터 유지)


### 레포 Description 추천(한 줄)
`Java 콘솔 Todo 앱 (CRUD + 입력 검증 + 진행 요약)`

원하면 README에 **실제 실행 로그(너 콘솔 출력 그대로)** 섹션까지 붙여서 더 신뢰감 있게 만들어줄 수도 있어.
::contentReference[oaicite:0]{index=0}
