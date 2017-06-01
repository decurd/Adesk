package kr.co.roonets.adesk.b.client;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;

import java.io.File;

import kr.co.roonets.adesk.b.dialog.WebViewDialog;
import kr.co.roonets.adesk.b.utils.MediaHelper;
import kr.co.roonets.adesk.b.webview.MainWebView;

public class RoonetsWebChromeClient extends WebChromeClient {
    
    private Context context;
    
    /**
     * The webview dialog.
     *
     * @see WebViewDialog
     */
    private WebViewDialog webviewDialog;
    
    /** 웹사이트 로딩시 진행바. */
    private ProgressBar progressBar;

    /** WebView의 파일 업로드 메시지 콜백. */
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessages;
    
    private String imageFilePath = null;
    private String videoFilePath = null;
    
    public RoonetsWebChromeClient(Context context, WebViewDialog webviewDialog, ProgressBar progressBar) {
        // TODO Auto-generated constructor stub
        this.webviewDialog = webviewDialog;
        this.progressBar = progressBar;
    }
    
    public WebViewDialog getWebviewDialog() {
        return webviewDialog;
    }

    /* (non-Javadoc)
     * @see android.webkit.WebChromeClient#onConsoleMessage(android.webkit.ConsoleMessage)
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

        return super.onConsoleMessage(consoleMessage);
    }

    /* (non-Javadoc)
     * @see android.webkit.WebChromeClient#onCloseWindow(android.webkit.WebView)
     */
    @Override
    public void onCloseWindow(WebView window) {

        if (webviewDialog != null) {
            webviewDialog.removeWebView(window);
        }

        super.onCloseWindow(window);
    }

    /* (non-Javadoc)
     * @see android.webkit.WebChromeClient#onCreateWindow(android.webkit.WebView, boolean, boolean, android.os.Message)
     */
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
            boolean isUserGesture, Message resultMsg) {

        // Webview에서 window를 open하는 경우. 팝업으로 새로운 window를 보여준다.
        MainWebView childWebview = new MainWebView(view.getContext());

        childWebview.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        if (webviewDialog == null) {
            webviewDialog = new WebViewDialog(view.getContext(),
                    childWebview);
            webviewDialog
                    .setUserActionListener(new WebViewDialog.UserActionListener() {

                        @Override
                        public void onClose() {

                            webviewDialog = null;
                        }
                    });

            webviewDialog.show();
        } else {
            webviewDialog.addWebView(childWebview);
        }
        childWebview.init(webviewDialog, null, null);

        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(childWebview);
        resultMsg.sendToTarget();

        return true;
    }

    /* (non-Javadoc)
     * @see android.webkit.WebChromeClient#onGeolocationPermissionsShowPrompt(java.lang.String, android.webkit.GeolocationPermissions.Callback)
     */
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
            Callback callback) {
        // TODO Auto-generated method stub
        callback.invoke(origin, true, false);
    }

    /* (non-Javadoc)
     * @see android.webkit.WebChromeClient#onProgressChanged(android.webkit.WebView, int)
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        // TODO Auto-generated method stub
        super.onProgressChanged(view, newProgress);

        if (progressBar != null) {
            progressBar.setProgress(newProgress);
        }
    }

    public ValueCallback<Uri> getmUploadMessage() {
        return mUploadMessage;
    }

    public ValueCallback<Uri[]> getmUploadMessages() {
        return mUploadMessages;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void uploadReset() {
        this.mUploadMessage = null;
        this.mUploadMessages = null;
        this.imageFilePath = null;
        this.videoFilePath = null;
    }

    private Intent createImageOpenableIntent() {
        // Create and return a chooser with the default OPENABLE
        // actions including the camera, camcorder and sound
        // recorder where available.
        Intent imageIntent = new Intent(Intent.ACTION_PICK);
        imageIntent.setType("image/*");
        return imageIntent;
    }

    private Intent createVideoOpenableIntent() {
        // Create and return a chooser with the default OPENABLE
        // actions including the camera, camcorder and sound
        // recorder where available.
        Intent videoIntent = new Intent(Intent.ACTION_PICK);
        videoIntent.setType("video/*");
        return videoIntent;
    }

    private Intent createImageCaptureIntent() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                MediaHelper.CAPTURE_DIR);

        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }

        imageFilePath = imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis())
                + ".jpg";
        // Create camera captured image file path and name
        File file = new File(imageFilePath);

        Uri imageFileUri = Uri.fromFile(file);

        // Camera capture image intent
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        return captureIntent;
    }

    private Intent createVideoCaptureIntent() {
        File videoStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                MediaHelper.CAPTURE_DIR);

        if (!videoStorageDir.exists()) {
            // Create AndroidExampleFolder at sdcard
            videoStorageDir.mkdirs();
        }

        videoFilePath = videoStorageDir + File.separator + "MOV_" + String.valueOf(System.currentTimeMillis())
                + ".mp4";
        // Create camera captured image file path and name
        File file = new File(videoFilePath);

        Uri videoFileUri = Uri.fromFile(file);

        // Camera capture image intent
        Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFileUri);
        return captureIntent;
    }
    

}
