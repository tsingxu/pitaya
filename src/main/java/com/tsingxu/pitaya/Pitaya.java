package com.tsingxu.pitaya;

import com.tsingxu.pitaya.acceptor.NIOAcceptor;
import com.tsingxu.pitaya.reactor.NIOReactor;
import com.tsingxu.pitaya.reactor.NIOReactorPool;

public class Pitaya
{
	public static void main(String[] args)
	{
		if (args.length != 1 && args.length != 2)
		{
			System.out.println("Usage: \tpitaya ip port\n\tpitaya port");
			return;
		}

		int reactorSize = Runtime.getRuntime().availableProcessors();

		NIOReactor reactor;

		reactor = new NIOReactor();

		for (int i = 0; i < reactorSize; i++)
		{
			reactor = new NIOReactor();
			NIOReactorPool.getInstance().addReactor(reactor);
			new Thread(reactor).start();
		}

		if (args.length == 1)
		{
			new Thread(new NIOAcceptor(Integer.valueOf(args[0]))).start();
		}
		else
		{
			new Thread(new NIOAcceptor(Integer.valueOf(args[1]), args[0])).start();
		}

	}
}
