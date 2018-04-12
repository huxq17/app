package com.aiqing.kaiheiba.neteasyim;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseFragment;
import com.aiqing.kaiheiba.neteasyim.main.main.fragment.MainTabFragment;
import com.aiqing.kaiheiba.neteasyim.main.main.helper.SystemMessageUnreadManager;
import com.aiqing.kaiheiba.neteasyim.main.main.reminder.ReminderItem;
import com.aiqing.kaiheiba.neteasyim.main.main.reminder.ReminderManager;
import com.netease.nim.uikit.common.fragment.TabFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;

import java.util.List;


public class IMFragment extends BaseFragment implements TabFragment.State, ReminderManager.UnreadNumChangedCallback {
    MainTabFragment sessionListFragment;
    MainTabFragment contactListFragment;
    FragmentManager fm;
    RadioGroup tab;
    MainTabFragment currentFragment;
    FrameLayout flSession, flContact;
    int checkedId = R.id.rb_im_sessionlist;

    public IMFragment() {
    }

    public static IMFragment newInstance(int id) {
        IMFragment fragment = new IMFragment();
        Bundle args = new Bundle();
        args.putInt(BaseFragment.ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 注册/注销系统消息未读数变化
     *
     * @param register
     */
    private void registerSystemMessageObservers(boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver,
                register);
    }

    private Observer<Integer> sysMsgUnreadCountChangedObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer unreadCount) {
            SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unreadCount);
            ReminderManager.getInstance().updateContactUnreadNum(unreadCount);
        }
    };

    /**
     * 查询系统消息未读数
     */
    private void requestSystemMessageUnreadCount() {
        int unread = NIMClient.getService(SystemMessageService.class).querySystemMessageUnreadCountBlock();
        SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unread);
        ReminderManager.getInstance().updateContactUnreadNum(unread);
    }

    /**
     * 注册未读消息数量观察者
     */
    private void registerMsgUnreadInfoObserver(boolean register) {
        if (register) {
            ReminderManager.getInstance().registerUnreadNumChangedCallback(this);
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fm = getChildFragmentManager();
        View view = inflater.inflate(R.layout.frg_im, container, false);
        sessionListFragment = newFrg(MainTab.RECENT_CONTACTS);
        contactListFragment = newFrg(MainTab.CONTACT);
        if (!sessionListFragment.isAdded()) {
            fm.beginTransaction().add(R.id.frg_sessionlist, sessionListFragment).commit();
        } else {
            fm.beginTransaction().show(sessionListFragment).commit();
        }
        if (!contactListFragment.isAdded()) {
            fm.beginTransaction().add(R.id.frg_contactlist, contactListFragment).commit();
        } else {
            fm.beginTransaction().show(contactListFragment).commit();
        }
        flSession = view.findViewById(R.id.frg_sessionlist);
        flContact = view.findViewById(R.id.frg_contactlist);
        tab = view.findViewById(R.id.tab_im);
        tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                IMFragment.this.checkedId = checkedId;
                if (checkedId == R.id.rb_im_sessionlist) {
                    currentFragment = sessionListFragment;
                    flSession.setVisibility(View.VISIBLE);
                    flContact.setVisibility(View.GONE);
                } else if (checkedId == R.id.rb_im_contactlist) {
                    currentFragment = contactListFragment;
                    flSession.setVisibility(View.GONE);
                    flContact.setVisibility(View.VISIBLE);
                }
            }
        });
//        tab.check(checkedId);
        ((RadioButton) tab.findViewById(checkedId)).toggle();
        registerMsgUnreadInfoObserver(true);
        registerSystemMessageObservers(true);
        requestSystemMessageUnreadCount();
        return view;
    }

    private MainTabFragment newFrg(MainTab tab) {
        MainTabFragment fragment = null;
        try {
            List<Fragment> fs = fm.getFragments();
            if (fs != null) {
                for (Fragment f : fs) {
                    if (f.getClass() == tab.clazz) {
                        fragment = (MainTabFragment) f;
                        break;
                    }
                }
            }
            if (fragment == null) {
                fragment = tab.clazz.newInstance();
            }
            fragment.setState(this);
            fragment.attachTabData(tab);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    @Override
    public boolean isCurrent(TabFragment fragment) {
        return currentFragment == fragment;
    }

    @Override
    public void onUnreadNumChanged(ReminderItem item) {
//        MainTab tab = MainTab.fromReminderId(item.getId());
//        if (tab != null) {
//            tabs.upd ateTab(tab.tabIndex, item);
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        registerMsgUnreadInfoObserver(false);
        registerSystemMessageObservers(false);
    }
}
