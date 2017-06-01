package kr.co.roonets.adesk.i2api;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import kr.co.roonets.adesk.constant.CodeConstant;
import kr.co.roonets.adesk.constant.NetworkConstant;
import kr.co.roonets.adesk.utils.PreferenceUtil;

/**
 * Created by berserk1147 on 2015. 7. 12..
 */
public class I2UrlHelper {



    public static Request getXmlPageRequest(String strURL, RequestBody formBody) {
        Request request = new Request.Builder()
                .url(strURL)
                .header("Authorization", "Bearer " + PreferenceUtil.getInstance().getString(PreferenceUtil.PREF_TOKEN))
                .addHeader("User-Agent", "andorid mobile")
                .post(formBody)
                .build();

        return request;
    }

    public static class ADESK {

        private static final String INIT_PAGE_SELECT            = "/nsmdp/pa/wpa010_s010/main?DEV_NO=&HP_NO=&PNO=AD328";
        private static final String MESSAGE_PAGE_SELECT         = "/NsmdP/pa/wpa140_s100/main";

        /**
         * 초기화면 select
         */
        public static Request getSelectInitPage() {
            return getSelectPage();
        }

        public static Request getSelectPage() {
            StringBuilder builder = new StringBuilder();
            builder.append(NetworkConstant.SERVER_HOST);
            builder.append(INIT_PAGE_SELECT);

            RequestBody formBody = new FormEncodingBuilder()
                    .build();

            return getXmlPageRequest(builder.toString(), formBody);
        }

        public static Request getListMessageByUser(String uid, String page, String token) {
            StringBuilder builder = new StringBuilder();
            builder.append(NetworkConstant.SERVER_HOST);
            builder.append(MESSAGE_PAGE_SELECT);


            RequestBody formBody = new FormEncodingBuilder()
                    .add("DEV_NO", "")
                    .add("HP_NO", "")
                    .add("PNO", CodeConstant.PNO)
                    .add("mtkey", "")
                    .add("UID", uid)
                    //.add("UID", "sake0226")
                    .add("PAGEINDEX", page)
                    .add("PAGESIZE", CodeConstant.LIMIT)
                    .add("TOKEN", token)
                    .build();

            return getXmlPageRequest(builder.toString(), formBody);
        }
    }
}
