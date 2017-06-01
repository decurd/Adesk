package kr.co.roonets.adesk.constant;

/**
 * Created by shlee on 15. 7. 14..
 */
public class NetworkConstant {

    // HTTP TIME OUT SETTING
    public static final int HTTP_REQUEST_TIME_OUT = 10000;  // 10 sec
    // bufferstream 버퍼 사이즈
    public static final int BUFFER_SIZE = 1024 * 8;
    // upload boundary 값
    public static final String UPLOAD_BOUNDARY = "AC10E3D004C051143C7A";

    public static final String OAUTH_CLIENT_ID = "DE970DFE51";
    public static final String OAUTH_CLIENT_SECRET = "AC10E3D004C051143C7A";
    public static final String SERVER_HOST = "http://devpms.rpms.kr";

    // Token Regist URL
    public static final String TOKEN_REGIST_URL = "/NsmdP/pa/wpa010_s010";

}
