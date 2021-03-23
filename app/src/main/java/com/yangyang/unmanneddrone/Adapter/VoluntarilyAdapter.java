package com.yangyang.unmanneddrone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yangyang.unmanneddrone.Body.VoluntarilyBody;
import com.yangyang.unmanneddrone.R;

import java.util.List;

//航线库
public class VoluntarilyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<VoluntarilyBody> mList;

    public VoluntarilyAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<VoluntarilyBody> mList) {
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
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_voluntarily_create, parent, false);
            return new ButtonViewHolder(view);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_voluntarily, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            // 点击监听
            ButtonViewHolder buttonViewHolder = (ButtonViewHolder) holder;
            buttonViewHolder.lineView.setOnClickListener(v -> {
                // 双击的情况---
                mListener.CreateNewLine(position);
            });
        } else {
            // 绑定数据
            DataViewHolder dataViewHolder = (DataViewHolder) holder;
            Glide.with(dataViewHolder.itemView)
                    .load(mList.get(position).getMap())
                    .placeholder(R.drawable.map)//加载中显示的图片
                    .error(R.drawable.map)// 错误后显示的图片
                    .into(dataViewHolder.mapView);
            dataViewHolder.titleView.setText(mList.get(position).getTitle());
            dataViewHolder.updateTimeView.setText(mList.get(position).getUpdate_time());
            dataViewHolder.locationView.setText(mList.get(position).getLocation());
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    // 数据
    public static class DataViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mapView;
        private final TextView titleView;
        private final TextView updateTimeView;
        private final TextView locationView;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mapView = itemView.findViewById(R.id.iv_map);
            titleView = itemView.findViewById(R.id.tv_title);
            updateTimeView = itemView.findViewById(R.id.tv_update_time);
            locationView = itemView.findViewById(R.id.tv_location);
        }
    }

    // 按钮
    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout lineView;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            lineView = itemView.findViewById(R.id.rl_create_line);
        }
    }

    public interface Listener {
        void CreateNewLine(int position);
    }

    private Listener mListener;

    public void setListener(Listener mListener) {
        this.mListener = mListener;
    }
}