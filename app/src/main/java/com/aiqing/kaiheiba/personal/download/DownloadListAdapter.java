package com.aiqing.kaiheiba.personal.download;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiqing.kaiheiba.R;
import com.aiqing.kaiheiba.common.BaseRecyclerViewAdapter;
import com.aiqing.kaiheiba.imageloader.ImageLoader;
import com.aiqing.kaiheiba.utils.DensityUtil;
import com.aiqing.kaiheiba.utils.Utils;
import com.buyi.huxq17.serviceagency.ServiceAgency;

public class DownloadListAdapter extends
        BaseRecyclerViewAdapter<DownloadItemBean, DownloadListAdapter.ViewHolder> {

    private static final String TAG = "DownloadListAdapter";
    private final Context context;

    public DownloadListAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_my_download, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DownloadItemBean data = getData(position);
//        try {
//            MyBusinessInfLocal myDownloadInfoById = dbController.findMyDownloadInfoById(data.getUri().hashCode());
//            if (myDownloadInfoById != null) {
//                holder.bindBaseInfo(myDownloadInfoById);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        holder.bindData(data, position, context);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_icon;
        private final TextView tv_size;
        private final ProgressBar pb;
        private final TextView tv_name;
        private final Button bt_action;
        private DownloadItemBean downloadInfo;
        private int itemPosition;

        public ViewHolder(View view) {
            super(view);
            itemView.setClickable(true);
            iv_icon = view.findViewById(R.id.dl_game_avatar);
            tv_size = view.findViewById(R.id.dl_game_process_text);
            pb = view.findViewById(R.id.dl_game_process);
            tv_name = view.findViewById(R.id.dl_game_name);
            bt_action = view.findViewById(R.id.dl_game_status);
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
            if (downloadInfo != null) {
//                downloadInfo.setDownloadListener(new MyDownloadListener(new SoftReference(ViewHolder.this)) {
//                    //  Call interval about one second
//                    @Override
//                    public void onRefresh() {
//                        LogUtils.e(" download onrefresh");
//                        if (getUserTag() != null && getUserTag().get() != null) {
//                            ViewHolder viewHolder = (ViewHolder) getUserTag().get();
//                            viewHolder.refresh();
//                        }
//                    }
//                });

            }
            refresh();
            bt_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadInfo != null) {

                    }
                }
            });

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
            int process = downloadInfo.process;
//                long completeTime = downloadInfo.getCompleteTime();
//                LogUtils.e("complete=" + completeTime);
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
            pb.setProgress(process);
            tv_size.setText(String.format("%d%%", process));
        }
    }
}
