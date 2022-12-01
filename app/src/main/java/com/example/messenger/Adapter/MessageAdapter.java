package com.example.messenger.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messenger.Model.Chats;
import com.example.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    Context context;
    List<Chats> chatsList;
    String imageUrl;
    public static final int MESSAGE_RIGHT=0;
    public static final int MESSAGE_LEFT=1;

    private onItemClickListener listener;


    public MessageAdapter(Context context, List<Chats> chatsList,String imageUrl) {
        this.context = context;
        this.chatsList = chatsList;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MESSAGE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new ViewHolder(view);
        }else {
            View view=LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chats chats=chatsList.get(position);

        if (chats.getMessage().equals("null")){
            holder.messagetext.setVisibility(View.GONE);
        }else {
            holder.messagetext.setText(chats.getMessage());
        }
        if (chats.getImagePost().equals("zero")){
            holder.messageImg.setVisibility(View.GONE);
        }else {
            Glide.with(context).load(chats.getImagePost()).into(holder.messageImg);
        }
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        CircleImageView imageView;
        ImageView messageImg;
        TextView messagetext;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageImg=itemView.findViewById(R.id.post_Photo);
            imageView=itemView.findViewById(R.id.chat_image_ID);
            messagetext=itemView.findViewById(R.id.chat_text_ID);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {

            if (listener!=null){
                int position=getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            }

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            contextMenu.setHeaderTitle("Any Option");
            MenuItem delete=contextMenu.add(Menu.NONE,1,1,"Delete");
            MenuItem download=contextMenu.add(Menu.NONE,2,2,"Download");


            delete.setOnMenuItemClickListener(this);
            download.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            if (listener!=null){
                int position=getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    switch (menuItem.getItemId()){
                        case 1:
                            listener.onDelete(position);
                            return true;
                        case 2:
                            listener.onDownload(position);
                            return true;
                    }
                }
            }
            return false;
        }


    }

    public interface onItemClickListener{
        void onItemClick(int position);
        void onDelete(int position);
        void onDownload(int position);
    }

    public void setOnClickListener(onItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public int getItemViewType(int position) {

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        if (chatsList.get(position).getSender().equals(firebaseUser.getUid())){
            return MESSAGE_RIGHT;
        }else {
            return MESSAGE_LEFT;
        }
    }
}
