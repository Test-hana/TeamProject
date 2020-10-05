package com.example.test1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

public class GoogleLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton btn_google;//구글 로그인 버튼
    private FirebaseAuth mAuth;//구글 로그인 인증
    private GoogleApiClient googleApiClient;//구글 API 클라이언트
    private static final int REQ_SIGN_GOOGLE = 100;//구글 로그인 결과 검토, 100 임의의 값

    private Button btn_guest,btn_join,btn_login,btn_passwordReset;
    private EditText tv_email, tv_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        //googleSignInButton이 눌릴 때 기본적인 세팅
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mAuth = FirebaseAuth.getInstance();//파이어베이스 인증객체 초기화
        tv_email = (EditText)findViewById(R.id.tv_email);
        tv_password = (EditText)findViewById(R.id.tv_password);

        //구글로그인 사용자
        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {//구글 로그인 버튼을 클릭했을때 이곳을 수행
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient); //구글이 따로 만들어놓은 인증 액티비티 화면으로 넘어가서 인증
                startActivityForResult(intent, REQ_SIGN_GOOGLE); //인증완료 후 결과값을 돌려받음
            }
        });

        //비회원 사용자
        btn_guest = findViewById(R.id.btn_guest);
        btn_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleLoginActivity.this,MainActivity.class);
                startActivity(intent);
                startToast("비회원입니다.");
            }
        });

        //회원가입
        btn_join = findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleLoginActivity.this,JoinActivity.class);
                startActivity(intent);
            }
        });

        //로그인
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        //비밀번호 재설정
        btn_passwordReset =findViewById(R.id.btn_passwordReset);
        btn_passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleLoginActivity.this,PasswordResetActivity.class);
                startActivity(intent);
            }
        });


        //로그인된 사용자가 null이 아니라면 바로 메인으로 이동(자동로그인)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(GoogleLoginActivity.this,MainActivity.class);
            startActivity(intent);
            for (UserInfo profile : user.getProviderData()) {
                String email = profile.getEmail();
                Toast.makeText(GoogleLoginActivity.this, email+"로 자동 로그인되었습니다.", Toast.LENGTH_SHORT).show();
            }
            finish();
        }





    }//onCreate 마지막

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//구글 로그인 인증을 요청했을때 결과값을 되돌려 받는 곳
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_SIGN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){//인증결과가 성공적이면..
                GoogleSignInAccount account = result.getSignInAccount();//account 라는 데이터는 구글로그인의 정보(닉네임, 프로필사진Url, 이메일주소..등등)을 담고있다.
                resultLogin(account);//로그인 결과 값 출력 수행하라는 메소드
            }
        }

    }

    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){//로그인이 성공했으면
                            startToast("로그인에 성공하였습니다.");

                            Intent intent2 = new Intent(GoogleLoginActivity.this, MainActivity.class);
                            intent2.putExtra("닉네임", account.getDisplayName());
                            startActivity(intent2);

                        }
                        else{//로그인이 실패했으면
                            startToast("로그인에 실패하였습니다.");
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시, 앱 종료
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);
    }

    private void Login(){ //자체 로그인 함수
        String email = tv_email.getText().toString();
        String password = tv_password.getText().toString();

        if(email.length() > 0 && password.length() > 0 ){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //성공 시
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인에 성공하였습니다.");

                                Intent intent = new Intent(GoogleLoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            } else {
                                //실패 시
                                if(task.getException()!=null){
                                    startToast(task.getException().toString()+"\n로그인에 실패하였습니다.)");
                                }
                            }
                        }
                    });
            } else {
                startToast("이메일 또는 비밀번호(6자리 이상)을 입력해주세요.");
        }
    }

    private void startToast(String msg){ //리스너에서 Toast msg가 불가해서
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
