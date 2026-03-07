import java.time.LocalDate;

public class Todo {
    private int id;
    private String title;
    private boolean done;
    private int priority;      // 1~5
    private LocalDate dueDate; // 없으면 null
    private String description; // 없으면 ""

    public Todo(int id, String title) {
        this(id, title, 3, null, "");
    }

    public Todo(int id, String title, int priority, LocalDate dueDate, String description) {
        this.id = id;
        this.title = title;
        this.done = false;
        this.priority = priority;
        this.dueDate = dueDate;
        this.description = (description == null) ? "" : description;
    }

    public int getId() {
        return id;
    }

    public boolean isDone() {
        return done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description;
    }

    public void markDone() {
        this.done = true;
    }

    public void print() {
        String status = done ? "완료" : "미완료";
        String dueStr = (dueDate == null) ? "-" : dueDate.toString();
        System.out.println(id + ". [" + status + "] (P" + priority + ", DUE " + dueStr + ") " + title);
    }

}