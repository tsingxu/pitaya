package com.tsingxu.pitaya.util;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * <b>the tool of socket info</b>
 * 
 * <ol>
 * <li>...</li>
 * </ol>
 * 
 * @since Mar 13, 2013 3:08:04 PM
 * @author xuhuiqing
 */
public final class SocketInfo {
	public static String getRemoteAddressAndPort(Socket socket) {
		if (socket == null) {
			return null;
		}
		return socket.getInetAddress().getHostAddress() + ":"
				+ socket.getPort();
	}

	public static String getRemoteAddressAndPort(ServerSocket socket) {
		if (socket == null) {
			return null;
		}
		return socket.getInetAddress().getHostName() + ":"
				+ socket.getLocalPort();
	}
}
