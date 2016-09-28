package com.harun.offloadmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.harun.offloadmanager.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etEmail, etPassowrd;
    Button btnLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.email);
        etPassowrd = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login_button);
        tvRegister = (TextView) findViewById(R.id.register_link);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                break;
            case R.id.register_link:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }
}
