import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class Main {
    private static final Path DATA_PATH = Path.of("orbit_todos.txt");
    private static boolean dirty = false;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Todo> todos = new ArrayList<>();
        int nextId = 1;

        while (true) {
            printMenu();
            int choice = readInt(sc, "선택: ");

            if (choice == 1) { // 추가
                String title = readNonEmptyLine(sc, "할 일 제목: ");
                int priority = readPriority(sc, "중요도(1~5): ");
                LocalDate due = readOptionalDate(sc, "마감일(YYYY-MM-DD, 없으면 Enter): ");
                String desc = readOptionalLine(sc, "설명(없으면 Enter): ");

                todos.add(new Todo(nextId, title, priority, due, desc));
                System.out.println("추가 완료! (id=" + nextId + ")");
                nextId++;
                dirty = true;

            } else if (choice == 2) { // 목록
                printTodos(todos);

            } else if (choice == 3) { // 완료처리
                int id = readInt(sc, "완료할 id 입력: ");
                Todo t = findById(todos, id);
                if (t == null) {
                    System.out.println("해당 id를 찾을 수 없습니다.");
                } else {
                    if (t.isDone()) {
                        System.out.println("이미 완료된 항목입니다!");
                    } else {
                        t.markDone();
                        dirty = true;
                        System.out.println("완료 처리됨!");
                    }
                }

            } else if (choice == 4) { // 삭제
                int id = readInt(sc, "삭제할 id 입력: ");
                boolean removed = removeById(todos, id);
                if (removed) {
                    dirty = true;
                    System.out.println("삭제 완료!");
                } else {
                    System.out.println("해당 id를 찾을 수 없습니다.");
                }

            } else if (choice == 5) { // 제목 수정
                int id = readInt(sc, "수정할 id 입력: ");
                Todo t = findById(todos, id);
                if (t == null) {
                    System.out.println("해당 id를 찾을 수 없습니다.");
                } else {
                    String newTitle = readNonEmptyLine(sc, "새 제목 입력: ");
                    t.setTitle(newTitle);
                    dirty = true;
                    System.out.println("수정 완료!");
                }

            } else if (choice == 6) { // 검색
                String keyword = readNonEmptyLine(sc, "검색 키워드 입력: ");
                String k = keyword.toLowerCase();
                boolean found = false;

                System.out.println("=== 검색 결과 ===");
                for (Todo todo : todos) {
                    String title = todo.getTitle().toLowerCase();
                    String desc = todo.getDescription().toLowerCase();
                    if (title.contains(k) || desc.contains(k)) {
                        todo.print();
                        found = true;
                    }
                }

                if (!found) {
                    System.out.println("검색 결과가 없습니다.");
                }

            } else if (choice == 7) { // 저장
                saveToFile(todos);
                dirty = false;

            } else if (choice == 8) { // 불러오기
                nextId = loadFromFile(todos);
                dirty = false;

            } else if (choice == 9) { // 종료
                if (dirty) {
                    boolean save = readYesNo(sc, "저장되지 않은 변경사항이 있어. 저장할까? (y/n): ");
                    if (save) {
                        saveToFile(todos);
                        dirty = false;
                    }
                }
                System.out.println("종료합니다.");
                break;
            } else {
                System.out.println("잘못된 선택입니다. (1~9)");
            }
        }

        sc.close();
    }

    // ====== 메뉴 출력 ======
    private static void printMenu() {
        System.out.println("\n=== Orbit 메뉴 ===");
        System.out.println("1) 추가");
        System.out.println("2) 목록");
        System.out.println("3) 완료처리");
        System.out.println("4) 삭제");
        System.out.println("5) 제목 수정");
        System.out.println("6) 검색");
        System.out.println("7) 저장");
        System.out.println("8) 불러오기");
        System.out.println("9) 종료");
    }

    // ====== 입력 유틸 ======
    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해줘.");
            }
        }
    }

    private static boolean readYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) return true;
            if (line.equals("n") || line.equals("no")) return false;
            System.out.println("y 또는 n으로 입력해줘.");
        }
    }

    private static String readNonEmptyLine(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            if (!line.isBlank()) return line.trim();
            System.out.println("빈 값은 안 돼. 다시 입력해줘.");
        }
    }

    private static String readOptionalLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readPriority(Scanner sc, String prompt) {
        while (true) {
            int p = readInt(sc, prompt);
            if (p >= 1 && p <= 5) return p;
            System.out.println("중요도는 1~5 사이로 입력해줘.");
        }
    }

    private static LocalDate readOptionalDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (line.isEmpty()) return null;
            try {
                return LocalDate.parse(line); // YYYY-MM-DD
            } catch (DateTimeParseException e) {
                System.out.println("형식이 올바르지 않아. 예: 2026-03-10");
            }
        }
    }

    // ====== 기능 메서드 ======
    // 목록 출력: 미완료 먼저, 완료는 아래 (출력 순서만 정렬)
    private static void printTodos(ArrayList<Todo> todos) {
        if (todos.isEmpty()) {
            System.out.println("할 일이 없습니다.");
            return;
        }

        System.out.println("=== 할 일 목록 (미완료 먼저) ===");

        // 1) 미완료 목록만 모아서 정렬 후 출력
        ArrayList<Todo> undone = new ArrayList<>();
        ArrayList<Todo> doneList = new ArrayList<>();

        for (Todo t : todos) {
            if (t.isDone()) doneList.add(t);
            else undone.add(t);
        }

        // 정렬 규칙: OrbitRank 높은 순 → (동점이면) 마감 빠른 순
        undone.sort(
                java.util.Comparator.comparingInt(Main::calcOrbitRank).reversed()
                        .thenComparing(Main::dueOrMax)
        );

        doneList.sort(
                java.util.Comparator.comparingInt(Main::calcOrbitRank).reversed()
                        .thenComparing(Main::dueOrMax)
        );

        // 출력
        for (Todo t : undone) t.print();
        for (Todo t : doneList) t.print();
    }

    private static Todo findById(ArrayList<Todo> todos, int id) {
        for (Todo t : todos) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    private static boolean removeById(ArrayList<Todo> todos, int id) {
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId() == id) {
                todos.remove(i);
                return true;
            }
        }
        return false;
    }

    // ====== 저장/불러오기 (Persistence) ======
    // 저장 파일: orbit_todos.txt (UTF-8)
    //
    // 현재 저장 포맷 (확장 가능한 key=value)
    // - 한 줄 = 한 Todo
    // - 예:
    //   id=1|done=false|priority=3|due=2026-03-10|title=...|desc=...
    //
    // 문자열 안전 처리
    // - title/desc는 '|', '=', 줄바꿈 등이 포함될 수 있어 URL 인코딩(encode/decode)로 안전하게 저장
    //
    // 구포맷 호환(이전 버전 파일 지원)
    // - 이전 파일이 "id|done|title" 형태여도 loadFromFile에서 자동으로 읽어줌
    private static void saveToFile(ArrayList<Todo> todos) {
        List<String> lines = new ArrayList<>();

        for (Todo t : todos) {
            String due = (t.getDueDate() == null) ? "" : t.getDueDate().toString();

            String titleEnc = encode(t.getTitle());
            String descEnc = encode(t.getDescription());

            String line = "id=" + t.getId()
                    + "|done=" + t.isDone()
                    + "|priority=" + t.getPriority()
                    + "|due=" + due
                    + "|title=" + titleEnc
                    + "|desc=" + descEnc;

            lines.add(line);
        }

        try {
            Files.write(DATA_PATH, lines, StandardCharsets.UTF_8);
            System.out.println("저장 완료! (" + DATA_PATH.toAbsolutePath() + ")");
        } catch (IOException e) {
            System.out.println("저장 실패: " + e.getMessage());
        }
    }

    private static int loadFromFile(ArrayList<Todo> todos) {
        if (!Files.exists(DATA_PATH)) {
            System.out.println("저장 파일이 없습니다. (" + DATA_PATH.toAbsolutePath() + ")");
            return calcNextId(todos);
        }

        try {
            List<String> lines = Files.readAllLines(DATA_PATH, StandardCharsets.UTF_8);

            todos.clear();
            int maxId = 0;

            for (String line : lines) {
                if (line == null) continue;
                line = line.trim();
                if (line.isEmpty()) continue;

                Todo t;

                // ✅ 새 포맷 판단: id= 포함이면 key=value 포맷
                if (line.contains("id=")) {
                    t = parseKeyValueTodo(line);
                    if (t == null) continue;
                } else {
                    // ✅ 구포맷 호환: id|done|title
                    String[] parts = line.split("\\|", 3);
                    if (parts.length < 3) continue;

                    int id = Integer.parseInt(parts[0]);
                    boolean done = Boolean.parseBoolean(parts[1]);
                    String title = parts[2];

                    t = new Todo(id, title); // 기본값: priority=3, due=null, desc=""
                    if (done) t.markDone();
                }

                todos.add(t);
                if (t.getId() > maxId) maxId = t.getId();
            }

            System.out.println("불러오기 완료! (" + todos.size() + "개)");
            return maxId + 1;

        } catch (IOException e) {
            System.out.println("불러오기 실패: " + e.getMessage());
            return calcNextId(todos);
        } catch (NumberFormatException e) {
            System.out.println("불러오기 실패(형식 오류): " + e.getMessage());
            return calcNextId(todos);
        }
    }

    private static int calcNextId(ArrayList<Todo> todos) {
        int maxId = 0;
        for (Todo t : todos) {
            if (t.getId() > maxId) maxId = t.getId();
        }
        return maxId + 1;
    }

    private static Todo parseKeyValueTodo(String line) {
        Integer id = null;
        boolean done = false;
        int priority = 3;
        LocalDate due = null;
        String title = "";
        String desc = "";

        String[] pairs = line.split("\\|");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx < 0) continue;

            String key = pair.substring(0, idx).trim();
            String value = pair.substring(idx + 1).trim();

            switch (key) {
                case "id" -> id = Integer.parseInt(value);
                case "done" -> done = Boolean.parseBoolean(value);
                case "priority" -> priority = Integer.parseInt(value);
                case "due" -> { if (!value.isEmpty()) due = LocalDate.parse(value); }
                case "title" -> title = decode(value);
                case "desc" -> desc = decode(value);
            }
        }

        if (id == null) return null;

        Todo t = new Todo(id, title, priority, due, desc);
        if (done) t.markDone();
        return t;
    }

    private static String encode(String s) {
        if (s == null) return "";
        s = s.replace("\n", " ").replace("\r", " ");
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String decode(String s) {
        if (s == null || s.isEmpty()) return "";
        return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    // OrbitRank: 점수가 높을수록 "안쪽 궤도(급함/중요)"로 보이게 할 기준
    private static int calcOrbitRank(Todo t) {
        // priority 기본 점수(1~5 → 10~50점)
        int score = t.getPriority() * 10;

        // 마감이 없으면 urgency 보너스 없음
        if (t.getDueDate() == null) return score;

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), t.getDueDate());

        // 마감 임박 보너스(룰은 나중에 조정 가능)
        if (daysLeft < 0) score += 100;       // 이미 지남(최우선)
        else if (daysLeft == 0) score += 80;  // 오늘 마감
        else if (daysLeft <= 1) score += 60;  // 내일/모레급
        else if (daysLeft <= 3) score += 40;
        else if (daysLeft <= 7) score += 20;
        // 그 이상은 보너스 0

        return score;
    }

    // 정렬 동점 처리용: dueDate가 없으면 가장 뒤로
    private static LocalDate dueOrMax(Todo t) {
        return (t.getDueDate() == null) ? LocalDate.MAX : t.getDueDate();
    }
}