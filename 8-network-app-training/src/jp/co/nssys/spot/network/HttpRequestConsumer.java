package jp.co.nssys.spot.network;

import jp.co.nssys.spot.network.HttpRequestReader.HttpRequest;
import jp.co.nssys.spot.network.HttpResponseWriter.HttpResponse;

/**
 * HTTPリクエストの処理を行います。
 */
public class HttpRequestConsumer {

	/**
	 * HTTPリクエストを処理し、HTTPレスポンスを生成します。
	 * @param request 処理するHTTPリクエスト
	 * @return 処理結果のHTTPレスポンス
	 * @throws IllegalArgumentException リクエストがnullの場合にスローされます。
	 */
	public HttpResponse consume(HttpRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("HttpRequest must not be null");
		}

		// レスポンスを返す
		return build(request);
	}

	// HTTPレスポンスを生成します。
	private HttpResponse build(HttpRequest request) {
		// レスポンス行を生成
		var responseLine = new HttpResponseWriter.ResponseLine("HTTP/1.1", 200, "OK");
		// ヘッダーを生成
		var headers = new HttpRequestReader.HttpHeaders();
		headers.addHeader(
				HttpRequestReader.HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		// ボディを生成
		String bodyContent = "{  \"message\": \"Hello, World!\" }";
		byte[] body = bodyContent.getBytes();
		headers.addHeader(
				HttpRequestReader.HttpHeaders.CONTENT_LENGTH, String.valueOf(body.length));
		return new HttpResponse(responseLine, headers, body);
	}
}
