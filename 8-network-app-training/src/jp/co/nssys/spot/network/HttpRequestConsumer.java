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
		return null; // ここではnullを返していますが、実際には適切なレスポンスを生成して返す必要があります。
	}
}
