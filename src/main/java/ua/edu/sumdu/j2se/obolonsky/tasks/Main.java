package ua.edu.sumdu.j2se.obolonsky.tasks;

public class Main {

	public static void main(String[] args) {
		Task t = new Task("t", 12);
		Task g = new Task("t", 12);
		System.out.println(t.equals(g));
		System.out.println(t.hashCode());
		System.out.println(g.hashCode());

	}
}
