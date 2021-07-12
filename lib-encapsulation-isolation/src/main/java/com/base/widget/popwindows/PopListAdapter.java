package com.base.widget.popwindows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capinfo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 */

public class PopListAdapter extends RecyclerView.Adapter{
    private List<PopWinMenuBean> mData = new ArrayList<>();

    private OnPopMenuClick mOnPopMenuClick;

    public interface OnPopMenuClick{
        void onMenuClick(PopWinMenuBean.PopMenuItemType popItemType);
    }


    public void setOnPopMenuClick(OnPopMenuClick onPopMenuClick){
        this.mOnPopMenuClick = onPopMenuClick;
    }

    public void setData(List<PopWinMenuBean> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popwindow_list_item,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       ViewHolder viewHolder = (ViewHolder) holder;
       PopWinMenuBean curBean = mData.get(position);
       viewHolder.mTextView.setText(curBean.content);
       viewHolder.mImageView.setImageResource(curBean.imgRes);
       viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(mOnPopMenuClick != null){
                   mOnPopMenuClick.onMenuClick(curBean.itemType);
               }
           }
       });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0:mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text_content);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_img);
        }
    }
}
