package com.tsingxu.pitaya.util;

import java.util.concurrent.atomic.AtomicInteger;

public class NioRectorThreadRename implements Runnable {
	final Runnable r;
	static final AtomicInteger index = new AtomicInteger(0);

	public NioRectorThreadRename(Runnable run) {
		r = run;
	}

	@Override
	public void run() {
		Thread.currentThread()
				.setName("NioAcceptor-" + index.incrementAndGet());
		r.run();
	}

}
