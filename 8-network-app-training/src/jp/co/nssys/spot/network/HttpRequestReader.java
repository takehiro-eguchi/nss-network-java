package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import jp.co.nssys.spot.network.entity.HttpHeaders;
import jp.co.nssys.spot.network.entity.HttpRequest;
import jp.co.nssys.spot.network.entity.HttpRequestLine;

/**
 * HTTPリクエストの内容を読み取るクラスです。
 */
public class HttpRequestReader implements Closeable {
	
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
		var requestLine = readRequestLine(firstLine);
		
		// ヘッダーを読み取る
		var headers = readHeaders();
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
			char[] buf = new char[contentLength.intValue()];
			int bytesRead = 0;
			while (bytesRead < contentLength) {
				int result = bufferedReader.read(buf, bytesRead, (int)(contentLength - bytesRead));
				if (result == -1) {
					throw new IOException("ボディの読み取り中に接続が切断されました。");
				}
				bytesRead += result;
			}
			
			// char配列をbyte配列に変換
			byte[] body = new byte[bytesRead];
			for (int i = 0; i < bytesRead; i++) {
				body[i] = (byte) buf[i];
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
	private HttpRequestLine readRequestLine(String line) {
		// 空白で分解
		String[] parts = line.split(" ");
		if (parts.length != 3) {
			throw new IllegalArgumentException("リクエスト行の形式が正しくありません: " + line);
		}
		
		// メソッド、URI、バージョンからリクエスト行を生成
		String method = parts[0];
		String uri = parts[1];
		String version = parts[2];
		return new HttpRequestLine(method, uri, version);
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
