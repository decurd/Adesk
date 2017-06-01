package kr.co.roonets.adesk.a;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import kr.co.roonets.adesk.R;

public class SectionA_mainFragment extends Fragment {
    static String TAG = SectionA_mainFragment.class.getSimpleName();

    public static final String MODE = "mode";
    protected String mPostMode = "";
    private AppCompatActivity acActivity;

    protected UltimateRecyclerView mUltiRV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_section_a_main, container, false);

        Bundle bundle = this.getArguments();
        // String title = "";
        String title = "PMS관리";
        if (bundle != null) {
            //mPostMode = bundle.getString(MODE, "FOLLOW");
            //title= bundle.getString(CodeConstant.TITLE, getString(R.string.connect_community));
        }

        acActivity = (AppCompatActivity)getActivity();
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        acActivity.setSupportActionBar(toolbar);

        // ((MainActivity) acActivity).setVisibleFabButton(true);

        final ActionBar ab = acActivity.getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(title);

        return v;
    }

}
