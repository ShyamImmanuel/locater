package com.example.locater;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText l_email,l_password;
    Button signin;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        l_email=(EditText)findViewById(R.id.l_email);
        l_password=(EditText)findViewById(R.id.l_password);
        signin=(Button)findViewById(R.id.signin);

        auth=FirebaseAuth.getInstance();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=l_email.getText().toString();
                String password=l_password.getText().toString();
                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(Login.this,"Please enter values in all field",Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            checkEmailVerification();
                        }
                        else
                        {
                            Toast.makeText(Login.this,"Email or Password is wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser=auth.getCurrentUser();
        boolean flag=firebaseUser.isEmailVerified();
        if(flag)
        {
            Toast.makeText(Login.this,"Login Sucess",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Login.this,Home.class));
        }
        else
        {
            Toast.makeText(Login.this,"Email is not verified",Toast.LENGTH_LONG).show();
            auth.signOut();
        }
    }

    public void gotoRegister(View view) {
        startActivity(new Intent(Login.this,Register.class));
    }

    public void forgotpassword(View view) {
        startActivity(new Intent(Login.this,ForgetPassword.class));
    }
}
