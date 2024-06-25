package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

/**
 * TCP通信を行うサーバサイドクラスです。
 */
public class TCPMultiEchoServer {

	/**
	 * 実行します。
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// スレッドプールを生成
		var threadPool = Executors.newCachedThreadPool();
		
		// サーバインスタンスの生成
		try (var serverSocket = new ServerSocket(8080)) {

			// 繰り返し処理を行う
			while (!serverSocket.isClosed()) {
				
				// 接続要求を受け付ける
				final var acceptedSocket = serverSocket.accept();
				
				// 受付後はスレッドプールで処理
				threadPool.execute(() -> {
					// 概要表示
					SocketAddress remoteAddr = acceptedSocket.getRemoteSocketAddress();
					if (remoteAddr instanceof InetSocketAddress inetRemoteAddr) {						
						System.out.println(
								inetRemoteAddr.getHostName() + ":" + inetRemoteAddr.getPort() + " との通信を始めます。");
					}
					
					// 受信ストリームと送信ストリームを取得
					try (
							// 受信ストリームを取得
							var inputStream = acceptedSocket.getInputStream();
							var inputStreamReader = new InputStreamReader(inputStream);
							var reader = new BufferedReader(inputStreamReader);
							// 送信ストリームを取得
							var outputStream = acceptedSocket.getOutputStream();
							var outputStreamWriter = new OutputStreamWriter(outputStream);
							var writer = new BufferedWriter(outputStreamWriter);) {
						// メッセージを受信しては出力する
						String msg = null;
						while ((msg = reader.readLine()) != null) {
							System.out.println("受信メッセージ：" + msg);
							
							// 受信したことの返信
							writer.write(msg);
							writer.newLine();
							writer.flush();
						}

						// シャットダウン
						if (remoteAddr instanceof InetSocketAddress inetRemoteAddr) {						
							System.out.println(
									inetRemoteAddr.getHostName() + ":" + inetRemoteAddr.getPort() + " との通信を終了します。");
						}
						acceptedSocket.shutdownInput();
						acceptedSocket.shutdownOutput();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}
	}
}
