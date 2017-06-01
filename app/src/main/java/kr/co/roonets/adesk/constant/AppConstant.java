package kr.co.roonets.adesk.constant;

import android.os.Environment;

import kr.co.roonets.adesk.utils.FileUtil;

/**
 * Created by decurd on 2017-03-20.
 */

public class AppConstant {

    //테스트옵션 :  기능메뉴를 서버에서 받을지 아니면 로컬에서 읽을지
    public static final boolean MENU_LOCAL_JSON_ENABLED = true;
    //GCM옵션 :  Google Cloud Message 서비스 사용여부
    public static final boolean GCM_ENABLED = true;


    /****
     * ROOT PATH
     ****/
    public static final String ROOT_PATH = "/.i2root";
    public static final String WV_ASSET_ROOT = "file:///android_asset";
    public static final String WV_EXSTORAGE_ROOT = "file://" + Environment.getExternalStorageDirectory() + ROOT_PATH;


    /****
     * Download PATH
     ****/
    public static final String WEB_CONTENTS_PATH = "/mobile_phone";
    // public static final String ROOT_DOWNLOAD_ZIP_PATH = FileUtil.getRootPathFromExternalSD(ROOT_PATH + WEB_CONTENTS_PATH);
    // public static final String ROOT_DOWNLOAD_FILE_PATH = FileUtil.getRootPathFromExternalSD(ROOT_PATH + "/download");
    public static final String ROOT_DOWNLOAD_IMAGE_PATH = FileUtil.getRootPathFromExternalSD(ROOT_PATH + "/images");



    /*** security mode */
    public static final boolean SECURITY_MODE_ENABLED = false;



}
