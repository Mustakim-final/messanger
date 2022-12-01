package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onStart() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            Intent intent=new Intent(StartActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    private Button buttonSignIn,buttonSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        buttonSignIn=findViewById(R.id.signIn_ID);
        buttonSignUp=findViewById(R.id.signUp_ID);
        buttonSignIn.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.signIn_ID){
            Intent intent=new Intent(StartActivity.this,MainActivity.class);
            startActivity(intent);
        }else if (v.getId()==R.id.signUp_ID){
            Intent intent=new Intent(StartActivity.this,Registration.class);
            startActivity(intent);
        }
    }
}