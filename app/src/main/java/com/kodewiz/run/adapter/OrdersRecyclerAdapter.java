package com.kodewiz.run.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kodewiz.run.R;
import com.kodewiz.run.Utilities;
import com.kodewiz.run.activity.ActivityOrder;
import com.kodewiz.run.activity.ActivityOrderDetail;
import com.kodewiz.run.data.GlobalVariables;
import com.kodewiz.run.data.Order;
import com.kodewiz.run.view.CustomTypefaceSpan;
import com.kodewiz.run.view.ProgressWheel;

import java.util.List;

public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrdersRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Order> mList;
    private Typeface mTypeface_RobotoMedium;

    public OrdersRecyclerAdapter(Context context, List<Order> list) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mList = list;
        mTypeface_RobotoMedium = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto/roboto_bold.ttf");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.listitem_orders, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setup(mList.get(position), position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View mainView;
        ProgressWheel mProgressWheel;
        TextView textView_title;
        TextView textView_sub_1;
        TextView textView_sub_2;
        TextView textView_end;

        public MyViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            mProgressWheel = (ProgressWheel) itemView.findViewById(R.id.progress_wheel);
            textView_title = (TextView) itemView.findViewById(R.id.textView_title);
            textView_sub_1 = (TextView) itemView.findViewById(R.id.textView_sub_1);
            textView_sub_2 = (TextView) itemView.findViewById(R.id.textView_sub_2);
            textView_end = (TextView) itemView.findViewById(R.id.textView_price);
            itemView.setOnClickListener(this);
        }

        public void setup(Order item, int position){

            String query = ActivityOrder.mQuery;
            String name = item.getName();
            int start =  name.toLowerCase().indexOf(query.toLowerCase());
            int end = start + query.length();

            if(start < 0){
                start = 0;
            }

            if(end > name.length()){
                end = name.length();
            }

            if(!TextUtils.isEmpty(query)) {
                SpannableString text = new SpannableString(name);
                //text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(new CustomTypefaceSpan("", mTypeface_RobotoMedium), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView_title.setText(text, TextView.BufferType.SPANNABLE);
            }else{
                textView_title.setText(name);
            }


            int orderNo = getItemCount() - position;
            mProgressWheel.setProgress(item.getTimeInRadians());
            mProgressWheel.setText(String.valueOf(orderNo));
            textView_sub_1.setText(item.getItem());
            textView_sub_2.setText(Utilities.getRelativeTimeSpanStringFromDate(mContext, item.getDate()));
            textView_end.setText(item.getFormatedTotalPrice());
        }

        @Override
        public void onClick(View v) {
            GlobalVariables.getInstance().setSelectedOrders(mList.get(getLayoutPosition()));
            Intent i = new Intent(mContext, ActivityOrderDetail.class);
            mContext.startActivity(i);
        }
    }
}
