package com.tsingxu.pitaya.reactor;

import java.util.ArrayList;

public class NIOReactorPool
{
	private ArrayList<NIOReactor> reactors = new ArrayList<NIOReactor>();

	private static final NIOReactorPool instance = new NIOReactorPool();

	private NIOReactorPool()
	{
		reactors.clear();
	}

	public static NIOReactorPool getInstance()
	{
		return instance;
	}

	public void addReactor(NIOReactor reactor)
	{
		if (reactor == null)
		{
			return;
		}
		synchronized (reactors)
		{
			reactors.add(reactor);
		}
	}

	public NIOReactor getReactorEvenly()
	{
		synchronized (reactors)
		{
			int selected = (int) (Math.random() * reactors.size());
			return reactors.get(selected);
		}
	}
}
