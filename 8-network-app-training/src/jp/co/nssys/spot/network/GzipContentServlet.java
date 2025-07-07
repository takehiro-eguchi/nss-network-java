package jp.co.nssys.spot.network;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

import jp.co.nssys.spot.network.entity.HttpHeaders;
import jp.co.nssys.spot.network.entity.HttpRequest;
import jp.co.nssys.spot.network.entity.HttpResponse;

/**
 * Gzip圧縮されたコンテンツを提供するサーブレットの実装です。
 * <p>
 * このクラスは、Gzip圧縮されたコンテンツを提供するためのサーブレットとして機能します。
 * </p>
 */
public class GzipContentServlet implements HttpRequestServlet {

	@Override
	public HttpResponse execute(HttpRequest request) throws Exception {
		// TODO: とても大きなコンテンツを作成する
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "....."
				+ "";
		
		// GZIPストリームを使って圧縮する
		var bytesOutputStream = new ByteArrayOutputStream();
		try (var gzipOutputStream = new GZIPOutputStream(bytesOutputStream)) {
			// 書き込み
			gzipOutputStream.write(content.getBytes());
			gzipOutputStream.flush();
		}

		// コンテンツ作成（クローズしてからでないとバイト配列が取れない）
		byte[] gzipContent = bytesOutputStream.toByteArray();
		var response = HttpResponse.ok(gzipContent);
		
		// TODO: レスポンスヘッダーを適切に設定
//		response.addHeader(HttpHeaders.XXX, HttpHeaders.XXX);
		
		return response;
	}
}
