import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class test
{
	public static void main(String[] args) throws IOException
	{
		SocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.setHandler(new EchoProtocolHandler());
		acceptor.bind(new InetSocketAddress(8080));
		System.out.println("Listening on port " + 8080);
	}
}
