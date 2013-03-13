package com.tsingxu.pitaya.reactor;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>the reactor pool</b>
 * 
 * <ol>
 * <li>...</li>
 * </ol>
 * 
 * @since Mar 13, 2013 3:07:48 PM
 * @author xuhuiqing
 */
public final class NIOReactorPool {
	private final ArrayList<NIOReactor> reactors = new ArrayList<NIOReactor>();
	private static final NIOReactorPool instance = new NIOReactorPool();
	private final AtomicInteger index = new AtomicInteger(0);

	private NIOReactorPool() {
		reactors.clear();
	}

	public static NIOReactorPool getInstance() {
		return instance;
	}

	public void addReactor(NIOReactor reactor) {
		if (reactor == null) {
			return;
		}
		synchronized (reactors) {
			reactors.add(reactor);
		}
	}

	public NIOReactor getReactorEvenly() {
		return reactors.get(index.incrementAndGet() % reactors.size());
	}
}
