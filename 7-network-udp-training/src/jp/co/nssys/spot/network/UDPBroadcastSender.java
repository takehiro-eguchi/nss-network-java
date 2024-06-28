package jp.co.nssys.spot.network;

import java.net.InetAddress;
import java.net.SocketException;

/**
 * UDPのユニキャスト送信をするクラスです。
 */
public class UDPBroadcastSender {

	public static void main(String[] args) throws SocketException {
		// TODO: ブロードキャストアドレス作成
		InetAddress broadcastAddr = getBroadcastAddress();
			
		// TODO: ソケットの作成（ブロードキャストモードをON）
		
		// TODO: メッセージの作成
		
		// TODO: 送信するパケットの作成
		
		// TODO: ソケットを用いてパケットを送信
	}

	/** ブロードキャストアドレスを取得します */
	private static InetAddress getBroadcastAddress() throws SocketException {
		// TODO: いずれかの方法で算出が可能です。（他にもあると思います）
		//   * 自身で求めたものをハードコード
		//   * 自身のアドレスとサブネットマスクより求める
		//   * ネットワークインタフェースを取得することにより取得
		return null;
	}
}
