package kr.co.roonets.adesk.b.webview;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Toast;

import java.net.URISyntaxException;

import kr.co.roonets.adesk.R;

/**
 * http와 https외의 URL에 대한 분기 처리.
 *
 * @author yeonkil choi
 * @since 2017. 3. 27
 */
public class WebUrlDelegator {
	
	/** The context. */
	private Context context;
	
	/**
	 * Instantiates a new web url delegator.
	 *
	 * @param context the context
	 */
	public WebUrlDelegator(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * Delegate url.
	 *
	 * @param url the url
	 * @return true, if successful
	 */
	public boolean delegateUrl(WebView webView, String url){
		if(!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url) && !URLUtil.isJavaScriptUrl(url)){
			return delegateUriScheme(webView, url);
		}
		return false;
	}

	/**
	 * Delegate uri scheme.
	 *
	 * @param url the url
	 * @return true, if successful
	 */
	private boolean delegateUriScheme(WebView webView, String url) {
		String packageName = null;
		try {
			Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
			packageName = intent.getPackage();
			context.startActivity(intent);
			return true;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActivityNotFoundException e) {
			// TODO: handle exception
			if(packageName != null){
				Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
				marketLaunch.setData(Uri.parse("market://details?id=" + packageName));
				context.startActivity(marketLaunch);
				return true;
			}else if(url.startsWith("kakaolink://")){
				Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
				marketLaunch.setData(Uri.parse("market://details?id=com.kakao.talk"));
				context.startActivity(marketLaunch);
				return true;
			}else if( url.startsWith("ispmobile://")){
				showIniPayAlert(webView);
			}else{
				return true;
			}
		}

		return false;
	}

	private void showIniPayAlert(final WebView webView){
		AlertDialog alertIsp = new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(R.string.alert_no_certification_app)
				.setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//ISP 설치 페이지 URL
						webView.loadUrl("http://mobile.vpay.co.kr/jsp/MISP/andown.jsp");
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(context, "(-1)결제를 취소 하셨습니다." , Toast.LENGTH_SHORT).show();
					}
				}).create();
		alertIsp.show();
	}
	


}
