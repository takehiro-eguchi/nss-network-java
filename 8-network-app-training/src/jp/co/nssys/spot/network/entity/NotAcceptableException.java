package jp.co.nssys.spot.network.entity;

/**
 * HTTP 406 Not Acceptable エラーを表す例外クラスです。
 * <p>
 * このクラスは、リクエストされたリソースが要求されたメディアタイプで提供できない場合に使用されます。
 * </p>
 */
public class NotAcceptableException extends Exception {
	
	// シリアルバージョンUID
	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ
	 * <p>
	 * メッセージを指定して例外を初期化します。
	 * </p>
	 *
	 * @param message エラーメッセージ
	 */
	public NotAcceptableException(String message) {
		super(message);
	}

	/**
	 * コンストラクタ
	 * <p>
	 * メッセージと原因を指定して例外を初期化します。
	 * </p>
	 *
	 * @param message エラーメッセージ
	 * @param cause   原因となる例外
	 */
	public NotAcceptableException(String message, Throwable cause) {
		super(message, cause);
	}
}
