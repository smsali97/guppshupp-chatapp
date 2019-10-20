package ua.naiksoftware.stompclientexample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.naiksoftware.R;
import ua.naiksoftware.stompclientexample.model.ChatMessage;

public class ChatMessageAdapter  extends BaseAdapter {

        List<ChatMessage> messages = new ArrayList<>();
        Context context;

        public ChatMessageAdapter(Context context) {
            this.context = context;
        }

        public void add(ChatMessage message) {
            this.messages.add(message);
            notifyDataSetChanged(); // to render the list we need to notify
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int i) {
            return messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            MessageViewHolder holder = new MessageViewHolder();
            LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            ChatMessage message = messages.get(i);




            if (message.getSender().equals(MainActivity.currentUsername)) { // this message was sent by us so let's create a basic chat bubble on the right
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.messageBody.setText(message.getContent());
            } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
                convertView = messageInflater.inflate(R.layout.their_message, null);
                holder.avatar = (View) convertView.findViewById(R.id.avatar);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);

                holder.name.setText(message.getSender().getUsername());
                holder.messageBody.setText(message.getContent());
                GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
                drawable.setColor(Color.rgb(194, 3, 252));
            }

            return convertView;
        }



    class MessageViewHolder {
        public View avatar;
        public TextView name;
        public TextView messageBody;
    }
}
