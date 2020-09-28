package com.example.test1;

import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

        import android.Manifest;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Address;
        import android.location.Geocoder;
        import android.location.Location;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.libraries.places.api.Places;
        import com.google.android.libraries.places.api.model.Place;
        import com.google.android.libraries.places.api.net.PlacesClient;
        import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
        import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
        import com.google.firebase.auth.FirebaseAuth;

        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.UserInfo;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.annotations.NotNull;

        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.Arrays;
        import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    //GoogleApiClient.ConnectionCallbacks;
    //GoogleApiClient.OnConnectionFailedListener;
    private GoogleMap mMap;
    //private GoogleApiClient mGoogleApiClient; //사용중단됨
    private FusedLocationProviderClient mfusedLocationProviderClient; //위치정보 얻는 객체
    public static final int REQUEST_CODE_PERMISSIONS = 1000; //권한체크 요청 코드 정의

    private Marker marker; //마커 삭제를 위한 마커객체 생성
    private FrameLayout frame; //정보창 프레임
    private EditText inputAddr; //입력받은 주소값
    private Button btn_go, btn_logout, btn_guest_login; //검색버튼

    final Geocoder geocoder = new Geocoder(this); //입력받은 주소 값 위도경도 값으로 변환해주는 거
    private double longitude; //경도
    private double latitude; //위도
    private String str, str2;

    private DrawerLayout drawerLayout; //네비게이션 바
    private View drawerView;
    private ImageView iv_profile;
    private TextView tv_nickname, tv_email;

    private static final String TAG = "MainActivity";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //지도구현
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //주소 검색 자동완성 기능 구현
        Places.initialize(getApplicationContext(), "AIzaSyAj9rimfetg7Vuse5vaTa9SDhpMli-VNkM");
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.

                inputAddr = (EditText) findViewById(R.id.inputAddr);
                inputAddr.setText(place.getName());
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //GoogleAPIClient의 인스턴스 생성
        /*
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }*/


        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btn_go = (Button) findViewById(R.id.btn_go);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        frame = (FrameLayout) findViewById(R.id.frame);


        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> list = null;
                str = inputAddr.getText().toString();

                try {
                    list = geocoder.getFromLocationName(str, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test", "입출력 오류");
                }

                marker.remove(); //이전의 마커 삭제
                if (list != null) {
                    if (list.size() == 0) {
                        //입력한 주소의 위도경도 값이 없다면
                        Toast.makeText(MainActivity.this, "해당되는 주소 정보는 없습니다", Toast.LENGTH_SHORT).show();

                    } else {

                        Address address = list.get(0);
                        longitude = address.getLongitude(); //경도
                        latitude = address.getLatitude(); //위도
                        LatLng latLng = new LatLng(latitude, longitude);
                        str2 = address.getAddressLine(0) + "\n"; //상세주소

                        marker = mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    }
                }


                /*
                //구현중!!!!!!!!!!!! 구글로그인 사용자랑 자체회원가입 사용자 정보 불러오기
                //DB에 위도경도 값 보내기
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("DB");

                InfoDTO infoDTO = new InfoDTO();


                infoDTO.place = str;
                infoDTO.Lat = latitude;
                infoDTO.Long = longitude;

                //네비게이션 바 할때, 구글 user id, photourl 받음 - 조정 필요
                //Intent intent = getIntent(); //GoogleLoginActivity로 부터 닉네임,프로필 사진url전달받음
                //infoDTO.userId = intent.getStringExtra("nickname");
                //infoDTO.profile = intent.getStringExtra("photourl");

                SimpleDateFormat format = new SimpleDateFormat("MM월dd일HH시mm분");
                infoDTO.uploadTime = format.format(System.currentTimeMillis());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    for (UserInfo profile : user.getProviderData()) {
                        infoDTO.userId = profile.getDisplayName();
                        //infoDTO.profile = profile.getPhotoUrl();
                    }
                }


                databaseReference.push().setValue(infoDTO);
                */







            }
        });


        /* 보류
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(frame.getVisibility() == View.GONE) { //btn_info 버튼 한번 눌렀을때

                    frame.setVisibility((View.VISIBLE));
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Info_Fragment info_fragment = new Info_Fragment();
                    transaction.replace(R.id.frame, info_fragment);
                    transaction.commit();


                    //fragment text 변경
                    //str = inputAddr.getText().toString();
                    Bundle bundle = new Bundle(); //번들 객체 생성

                    bundle.putString("장소이름",str);
                    bundle.putString("장소주소",str2);

                    info_fragment.setArguments(bundle);//정보창 fragment로 전달


                }
                else{ //btn_info 버튼 두번 눌렀을때
                    frame.setVisibility(View.GONE);
                }

            }
        });*/

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); //로그아웃 되면서 로그인 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, GoogleLoginActivity.class);
                startActivity(intent);
            }
        });

        //네비게이션 메뉴바 - 이메일, 닉네임, 프로필 나타내기
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        iv_profile = (ImageView)findViewById(R.id.iv_profile);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_nickname = (TextView)findViewById(R.id.tv_nickname);
        btn_guest_login = (Button)findViewById(R.id.btn_guest_login);//비회원일때만 보이는 로그인버튼


        if (user != null) { //회원이라면..
            for (UserInfo profile : user.getProviderData()) {

                tv_email.setText(profile.getEmail()); //로그인한 이메일 불러오기

                if(tv_nickname.length()==0){
                    //MemberInfoActivity에서 닉네임이랑 프로필 정보 받아서 보여주기 구

                    //안됨 왜안되는 지 모르겠음
                    tv_nickname.setText("닉네임 설정하기");
                    //iv_profile.setImageResource(R.mipmap.ic_launcher_round);


                }else {
                    //GoogleLoginActivity에서 구글 정보(id, photoUrl 불러오기)
                    Intent intent = getIntent();
                    String nickname = intent.getStringExtra("nickname");
                    String photourl = intent.getStringExtra("photourl");

                    tv_nickname.setText(nickname);//닉네임 text를 텍스트 뷰에 세팅
                    Glide.with(this).load(photourl).into(iv_profile);//프로필 url를 이미지 뷰에 세팅
                }


            }
        }
        else {//비회원이라면..
            iv_profile.setImageResource(R.mipmap.ic_launcher_round);
            tv_nickname.setText("비회원");
            tv_email.setText("로그인 하세요 >");
            btn_logout.setVisibility(View.INVISIBLE); //로그아웃 버튼 안보이게
            btn_guest_login.setVisibility(View.VISIBLE); //비회원 전용 로그인 버튼 보이게

            btn_guest_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this , GoogleLoginActivity.class);
                    startActivity(intent);
                }
            });
        }








    }//oncreate 마지막


    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시, 앱 종료
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);
    }

    /*
    @Override
    protected void onStart() {
        //mGoogleApiClient.connect(); //GoogleApi 접속
        super.onStart();
    }

    @Override
    protected void onStop() {
        //mGoogleApiClient.disconnect(); //GoogleApi 연결해제
        super.onStop();
    }
*/
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        LatLng Seoul = new LatLng(37.551036, 126.990899); //처음 지도 켰을때 위치
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Seoul).title("서울 남산공원").snippet("서울특별시 중구 용산2가동 삼일대로 231");
        marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Seoul, 10));

        str = marker.getTitle();
        str2 = "서울특별시 중구 용산2가동 삼일대로 231";


        //마커 클릭시 정보창 나옴
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (frame.getVisibility() == View.GONE) { //btn_info 버튼 한번 눌렀을때

                    frame.setVisibility((View.VISIBLE));
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Info_Fragment info_fragment = new Info_Fragment();
                    transaction.replace(R.id.frame, info_fragment);
                    transaction.commit();

                    //fragment text 변경
                    Bundle bundle = new Bundle(); //번들 객체 생성

                    bundle.putString("장소이름", str);
                    bundle.putString("장소주소", str2);

                    info_fragment.setArguments(bundle);//정보창 fragment로 전달


                } else { //btn_info 버튼 두번 눌렀을때
                    frame.setVisibility(View.GONE);
                }
                return true;
            }
        });

    }

    /*
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //GoogleApi 연결상태에 따라
    }

    @Override
    public void onConnectionSuspended(int i) {
        //GoogleApi 연결상태에 따라
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //GoogleApi 연결상태에 따라
    }
*/


    public void mCurrentLocation(View v) {
        //권한체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);
            return;
        }


        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        marker.remove();
                        if (location != null) {
                            //현재위치
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            LatLng myLocation = new LatLng(latitude, longitude);
                            marker = mMap.addMarker(new MarkerOptions().position(myLocation).title("현재 위치")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));

                            List<Address> list = null;
                            try {
                                list = geocoder.getFromLocation(latitude, longitude, 10);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("test", "역지오코딩 에러발생");
                            }

                            if (list != null) {
                                if (list.size() == 0) {
                                    Toast.makeText(MainActivity.this, "해당되는 주소 정보는 없습니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    str = marker.getTitle();
                                    str2 = list.get(0).getAddressLine(0) + "\n";
                                }
                            }
                        }
                    }
                });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }

                return;
        }
    }



}



