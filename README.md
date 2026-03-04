# Orbit (Console Core)

**Orbit**은 “궤도(Orbit)” 컨셉의 Todo 프로젝트입니다.  
현재는 최종 웹/궤도 UI 구현 전 단계로, **콘솔 기반 코어 로직(데이터/기능/영속성)**을 개발하고 있습니다.

- 데이터 영속성(Persistence): 파일 저장/불러오기
- Todo 기능: 추가/목록/완료/삭제/제목수정/검색
- 목록 UX: 미완료 먼저 출력 + 진행 요약(완료율)

---

## Features

### Todo 관리
- Create: 할 일 추가 (id 자동 증가)
- Read: 할 일 목록 출력
  - 미완료 Todo를 먼저 출력(출력 순서 정렬)
  - 총/완료/남은/진행률(%) 요약 출력
- Update: 완료 처리
  - 이미 완료된 항목 재처리 시 안내 메시지 출력
- Update: 제목 수정 (id로 찾아 제목 변경)
- Delete: id 기반 삭제
- Search: 키워드 검색(제목 포함, 대소문자 무시)

### Persistence (파일 영속성)
- 저장: 현재 Todo 목록을 파일로 저장
- 불러오기: 파일에서 Todo 목록 복구
- 불러오기 후 id 중복 방지를 위해 `nextId = maxId + 1`로 보정

---

## Data File Format

저장 파일: `orbit_todos.txt`  
한 줄 = 한 Todo

형식:
```text
id|done|title
```

예시:
```text
1|false|Java 공부하기
2|true|운동하기
```

> `split("\\|", 3)`을 사용해 제목에 `|`가 포함되어도 파싱이 깨지지 않게 처리했습니다.  
> 또한 제목에 줄바꿈이 들어가면 포맷이 깨질 수 있어 저장 시 줄바꿈을 공백으로 치환합니다.

---

## Project Structure

```text
src/
 ├─ Main.java   # 메뉴 루프 + 입력 처리 + 기능 메서드 + 파일 저장/불러오기
 └─ Todo.java   # Todo 모델(id/title/done) + 출력/완료/수정 메서드
```

---

## How to Run

### Requirements
- Java JDK (예: 17+ 또는 25)
- IntelliJ IDEA (권장)

### Run
- IntelliJ에서 `Main.java` 실행

---

## Menu

- 1) 추가
- 2) 목록
- 3) 완료처리
- 4) 삭제
- 5) 제목 수정
- 6) 검색
- 7) 저장
- 8) 불러오기
- 9) 종료

> 종료는 항상 마지막 메뉴로 유지합니다.

---

## Implementation Highlights

- 입력 안정화:
  - 모든 입력을 `nextLine()` 기반으로 처리하고 숫자는 `Integer.parseInt()`로 변환
  - 숫자 입력은 `try-catch`로 방어하여 프로그램이 종료되지 않도록 처리
  - 빈 값/공백만 입력 방지 (`isBlank` + `trim`)
- 파일 I/O:
  - `Files.write / Files.readAllLines` + UTF-8 고정으로 한글 깨짐 방지
  - 불러오기 시 `todos.clear()`로 중복 방지
  - 불러오기 후 `nextId` 보정으로 id 중복 방지

---

## Next Steps (Roadmap 연결)

콘솔 코어를 기반으로 다음 단계에서 Spring Boot + DB + 협업 + Orbit UI로 확장합니다.

- Todo 확장 필드: priority / dueDate / description(노트)
- 완료 기록: Constellation Vault(별자리 보관소, 영구 아카이브)
- 협업: Workspace/RBAC, 댓글/이벤트/알림, 실시간 동기화(WebSocket)