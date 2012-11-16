package com.tsingxu.pitaya.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.tsingxu.pitaya.util.SocketInfo;

public class NIOReactor implements Runnable
{
	private Selector selector;
	private static final Logger logger = Logger.getLogger(NIOReactor.class);

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
	}

	public void register(SocketChannel sc) throws ClosedChannelException
	{
		if (selector == null)
		{
			return;
		}
		sc.register(selector, SelectionKey.OP_READ);
	}

	@Override
	public void run()
	{
		if (selector == null)
		{
			return;
		}

		SelectionKey key;
		ByteBuffer buff = ByteBuffer.allocate(10240);
		while (true)
		{
			try
			{
				selector.select(2000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();

				if (selectedKeys.isEmpty())
				{
					for (Iterator<SelectionKey> ite = selector.keys().iterator(); ite.hasNext();)
					{
						key = ite.next();
						SocketChannel sc = (SocketChannel) key.channel();

						try
						{
							buff.clear();
							buff.put("heartbeat\n\r".getBytes());
							buff.flip();
							respond(sc, buff);
						}
						catch (IOException e)
						{
							logger.warn(SocketInfo.getRemoteAddressAndPort(sc.socket())
									+ " write fail ", e);
							closeChannel(sc);
							key.cancel();
						}
					}
				}
				else
				{
					for (Iterator<SelectionKey> ite = selectedKeys.iterator(); ite.hasNext();)
					{
						key = ite.next();
						ite.remove();

						if (key.isReadable())
						{
							SocketChannel sc = (SocketChannel) key.channel();
							try
							{
								readData(sc, buff);
								buff.put("\n".getBytes());
								buff.flip();
								respond(sc, buff);
							}
							catch (IOException e)
							{
								logger.warn(SocketInfo.getRemoteAddressAndPort(sc.socket())
										+ " read fail ", e);
								closeChannel(sc);
								key.cancel();
							}
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

	private void readData(SocketChannel sc, ByteBuffer buff) throws IOException
	{
		buff.clear();
		sc.read(buff);
	}

	private void closeChannel(SocketChannel sc)
	{
		try
		{
			logger.error("channel closed " + SocketInfo.getRemoteAddressAndPort(sc.socket()));
			sc.close();
		}
		catch (IOException e)
		{
			logger.error("close channel fail", e);
		}
	}

	private void respond(SocketChannel sc, ByteBuffer buff) throws IOException
	{
		sc.write(buff);
	}
}
