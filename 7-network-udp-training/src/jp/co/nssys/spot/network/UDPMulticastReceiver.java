package jp.co.nssys.spot.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * UDPデータのマルチキャスト受信を行うクラスです。
 */
public class UDPMulticastReceiver {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		// 受信ソケットの作成
		try (MulticastSocket socket = new MulticastSocket(UDPMulticastSender.MULTI_PORT)) {
			// マルチキャストアドレスに参加
			var multicastAddr = InetAddress.getByName(UDPMulticastSender.MULTI_ADDR);
			socket.joinGroup(multicastAddr);
			
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
			
			// マルチキャストアドレスから除去
//			socket.leaveGroup(multicastAddr);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
