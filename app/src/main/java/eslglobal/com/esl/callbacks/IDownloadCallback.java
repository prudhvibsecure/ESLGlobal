package eslglobal.com.esl.callbacks;

public interface IDownloadCallback {
	public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId);
}
