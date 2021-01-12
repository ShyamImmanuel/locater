package com.example.locater;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    FirebaseAuth auth;
    EditText f_email;
    Button changepassword,login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        f_email=(EditText)findViewById(R.id.f_email);
        changepassword=(Button)findViewById(R.id.changePassword);
        login=(Button)findViewById(R.id.login);
        auth=FirebaseAuth.getInstance();
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usermail=f_email.getText().toString();
                if(usermail.isEmpty())
                {
                    Toast.makeText(ForgetPassword.this,"Please enter values in all field",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(usermail.equals(""))
                {
                    Toast.makeText(ForgetPassword.this,"Please Enter Your Email",Toast.LENGTH_LONG).show();
                }
                else
                {
                    auth.sendPasswordResetEmail(usermail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ForgetPassword.this,"Password reset mail sent",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(ForgetPassword.this,Login.class));
                            }
                            else
                            {
                                Toast.makeText(ForgetPassword.this,"Please Enter Correct Email",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void gotoLogin(View view) {
        startActivity(new Intent(ForgetPassword.this,Login.class));
    }
}
