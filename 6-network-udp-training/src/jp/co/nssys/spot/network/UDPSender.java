package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * UDPデータを送信するクラスです。
 */
public class UDPSender {

	public static void main(String[] args) {
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
						new InetSocketAddress("localhost", 8080));
				
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
