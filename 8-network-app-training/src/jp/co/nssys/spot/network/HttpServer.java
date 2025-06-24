package jp.co.nssys.spot.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * HTTPサーバーの実装を提供します。
 * <p>
 * このパッケージは、HTTPリクエストを処理し、HTTPレスポンスを生成するためのクラスを含んでいます。
 * </p>
 *
 */
public class HttpServer {

	/**
	 * HTTPサーバーのエントリポイントです。
	 * <p>
	 * このメソッドは、HTTPサーバーを起動し、リクエストを待ち受けます。
	 * </p>
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		// ポート番号とバックログの設定
		int port = 8080;
		int backlog = 10;
		var executor = Executors.newFixedThreadPool(backlog);
		
		// 受信を開始する
		try (var serverSocket = new ServerSocket(port, backlog)) {
			System.out.println("HTTPサーバーがポート " + port + " で起動しました。");
			while (!serverSocket.isClosed()) {
				// リクエストを受け付ける
				final var acceptedSocket = serverSocket.accept();
				
				// 受付後はスレッドプールで処理
				executor.execute(() -> handleSocket(acceptedSocket));
			}
		} catch (IOException e) {
			throw new RuntimeException("HTTPサーバーの送受信に失敗しました。ポート: " + port, e);
		} finally {
			// スレッドプールをシャットダウン
			executor.shutdown();
			System.out.println("HTTPサーバーをシャットダウンしました。");
		}
	}
		
	// 受付ソケットの処理を行います。
	private static void handleSocket(Socket socket) {
		try (
				var reader = new HttpRequestReader(socket);
				var writer = new HttpResponseWriter(socket)) {
			
			// リクエストを読み込む
			var request = reader.read();
			if (request == null) {
				return; // リクエストがnullの場合は処理を終了
			}
			
			// リクエストに基づいてレスポンスを生成する
			var consumer = new HttpRequestConsumer();
			var response = consumer.consume(request);
			
			// レスポンスを書き込む
			writer.writeResponse(response);
			
		} catch (Exception e) {
			throw new RuntimeException("HTTPサーバーのソケット処理に失敗しました。", e);
		} 
	}
}
