package kr.co.roonets.adesk.b.utils;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The Class CommonUtils.
 *
 * @author yeonkil choi
 * @since 2017. 3. 27
 */
public class CommonUtils {
    
    /**
     * File to base64.
     *
     * @param filePath the file path
     * @return the string
     * @throws FileNotFoundException the file not found exception
     */
    public static String fileToBase64(String filePath)
            throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(filePath);
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        bytes = output.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    
    /**
     * Creates a new AlertDialog object.
     *
     * @param context the context
     * @param title the title
     * @param message the message
     * @param yesBtnText Positive 버튼의 표시 문자열
     * @param noBtnText Negative 버튼의 표시 문자열
     * @param onClickListener 다이알로그 버튼 클릭시 리스너
     * @return the alert dialog
     */
    public static AlertDialog createYesNoDialog(Context context, String title, String message, String yesBtnText,
                                                String noBtnText, OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setCancelable(false).setPositiveButton(yesBtnText, onClickListener)
                .setNegativeButton(noBtnText, onClickListener);
        return builder.create();
    }
    
    /**
     * Creates the one button dialog.
     *
     * @param context the context
     * @param title the title
     * @param message the message
     * @param btnText the btn text
     * @param onClickListener the on click listener
     * @return the alert dialog
     */
    public static AlertDialog createOneButtonDialog(Context context, String title, String message, String btnText,
                                                    OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setCancelable(false).setPositiveButton(btnText, onClickListener);
        return builder.create();
    }
    
    /**
     * push 알림 받을 것인가 여부를 DB에 저장
     * @param isUse
     */
    public static void pushSetting(Context context, boolean isUse){
        SettingsUtil.setPushUse(context, isUse);
    }
}
