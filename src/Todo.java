public class Todo {
    private int id;
    private String title;
    private boolean done;

    public Todo(int id, String title) {
        this.id = id;
        this.title = title;
        this.done = false;
    }

    public int getId() {
        return id;
    }

    public boolean isDone() {
        return done;
    }

    public void markDone() {
        this.done = true;
    }

    public void print() {
        String status = done ? "완료" : "미완료";
        System.out.println(id + ". [" + status + "] " + title);
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

}