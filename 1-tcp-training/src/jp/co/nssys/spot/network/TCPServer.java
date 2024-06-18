package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP通信を行うサーバサイドクラスです。
 */
public class TCPServer {

	/**
	 * 実行します。
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// サーバインスタンスの生成
		try (var serverSocket = new ServerSocket(8080)) {

			// 接続要求を待機し、処理を行う
			Socket acceptedSocket = null;
			while ((acceptedSocket = serverSocket.accept()) != null) {
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
					var line = reader.readLine();
					System.out.println("受信メッセージ：" + line);
					
					// 受信したことの返信
					writer.write("メッセージを受信しました。");
					writer.newLine();
					writer.flush();

					// シャットダウン
					acceptedSocket.shutdownInput();
					acceptedSocket.shutdownOutput();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
