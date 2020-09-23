package com.example.test1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class Info_Fragment extends Fragment {

    private TextView Addr_name,Addr_detail;
    private Button btn_upload, btn_view1;
    private String str,str2;
    public Info_Fragment(){


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment,container,false);

        btn_upload = (Button)view.findViewById(R.id.btn_upload);//리뷰등록 버튼
        btn_view1 = (Button)view.findViewById(R.id.btn_view1);//리뷰보기 버튼
        Addr_name = (TextView)view.findViewById(R.id.Addr_name); //장소이름
        Addr_detail = (TextView)view.findViewById(R.id.Addr_detail); //장소주소


        Bundle bundle = getArguments(); //MainActivity에서 전달한 str값 bundle에 저장

        str = bundle.getString("장소이름"); //bundle안의 텍스트 불러오기
        str2 = bundle.getString("장소주소");
        Addr_name.setText(str);
        Addr_detail.setText(str2);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //메인->리뷰등록 UploadActivity로 이동

                //로그인 상태 체크, guest계정일 때 제한, 로그인해야됨 알리기
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {//로그인된 사용자가 없다면, 즉 비회원이라면

                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                    ad.setIcon(R.mipmap.ic_launcher);
                    ad.setTitle("알림");
                    ad.setMessage("회원가입이 필요합니다.");

                    ad.setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().startActivity(new Intent(getActivity(), JoinActivity.class));
                        }
                    });

                    ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();

                }else{
                    getActivity().startActivity(new Intent(getActivity(), UploadActivity.class));
                }

            }
        });

        btn_view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //최근 영상 자동 재생을 위한 PlayActivity로 이동
                getActivity().startActivity(new Intent(getActivity(), PlayActivity.class));
            }
        });

        return view;

    }
}
