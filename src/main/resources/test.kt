/**
 * API実行結果。
 */
sealed class ApiResponse<R>

/**
 * 成功時のAPI実行結果。
 */
data class ApiSuccess<R>(val result: R) : ApiResponse<R>()

/**
 * 失敗時のAPI実行結果。
 */
data class ApiFailure<R>(val error: Error) : ApiResponse<R>()

/**
 * [ApiSuccess]にセーフキャストする。
 * キャストに失敗した場合はnullを返す。
 */
fun <R> ApiResponse<R>.success(): ApiSuccess<R>? = this as? ApiSuccess

/**
 * [ApiFailure]にセーフキャストする。
 * キャストに失敗した場合はnullを返す。
 */
fun <R> ApiResponse<R>.failure(): ApiFailure<R>? = this as? ApiFailure

/**
 * [ApiSuccess]の時に処理を実行する。
 */
inline fun <R> ApiResponse<R>.successIf(handle: (R) -> Unit) {
    val result = success()?.result ?: return
    handle(result)
}

/**
 * [ApiFailure]の時に処理を実行する。
 */
inline fun <R> ApiResponse<R>.failureIf(handle: (Error) -> Unit) {
    val error = failure()?.error ?: return
    handle(error)
}

/**
 * [ApiSuccess]または[ApiSuccess.result] がnullではないときは値を返す。
 * それ以外は[default]値を返す。
 */
fun <R> ApiResponse<R>.getOrDefault(default: () -> R): R = success()?.result ?: default()

/**
 * [ApiSuccess.result]の型[R]を他の型[T]に変換する。
 */
inline fun <R, T> ApiSuccess<R>.map(transform: (R) -> T): T = transform(result)
