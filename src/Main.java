import java.util.ArrayList;
import java.util.Scanner;

public class Main {

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
            } else if (choice == 6) { // 제목 수정
                int id = readInt(sc, "수정할 id 입력: ");
                Todo t = findById(todos, id);

                if (t == null) {
                    System.out.println("해당 id를 찾을 수 없습니다.");
                } else {
                    String newTitle = readNonEmptyLine(sc, "새 제목 입력: ");
                    t.setTitle(newTitle);
                    System.out.println("수정 완료!");
                }

            } else if (choice == 5) { // 종료
                System.out.println("종료합니다.");
                break;

            } else {
                System.out.println("잘못된 선택입니다. (1~5)");
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
        System.out.println("5) 종료");
        System.out.println("6) 제목 수정");
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
        System.out.println("=== 할 일 목록 ===");

        int done = 0;
        for (Todo t : todos) {
            if(t.isDone()) done++;
            t.print();
        }

        int total = todos.size();
        int remaining = total - done;
        int percent = (total == 0) ? 0 : (done *100/ total);

        System.out.println("총 "+total+"개 / 완료 "+done+"개 / 남은 "+ remaining + "개 (" + percent + "%)");
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
}