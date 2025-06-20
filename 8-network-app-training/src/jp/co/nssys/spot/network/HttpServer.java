package jp.co.nssys.spot.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

import jp.co.nssys.spot.network.HttpRequestServlet.SampleXmlServlet;

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
		
		// リクエストを処理するオブジェクトの生成
		var consumer = new HttpRequestConsumer();
		// 独自にサーブレットを登録する場合は、パスとサーブレットをマッピングする
		consumer.addServlet("/xml", new SampleXmlServlet());
		consumer.addServlet("/ex4", new GzipContentServlet());
		consumer.addServlet("/ex6-2", new PostContentServlet());
		
		// 受信を開始する
		try (var serverSocket = new ServerSocket(port, backlog)) {
			System.out.println("HTTPサーバーがポート " + port + " で起動しました。");
			while (!serverSocket.isClosed()) {
				// リクエストを受け付ける
				final var acceptedSocket = serverSocket.accept();
				
				// 受付後はスレッドプールで処理
				executor.execute(() -> handleSocket(consumer, acceptedSocket));
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
	private static void handleSocket(HttpRequestConsumer consumer, Socket socket) {
		try (
				var reader = new HttpRequestReader(socket);
				var writer = new HttpResponseWriter(socket)) {
			
			// リクエストを読み込む
			var request = reader.read();
			if (request == null) {
				return; // リクエストがnullの場合は処理を終了
			}
			
			// リクエストに基づいてレスポンスを生成する
			var response = consumer.consume(request);
			
			// レスポンスを書き込む
			writer.writeResponse(response);
			
		} catch (Exception e) {
			throw new RuntimeException("HTTPサーバーのソケット処理に失敗しました。", e);
		} 
	}
}
