package com.aiqing.kaiheiba.personal.download;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseRecyclerViewAdapter;
import com.aiqing.kaiheiba.download.DBService;
import com.aiqing.kaiheiba.imageloader.ImageLoader;
import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.utils.Utils;
import com.buyi.huxq17.serviceagency.ServiceAgency;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadListAdapter extends BaseRecyclerViewAdapter<DownloadItemBean, DownloadListAdapter.ViewHolder> {

    private final Context context;
    private MyDownloadAct act;
    private android.app.DownloadManager downloadManager;

    public DownloadListAdapter(Context context) {
        super(context);
        this.context = context;
        act = (MyDownloadAct) context;
        downloadManager = (android.app.DownloadManager) act.getSystemService(DOWNLOAD_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_my_download, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DownloadItemBean data = getData(position);
        holder.bindData(data, position, context);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_icon;
        private final TextView tv_size;
        private final ProgressBar pb;
        private final TextView tv_name;
        private final Button bt_action;
        private DownloadItemBean downloadInfo;
        private int itemPosition;
        private View.OnClickListener btOnClickListenr;

        public void setOnClickListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(itemPosition);
                }
            });
        }

        private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isClick = true;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.performClick();
                        isClick = false;
                        break;
                }
                return false;
            }
        };

        public ViewHolder(final View view) {
            super(view);
            itemView.setClickable(true);
            iv_icon = view.findViewById(R.id.dl_game_avatar);
            tv_size = view.findViewById(R.id.dl_game_process_text);
            pb = view.findViewById(R.id.dl_game_process);
            tv_name = view.findViewById(R.id.dl_game_name);
            bt_action = view.findViewById(R.id.dl_game_status);
            setOnClickListener();
            itemView.setOnTouchListener(onTouchListener);
            btOnClickListenr = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = bt_action.getText().toString();
                    if (text.equals("删除")) {
                        DBService.getInstance(context).deleteGroup(downloadInfo.url);
//                        Utils.deleteFileSafely(new File(downloadInfo.filePath));
                        downloadManager.remove(downloadInfo.id);
                    } else if (text.equals("取消")) {
                        downloadManager.remove(downloadInfo.id);
                        DBService.getInstance(context).deleteGroup(downloadInfo.url);
                    }
                    act.mockData();
                }
            };
            bt_action.setOnClickListener(btOnClickListenr);
            bt_action.setOnTouchListener(onTouchListener);

        }

        public void bindBaseInfo(MyBusinessInfLocal myBusinessInfLocal) {
            ServiceAgency.getService(ImageLoader.class).loadImage(myBusinessInfLocal.getIcon(), iv_icon);
            tv_name.setText(myBusinessInfLocal.getName());
        }

        @SuppressWarnings("unchecked")
        public void bindData(final DownloadItemBean data, final int position, final Context context) {
            this.itemPosition = position;
            // Get download task status
            downloadInfo = data;
            // Set a download listener
            refresh();
            ServiceAgency.getService(ImageLoader.class).loadImage(downloadInfo.avatarUrl, iv_icon);
            String name = downloadInfo.name;
            tv_name.setText(name.replace(".apk", ""));
            int round = DensityUtil.dip2px(context, 5);
            int strokenWidth = DensityUtil.dip2px(context, 1);
            int background = Color.TRANSPARENT;
            int textColor;
            int strokenColor = Color.parseColor("#F48432");
            textColor = Color.parseColor("#F48432");
            Drawable drawable = Utils.setStrokenBg(strokenWidth, round, strokenColor, background);
            bt_action.setBackgroundDrawable(drawable);
            bt_action.setTextColor(textColor);
            bt_action.setTextSize(14);
        }

        private void refresh() {
            int percent = downloadInfo.progress;
//                long downloadSize = downloadInfo.getSize();
//                switch (downloadInfo.getStatus()) {
//                    case DownloadInfo.STATUS_NONE:
//                        bt_action.setText("下载");
//                        break;
//                    case DownloadInfo.STATUS_PAUSED:
//                    case DownloadInfo.STATUS_ERROR:
//                        bt_action.setText("继续");
//                        if (downloadSize > 0) {
//                            process = (int) (downloadInfo.getProgress() * 100 / downloadInfo.getSize());
//                        }
//                        break;
//                    case DownloadInfo.STATUS_DOWNLOADING:
//                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
//                        bt_action.setText("停止");
//                        if (downloadSize > 0) {
//                            process = (int) (downloadInfo.getProgress() * 100 / downloadInfo.getSize());
//                        }
//                        break;
//                    case STATUS_COMPLETED:
//                        bt_action.setText("删除");
//                        if (downloadSize > 0) {
//                            process = (int) (downloadInfo.getProgress() * 100 / downloadInfo.getSize());
//                        }
//                        break;
//                    case STATUS_REMOVED:
//                        tv_size.setText("");
//                        bt_action.setText("下载");
//                    case STATUS_WAIT:
//                        tv_size.setText("");
//                        bt_action.setText("停止");
//                        break;
//                }
            pb.setProgress(percent);
            tv_size.setText(String.format("%d%%", percent));
            if (percent == 100) {
                bt_action.setText("删除");
            } else {
                bt_action.setText("取消");
                bt_action.setTag(downloadInfo.url);
            }
        }
    }
}
