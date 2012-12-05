package com.tsingxu.pitaya.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.tsingxu.pitaya.util.SocketInfo;

public class NIOReactor implements Runnable
{
	private Selector selector;
	private static final Logger logger = Logger.getLogger(NIOReactor.class);
	private static int BUFF_SIZE = 10240;

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

	class handler
	{
		byte[] buff_ = new byte[BUFF_SIZE];

		public void process(ByteBuffer buff)
		{
			buff.get(buff_, 0, buff.limit());
			buff.position(0);
			System.out.print(new String(buff_, 0, buff.limit()));
		}
	}

	public void register(SocketChannel sc) throws ClosedChannelException
	{
		logger.fatal("3");
		if (selector == null)
		{
			return;
		}

		sc.register(selector, SelectionKey.OP_READ, new handler());
		logger.fatal("4");
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
		int count;
		int idleTime = 0;
		while (true)
		{
			try
			{
				count = selector.select(600L);
				if (count <= 0 && idleTime >= 5)
				{
					idleTime = 0;
					for (Object obj : selector.keys().toArray())
					{
						key = (SelectionKey) obj;
						SocketChannel sc = (SocketChannel) key.channel();

						if (!key.isValid())
						{
							key.cancel();
							continue;
						}

						try
						{
							buff.clear();
							buff.put("heartbeat".getBytes());
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
				else if (count <= 0 && idleTime < 5)
				{
					idleTime++;
				}
				else
				{
					idleTime = 0;
					for (Iterator<SelectionKey> ite = selector.selectedKeys().iterator(); ite
							.hasNext();)
					{
						key = ite.next();
						handler han = (handler) key.attachment();
						ite.remove();

						if (!key.isValid())
						{
							key.cancel();
							continue;
						}

						if (key.isReadable())
						{
							SocketChannel sc = (SocketChannel) key.channel();
							try
							{
								readData(sc, buff);
								han.process(buff);
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
			catch (Exception e)
			{
				logger.error("select fail", e);
			}
		}

	}

	private void readData(SocketChannel sc, ByteBuffer buff) throws IOException
	{
		buff.clear();
		if (sc.read(buff) == -1)
		{
			throw new IOException("read -1");
		}
		buff.flip();
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
