package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * UDPのユニキャスト送信をするクラスです。
 */
public class UDPUnicastSender {
	
	// 送信先
	private static final String DEST_ADDR = "localhost";
	public static final int DEST_PORT = 8080;

	public static void main(String[] args) throws SocketException {
		// ソケットの作成
		try (
				var consoleStreamReader = new InputStreamReader(System.in);
				var consoleReader = new BufferedReader(consoleStreamReader)) {

			while (true) {
				// 標準入力から読み込む
				System.out.println("メッセージの送信をやめる場合はエンターを入力してください。");
				System.out.print("メッセージ：");
				var msg = consoleReader.readLine();
				if (msg == null || msg.isEmpty()) {
					break;
				}

				// 送信するパケットを作成する
				byte[] msgBytes = msg.getBytes();
				DatagramPacket packet = new DatagramPacket(
						msgBytes, msgBytes.length,
						new InetSocketAddress(DEST_ADDR, DEST_PORT));
				
				// 送信する
				try (DatagramSocket socket = new DatagramSocket()) {
					socket.send(packet);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
