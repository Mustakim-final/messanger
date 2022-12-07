package com.example.messenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messenger.MessageActivity;
import com.example.messenger.Model.Chats;
import com.example.messenger.Model.Users;
import com.example.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context context;
    List<Users> userList;
    boolean isChat;

    String lastMessage,lastMessage1;

    String userID;

    FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<Users> userList, boolean isChat) {
        this.context = context;
        this.userList = userList;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users=userList.get(position);

        userID=users.getId();

        holder.textView.setText(users.getUsername());

        if (users.getImageUrL().equals("default")){
            holder.circleImageView.setImageResource(R.drawable.ic_baseline_perm_identity_24);
        }else {
            Glide.with(context).load(users.getImageUrL()).into(holder.circleImageView);
        }

        if (isChat){
            if (users.getStatus().equals("online")){
                holder.img_online.setVisibility(View.VISIBLE);
                holder.img_offline.setVisibility(View.GONE);
            }else {
                holder.img_online.setVisibility(View.GONE);
                holder.img_offline.setVisibility(View.VISIBLE);
            }
        }else {
            holder.img_online.setVisibility(View.GONE);
            holder.img_offline.setVisibility(View.GONE);
        }

        if (isChat){
            LastMessage(users.getId(),holder.lastMessageText);
        }else {
            holder.lastMessageText.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CircleImageView circleImageView,img_online,img_offline;

        private TextView textView,lastMessageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.userProfileListImg_ID);
            img_online=itemView.findViewById(R.id.onLine_ID);
            img_offline=itemView.findViewById(R.id.offLine_ID);
            textView=itemView.findViewById(R.id.userProfileList_ID);
            lastMessageText=itemView.findViewById(R.id.lastMessage_ID);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Users users=userList.get(getAdapterPosition());

            userID=users.getId();

            Intent intent=new Intent(context,MessageActivity.class);
            intent.putExtra("userID",userID);
            context.startActivity(intent);

        }
    }




    public void LastMessage(final String userID,final TextView lastMessageText){

        lastMessage="default";
        lastMessage1="photo";
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chats chats=dataSnapshot.getValue(Chats.class);

                    if (firebaseUser!=null && chats!=null){
                        if (chats.getSender().equals(userID) && chats.getReciver().equals(firebaseUser.getUid()) || chats.getSender().equals(firebaseUser.getUid()) && chats.getReciver().equals(userID)){
                            lastMessage=chats.getMessage();
                        }
                    }
                }

                switch (lastMessage){
                    case "default":
                        lastMessageText.setText("No message");
                        break;
                    default:
                        lastMessageText.setText(lastMessage);
                }
                lastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
