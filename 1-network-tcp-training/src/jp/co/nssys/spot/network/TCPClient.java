package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * TCP通信におけるクライアントクラスです。
 */
public class TCPClient {

	/**
	 * 実行します。
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// ソケットの作成
		try (var socket = new Socket("localhost", 8080)) {
			try (
					var consoleStreamReader = new InputStreamReader(System.in);
					var consoleReader = new BufferedReader(consoleStreamReader);
					var sockInputStream = socket.getInputStream();
					var sockInputStreamReader = new InputStreamReader(sockInputStream);
					var sockReader = new BufferedReader(sockInputStreamReader);
					var sockOutputStream = socket.getOutputStream();
					var sockOutputStreamWriter = new OutputStreamWriter(sockOutputStream);
					var sockWriter = new BufferedWriter(sockOutputStreamWriter);) {
				// 標準入力から読み込む
				System.out.print("メッセージ：");
				var msg = consoleReader.readLine();

				// 送信する
				sockWriter.write(msg);
				sockWriter.newLine();
				sockWriter.flush();

				// シャットダウン
				socket.shutdownInput();
				socket.shutdownOutput();
			}
		}
	}
}
