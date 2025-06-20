package jp.co.nssys.spot.network;

import jp.co.nssys.spot.network.entity.HttpHeaders;
import jp.co.nssys.spot.network.entity.HttpRequest;
import jp.co.nssys.spot.network.entity.HttpResponse;

/**
 * HTTPリクエストを処理するためのインターフェースです。
 * <p>
 * このインターフェースは、HTTPリクエストを受け取り、適切なレスポンスを生成するためのメソッドを定義しています。
 * </p>
 */
@FunctionalInterface
public interface HttpRequestServlet {
	
	/**
	 * サンプルのXMLサーブレット実装です。
	 */
	public static class SampleXmlServlet implements HttpRequestServlet {
		@Override
		public HttpResponse execute(HttpRequest request) throws Exception {
			var response = HttpResponse.ok("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<message>\n"
					+ "    <text>Hello, world</text>\n"
					+ "</message>\n"
					+ "");
			response.addHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.APPLICATION_XML);
			return response;
		}
	}

	/**
	 * HTTPリクエストを処理し、HTTPレスポンスを生成します。
	 * <p>
	 * このメソッドは、HTTPリクエストを受け取り、適切なHTTPレスポンスを返します。
	 * </p>
	 *
	 * @param request HTTPリクエスト
	 * @return HTTPレスポンス
	 * @throws Exception リクエストの処理中に発生した例外
	 */
	HttpResponse execute(HttpRequest request) throws Exception;
}
