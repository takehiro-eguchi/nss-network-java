# 概要

HTTPレスポンスを学習するためのアプリケーションです。
HTTPレスポンスフォーマットに則ったファイルを配置しておくことにより、リソースパス・メソッドに則ったコンテンツをレスポンスすることができます。

## 実行環境

* Eclipse (pleiades-2025-03 Java All In One)
* Java 17

## 実行方法

`launch/アプリ起動.launch`を右クリックし、「実行」→「アプリ起動」を選択することにより実行することができます。

## コンテンツの配置方法

レスポンスのルートパスは`resources`となっており、パスはその配下のフォルダになります。
対応するコンテンツはリクエストのメソッドで始まるファイルを利用します。

例として、以下のリクエストを受信した場合は

```text
GET /sample HTTP/1.1
```

`resources/sample/get.txt`の内容をレスポンスすることになります。
なお、`Content-Length`ヘッダについては、ボディ部のデータサイズを自動で設定しますので設定は不要です。明示的に設定した場合はそちらが優先されます。

## 例外のハンドリングについて

以下の問題が発生した場合は、それぞれに対応したコンテンツを返すことができます。

|問題|ステータスコード|対応ファイル|
|---|---|---|
|リクエストに紐づくディレクトリ、ファイルが存在しない|404(Not Found)|resources/errors/404.txt|
|レスポンスの解析やその他のトラブルが発生|500(Internal Server Error)|resources/errors/500.txt|

## 独自のサーブレットの追加について

通常は配置したリソースに基づいて静的レスポンスが返却されますが、独自にサーブレットを追加したい場合はパスとサーブレットをセットで追加することができます。

```java
// HttpServer で設定することが可能
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

主に動的なコンテンツやgzip圧縮したデータをレスポンスしたい場合に利用します。

## コンテンツの変数埋め込みについて

静的なコンテンツに対してテンプレート化し、一部項目を変数から置き換えることができます。
以下のようなテンプレートコンテンツを用意します。
テンプレートコンテンツは`コンテンツの配置方法`に則って配置してください。

```html
<div class="info">
  <div class="label">名前:</div>
  <div class="value">${username}</div>
</div>
<div class="info">
  <div class="label">メールアドレス:</div>
  <div class="value">${email}</div>
</div>
<div class="info">
  <div class="label">メッセージ:</div>
  <div class="value">${message}</div>
</div>
```

`${}`内の変数をキーとしたMapを作成し、それを引数に渡してコンテンツを作成します。

```java
// リクエストオブジェクトを取得
HttpRequest request = ...;

// 埋め込み変数の作成
var params = new HashMap<String, String>();
params.put("username", "t-eguchi");
params.put("email", "t-eguchi@xxx.xx.xx");
params.put("message", "こんにちは！");

// このMapを渡してレスポンスを作成
HttpResponse response = resourceServlet.execute(request, params);
```

これにより、変数を置き換えたコンテンツが作成されます。

```html
<div class="info">
  <div class="label">名前:</div>
  <div class="value">t-eguchi</div>
</div>
<div class="info">
  <div class="label">メールアドレス:</div>
  <div class="value">t-eguchi@xxx.xx.xx</div>
</div>
<div class="info">
  <div class="label">メッセージ:</div>
  <div class="value">こんにちは！</div>
</div>
```
