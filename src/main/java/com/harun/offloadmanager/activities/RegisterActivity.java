package com.harun.offloadmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.tasks.PostToServerTask;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    EditText atEmail, etPhoneNo, etPassword;
    Button btnSignUp;
    TextView tvLoginLink;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        atEmail = (EditText) findViewById(R.id.email_register);
        etPhoneNo = (EditText) findViewById(R.id.phone_register);
        etPassword = (EditText) findViewById(R.id.password_register);

        btnSignUp = (Button) findViewById(R.id.button_register);
        tvLoginLink = (TextView) findViewById(R.id.login_link);

        btnSignUp.setOnClickListener(this);
        tvLoginLink.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_register:
                String method = "register_user";
                String email = atEmail.getText().toString();
                String phoneNumber = etPhoneNo.getText().toString();
                String password = etPassword.getText().toString();

                if (email.equals("") || phoneNumber.equals("") || password.equals("")){

                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something Went Wrong !!");
                    builder.setMessage("Please fill all the fields");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else {
                    PostToServerTask postToServerTask = new PostToServerTask(getBaseContext());
                    postToServerTask.execute(
                            method,
                            email,
                            phoneNumber,
                            password);
                }

//                startActivity(new Intent(this, MainActivity.class));

                break;
            case R.id.login_link:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

    }
}
