package com.kodewiz.run;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kodewiz.run.data.Order;

import java.util.List;

public class OrderListAdapter extends ArrayAdapter<Order>{

    Context mContext;
    private List<Order> packsList;

    public OrderListAdapter(Context context, int resource, List<Order> list) {
        super(context, resource, list);
        mContext = context;
        packsList = list;

    }


    @Override
    public Order getItem(int position) {
        return packsList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Order order = getItem(position);
        String name = order.getName();
        String address = order.getAddress();
        String order_time = order.getOrderTime();

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_order, null);
            holder = new ViewHolder();
            holder.textView_name = (TextView) convertView.findViewById(R.id.textView_status);
            holder.textView_sub = (TextView) convertView.findViewById(R.id.textView_sub);
            holder.textView_order_time = (TextView) convertView.findViewById(R.id.textView_order_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView_name.setText(name);
        holder.textView_sub.setText("Address" + address);
        holder.textView_order_time.setText(order.getOrderTimeAsTimePassed());

        return convertView;
    }

    private class ViewHolder {
        TextView textView_name;
        TextView textView_sub;
        TextView textView_order_time;
    }
}
