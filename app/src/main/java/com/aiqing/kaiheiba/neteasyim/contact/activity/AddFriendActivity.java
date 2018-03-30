package com.aiqing.kaiheiba.neteasyim.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.api.ApiManager;
import com.aiqing.kaiheiba.api.UserApi;
import com.aiqing.kaiheiba.bean.AccountBean;
import com.aiqing.kaiheiba.neteasyim.DemoCache;
import com.aiqing.kaiheiba.rxjava.BaseObserver;
import com.aiqing.kaiheiba.rxjava.RxSchedulers;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;

import java.util.List;

/**
 * 添加好友页面
 * Created by huangjun on 2015/8/11.
 */
public class AddFriendActivity extends UI {

    private ClearableEditTextWithIcon searchEdit;
    RecyclerView searchResultView;
    SearchResultAdapter mAdapter;

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AddFriendActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_activity);

        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.add_buddy;
        setToolBar(R.id.toolbar, options);

        findViews();
        initActionbar();
    }

    private void findViews() {
        searchEdit = findView(R.id.search_friend_edit);
        searchEdit.setDeleteImage(R.drawable.nim_grey_delete_icon);
    }

    private void initActionbar() {
        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        toolbarView.setText(R.string.search);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchEdit.getText().toString())) {
                    Toast.makeText(AddFriendActivity.this, R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                } else if (searchEdit.getText().toString().equals(DemoCache.getAccount())) {
                    Toast.makeText(AddFriendActivity.this, R.string.add_friend_self_tip, Toast.LENGTH_SHORT).show();
                } else {
                    query();
                }
            }
        });
        searchResultView = findView(R.id.search_result);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        searchResultView.setLayoutManager(layoutmanager);
        mAdapter = new SearchResultAdapter();
        searchResultView.setAdapter(mAdapter);
    }

//    private Observable<BaseResponse<AccountBean>> searchOnServer(final BaseResponse<AccountBean> bean) {
//        return Observable.create(new ObservableOnSubscribe<BaseResponse<AccountBean>>() {
//            @Override
//            public void subscribe(final ObservableEmitter<BaseResponse<AccountBean>> e) throws Exception {
//                if (bean == null) {
//                    e.onNext(bean);
//                    return;
//                }
//                int code = bean.getCode();
//                if (code == Code.OK) {
//                    final AccountBean user = bean.getData();
//                    String account = user.getAccid();
//                    NimUIKit.getUserInfoProvider().getUserInfoAsync(account, new SimpleCallback<NimUserInfo>() {
//                        @Override
//                        public void onResult(boolean success, NimUserInfo result, int code) {
//                            DialogMaker.dismissProgressDialog();
//                            if (success) {
//                                if (result == null) {
//                                    EasyAlertDialogHelper.showOneButtonDiolag(AddFriendActivity.this, R.string.user_not_exsit,
//                                            R.string.user_tips, R.string.ok, false, null);
//                                } else {
////                                    UserProfileActivity.start(AddFriendActivity.this, account);
//                                }
//                            } else if (code == 408) {
//                                e.onError(new Exception("网络连接失败，请检查你的网络设置"));
//                            } else if (code == ResponseCode.RES_EXCEPTION) {
//                                e.onError(new Exception("on exception"));
//                                Toast.makeText(AddFriendActivity.this, "on exception", Toast.LENGTH_SHORT).show();
//                            } else {
//                                e.onError(new Exception("on failed:" + code));
//                            }
//                        }
//                    });
//                } else {
//                    e.onNext(bean);
//                }
//
//            }
//        }).compose(RxSchedulers.<LoginApi.Bean>compose());
//    }

    private void query() {
        DialogMaker.showProgressDialog(this, null, false);
        final String account = searchEdit.getText().toString().toLowerCase();
        ApiManager.INSTANCE.getApi(UserApi.class).search(account)
                .compose(RxSchedulers.<UserApi.SearchBean>compose())
                .subscribe(new BaseObserver<List<AccountBean>>() {
                    @Override
                    protected void onSuccess(List<AccountBean> accountBeans) {
                        DialogMaker.dismissProgressDialog();
                        mAdapter.setData(accountBeans);
                    }

                    @Override
                    protected void onFailed(String msg) {
                        super.onFailed(msg);
                        DialogMaker.dismissProgressDialog();
                    }
                });

    }
}
