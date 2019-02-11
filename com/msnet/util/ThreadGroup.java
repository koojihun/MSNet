package com.msnet.util;

import java.util.HashMap;

public class ThreadGroup {
	public static HashMap<Long, Thread> thread_group = new HashMap<>();
	
	public ThreadGroup() {}
	
	public static void addThread(Thread t) {
		thread_group.put(t.getId(), t);
		t.start();
	}
	
	public static void destroyThread(Thread t) {
		t.interrupt();
	}
	
	public static void destroyAllThread() {
		for (Long tid : thread_group.keySet()) {
			Thread t = thread_group.get(tid);
			if (t.isAlive())
				t.interrupt();
		}
	}
}
