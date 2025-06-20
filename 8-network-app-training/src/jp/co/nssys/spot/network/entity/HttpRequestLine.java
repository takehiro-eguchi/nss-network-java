package jp.co.nssys.spot.network.entity;

/** リクエスト行 */
public record HttpRequestLine(String method, String uri, String version) { 
	/** メソッドがGETかどうかを判定します */
	public boolean isGet() {
		return "GET".equalsIgnoreCase(method);
	}
	
	/** メソッドがPOSTかどうかを判定します */
	public boolean isPost() {
		return "POST".equalsIgnoreCase(method);
	}}

