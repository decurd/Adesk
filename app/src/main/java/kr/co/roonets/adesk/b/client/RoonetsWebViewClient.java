package kr.co.roonets.adesk.b.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import kr.co.roonets.adesk.b.constant.Conts;
import kr.co.roonets.adesk.b.webview.WebUrlDelegator;

public class RoonetsWebViewClient extends WebViewClient {
    
    /** @see WebUrlDelegator */
    private WebUrlDelegator webUrlDelegator;
    
    /** 웹사이트 로딩시 진행바. */
    private ProgressBar progressBar;
    
    public RoonetsWebViewClient(Context context, ProgressBar progressBar) {
        // TODO Auto-generated constructor stub
        this.webUrlDelegator = new WebUrlDelegator(context);
        this.progressBar = progressBar;
    }
    
    /* (non-Javadoc)
     * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!webUrlDelegator.delegateUrl(view, url)) {
            view.loadUrl(url);
        }

        return true;
    }

    /* (non-Javadoc)
     * @see android.webkit.WebViewClient#onPageStarted(android.webkit.WebView, java.lang.String, android.graphics.Bitmap)
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // TODO Auto-generated method stub
        super.onPageStarted(view, url, favicon);

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /* (non-Javadoc)
     * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        super.onPageFinished(view, url);
        
        Context context = view.getContext();
        if(context != null) context.sendBroadcast(new Intent(Conts.ACTION_LOADED_URL_CHANGED));
        
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /* (non-Javadoc)
     * @see android.webkit.WebViewClient#onFormResubmission(android.webkit.WebView, android.os.Message, android.os.Message)
     */
    @Override
    public void onFormResubmission(WebView view, Message dontResend,
            Message resend) {
        //요청된 페이지가 POST DATA에 의한 결과 페이지 일 경우 Post Data를 다시 전송
        //Webview에서 history back시에 오류가 발생할 수 있어 추가 
        resend.sendToTarget();
        super.onFormResubmission(view, dontResend, resend);
    }
}
