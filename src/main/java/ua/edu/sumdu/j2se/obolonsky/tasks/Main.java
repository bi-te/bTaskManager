package ua.edu.sumdu.j2se.obolonsky.tasks;

public class Main {

	public static void main(String[] args) {
		Task t = new Task("t", 12);
		t.setActive(true);
		Task g = new Task("g", 13);
		ArrayTaskList tasks = new ArrayTaskList();
		tasks.add(t);
		tasks.add(g);
		tasks.remove(null);
		tasks.remove(g);
		System.out.println(tasks.incoming(-10, -3));

	}
}
