import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class DHTClient {

	// Simple argument checking
	public static void main(String args[]) throws IOException {
		if (args.length < 5 || args.length > 7) {
			System.err.println("Usage error");
			return;
		}

		// Creating local and peer socket addresses
		InetSocketAddress source = new InetSocketAddress(args[0],
				Integer.parseInt(args[1]));
		InetSocketAddress dest = new InetSocketAddress(args[2],
				Integer.parseInt(args[3]));

		// Creating socket and binding it to the local address
		// Also connecting it to the peer address
		DatagramSocket socket = new DatagramSocket(null);
		socket.bind(source);

		byte[] message = null;

		if (args[4].equals("PING") || args[4].equals("GETSUCCESSOR")) {
			message = (args[4] + "\n").getBytes();
		} else if (args[4].equals("PUT")) {
			message = (args[4] + "\nIP:" + args[0] + "\nPORT:" + args[1]
					+ "\nVALUE:" + args[5] + "\n").getBytes();
		} else if (args[4].equals("GET")) {
			message = (args[4] + "\nIP:" + args[0] + "\nPORT:" + args[1]
					+ "\nKEY:" + args[5] + "\n").getBytes();
		}

		message = concatenateByteArrays(intToBytes(message.length), message);

		// Create the packet
		DatagramPacket sendPacket = new DatagramPacket(message, message.length,
				dest);

		// Send the packet
		socket.send(sendPacket);
		System.out.print(byteArrayToInt(Arrays.copyOfRange(message, 0, 4))
				+ "\n" + new String(message, 4, message.length - 4));

		// Create the response
		byte[] response = new byte[8192];
		DatagramPacket receivePacket = new DatagramPacket(response,
				response.length);

		// Receive the response
		socket.setSoTimeout(5000);
		try {
			socket.receive(receivePacket);
		} catch (SocketTimeoutException retry) {
			System.out.print("RETRYING\n");
			socket.send(sendPacket);
			socket.setSoTimeout(10000);
			try {
				socket.receive(receivePacket);
			} catch (SocketTimeoutException exit) {
				System.exit(1);
			}
		}

		System.out.print(receivePacket.getLength()-4 + "\n"
				+ new String(response, 4, receivePacket.getLength() - 4));

		socket.close();
		}

	private static byte[] intToBytes(int n) {
		/**
		 * This method ignores the fact Java ints are signed, and the problem
		 * asked for unsigned. Unlikely to have UDP packets > 2^31 bytes :) so
		 * it's OK.
		 */
		ByteBuffer b = ByteBuffer.allocate(4);
		b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a
										// byte buffer is always BIG_ENDIAN.
		b.putInt(n);
		return b.array();
	}

	public static int byteArrayToInt(byte[] b) {
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.BIG_ENDIAN);
		return bb.getInt();
	}

	private static byte[] concatenateByteArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
}
