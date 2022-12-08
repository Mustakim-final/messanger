package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.example.messenger.Adapter.MessageAdapter;
import com.example.messenger.Model.Chats;
import com.example.messenger.Model.Users;
import com.example.messenger.Notification.ApiService;
import com.example.messenger.Notification.Client;
import com.example.messenger.Notification.Data;
import com.example.messenger.Notification.MyResponse;
import com.example.messenger.Notification.Sender;
import com.example.messenger.Notification.Token;
import com.example.messenger.Notification.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView profileText;

    EditText messageEditText;
    ImageButton sentMegBtn;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String userID,message,myId;
    Intent intent;

    List<Chats> chatsList;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;

    ImageButton imageButton;
    private Uri imageUri=null;

    private static final int GALLERY_CODE1=300;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    StorageTask storageTask;

    ApiService apiService;
    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        circleImageView=findViewById(R.id.profileImage_ID);
        profileText=findViewById(R.id.profileName_ID);
        imageButton=findViewById(R.id.imageViewBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        apiService= Client.getCLient("https://fcm.googleapis.com/").create(ApiService.class);

        recyclerView=findViewById(R.id.recyclerMessage_ID);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        messageEditText=findViewById(R.id.sentMegEditText_ID);
        sentMegBtn=findViewById(R.id.sentMegBtn_ID);


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        myId=firebaseUser.getUid();

        intent=getIntent();
        userID=intent.getStringExtra("userID");

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                profileText.setText(users.getUsername());

                readMessage(myId,userID,users.getImageUrL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        sentMegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                message=messageEditText.getText().toString().trim();
                if (!message.equals("")){
                    sentMessage(myId,userID,message);
                }else {
                    Toast.makeText(MessageActivity.this,"You can't send empty message!!",Toast.LENGTH_SHORT).show();
                }


                messageEditText.setText("");
            }
        });


    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType(("image/*"));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,GALLERY_CODE1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.image_view_item,null,false);
        builder.setView(view);

        AlertDialog alertDialog=builder.show();

        ImageView imageView=view.findViewById(R.id.viewImage_ID);
        Button sentButton=view.findViewById(R.id.sentImage);
        if (requestCode == GALLERY_CODE1 && resultCode == RESULT_OK &&data!=null && data.getData()!=null ) {
            imageUri = data.getData();
            Glide.with(view).load(imageUri).into(imageView);

        }

        sentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentMessage(myId,userID,message);
                alertDialog.dismiss();
            }
        });


    }

    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }



    private void sentMessage(String myId, String userID, String message) {

        if (imageUri==null){
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("sender",myId);
            hashMap.put("reciver",userID);
            hashMap.put("message",message);
            hashMap.put("imagePost","zero");

            reference.child("Chats").push().setValue(hashMap);

        }else {

            storageReference = FirebaseStorage.getInstance().getReference("Post");
            StorageReference sreference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            sreference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String myId = firebaseUser.getUid();
                            String imageUri = uri.toString();
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();


                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender",myId);
                            hashMap.put("reciver",userID);
                            hashMap.put("message","null");
                            hashMap.put("imagePost", imageUri);
                            reference1.child("Chats").push().setValue(hashMap);

                        }
                    });

                }
            });

        }


                DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Chatlist").child(myId).child(userID);
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            reference1.child("id").setValue(userID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isComplete()){
                            FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                            String refreshToken=task.getResult().getToken();
                            if (firebaseUser!=null){
                                updateToken(refreshToken);
                            }


                        }
                    }
                });

               final String msg=message;
                reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);

                        if (notify){
                            sendNotitfication(userID,users.getUsername(), msg);
                        }
                        notify=false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("token");
        Token token=new Token(refreshToken);
        databaseReference.child(firebaseUser.getUid()).setValue(token);
    }

    private void sendNotitfication(String userID, String username, String msg) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("token");

        Query query=tokens.orderByKey().equalTo(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);
                    Data data=new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username+":"+msg,"New Message",userID);
                    Notification notification=new Notification(username,msg,R.mipmap.ic_launcher);

                    Sender sender=new Sender(token.getToken(),data,notification);
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code()==200){
                                        if (response.body().success!=1){
                                            Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readMessage(String myId, String userID, String imageURL) {
        chatsList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatsList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Chats chats=snapshot1.getValue(Chats.class);

                    chats.setKey(snapshot1.getKey());
                    if (chats.getSender().equals(myId) && chats.getReciver().equals(userID) || chats.getSender().equals(userID) && chats.getReciver().equals(myId)){
                        chatsList.add(chats);
                    }



                    messageAdapter=new MessageAdapter(MessageActivity.this,chatsList,imageURL);
                    recyclerView.setAdapter(messageAdapter);

                    messageAdapter.setOnClickListener(new MessageAdapter.onItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Toast.makeText(MessageActivity.this,"Position: "+position,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDelete(int position) {

                            Chats selectedItem=chatsList.get(position);
                            String key=selectedItem.getKey();
                            reference.child(key).removeValue();

//                            StorageReference storageReference=firebaseStorage.getReferenceFromUrl(selectedItem.getImagePost());
//                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//
//                                }
//                            });



                        }

                        @Override
                        public void onDownload(int position) {
                            Chats selectedItem=chatsList.get(position);
                            String key=selectedItem.getKey();
                            String url=selectedItem.getImagePost();

                            DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                    DownloadManager.Request.NETWORK_MOBILE);
                            request.setTitle("Download");
                            request.setDescription("Downloading file...");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+System.currentTimeMillis());


                            DownloadManager manager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);


                            //downloadImage(url,position);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void downloadImage(String url, int position) {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Downloading...");
        pd.setCancelable(false);
        pd.show();


        File file= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        PRDownloader.download(url,file.getPath(), URLUtil.guessFileName(url,null,null))
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        Long per=progress.currentBytes*100/progress.totalBytes;

                        pd.setMessage("Downloading: "+per+" %");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        pd.dismiss();

//                        Chats selectedItem=chatsList.get(position);
//                        String key=selectedItem.getKey();
//
//                        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(selectedItem.getImagePost());
//                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                reference.child(key).removeValue();
//                            }
//                        });

                        Toast.makeText(MessageActivity.this,"Done",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(MessageActivity.this,"Not Done",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
    }

    private void Status(String status){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
    }

}