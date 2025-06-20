package jp.co.nssys.spot.network.entity;

/** HTTPリクエスト */
public record HttpRequest(HttpRequestLine requestLine, HttpHeaders headers, byte[] body) {
	// 内容をテキストとして取得します
	public String getContent() {
		return new String(body);
	}
	
	/** メソッドがGETかどうかを判定します */
	public boolean isGet() {
		return requestLine.isGet();
	}
	
	/** メソッドがPOSTかどうかを判定します */
	public boolean isPost() {
		return requestLine.isPost();
	}
}
