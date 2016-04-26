package com.kodewiz.run.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kodewiz.run.R;
import com.kodewiz.run.Utilities;
import com.kodewiz.run.data.Order;

import java.util.List;

public class OrdersAdapter extends ArrayAdapter<Order>{

    Context mContext;
    private List<Order> list;

    public OrdersAdapter(Context context, int resource, List<Order> list) {
        super(context, resource, list);
        mContext = context;
        this.list = list;
    }

    @Override
    public Order getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Order item = getItem(position);
        int orderNo = getCount()-position;

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_orders, null);
            holder = new ViewHolder();
            holder.textView_title = (TextView) convertView.findViewById(R.id.textView_title);
            holder.textView_sub_1 = (TextView) convertView.findViewById(R.id.textView_sub_1);
            holder.textView_sub_2 = (TextView) convertView.findViewById(R.id.textView_sub_2);
            holder.textView_end = (TextView) convertView.findViewById(R.id.textView_price);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView_title.setText(item.getName());
        holder.textView_sub_1.setText(item.getItem());
        holder.textView_sub_2.setText(Utilities.getRelativeTimeSpanStringFromDate(getContext(), item.getDate()));
        holder.textView_end.setText("\u20B9" + item.getTotalPrice());

        return convertView;
    }

    private class ViewHolder {
        TextView textView_title;
        TextView textView_sub_1;
        TextView textView_sub_2;
        TextView textView_end;
    }
}
