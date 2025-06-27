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
	private static final String ERROR_PATH = "errors";

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
		try {
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
				var notFoundResponse = readResponse(ERROR_PATH, "404");
				return build(notFoundResponse);
			} 
		} catch (Exception e) {
			// その他の例外は500 Internal Server Errorを返す
			var errorResponse = readResponse(ERROR_PATH, "500");
			return build(errorResponse);
		}
	}

	/** HTTPリクエストに合致するレスポンスコンテンツを読み込みます。   */
	private HttpResponse readResponse(HttpRequest request) throws FileNotFoundException, IOException, URISyntaxException {
		// パスを取得
		var reqLine = request.requestLine();
		var method = reqLine.method();
		var path = reqLine.uri();
		
		// 委譲
		return readResponse(path, method);
	}
	
	/** リソースに合致するHTTPレスポンスを作成します */
	private HttpResponse readResponse(String dirPath, String contentName) throws FileNotFoundException, IOException, URISyntaxException {
		// icoファイルなどの静的コンテンツは内容をそのまま返す
		var resourcePath = Paths.get(RESOURCE_ROOT + "/" + dirPath);
		if (resourcePath.toString().endsWith(".ico")) {
			return HttpResponse.ok(Files.readAllBytes(resourcePath));
		}
		
		// コンテンツファイルの取得
		var contentFile = Files.list(resourcePath)
				.filter(file -> startsWithIgnoreCase(
						file.getFileName().toString(), contentName))
				.findFirst()
				.orElseThrow(() -> new FileNotFoundException(
						"File not found: Path = " + dirPath + ", Method = " + contentName));
		
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
