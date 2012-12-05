import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class EchoProtocolHandler extends IoHandlerAdapter
{
	public void messageReceived(IoSession session, Object msg)
	{
		IoBuffer buff = (IoBuffer) msg;

		byte[] data = new byte[buff.limit()];
		buff.get(data);
		System.out.print(new String(data));
		buff.clear();
		buff.put(data);
		buff.flip();
		session.write(buff);
	}
}