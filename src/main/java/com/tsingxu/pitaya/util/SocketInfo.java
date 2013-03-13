package com.tsingxu.pitaya.util;

import java.net.ServerSocket;
import java.net.Socket;

public final class SocketInfo
{
	public static String getRemoteAddressAndPort(Socket socket)
	{
		if (socket == null)
		{
			return null;
		}
		return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}

	public static String getRemoteAddressAndPort(ServerSocket socket)
	{
		if (socket == null)
		{
			return null;
		}
		return socket.getInetAddress().getHostName() + ":" + socket.getLocalPort();
	}
}
