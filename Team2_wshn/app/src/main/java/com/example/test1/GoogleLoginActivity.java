package com.example.test1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton btn_google;//구글 로그인 버튼
    private FirebaseAuth auth;//구글 로그인 인증
    private GoogleApiClient googleApiClient;//구글 API 클라이언트
    private static final int REQ_SIGN_GOOGLE = 100;//구글 로그인 결과 검토, 100 임의의 값

    private Button btn_guest;

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

        auth = FirebaseAuth.getInstance();//파이어베이스 인증객체 초기화

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {//구글 로그인 버튼을 클릭했을때 이곳을 수행
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient); //구글이 따로 만들어놓은 인증 액티비티 화면으로 넘어가서 인증
                startActivityForResult(intent, REQ_SIGN_GOOGLE); //인증완료 후 결과값을 돌려받음
            }
        });

        btn_guest = findViewById(R.id.btn_guest);
        btn_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleLoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }

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
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){//로그인이 성공했으면
                            Toast.makeText(GoogleLoginActivity.this, "로그인 성공",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), GoogleResultActivity.class); //GoogleResultActivity로 전달
                            intent.putExtra("nickname",account.getDisplayName());
                            intent.putExtra("photourl",String.valueOf(account.getPhotoUrl()));//String.valueof() 특정 자료형을 string형으로 변환할때

                            Intent intent2 = new Intent(getApplicationContext(), MainActivity.class); //DB에 userid, profileUrl 저장하기위해 MainActivity로 닉네임 값 전달
                            intent2.putExtra("nickname",account.getDisplayName());
                            intent2.putExtra("photourl",String.valueOf(account.getPhotoUrl()));

                            //startActivity(intent); //로그인성공 후 프로필이랑 닉네임 보여주는 거 필요없을 거 같아서 보류, 주석처리
                            startActivity(intent2);

                        }
                        else{//로그인이 실패했으면
                            Toast.makeText(GoogleLoginActivity.this, "로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
