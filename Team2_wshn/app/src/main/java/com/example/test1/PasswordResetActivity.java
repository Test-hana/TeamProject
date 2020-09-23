package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;//구글 로그인 인증
    private Button btn_send;
    private EditText tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mAuth = FirebaseAuth.getInstance();//파이어베이스 인증객체 초기화
        tv_email = (EditText)findViewById(R.id.tv_email);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

    }//onCreate 마지막


    private void send(){
        String email = tv_email.getText().toString();

        if(email.length()>0){
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startToast("이메일을 전송했습니다.");
                            }
                        }
                    });
        }else{
            startToast("이메일을 입력해주세요.");
        }

    }
    private void startToast(String msg){ //리스너에서 Toast msg가 불가해서
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
