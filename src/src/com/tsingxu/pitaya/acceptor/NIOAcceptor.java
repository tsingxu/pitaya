package com.tsingxu.pitaya.acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

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
			selector = null;
			listener = null;
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

		SelectionKey key;
		while (true)
		{
			try
			{
				selector.select(3000);
				Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
				for (; ite.hasNext();)
				{
					key = ite.next();
					ite.remove();

					if (key.isAcceptable())
					{
						ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
						SocketChannel client = ssc.accept();
						client.configureBlocking(false);

						try
						{
							NIOReactor reactor = NIOReactorPool.getInstance().getReactorEvenly();
							if (reactor != null)
							{
								logger.info("receive new client "
										+ client.socket().getInetAddress().getHostAddress() + ":"
										+ client.socket().getPort());
								reactor.register(client);
							}
							else
							{
								client.close();
							}
						}
						catch (ClosedChannelException e)
						{
							logger.error("client " + client.socket().getInetAddress() + " closed",
									e);
							client.close();
						}
					}
				}
			}
			catch (IOException e)
			{
				logger.error("select fail", e);
			}
		}

	}
}
