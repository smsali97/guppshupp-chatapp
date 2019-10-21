package ua.naiksoftware.stompclientexample;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.naiksoftware.R;
import ua.naiksoftware.stompclientexample.model.ChatMessage;
import ua.naiksoftware.stompclientexample.util.ChatUtil;

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

            if (message.getSender().getUsername().equals(ChatUtil.currentUsername)) { // this message was sent by us so let's create a basic chat bubble on the right
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);

                holder.timestamp.setText("Sent at: " + message.getTimestamp() + " \uD83D\uDD52");


                if (message.getType().equals(ChatMessage.MessageType.CHAT))
                holder.messageBody.setText(message.getContent());
                else if (message.getType().equals(ChatMessage.MessageType.STICKER)) {
                    holder.messageBody.setText("");
                    holder.messageBody.setBackgroundResource(android.R.color.transparent);
                    holder.iv = convertView.findViewById(R.id.my_message_image);
                    Resources res = holder.iv.getResources();
                    int resID = res.getIdentifier(message.getContent() , "drawable", holder.iv.getContext().getPackageName());
                    holder.iv.setImageResource(resID);
                }
                else if (message.getType().equals(ChatMessage.MessageType.JOIN)) {
                    holder.messageBody.setText(message.getSender().getUsername() + " just joined the chat!");
                    holder.messageBody.setTypeface(null, Typeface.ITALIC);
                }
                else if (message.getType().equals(ChatMessage.MessageType.LEAVE)) {
                    holder.messageBody.setText(message.getSender().getUsername() + " just left the chat :( ");
                    holder.messageBody.setTypeface(null, Typeface.ITALIC);
                }
            } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
                convertView = messageInflater.inflate(R.layout.their_message, null);
                holder.avatar = (View) convertView.findViewById(R.id.avatar);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);

                convertView.setTag(holder);

                holder.timestamp.setText("Sent at: " + message.getTimestamp() + " \uD83D\uDD52");
                holder.name.setText(message.getSender().getUsername());
                if (message.getType().equals(ChatMessage.MessageType.CHAT))
                    holder.messageBody.setText(message.getContent());
                else if (message.getType().equals(ChatMessage.MessageType.STICKER)) {
                    holder.messageBody.setText("");
                    holder.messageBody.setBackgroundResource(android.R.color.transparent);
                    holder.iv = convertView.findViewById(R.id.their_message_image);
                    Resources res = holder.iv.getResources();
                    int resID = res.getIdentifier(message.getContent() , "drawable", holder.iv.getContext().getPackageName());
                    holder.iv.setImageResource(resID);
                    Log.d("Image used", message.getContent());
                }
                else if (message.getType().equals(ChatMessage.MessageType.JOIN)) {
                    holder.messageBody.setText(message.getSender().getUsername() + " just joined the chat!");
                    holder.messageBody.setTypeface(null, Typeface.ITALIC);
                }
                else if (message.getType().equals(ChatMessage.MessageType.LEAVE)) {
                    holder.messageBody.setText(message.getSender().getUsername() + " just left the chat :( ");
                    holder.messageBody.setTypeface(null, Typeface.ITALIC);
                }
                GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
                drawable.setColor(Color.rgb(194, 3, 252));
            }

            return convertView;
        }



    class MessageViewHolder {
        public View avatar;
        public TextView name;
        public TextView messageBody;
        public TextView timestamp;
        public ImageView iv;

    }
}
