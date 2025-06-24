package jp.co.nssys.spot.network.entity;

/**
 * HTTPレスポンス行を表すクラスです。
 */
public record HttpResponseLine(
		String version, int statusCode, String reasonPhrase) { 
	// 追加実装なし
}
