package jp.co.nssys.spot.network;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;

import jp.co.nssys.spot.network.entity.HttpHeaders;
import jp.co.nssys.spot.network.entity.HttpResponse;
import jp.co.nssys.spot.network.entity.HttpResponseLine;

/**
 * HTTPレスポンスの読み取りを行います。
 */
public class HttpResponseReader implements Closeable {

	// 読み込み用のストリーム
	private final InputStream inputStream;
	private final InputStreamReader inputStreamReader;
	private final BufferedReader bufferedReader;
	
	/**
	 * 入力ストリームを渡すことによりインスタンスを生成します。
	 * @param inputStream 入力ストリーム
	 */
	public HttpResponseReader(InputStream inputStream) {
		if (inputStream == null) {
			throw new IllegalArgumentException("入力ストリームにはnullは設定できません。");
		}
		this.inputStream = inputStream;
		this.inputStreamReader = new InputStreamReader(inputStream);
		this.bufferedReader = new BufferedReader(inputStreamReader);
	}
	
	/**
	 * HTTPレスポンスを読み取ります。
	 * @return 読み取ったHTTPレスポンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public HttpResponse read() throws IOException {
		return read(null);
	}
	
	/**
	 * HTTPレスポンスを読み取ります。
	 * @param params 埋め込み変数
	 * @return 読み取ったHTTPレスポンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public HttpResponse read(Map<String, String> params) throws IOException {
		// レスポンス行を読み取る
		var responseLine = readResponseLine();
		
		// ヘッダーを読み取る
		var headers = readHeaders();
		
		// 残りのデータを読み取る
		byte[] body = readBody();
		
		// パラメータが指定されている場合は、ボディの内容を置換
		if (params != null && !params.isEmpty()) {
			String bodyString = new String(body);
			for (Map.Entry<String, String> entry : params.entrySet()) {
				var key = entry.getKey();
				var value = entry.getValue();
				// valueはURLデコードする
				value = URLDecoder.decode(value, Charset.defaultCharset());
				bodyString = bodyString.replaceAll("\\$\\{" + key + "\\}", value);
			}
			body = bodyString.getBytes();
		}
		
		// レスポンスを作成
		return new HttpResponse(responseLine, headers, body);
	}

	/** レスポンス行を読み込みます */
	private HttpResponseLine readResponseLine() throws IOException {
		String firstLine = bufferedReader.readLine();
		if (firstLine == null) {
			throw new IllegalArgumentException("リクエスト行が存在しません。");
		}
		var responseLineElements = firstLine.split(" ", 3);
		if (responseLineElements.length < 2) {
			throw new IllegalArgumentException("不正なHTTPレスポンス行です: " + firstLine);
		}
		return new HttpResponseLine(
				responseLineElements[0], 
				Integer.parseInt(responseLineElements[1]), 
				responseLineElements.length > 2 ? responseLineElements[2] : "");
	}
	
	/** ヘッダーを読み込みます 
	 * @throws IOException */
	private HttpHeaders readHeaders() throws IOException {
		var headers = new HttpHeaders();
		String line;
		while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
			// コメント行はスキップ
			if (line.startsWith("#") || line.startsWith("//")) {
				continue;
			}
			
			// ヘッダーとして読み込み
			String[] parts = line.split(":", 2);
			if (parts.length == 2) {
				String name = parts[0].trim();
				String value = parts[1].trim();
				headers.addHeader(name, value);
			} else {
				throw new IllegalArgumentException("不正なHTTPヘッダー行です: " + line);
			}
		}
		return headers;
	}
	
	/** レスポンスボディを読み込みます  */
	private byte[] readBody() throws IOException {
		ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
		char[] buf = new char[1024];
		int bytesRead;
		while ((bytesRead = bufferedReader.read(buf)) != -1) {
			// char配列からバイト配列に変換
			byte[] byteBuf = new String(buf, 0, bytesRead).getBytes();
			// 読み取ったデータを書き込む
			bytesOutputStream.write(byteBuf);
		}
		return bytesOutputStream.toByteArray();
	}

	@Override
	public void close() {
		try {
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
		} catch (IOException e) {
			// 入力ストリームのクローズに失敗しても、例外をスローしない
		}
	}
}
