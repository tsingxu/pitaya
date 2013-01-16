package com.tsingxu.pitaya.acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.tsingxu.pitaya.reactor.NIOReactor;
import com.tsingxu.pitaya.reactor.NIOReactorPool;
import com.tsingxu.pitaya.util.SocketInfo;

public class NIOAcceptor implements Runnable
{
	private Selector selector;
	private ServerSocketChannel listener;
	private static final Logger logger = Logger.getLogger(NIOAcceptor.class);

	public NIOAcceptor(int port)
	{
		this(port, null);
	}

	public NIOAcceptor()
	{
		this(8080);
	}

	public NIOAcceptor(int port, String ip)
	{
		try
		{
			listener = ServerSocketChannel.open();
			selector = Selector.open();
			listener.configureBlocking(false);

			if (ip == null || ip.trim().equals(""))
			{
				listener.socket().bind(new InetSocketAddress(port));
			}
			else
			{
				listener.socket().bind(new InetSocketAddress(ip, port));
			}

			listener.register(selector, SelectionKey.OP_ACCEPT);
		}
		catch (IOException e)
		{
			logger.error("listen on " + (ip != null ? ip : "") + " " + port + " fail ", e);
			System.exit(-1);
		}

		String msg = "listen on " + SocketInfo.getRemoteAddressAndPort(listener.socket())
				+ " succeed ";
		logger.info(msg);
		System.out.println(msg);
	}

	@Override
	public void run()
	{
		if (selector == null || listener == null)
		{
			return;
		}

		while (true)
		{
			try
			{
				selector.select();
				selector.selectedKeys().clear();

				for (;;)
				{
					SocketChannel acceptedSocket = listener.accept();
					if (acceptedSocket == null)
					{
						break;
					}
					acceptedSocket.configureBlocking(false);
					acceptedSocket.socket().setSoLinger(true, 0);
					acceptedSocket.socket().setReuseAddress(true);// 重用地址
					NIOReactor reactor = NIOReactorPool.getInstance().getReactorEvenly();
					System.err.println("start to register");
					reactor.register(acceptedSocket);
					System.err.println("register");
				}

			}
			catch (IOException e)
			{
				logger.error("select fail", e);
			}
		}

	}
}
