package jp.co.nssys.spot.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * UDPデータの受信を行うクラスです。
 */
public class UDPReceiver {

	public static void main(String[] args) {
		// 受信ソケットの作成
		try (DatagramSocket socket = new DatagramSocket(8080)) {
			while (true) {
				// 受信用のパケットを作成する
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				
				// 受信する
				socket.receive(packet);
				
				// 内容の表示
				byte[] data = packet.getData();
				String dataStr = new String(data, 0, packet.getLength());
				var srcAddr = packet.getAddress();
				System.out.println(
						srcAddr.getHostName() + ":" + packet.getPort() + " より [" + dataStr + "] を受信しました。");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
