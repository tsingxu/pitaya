package com.tsingxu.pitaya;

import com.tsingxu.pitaya.acceptor.NIOAcceptor;
import com.tsingxu.pitaya.reactor.NIOReactor;
import com.tsingxu.pitaya.reactor.NIOReactorPool;
import com.tsingxu.pitaya.util.NioAcceptorThreadRename;
import com.tsingxu.pitaya.util.NioRectorThreadRename;

public class Pitaya {
	public static void main(String[] args) {
		if (args.length != 1 && args.length != 2) {
			System.out.println("Usage: \tpitaya ip port\n\tpitaya port");
			return;
		}

		final int reactorSize = Runtime.getRuntime().availableProcessors();
		NIOReactor reactor;

		for (int i = 0; i < reactorSize + 1; i++) {
			reactor = new NIOReactor();
			new Thread(new NioRectorThreadRename(reactor)).start();
			NIOReactorPool.getInstance().addReactor(reactor);
		}

		if (args.length == 1) {
			new Thread(new NioAcceptorThreadRename(new NIOAcceptor(Integer.valueOf(args[0])))).start();
			System.out.println("listen on port " + Integer.valueOf(args[0]));
		} else {
			new Thread(new NIOAcceptor(Integer.valueOf(args[1]), args[0]))
					.start();
			System.out.println("listen on ip " + Integer.valueOf(args[0])
					+ " port " + Integer.valueOf(args[1]));
		}

	}
}
