package com.yangyang.unmanneddrone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yangyang.unmanneddrone.body.MeasurementBody;
import com.yangyang.unmanneddrone.R;

import java.util.List;
/**
 * 测量记录
 */
public class MeasurementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private List<MeasurementBody> mList;

    public MeasurementAdapter(Context context) {
        this.mContext = context;
    }

    public List<MeasurementBody> getList() {
        return mList;
    }

    public void setList(List<MeasurementBody> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position) == null ? 0 : position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_measurement, parent, false);
        return new MeasurementAdapter.DataViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MeasurementAdapter.DataViewHolder dataViewHolder = (MeasurementAdapter.DataViewHolder) holder;
        dataViewHolder.mMeasurementNameView.setText(mList.get(position).getMeasurementName());
        dataViewHolder.mMeasureTimeView.setText(mList.get(position).getMeasureTime());
        dataViewHolder.mFlowView.setText(mList.get(position).getFlow());
        dataViewHolder.mAverageVelocityView.setText(mList.get(position).getAverageVelocity());
        dataViewHolder.mLlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.itemOnClick(position);
            }
        });
        // 下载
        dataViewHolder.mDownLoadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.downloadOnClick(position);
            }
        });
        // 修改
        dataViewHolder.mModifyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editOnClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    // 数据
    public static class DataViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mDownLoadView;
        private final ImageView mModifyView;
        private final TextView mMeasurementNameView;
        private final TextView mAverageVelocityView;
        private final TextView mMeasureTimeView;
        private final TextView mFlowView;
        private final LinearLayout mLlView;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mDownLoadView = itemView.findViewById(R.id.iv_download);
            mModifyView = itemView.findViewById(R.id.iv_modify);
            mMeasurementNameView = itemView.findViewById(R.id.tv_measurement_name);
            mAverageVelocityView = itemView.findViewById(R.id.tv_average_velocity);
            mMeasureTimeView = itemView.findViewById(R.id.tv_measure_time);
            mFlowView = itemView.findViewById(R.id.tv_flow);
            mLlView = itemView.findViewById(R.id.ll_measurement_parent);

        }

    }

    public interface Listener {
        /**
         * item点击事件
         */
        void itemOnClick(int position);

        /**
         * item的修改
         */
        void editOnClick(int position);

        /**
         * item的下载
         */
        void downloadOnClick(int position);
    }

    private MeasurementAdapter.Listener mListener;

    public void setListener(MeasurementAdapter.Listener mListener) {
        this.mListener = mListener;
    }

}
