import java.util.ArrayList;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.IOException;

public class Main {
    private static final String DATA_FILE = "todos.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Todo> todos = new ArrayList<>();
        int nextId = 1;

        while (true) {
            printMenu();
            int choice = readInt(sc, "선택: ");

            if (choice == 1) { // 추가
                String title = readNonEmptyLine(sc, "할 일 제목: ");
                todos.add(new Todo(nextId, title));
                System.out.println("추가 완료! (id=" + nextId + ")");
                nextId++;

            } else if (choice == 2) { // 목록
                printTodos(todos);

            } else if (choice == 3) { // 완료처리
                int id = readInt(sc, "완료할 id 입력: ");
                Todo t = findById(todos, id);
                if (t == null) {
                    System.out.println("해당 id를 찾을 수 없습니다.");
                } else {
                    if(t.isDone()) {
                        System.out.println("이미 완료된 항목입니다!");
                    }
                    else{
                        t.markDone();
                        System.out.println("완료 처리됨!");
                    }
                }

            } else if (choice == 4) { // 삭제
                int id = readInt(sc, "삭제할 id 입력: ");
                boolean removed = removeById(todos, id);
                if (removed) {
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
                    System.out.println("수정 완료!");
                }
            } else if (choice == 6) { // 검색
                String keyword = readNonEmptyLine(sc, "검색 키워드 입력: ");
                boolean found = false;

                System.out.println("=== 검색 결과 ===");
                for (Todo todo : todos) {
                    if (todo.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                        todo.print();
                        found = true;
                    }
                }

                if (!found) {
                    System.out.println("검색 결과가 없습니다.");
                }
            } else if (choice == 7) { // 저장
                saveToFile(todos);

            } else if (choice == 8) { // 불러오기
                nextId = loadFromFile(todos); // ★ 불러온 뒤 nextId 보정

            } else if (choice == 9) { // 종료
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
        System.out.println("\n=== Todo 메뉴 ===");
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

    // ====== 입력 유틸 (버퍼/nextLine 문제 방지: 전부 nextLine 기반) ======
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

    private static String readNonEmptyLine(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            if (!line.isBlank()) return line.trim();
            System.out.println("빈 값은 안 돼. 다시 입력해줘.");
        }
    }

    // ====== 기능 메서드 ======
    private static void printTodos(ArrayList<Todo> todos) {
        if (todos.isEmpty()) {
            System.out.println("할 일이 없습니다.");
            return;
        }

        System.out.println("=== 할 일 목록 (미완료 먼저) ===");

        // 완료 개수 먼저 계산(요약용)
        int done = 0;
        for (Todo t : todos) {
            if (t.isDone()) done++;
        }

        // 1) 미완료 먼저 출력
        for (Todo t : todos) {
            if (!t.isDone()) {
                t.print();
            }
        }

        // 2) 완료 출력
        for (Todo t : todos) {
            if (t.isDone()) {
                t.print();
            }
        }

        int total = todos.size();
        int remaining = total - done;
        int percent = (total == 0) ? 0 : (done * 100 / total);

        System.out.println("총 " + total + "개 / 완료 " + done + "개 / 남은 " + remaining + "개 (" + percent + "%)");
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

    private static void saveToFile(ArrayList<Todo> todos) {
        java.util.ArrayList<String> lines = new java.util.ArrayList<>();

        // 한 줄 = id|done|title
        for (Todo t : todos) {
            String safeTitle = t.getTitle().replace("\n",  " ").replace("\r", " ");
            lines.add(t.getId() + "|" + t.isDone() + "|" + safeTitle);
        }

        try {
            java.nio.file.Files.write(
                    java.nio.file.Path.of(DATA_FILE),
                    lines,
                    java.nio.charset.StandardCharsets.UTF_8
            );
            System.out.println("저장 완료! (" + DATA_FILE + ")");
        } catch (java.io.IOException e) {
            System.out.println("저장 실패: " + e.getMessage());
        }
    }

    private static int loadFromFile(ArrayList<Todo> todos) {
        java.nio.file.Path path = java.nio.file.Path.of(DATA_FILE);

        if (!java.nio.file.Files.exists(path)) {
            System.out.println("저장 파일이 없습니다. (" + DATA_FILE + ")");
            return calcNextId(todos);
        }

        try {
            java.util.List<String> lines =
                    java.nio.file.Files.readAllLines(path, java.nio.charset.StandardCharsets.UTF_8);

            todos.clear(); // ★ 기존 목록 제거(중복 방지)
            int maxId = 0;

            for (String line : lines) {
                if (line == null) continue;
                line = line.trim();
                if (line.isEmpty()) continue;

                // ★ limit=3: title에 |가 있어도 안전
                String[] parts = line.split("\\|", 3);
                if (parts.length < 3) continue;

                int id = Integer.parseInt(parts[0]);
                boolean done = Boolean.parseBoolean(parts[1]);
                String title = parts[2];

                Todo t = new Todo(id, title);
                if (done) t.markDone();

                todos.add(t);
                if (id > maxId) maxId = id;
            }

            System.out.println("불러오기 완료! (" + todos.size() + "개)");
            return maxId + 1;

        } catch (java.io.IOException e) {
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
}