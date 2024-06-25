package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * UDPのユニキャスト送信をするクラスです。
 */
public class UDPBroadcastSender {

	public static void main(String[] args) throws UnknownHostException, SocketException {
		// ブロードキャストアドレス取得
		InetAddress broadcastAddr = getBroadcastAddress();
			
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
						new InetSocketAddress(broadcastAddr, UDPUnicastSender.DEST_PORT));
				
				// 送信する
				try (DatagramSocket socket = new DatagramSocket()) {
					socket.send(packet);
					System.out.println(packet.getAddress() + ":" + packet.getPort() + "にデータを送信しました。");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** ブロードキャストアドレスを取得します */
	private static InetAddress getBroadcastAddress() throws SocketException {
		var netInterfaces = NetworkInterface.getNetworkInterfaces();
		while (netInterfaces.hasMoreElements()) {
			// ネットワークインタフェースを取得
			var netInterface = netInterfaces.nextElement();
			
			// ループバックアドレスやリンクダウンしているものは除外
			if (netInterface.isLoopback() || !netInterface.isUp()) {
				continue;
			}
			
			// ブロードキャストアドレスを持つものを検索する
			var broadcastAddr = netInterface.getInterfaceAddresses().stream()
					.map(addr -> addr.getBroadcast())
					.filter(bAddr -> bAddr != null)
					.findFirst();
			if (broadcastAddr.isPresent()) {
				return broadcastAddr.get();
			}
		}
		return null;
	}
}
