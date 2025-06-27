package jp.co.nssys.spot.network.entity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {
	/** HTTPヘッダー */
	public static record HttpHeader(String name, String value) { }
	
	// 定数
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final String ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String GZIP = "gzip";
	
	// ヘッダーのリスト
	private final Map<String, HttpHeader> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	/** ヘッダーを追加します。 */
	public void addHeader(String name, String value) {
		String trimmedName = name.trim();
		headers.put(trimmedName, new HttpHeader(trimmedName, value.trim()));
	}

	/** ヘッダーを取得します。 */
	public List<HttpHeader> getHeaders() {
		return headers.values().stream().toList();
	}
	
	public HttpHeader getHeader(String name) {
		return headers.get(name);
	}
	
	/** ヘッダーが存在するか確認します。 */
	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}
	
	// Content-Lengthヘッダーを取得します。
	public Long getContentLength() {
		HttpHeader header = headers.get(CONTENT_LENGTH);
		if (header != null) {
			try {
				return Long.parseLong(header.value());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Content-Lengthヘッダーの値が不正です: " + header.value(), e);
			}
		}
		return null; // Content-Lengthヘッダーが存在しない場合はnullを返す
	}
}
