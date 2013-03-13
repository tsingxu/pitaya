package com.tsingxu.pitaya.util;

import java.util.concurrent.atomic.AtomicInteger;

public class NioAcceptorThreadRename implements Runnable {
	final Runnable r;
	static final AtomicInteger index = new AtomicInteger(0);

	public NioAcceptorThreadRename(Runnable run) {
		r = run;
	}

	@Override
	public void run() {
		Thread.currentThread().setName("NioRector-" + index.incrementAndGet());
		r.run();
	}

}
