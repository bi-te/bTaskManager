package ua.edu.sumdu.j2se.obolonsky.tasks;

public class Main {
    public static void main(String[] args) {
        Task t = new Task("t", 12);
        Task g = new Task("g", 13);
        LinkedTaskList tasks = new LinkedTaskList();
        tasks.add(t);
        tasks.add(g);
        tasks.add(new Task("k", 14));
        tasks.add(new Task("l", 15));
        tasks.add(new Task("j", 16, 23, 2));

        System.out.println(tasks);
    }
}
