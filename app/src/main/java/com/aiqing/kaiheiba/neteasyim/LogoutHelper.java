package com.aiqing.kaiheiba.neteasyim;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ui.drop.DropManager;

/**
 * Created by Administrator on 2018/3/28.
 */

public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        NimUIKit.logout();
        DropManager.getInstance().destroy();
    }
}
