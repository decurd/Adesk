package kr.co.roonets.adesk;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.roonets.adesk.component.BaseAppCompatActivity;
import kr.co.roonets.adesk.component.EndlessRecyclerOnScrollListener;
import kr.co.roonets.adesk.i2api.I2ConnectApi;
import kr.co.roonets.adesk.i2api.I2ResponseParser;
import kr.co.roonets.adesk.i2api.I2UrlHelper;
import kr.co.roonets.adesk.utils.DialogUtil;
import kr.co.roonets.adesk.utils.PreferenceUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessageListActivity extends BaseAppCompatActivity {

    static String TAG = MessageListActivity.class.getSimpleName();
    private PreferenceUtil mPref;
    private RecyclerView mRV;
    private MessageRecyclerViewAdapter mAdapter;
    public boolean checkLoading = false;
    private int mListPage, mTotalCnt;
    private List<JSONObject> mMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        // Handle Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("받은 메세지");
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        setSupportActionBar(toolbar);

        mPref = PreferenceUtil.getInstance();

        mRV = (RecyclerView) findViewById(R.id.rv_message);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mRV.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRV.setLayoutManager(layoutManager);

        mRV.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (!checkLoading) {

                    if (mTotalCnt > mMessageList.size()) {
                        mListPage++;

                            loadUserFileList(mListPage);
                    }

                    checkLoading = true;
                }
            }
        });

        mTotalCnt = 0;
        mListPage = 1;
        mMessageList = new ArrayList<>();
        mAdapter = new MessageRecyclerViewAdapter(MessageListActivity.this, mMessageList);
        mRV.setAdapter(mAdapter);


        loadUserFileList(mListPage);
    }

    public void loadUserFileList(int page) {

        DialogUtil.showCircularProgressDialog(MessageListActivity.this);
        mPref = PreferenceUtil.getInstance();
        String uid = mPref.getString(PreferenceUtil.PREF_LOGIN_ID);
        String token = mPref.getString(PreferenceUtil.PREF_TOKEN);
        I2ConnectApi.requestJSON2XML(MessageListActivity.this, I2UrlHelper.ADESK.getListMessageByUser(uid, String.format("%d", page), token))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        checkLoading= false;
                        DialogUtil.removeCircularProgressDialog();
                        Log.d(TAG, "onCompleted: I2UrlHelper.ADESK.getListMessageByUser");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: I2UrlHelper.ADESK.getListMessageByUser");
                        e.printStackTrace();
                        DialogUtil.showErrorDialogWithValidateSession(MessageListActivity.this, e);
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext: I2UrlHelper.ADESK.getListMessageByUser");
                        //Log.e(TAG, "onNext: jsonObject = " + jsonObject);

                        if (I2ResponseParser.checkReponseStatus(jsonObject)) {

                            JSONObject statusInfo = I2ResponseParser.getStatusInfo(jsonObject);
                            List<JSONObject> statusInfoList = I2ResponseParser.getJsonArrayAsList(I2ResponseParser.getJsonObject(statusInfo, "LIST"), "ITEM");
                            Log.e(TAG, "onNext: statusInfoList = " + statusInfoList);
                            if (statusInfoList.size() > 0) {
                                try {
                                    mTotalCnt = Integer.parseInt(statusInfo.getString("LIST_TOTAL_COUNT"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mMessageList.addAll(statusInfoList);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                // Toast.makeText(getBaseContext(), getString(R.string.no_file_data_available), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), I2ResponseParser.getStatusMessage(jsonObject), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class MessageRecyclerViewAdapter
            extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

        protected Context mContext;

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<JSONObject> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {


            public final View mView;
            public final TextView mPushContent, mPushPurpose, mSendDate;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mPushPurpose = (TextView) view.findViewById(R.id.tv_push_purpose);
                mPushContent = (TextView) view.findViewById(R.id.tv_push_content);
                mSendDate = (TextView) view.findViewById(R.id.tv_send_date);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        }

        public JSONObject getValueAt(int position) {
            return mValues.get(position);
        }

        public MessageRecyclerViewAdapter(Context context, List<JSONObject> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mContext = context;
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            try {
                final JSONObject jsonObject = mValues.get(position);

                holder.mPushPurpose.setText(jsonObject.getString("PUSH_PURPOSE"));
                holder.mPushContent.setText(jsonObject.getString("PUSH_CONTENT"));
                holder.mSendDate.setText((jsonObject.getString("SEND_DATE")).substring(0, 16));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
