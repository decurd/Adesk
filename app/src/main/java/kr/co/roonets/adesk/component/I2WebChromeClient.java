package kr.co.roonets.adesk.component;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Message;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.co.roonets.adesk.MainActivity;
import kr.co.roonets.adesk.R;
import kr.co.roonets.adesk.b.SectionB_mainFragment;
import kr.co.roonets.adesk.utils.DialogUtil;


public class I2WebChromeClient extends WebChromeClient {
	private Context mContext;
	private SectionB_mainFragment mCurFregment;
	private ValueCallback<Uri> mUploadMessage;
	public final static int FILECHOOSER_RESULTCODE = 5555;

	public I2WebChromeClient(Context context) {
		mContext = context;
	}

	public I2WebChromeClient(Context context, ValueCallback<Uri> uploadMsg) {
		mContext = context;
		mUploadMessage = uploadMsg;
	}

	public I2WebChromeClient(Context context, SectionB_mainFragment webviewFragment, ValueCallback<Uri> uploadMsg) {
		mContext = context;
		mCurFregment = webviewFragment;
		mUploadMessage = uploadMsg;
	}

	@Override
	public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
		Log.d("I2WebChromeClient", "onCreateWindow "+resultMsg);

		WebView newWebView = new WebView(view.getContext());
		newWebView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.contains("i2livechat")) {
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
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.d("I2WebChromeClient", "onCreateWindow onPageStarted url "+url);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri u = Uri.parse(url);
				intent.setData(u);
				mContext.startActivity(intent);
				//super.onPageStarted(view, url, favicon);
			}
		});

		WebView.WebViewTransport transport = (WebView.WebViewTransport)resultMsg.obj;
		transport.setWebView(newWebView);
		resultMsg.sendToTarget();

		return true;
	}

	@Override
	public void onCloseWindow(WebView window) {
		super.onCloseWindow(window);

//		mWebViewContainer.removeView(window);    // 화면에서 제거
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.app_name))
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int wicht) {
				result.confirm();
			}
		}).setCancelable(false).create().show();
		return true;
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		new AlertDialog.Builder(mContext)
			.setTitle(mContext.getString(R.string.app_name))
			.setMessage(message)
			.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
	                	public void onClick(DialogInterface dialog, int which) {
	                		result.confirm();
	                	}
			}).setNegativeButton(android.R.string.cancel, 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
			})
			.create()
			.show();
		return true;
	}

	//The undocumented magic method override
	//Eclipse will swear at you if you try to put @Override here
	// For Android 3.0+
	public void openFileChooser(ValueCallback<Uri> uploadMsg) {
		Log.e("I2WebChromeClient", "openFileChooser For Android 3.0+");
		mUploadMessage = uploadMsg;
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("*/*");
		if(mCurFregment != null)mCurFregment.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);
		else ((MainActivity)mContext).startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);

	}

	// For Android 3.0+
	public void openFileChooser(ValueCallback uploadMsg, String acceptType ) {
		Log.e("I2WebChromeClient", "openFileChooser 3.0+");
		mUploadMessage = uploadMsg;
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("*/*");
		if(mCurFregment != null)mCurFregment.startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
		else ((MainActivity)mContext).startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
	}

	//For Android 4.1
	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
		Log.e("I2WebChromeClient", "openFileChooser 4.1");
		mUploadMessage = uploadMsg;
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("*/*");
		if(mCurFregment != null)mCurFregment.startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );
		else ((MainActivity)mContext).startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );

	}
}
