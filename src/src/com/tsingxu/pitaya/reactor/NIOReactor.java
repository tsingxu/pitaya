package com.tsingxu.pitaya.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

public class NIOReactor implements Runnable
{
	private Selector selector;
	private static final Logger logger = Logger.getLogger(NIOReactor.class);
	private static int BUFF_SIZE = 10240;
	private final Queue<SocketChannel> q = new LinkedList<SocketChannel>();

	public NIOReactor()
	{
		try
		{
			selector = Selector.open();
		}
		catch (IOException e)
		{
			logger.error("open selector fail", e);
			selector = null;
		}
		q.clear();
	}

	public void register(SocketChannel sc) throws ClosedChannelException
	{
		if (selector == null)
		{
			return;
		}
		q.offer(sc);
		selector.wakeup();
	}

	@Override
	public void run()
	{
		if (selector == null)
		{
			return;
		}

		SelectionKey key;
		ByteBuffer buff = ByteBuffer.allocate(BUFF_SIZE);
		while (true)
		{
			try
			{
				selector.select(600L);

				process();

				for (Iterator<SelectionKey> ite = selector.selectedKeys().iterator(); ite.hasNext();)
				{
					key = ite.next();
					SocketChannel sc = (SocketChannel) key.channel();
					try
					{
						buff.clear();
						if (sc.read(buff) == -1)
						{
							throw new IOException("read -1");
						}
						buff.flip();
						sc.write(buff);
					}
					catch (IOException e)
					{
						System.err.println("disconnect with "
								+ sc.socket().getRemoteSocketAddress());
						logger.error("I/O error", e);
						key.cancel();
						sc.close();
					}
				}
				selector.selectedKeys().clear();
			}
			catch (Exception e)
			{
				logger.error("select fail", e);
			}
		}

	}

	private void process()
	{
		if (!q.isEmpty())
		{
			SocketChannel sc = null;
			while ((sc = q.poll()) != null)
			{
				try
				{
					sc.register(selector, SelectionKey.OP_READ);
				}
				catch (ClosedChannelException e)
				{
					System.err.println("disconnect with " + sc.socket().getRemoteSocketAddress());
					try
					{
						sc.close();
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
