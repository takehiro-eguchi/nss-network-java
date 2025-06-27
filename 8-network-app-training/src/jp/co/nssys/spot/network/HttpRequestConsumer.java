package jp.co.nssys.spot.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.nssys.spot.network.entity.HttpHeaders;
import jp.co.nssys.spot.network.entity.HttpRequest;
import jp.co.nssys.spot.network.entity.HttpResponse;

/**
 * HTTPリクエストの処理を行います。
 */
public class HttpRequestConsumer {
	
	// 定数
	private static final String ERROR_PATH = "errors";
	
	/** HTTPリクエストを処理するサーブレットのマッピング*/
	private final Map<String, HttpRequestServlet> servletMapping = new ConcurrentHashMap<>();
	
	/** リソースコンテンツを提供するサーブレット */
	private final HttpResourceContentServlet resourceServlet = new HttpResourceContentServlet();

	/**
	 * HTTPリクエストを処理し、HTTPレスポンスを生成します。
	 * @param request 処理するHTTPリクエスト
	 * @return 処理結果のHTTPレスポンス
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws URISyntaxException 
	 * @throws IllegalArgumentException リクエストがnullの場合にスローされます。
	 */
	public HttpResponse consume(HttpRequest request) throws FileNotFoundException, IOException, URISyntaxException {
		try {
			if (request == null) {
				throw new IllegalArgumentException("HttpRequest must not be null");
			}
			
			try {
				// リクエストパスに紐づくサーブレットが登録されているか確認
				var path = request.requestLine().uri();
				var servlet = servletMapping.get(path.toLowerCase());	// 大文字小文字は区別せず判別
				
				// サーブレットがある場合はそれを実行し、そうでない場合はリソースから読み込む
				HttpResponse response;
				if (servlet != null) {
					response = servlet.execute(request);
				} else {
					response = resourceServlet.execute(request);
				}
				
				// レスポンスデータを補正する
				return build(request, response);
				
			} catch (FileNotFoundException | NoSuchFileException e) {
				// ファイルが見つからない場合は404 Not Foundを返す
				var notFoundResponse = resourceServlet.execute(ERROR_PATH, "404");
				return build(request, notFoundResponse);
			} 
		} catch (Exception e) {
			// その他の例外は500 Internal Server Errorを返す
			e.printStackTrace();
			var errorResponse = resourceServlet.execute(ERROR_PATH, "500");
			return build(request, errorResponse);
		}
	}
	
	/**
	 * HTTPリクエストパスに対するサーブレットを追加します。
	 * @param path サーブレットのパス
	 * @param servlet サーブレットのインスタンス
	 * @throws IllegalArgumentException パスまたはサーブレットがnullの場合にスローされます。
	 */
	public void addServlet(String path, HttpRequestServlet servlet) {
		if (path == null || servlet == null) {
			throw new IllegalArgumentException("Path and servlet must not be null");
		}
		// パスは小文字にして格納
		servletMapping.put(path.toLowerCase(), servlet);
	}
	
	// HTTPレスポンスを補完します。
	private HttpResponse build(HttpRequest request, HttpResponse response) {
		// ボディのサイズを取得し、Content-Lengthヘッダーを設定します。
		long contentLength = response.getContentLength();
		if (!response.containsHeader(HttpHeaders.CONTENT_LENGTH)) {
			// Content-Lengthヘッダーが存在しない場合は追加
			response.addHeader(HttpHeaders.CONTENT_LENGTH, contentLength);
		}
		return response;
	}
}
