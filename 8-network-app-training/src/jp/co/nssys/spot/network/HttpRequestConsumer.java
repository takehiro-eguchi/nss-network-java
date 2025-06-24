package jp.co.nssys.spot.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import jp.co.nssys.spot.network.entity.HttpHeaders;
import jp.co.nssys.spot.network.entity.HttpRequest;
import jp.co.nssys.spot.network.entity.HttpResponse;

/**
 * HTTPリクエストの処理を行います。
 */
public class HttpRequestConsumer {
	
	// 定数
	private static final String RESOURCE_ROOT = "resources";

	/**
	 * HTTPリクエストを処理し、HTTPレスポンスを生成します。
	 * @param request 処理するHTTPリクエスト
	 * @return 処理結果のHTTPレスポンス
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws URISyntaxException 
	 * @throws IllegalArgumentException リクエストがnullの場合にスローされます。
	 */
	public HttpResponse consume(HttpRequest request) throws FileNotFoundException, IOException, URISyntaxException {
		if (request == null) {
			throw new IllegalArgumentException("HttpRequest must not be null");
		}
		
		// リソースパスより対象ファイルを読み込み
		try {
			var response = readResponse(request);
			
			// レスポンスデータを補正する
			return build(response);
		} catch (FileNotFoundException | NoSuchFileException e) {
			// ファイルが見つからない場合は404 Not Foundを返す
			return HttpResponse.notFound(e.getMessage());
		}
	}

	/** HTTPリクエストに合致するレスポンスコンテンツを読み込みます。   */
	private HttpResponse readResponse(HttpRequest request) throws FileNotFoundException, IOException, URISyntaxException {
		// パスを取得
		var reqLine = request.requestLine();
		var method = reqLine.method();
		var path = reqLine.uri();
		
		// icoファイルなどの静的コンテンツは内容をそのまま返す
		var resourcePath = Paths.get(RESOURCE_ROOT + path);
		if (path.endsWith(".ico")) {
			return HttpResponse.ok(Files.readAllBytes(resourcePath));
		}
		
		// コンテンツファイルの取得
		var contentFile = Files.list(resourcePath)
				.filter(file -> startsWithIgnoreCase(
						file.getFileName().toString(), method))
				.findFirst()
				.orElseThrow(() -> new FileNotFoundException(
						"File not found: Path = " + path + ", Method = " + method));
		
		// 取得したファイルの内容を読み込み
		try (
				var inputStream = Files.newInputStream(contentFile);
				var responseReader = new HttpResponseReader(inputStream)) {
			return responseReader.read();
		}
	}

	// HTTPレスポンスを補完します。
	private HttpResponse build(HttpResponse response) {
		// ボディのサイズを取得し、Content-Lengthヘッダーを設定します。
		long contentLength = response.getContentLength();
		response.addHeader(HttpHeaders.CONTENT_LENGTH, contentLength);
		return response;
	}
	
	/** 指定された文字列が指定されたプレフィックスで始まるかどうかを、ケースを無視してチェックします。  */
	private static boolean startsWithIgnoreCase(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		return str.substring(0, prefix.length()).equalsIgnoreCase(prefix);
	}
}
