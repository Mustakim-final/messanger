package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    private MaterialEditText gmailEdit,passwordEdit;
    private Button button;
    private TextView textView;
    private ProgressBar progressBar;

    FirebaseAuth mAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gmailEdit=findViewById(R.id.loginEmail_ID);
        passwordEdit=findViewById(R.id.loginPassword_ID);
        button=findViewById(R.id.loginBtn_ID);
        textView=findViewById(R.id.goSignUp_ID);
        progressBar=findViewById(R.id.signProgressBar_ID);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Registration.class);
                startActivity(intent);
            }
        });

        mAuth=FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gmail=gmailEdit.getText().toString();
                String password=passwordEdit.getText().toString();

                if (gmail.isEmpty()){
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
                    signInUser(gmail,password);
                }
            }
        });
    }

    private void signInUser(String gmail, String password) {
        mAuth.signInWithEmailAndPassword(gmail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(MainActivity.this, "Log In Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}