package com.tsingxu.pitaya.acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
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
		int count;
		while (true)
		{
			try
			{
				count = selector.select(10);

				if (count > 0)
				{
					Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
					while (ite.hasNext())
					{
						key = ite.next();
						ite.remove();

						if (key.isAcceptable())
						{
							logger.fatal("1");
							ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
							SocketChannel client = ssc.accept();
							client.configureBlocking(false);
							client.socket().setSoLinger(true, 0);
							client.socket().setReuseAddress(true);// 重用地址
							client.socket().setSoLinger(true, 0);// 设置断链的时候服务端强行关闭连接，立即释放TCP缓冲区数据
							client.socket().setReceiveBufferSize(256 * 1024);// 将底层buffer开大一些
							client.socket().setSendBufferSize(256 * 1024);

							client.register(selector, SelectionKey.OP_READ);
							logger.fatal("receive new client "
									+ client.socket().getInetAddress().getHostAddress() + ":"
									+ client.socket().getPort());
							logger.fatal("2");
						}
						else if (key.isReadable())
						{
							NIOReactor reactor = NIOReactorPool.getInstance().getReactorEvenly();
							reactor.register((SocketChannel) key.channel());
							key.cancel();
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
