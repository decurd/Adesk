package kr.co.roonets.adesk.component;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import kr.co.roonets.adesk.R;


/**
 * Created by berserk1147 on 15. 8. 2..
 */
public class CircularProgressDialog extends Dialog {

    public CircularProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_circular_progress);
    }
}