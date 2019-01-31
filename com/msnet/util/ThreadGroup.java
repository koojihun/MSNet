package com.msnet.util;

import java.util.ArrayList;

public class ThreadGroup {
	public static ArrayList<Thread> thread_group = new ArrayList<>();
	
	public ThreadGroup() {}
	
	public static void addThread(Thread t) {
		thread_group.add(t);
		t.start();
	}
	
	public static void destroyThread(Thread t) {
		t.interrupt();
	}
	
	public static void destroyAllThread() {
		for (Thread t : thread_group) {
			if (t.isAlive())
				t.interrupt();
		}
	}
}
