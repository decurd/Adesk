package kr.co.roonets.adesk.component;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.UrlQuerySanitizer;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.co.roonets.adesk.utils.DialogUtil;
import kr.co.roonets.adesk.utils.IntentUtil;


public class I2WebViewClient extends WebViewClient {
	private OnLinkedClickListener _onLinkedClickListener;
	private OnUrlLoadCompleteListener _onUrlLoadCompleteListener;
//	private BaseFragment _baseFragment;
	private Context mContext;

	public I2WebViewClient(Context context) {
		mContext = context;
	}
	
//	private void createProgressDialog() {
//		_baseFragment.setVisibleProgressBar(true);
//	}

//	private void progressDialogDismiss() {
//		_baseFragment.setVisibleProgressBar(false);
//	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.e("WebClient", "shouldOverrideUrlLoading==========>" + url);
		//KITKAT 이후 webview의 경우 http:// 일반적이지 않은 URL일경우 shouldOverrideUrlLoading 함수를 거치지 않음

		// youtube 동영상 처리
		if(url.contains("vnd.youtube")) {
			String youtubeId = url.substring(url.lastIndexOf(":")+1);
			Log.e("WebClient", "youtubeId = " + youtubeId);
			mContext.startActivity(IntentUtil.getYoutubeVideo(youtubeId));
			return true;
		} else if(url.contains("i2livechat")) {
			//TYPE, TAR_ID 파라미터 취득
			UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
			sanitizer.setAllowUnregisteredParamaters(true);
			sanitizer.parseUrl(url);
			String type = sanitizer.getValue("type");
			String tarId = sanitizer.getValue("tar_id");
			try {
				/*Intent intent = IntentUtil.getI2LiveChatIntent(type, tarId);
				mContext.startActivity(intent);*/
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
				DialogUtil.showConfirmDialog(mContext, "알림", "I2LiveChat앱이 설치되어 있지않습니다.\n다운로드를 진행합니다."
                );
			}
			return true;
		} else {
			view.loadUrl(url);
		}

		return false;
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		// 웹 페이지 리소스들을 로딩하면서 계속해서 호출된다.
		super.onLoadResource(view, url);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		Log.d("webview", "onPageFinished==========>" + url);
		super.onPageFinished(view, url);
	}
	// 페이지 요청이 시작될 경우 호출된다.
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		Log.d("webview", "PageStarted==========>" + url);
		super.onPageStarted(view, url, favicon);
	}

	
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
//		progressDialogDismiss();
		Log.d("webview", "error code======>" + errorCode);
		Log.d("webview", "description======>" + description);
		Log.d("webview", "failingUrl======>" + failingUrl);
		switch (errorCode) {
		case ERROR_AUTHENTICATION:
			break; // 서버에서 사용자 인증 실패
		case ERROR_BAD_URL:
			break; // 잘못된 URL
		case ERROR_CONNECT:
			break; // 서버로 연결 실패
		case ERROR_FAILED_SSL_HANDSHAKE:
			break; // SSL handshake 수행 실패
		case ERROR_FILE:
			break; // 일반 파일 오류
		case ERROR_FILE_NOT_FOUND:
			break; // 파일을 찾을 수 없습니다
		case ERROR_HOST_LOOKUP:
			break; // 서버 또는 프록시 호스트 이름 조회 실패
		case ERROR_IO:
			break; // 서버에서 읽거나 서버로 쓰기 실패
		case ERROR_PROXY_AUTHENTICATION:
			break; // 프록시에서 사용자 인증 실패
		case ERROR_REDIRECT_LOOP:
			break; // 너무 많은 리디렉션
		case ERROR_TIMEOUT:
			break; // 연결 시간 초과
		case ERROR_TOO_MANY_REQUESTS:
			break; // 페이지 로드중 너무 많은 요청 발생
		case ERROR_UNKNOWN:
			break; // 일반 오류
		case ERROR_UNSUPPORTED_AUTH_SCHEME:
			break; // 지원되지 않는 인증 체계
		case ERROR_UNSUPPORTED_SCHEME:
			break; // URI가 지원되지 않는 방식
		}
	}

	public void setOnLinkedClickListener(OnLinkedClickListener aOnLinkedClickListener) {
		this._onLinkedClickListener = aOnLinkedClickListener;
	}

	public void setOnLoadCompleteListener(OnUrlLoadCompleteListener aOnUrlLoadCompleteListener) {
		this._onUrlLoadCompleteListener = aOnUrlLoadCompleteListener;
	}
}
