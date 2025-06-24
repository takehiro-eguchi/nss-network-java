package jp.co.nssys.spot.network.entity;

/** リクエスト行 */
public record HttpRequestLine(String method, String uri, String version) { 
	// 追加実装なし
}

