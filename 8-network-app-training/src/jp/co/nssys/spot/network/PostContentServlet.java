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
		
		// TODO パラメタを要素ごとに分解する
		var params = new HashMap<String, String>();	// パラメタキーとパラメタ値のマッピング
		
		// TODO 変数を埋め込むことで、レスポンスを生成
		// ここで全て動的に作成してしまっても良いです。
		return resourceServlet.execute(request, params);
	}
}
