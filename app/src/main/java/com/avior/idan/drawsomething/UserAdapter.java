package com.avior.idan.drawsomething;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Idan Avior on 10/1/2017.
 */

/**
 * UserAdapter
 */
public class UserAdapter extends ArrayAdapter<User> {

    private List<User> userList;
    public UserAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> users) {
        super(context, resource);
        userList = users;
    }

    @Override
    public int getCount(){return userList.size(); }

    @Override
    public @Nullable User getItem(int position){return userList.get(position);}


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if (convertView == null){

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.select_user_list_item, null);
        }
        final TextView textView = (TextView) convertView.findViewById(R.id.username);
        textView.setText(userList.get(position).getEmail());
        return convertView;
    }
}
