package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * HTTPリクエストの内容を読み取るクラスです。
 */
public class HttpRequestReader implements Closeable {

	/** リクエスト行 */
	public static record RequestLine(String method, String uri, String version) {  }

	/** HTTPヘッダー */
	public static record HttpHeader(String name, String value) { }
	public static class HttpHeaders {
		// 定数
		public static final String CONTENT_LENGTH = "Content-Length";
		public static final String CONTENT_TYPE = "Content-Type";
		
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
	
	/** レスポンス行 */
	public static record ResponseLine(String version, int statusCode, String reasonPhrase) { }

	/** HTTPリクエスト */
	public static record HttpRequest(RequestLine requestLine, HttpHeaders headers, byte[] body) {
		// 内容をテキストとして取得します
		public String getContent() {
			return new String(body);
		}
	}
	
	private final Socket socket;
	private final InputStream inputStream;
	private final InputStreamReader inputStreamReader;
	private final BufferedReader bufferedReader;
	
	/**
	 * 入力ストリームを渡すことによりインスタンスを生成します。
	 * @param socket ソケットオブジェクト
	 */
	public HttpRequestReader(Socket socket) {
		if (socket == null) {
			throw new IllegalArgumentException("ソケットはnullではいけません。");
		}
		this.socket = socket;
		try {
			this.inputStream = socket.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException("ソケットから入力ストリームを取得できませんでした。", e);
		}
		this.inputStreamReader = new InputStreamReader(inputStream);
		this.bufferedReader = new BufferedReader(inputStreamReader);
	}
	
	/**
	 * HTTPリクエストを読み取ります。
	 * @return 読み取ったHTTPリクエスト
	 * @throws IOException 
	 */
	public HttpRequest read() throws IOException {
		// リクエスト行を読み取る
		String firstLine = bufferedReader.readLine();
		if (firstLine == null) {
			return null; // コンテンツが空か終了している場合はnullを返す
		}
		RequestLine requestLine = readRequestLine(firstLine);
		
		// ヘッダーを読み取る
		HttpHeaders headers = readHeaders();
		// Content-Lengthヘッダーを取得する
		Long contentLength = headers.getContentLength();
		
		// ボディを読み取る
		byte[] body = readBody(contentLength);
		
		// HttpRequestオブジェクトを生成して返す
		var request = new HttpRequest(requestLine, headers, body);
		printRequest(request);
		return request;
	}

	// リクエストの内容をコンソールに出力するメソッド
	private void printRequest(HttpRequest request) {
		System.out.println("---------- START HTTPリクエスト ----------");
		var requestLine = request.requestLine();
		System.out.println(String.join(" ", requestLine.method(), requestLine.uri(), requestLine.version()));
		var headers = request.headers();
		for (var header : headers.getHeaders()) {
			System.out.println(String.format("%s: %s", header.name(), header.value()));
		}
		System.out.println();
		System.out.println(request.getContent());
		System.out.println("---------- END HTTPリクエスト ----------");
	}

	// ボディを読み取るメソッド
	private byte[] readBody(Long contentLength) {
		if (contentLength == null || contentLength <= 0) {
			return new byte[0]; // Content-Lengthがないか0の場合は空のボディを返す
		}
		try {
			byte[] body = new byte[contentLength.intValue()];
			int bytesRead = 0;
			while (bytesRead < contentLength) {
				int result = inputStream.read(body, bytesRead, (int)(contentLength - bytesRead));
				if (result == -1) {
					throw new IOException("ボディの読み取り中に接続が切断されました。");
				}
				bytesRead += result;
			}
			return body;
		} catch (IOException e) {
			throw new RuntimeException("ボディの読み取りに失敗しました。", e);
		}
	}

	// ヘッダーを読み取るメソッド
	private HttpHeaders readHeaders() throws IOException {
		// 空白行が出るまでヘッダーを読み取る
		var headers = new HttpHeaders();
		String line;
		while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
			// ヘッダーを解析してリストに追加
			String[] parts = line.split(": ", 2);
			if (parts.length != 2) {
				throw new IllegalArgumentException("ヘッダーの形式が正しくありません: " + line);
			}
			String name = parts[0];
			String value = parts[1];
			headers.addHeader(name, value);
		}
		return headers;
	}

	// リクエスト行を解析するメソッド
	private RequestLine readRequestLine(String line) {
		// 空白で分解
		String[] parts = line.split(" ");
		if (parts.length != 3) {
			throw new IllegalArgumentException("リクエスト行の形式が正しくありません: " + line);
		}
		
		// メソッド、URI、バージョンからリクエスト行を生成
		String method = parts[0];
		String uri = parts[1];
		String version = parts[2];
		return new RequestLine(method, uri, version);
	}

	@Override
	public void close() throws IOException {
		try {
			socket.shutdownInput();
		} catch (IOException e) {
			// ソケットの入力ストリームをシャットダウンできなかった場合は、例外をスローしない
		} finally {
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
		}
	}
}
