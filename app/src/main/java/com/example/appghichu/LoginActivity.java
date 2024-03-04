package com.example.appghichu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText edtTaiKhoan, edtMauKhau;
    private Button btnLogin;
    private TextInputLayout edtLayoutTaikhoan, edtLayoutMatkhau;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        edtTaiKhoan = findViewById(R.id.edt_taiKhoan);
        edtMauKhau = findViewById(R.id.edt_matkhau);
        edtLayoutTaikhoan = findViewById(R.id.edtlayout_taikhoan);
        edtLayoutMatkhau = findViewById(R.id.edtlayout_matkhau);
        btnLogin = findViewById(R.id.btn_logout);

        mAuth = FirebaseAuth.getInstance();





        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLogin();
            }
        });

    }

    private void clickLogin() {
        String strUserName = edtTaiKhoan.getText().toString().trim();
        String strPassWord = edtMauKhau.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(strUserName, strPassWord)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}