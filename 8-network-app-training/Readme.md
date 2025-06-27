# 概要
HTTPレスポンスを学習するためのアプリケーションです。
HTTPレスポンスフォーマットに則ったファイルを配置しておくことにより、リソースパス・メソッドに則ったコンテンツをレスポンスすることができます。

# 実行環境
* Eclipse (pleiades-2025-03 Java All In One)
* Java 17

# 実行方法
`launch/アプリ起動.launch`を右クリックし、「実行」→「アプリ起動」を選択することにより実行することができます。

# コンテンツの配置方法
レスポンスのルートパスは`resources`となっており、パスはその配下のフォルダになります。
対応するコンテンツはリクエストのメソッドで始まるファイルを利用します。

例として、以下のリクエストを受信した場合は

```
GET /sample HTTP/1.1
```

`resources/sample/get.txt`の内容をレスポンスすることになります。
なお、`Content-Length`ヘッダについては、ボディ部のデータサイズを自動で設定します。

# 例外のハンドリングについて
以下の問題が発生した場合は、それぞれに対応したコンテンツを返すことができます。

|問題|ステータスコード|対応ファイル|
|---|---|---|
|リクエストに紐づくディレクトリ、ファイルが存在しない|404(Not Found)|resources/errors/404.txt|
|レスポンスの解析やその他のトラブルが発生|500(Internal Server Error)|resources/errors/500.txt||

# 独自のサーブレットの追加について
通常は配置したリソースに基づいてレスポンスが返却されますが、独自にサーブレットを追加したい場合はパスとサーブレットをセットで追加することができます。

```java
HttpRequestConsumer consumer = ...;
// パスとサーブレットを追加
consumer.addServlet(
		"/hello", 
		new HttpRequestServlet () {
			@Override
			public HttpResponse execute(HttpRequest request) throws Exception {
				return HttpResponse.ok("Hello, World!");
			}
		});
```