package jp.co.nssys.spot.network.entity;

/**
 * HTTPレスポンスのエンティティを表します。
 */
public record HttpResponse(HttpResponseLine responseLine, HttpHeaders headers, byte[] body) {
	// 定数
	private static final String HTTP_PROTOCOL = "HTTP/1.1";
	
	// レスポンスボディの内容を取得します。
	public String getContent() {
		return body != null ? new String(body) : "";
	}
	
	// レスポンスボディの長さを取得します。
	public long getContentLength() {
		return body != null ? (long) body.length : 0L;
	}
	
	/** ヘッダ―を追加します */
	public void addHeader(String name, String value) {
		headers.addHeader(name, value);
	}
	
	/** ヘッダ―を追加します */
	public void addHeader(String name, Long value) {
		headers.addHeader(name, String.valueOf(value));
	}
	
	/** ヘッダーが存在するか確認します */
	public boolean containsHeader(String name) {
		return headers.containsHeader(name);
	}
	
	/** OKのHTTPレスポンスを生成します */
	public static HttpResponse ok() {
		return ok(new byte[0]);
	}
	
	/** OKのHTTPレスポンスを生成します */
	public static HttpResponse ok(byte[] body) {
		HttpResponseLine responseLine = new HttpResponseLine(HTTP_PROTOCOL, 200, "OK");
		return new HttpResponse(responseLine, new HttpHeaders(), body);
	}
	
	/** OKのHTTPレスポンスを生成します */
	public static HttpResponse ok(String body) {
		byte[] bodyBytes = body != null ? body.getBytes() : new byte[0];
		return ok(bodyBytes);
	}
	
	/** Not FoundのHTTPレスポンスを生成します */
	public static HttpResponse notFound(String message) {
		return new HttpResponse(
				new HttpResponseLine(HTTP_PROTOCOL, 404, "Not Found"),
				new HttpHeaders(), message.getBytes());
	}
}
