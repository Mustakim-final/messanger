package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class Registration extends AppCompatActivity {

    private MaterialEditText userNameEdit,gmailEdit,passwordEdit;
    private ProgressBar progressBar;
    Button regButton;
    TextView signUpText;

    FirebaseAuth mAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userNameEdit=findViewById(R.id.regUserName_ID);
        gmailEdit=findViewById(R.id.regEmail_ID);
        passwordEdit=findViewById(R.id.regPassword_ID);
        regButton=findViewById(R.id.regBtn_ID);
        signUpText=findViewById(R.id.goSignIn_ID);
        progressBar=findViewById(R.id.regProgressBar_ID);

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registration.this,MainActivity.class);
                startActivity(intent);
            }
        });


        mAuth=FirebaseAuth.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=userNameEdit.getText().toString().trim();
                String gmail=gmailEdit.getText().toString();
                String password=passwordEdit.getText().toString();

                if (username.isEmpty()){
                    userNameEdit.setError("Enter username!!!");
                    userNameEdit.requestFocus();
                    return;
                }else if (gmail.isEmpty()){
                    gmailEdit.setError("Enter gmail");
                    gmailEdit.requestFocus();
                    return;
                }else if (!Patterns.EMAIL_ADDRESS.matcher(gmail).matches()){
                    gmailEdit.setError("Enter a valid gmail");
                    gmailEdit.requestFocus();
                    return;
                }else if (password.isEmpty()){
                    passwordEdit.setError("Enter password!!!");
                    passwordEdit.requestFocus();
                    return;
                }else if (password.length()<6){
                    passwordEdit.setError("Enter 6 digit password!!!");
                    passwordEdit.requestFocus();
                    return;
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(username,gmail,password);
                }
            }
        });

    }

    private void registerUser(String username, String gmail, String password) {
        mAuth.createUserWithEmailAndPassword(gmail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser=mAuth.getCurrentUser();
                    String userID=firebaseUser.getUid();

                    reference= FirebaseDatabase.getInstance().getReference("Users").child(userID);

                    HashMap<String,Object> hashMap=new HashMap<>();

                    hashMap.put("username",username);
                    hashMap.put("password:",password);
                    hashMap.put("gmail",gmail);
                    hashMap.put("id",userID);
                    hashMap.put("imageUrl","default");
                    hashMap.put("status","offline");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Registration.this,"Registration successful",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(Registration.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }
}