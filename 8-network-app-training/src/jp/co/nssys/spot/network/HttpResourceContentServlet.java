package jp.co.nssys.spot.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import jp.co.nssys.spot.network.entity.HttpRequest;
import jp.co.nssys.spot.network.entity.HttpResponse;

/**
 * HTTPリソースのコンテンツを提供するサーブレットです。
 * <p>
 * このサーブレットは、HTTPリクエストを受け取り、対応するリソースのコンテンツを返します。
 * </p>
 */
public class HttpResourceContentServlet implements HttpRequestServlet {

	// 定数
	private static final String RESOURCE_ROOT = "resources";
	
	@Override
	public HttpResponse execute(HttpRequest request) throws Exception {
		// パスを取得
		var reqLine = request.requestLine();
		var method = reqLine.method();
		var path = reqLine.uri();
		
		// 委譲
		return execute(path, method);
	}

	/** リソースに合致するHTTPレスポンスを作成します */
	public HttpResponse execute(String dirPath, String contentName) throws FileNotFoundException, IOException, URISyntaxException {
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
