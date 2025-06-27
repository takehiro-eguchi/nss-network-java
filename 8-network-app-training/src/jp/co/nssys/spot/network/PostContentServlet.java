package jp.co.nssys.spot.network;

import java.util.HashMap;

import jp.co.nssys.spot.network.entity.HttpRequest;
import jp.co.nssys.spot.network.entity.HttpResponse;

public class PostContentServlet implements HttpRequestServlet {

	// リソースサーブレット
	private final HttpResourceContentServlet resourceServlet = new HttpResourceContentServlet();
	
	@Override
	public HttpResponse execute(HttpRequest request) throws Exception {
		// Postメソッドの場合のみ実行
		if (!request.isPost()) {
			throw new IllegalArgumentException("PostContentServletはPOSTメソッドでのみ実行できます。");
		}
		
		// Postされたパラメタを取得
		var bodyString = request.getContent();
		if (bodyString == null || bodyString.isEmpty()) {
			throw new IllegalArgumentException("POSTリクエストのボディが空です。");
		}
		
		// パラメタを分解
		var params = new HashMap<String, String>();
		var paramElements = bodyString.split("&");
		for (String element : paramElements) {
			var keyValue = element.split("=");
			if (keyValue.length == 2) {
				params.put(keyValue[0], keyValue[1]);
			} else {
				throw new IllegalArgumentException("POSTリクエストのパラメタが不正です: " + element);
			}
		}
		
		// レスポンスを生成
		return resourceServlet.execute(request, params);
	}
}
