package jp.co.nssys.spot.network;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import jp.co.nssys.spot.network.entity.HttpResponse;

/**
 * HTTPレスポンスの書き込みを行います。
 */
public class HttpResponseWriter implements Closeable {

	private final Socket socket;
	private final OutputStream outputStream;
	private final OutputStreamWriter outputStreamWriter;
	private final BufferedWriter bufferedWriter;
	
	/**
	 * ソケットを渡すことによってインスタンスを生成します。
	 * @param socket
	 */
	public HttpResponseWriter(Socket socket) {
		if (socket == null) {
			throw new IllegalArgumentException("Socket must not be null");
		}
		this.socket = socket;
		try {
			this.outputStream = socket.getOutputStream();
			this.outputStreamWriter = new OutputStreamWriter(outputStream);
			this.bufferedWriter = new BufferedWriter(outputStreamWriter);
		} catch (Exception e) {
			throw new RuntimeException("Output stream initialization failed", e);
		}
	}
	
	/**
	 * HTTPレスポンスの内容を書き込みます。
	 * @param response
	 * @throws IOException
	 */
	public void writeResponse(HttpResponse response) throws IOException {
		if (response == null) {
			throw new IllegalArgumentException("HttpResponse must not be null");
		}
		
		// 出力
		printResponse(response);
		
		// レスポンス行の書き込み
		var responseLine = response.responseLine();
		bufferedWriter.write(String.format(
				"%s %d %s", 
				responseLine.version(), responseLine.statusCode(), responseLine.reasonPhrase()));
		bufferedWriter.newLine();
		
		// ヘッダーの書き込み
		for (var header : response.headers().getHeaders()) {
			bufferedWriter.write(
					String.format("%s: %s", header.name(), header.value()));
			bufferedWriter.newLine();
		}
		bufferedWriter.newLine();
		bufferedWriter.flush();
		
		// ボディの書き込み
		if (response.body() != null && response.body().length > 0) {
			outputStream.write(response.body());
			outputStream.flush();
		}
		
		bufferedWriter.flush();
	}

	// HTTPレスポンスの内容を出力します。
	private void printResponse(HttpResponse response) {
		System.out.println("---------- START HTTPレスポンス ----------");
		var responseLine = response.responseLine();
		System.out.println(String.format(
				"%s %d %s", 
				responseLine.version(), responseLine.statusCode(), responseLine.reasonPhrase()));
		var headers = response.headers();
		for (var header : headers.getHeaders()) {
			System.out.println(String.format("%s: %s", header.name(), header.value()));
		}
		System.out.println();
		System.out.println(response.getContent());
		System.out.println("---------- END HTTPレスポンス ----------");
	}

	@Override
	public void close() throws IOException {
		try {
			socket.shutdownOutput();
		} catch (IOException e) {
			// 出力ストリームのシャットダウンに失敗した場合は、例外をスローしない
		} finally {
			bufferedWriter.close();
			outputStreamWriter.close();
			outputStream.close();
			socket.close();
		}
	}
}