package com.example.test1.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

        import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
        import android.location.Geocoder;
        import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
        import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
        import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.test1.Info_Fragment;
import com.example.test1.MyItem;
import com.example.test1.PlayUpload.PlayActivity;
import com.example.test1.PlayUpload.PlayUploadActivity;
import com.example.test1.R;
import com.google.android.gms.common.api.Status;
        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
        import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
        import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
        import com.google.firebase.auth.FirebaseAuth;

        import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.MarkerManager;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Geocoder geocoder;
    MyItem offsetItem;

    private ClusterManager<MyItem> mClusterManager;
    private FusedLocationProviderClient mfusedLocationProviderClient; //위치정보 얻는 객체
    private LocationManager locationManager; //GPS (위치서비스 권한)
    public static final int REQUEST_CODE_PERMISSIONS = 1000; //권한체크 요청 코드 정의
    public MarkerManager.Collection normalMarkersCollection; //클러스터에 포함되지 않은 마커 클릭 리스너를 위해서

    private Marker marker; //마커 삭제를 위한 마커객체 생성
    private FrameLayout frame; //정보창 프레임
    private EditText inputAddr; //입력받은 주소값
    private Button btn_go, btn_logout, btn_guest_login; //검색버튼

    private double longitude; //경도
    private double latitude; //위도
    private String str, str2; //장소이름, 장소주소
    private Bitmap placeBitmap;//장소사진
    private Bitmap resizeBitmap; //사이즈 줄인 장소사진

    private DrawerLayout drawerLayout; //네비게이션 바
    private View drawerView; //드로어 메뉴
    private ImageView iv_profile; //사용자 프로필
    public TextView profile,tv_nickname, tv_email; //profile : 프로필 url 담는 텍스트뷰, 닉네임, 이메일
    private static final String TAG = "MainActivity";

    String kakaoId; //카톡공유
    FirebaseFirestore firestore;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPSEnabled(); //boolean 값 받기
        //위치 서비스(GPS) 권한체크
        if (!GPSEnabled()) {
            //GPS 설정화면으로 이동
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setIcon(R.mipmap.ic_launcher).setTitle("알림")
                    .setMessage("위치 서비스(GPS)가 꺼져있습니다. 현재 위치 탐색을 위해 On 해주세요.")
                    .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })

                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = ad.create();
            alert.show();
        }

        //Toolbar 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        //지도구현
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //주소 검색 자동완성 기능 구현
        Places.initialize(getApplicationContext(), "AIzaSyAj9rimfetg7Vuse5vaTa9SDhpMli-VNkM");
        final PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.PHOTO_METADATAS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NotNull Place place) {

                inputAddr = findViewById(R.id.inputAddr);
                inputAddr.setText(place.getName());

                try{
                    PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();

                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                            @Override
                        public void onSuccess(FetchPhotoResponse photoResponse) {
                            placeBitmap = photoResponse.getBitmap();
                            resizeBitmap = Bitmap.createScaledBitmap(placeBitmap,300,placeBitmap.getHeight()/(placeBitmap.getWidth()/300),true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Handle failure exception...
                        }
                    });
                }catch(NullPointerException e){
                    //Photo meta data was null, handle exception...
                }
            }

            @Override
            public void onError(@NotNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btn_go = findViewById(R.id.btn_go);
        btn_logout = findViewById(R.id.btn_logout);
        frame = findViewById(R.id.frame);

        geocoder = new Geocoder(MainActivity.this); //입력받은 주소 값 위도경도 값으로 변환해주는 거
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputAddr != null){
                    List<Address> list = null;
                    str = inputAddr.getText().toString(); //place.getName() 값을 inputAddr에 저장하고 동일한 값을 str에 저장
                    try {
                        list = geocoder.getFromLocationName(str, 10); //str->위,경도 값으로 변환
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("test", "입출력 오류");
                    }

                    if(marker!=null){
                        marker.remove(); //이전의 마커 삭제
                    }

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

                            marker = normalMarkersCollection.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        }
                    }

                   /*
                    //DB에 위도경도 값 보내기
                    //동영상 업로드할때로 옮기기
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("DB");
                    SimpleDateFormat format = new SimpleDateFormat("MM월dd일HH시mm분");

                    InfoDTO infoDTO = new InfoDTO(str, latitude,longitude, user.getEmail(),format.format(System.currentTimeMillis()));
                    databaseReference.push().setValue(infoDTO);
                     */

                } else {
                    Toast.makeText(MainActivity.this, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final CheckBox cb_save = findViewById(R.id.cb_save);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); //로그아웃 되면서 로그인 액티비티로 이동

                //로그인 상태 유지 체크 값도 false로 초기화하기
                SharedPreferences shp = getSharedPreferences("AutoLoginInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = shp.edit();
                cb_save.setChecked(false);
                editor.putBoolean("shp체크값",false);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, GoogleLoginActivity.class);
                startActivity(intent);
            }
        });

        //네비게이션 메뉴바 - 이메일, 닉네임, 프로필 나타내기
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer);

        drawerLayout.addDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        iv_profile = findViewById(R.id.iv_profile);
        tv_email = findViewById(R.id.tv_email);
        tv_nickname = findViewById(R.id.tv_nickname);
        profile = findViewById(R.id.profile);//프로필 url을 저장하기위해
        btn_guest_login = findViewById(R.id.btn_guest_login);//비회원일때만 보이는 로그인버튼


        cb_save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            SharedPreferences shp = getSharedPreferences("AutoLoginInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(MainActivity.this, "로그인 상태 유지", Toast.LENGTH_SHORT).show();
                    String nick = tv_nickname.getText().toString();
                    String pro = profile.getText().toString();
                    editor.putString("shp닉네임", nick);
                    editor.putString("shp자체회원프로필값", pro);
                    editor.putBoolean("shp체크값", true);
                    editor.commit();
                    cb_save.setChecked(true);

                }else{
                    Toast.makeText(MainActivity.this, "로그인 상태 해제", Toast.LENGTH_SHORT).show();
                    cb_save.setChecked(false);
                    editor.putBoolean("shp체크값",false);
                    editor.commit();

                }
            }
        });

        //MemberInfoActivity에서 닉네임이랑 프로필 정보 받아서 보여주기
        Intent intent = getIntent();
        String value1 = intent.getStringExtra("자체닉네임");
        String value2 = intent.getStringExtra("자체프로필uri");

        //GoogleLoginActivity에서 구글 정보(구글 닉네임 불러오기)
        String value3 = intent.getStringExtra("구글닉네임");

        if (user != null){
            tv_email.setText(user.getEmail()); //로그인한 이메일 불러오기

            //자동로그인 시 프로필과 이메일 불러오기
            SharedPreferences shp = getSharedPreferences("AutoLoginInfo", MODE_PRIVATE);
            String shpnickname = shp.getString("shp닉네임", "shp닉네임값 없음");
            String shpprofile = shp.getString("shp자체회원프로필값", "shp자체회원 프로필값 없음");
            Boolean shpvalue = shp.getBoolean("shp체크값", false);

            if (shpvalue) { //로그인 상태 유지 체크되어있다면(자동로그인이라면) shpvalue == true
                tv_nickname.setText(shpnickname);
                if(user.getPhotoUrl() !=null){
                    Glide.with(this).load(user.getPhotoUrl()).centerCrop().override(500).into(iv_profile);//프로필 url를 이미지 뷰에 세팅
                }else{
                    profile.setText(shpprofile);
                    Glide.with(this).load(profile.getText().toString()).centerCrop().override(500).into(iv_profile); //프로필 url를 이미지 뷰에 세팅
                }
                cb_save.setChecked(shpvalue); //true로 유지
            }
            else{
                //자동로그인 아니고, 초기 로그인이나 자동로그인 상태 유지를 체크하지않은 경우
                //회원이라면..
                if(value2 != null){
                    //자체 회원가입 한 후 로그인 ,사용자 정보 나타내기
                    tv_nickname.setText(value1);
                    profile.setText(value2);
                    Glide.with(this).load(profile.getText().toString()).centerCrop().override(500).into(iv_profile); //프로필 url를 이미지 뷰에 세팅
                }else if(value3 != null){
                    //구글 회원 사용자 정보 나타내기
                    tv_nickname.setText(value3);//닉네임 text를 텍스트 뷰에 세팅
                    Glide.with(this).load(user.getPhotoUrl()).centerCrop().override(500).into(iv_profile);//프로필 url를 이미지 뷰에 세팅
                }else {
                    //회원가입 되어있는 회원 로그인
                    tv_nickname.setText(shpnickname);
                    profile.setText(shpprofile);
                    Glide.with(this).load(profile.getText().toString()).centerCrop().override(500).into(iv_profile); //프로필 url를 이미지 뷰에 세팅
                    cb_save.setChecked(shpvalue); //false로 유지
                }
            }

        } else{
            //비회원이라면..
            iv_profile.setImageResource(R.mipmap.ic_launcher_round);
            tv_nickname.setText("비회원");
            tv_email.setText("로그인 하세요 >");
            btn_logout.setVisibility(View.INVISIBLE); //로그아웃 버튼 안보이게
            btn_guest_login.setVisibility(View.VISIBLE); //비회원 전용 로그인 버튼 보이게
            cb_save.setVisibility(View.INVISIBLE);//로그인 상태 체크칸 안보이게

            btn_guest_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this , GoogleLoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }//oncreate 마지막

    protected boolean GPSEnabled(){
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){ return false;
        } else { return true; } }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_menu:
                drawerLayout.openDrawer(drawerView);
                return true;

            case R.id.btn_share:
                //카카오톡 공유메시지 보내기
                if (inputAddr == null) {
                    Toast.makeText(MainActivity.this, "검색창에서 주소를 검색한 후 공유를 해주세요.", Toast.LENGTH_SHORT).show();
                } else if (str.equals("핀 고정함") || str.equals("현재 위치")) {
                    Toast.makeText(MainActivity.this, "검색창에서 주소를 검색한 후 공유를 해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    shareKaKao();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu) ;
        return true ;
    }

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
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시, 앱 종료
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mClusterManager = new ClusterManager<>(this,mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager.getMarkerManager());
        normalMarkersCollection = mClusterManager.getMarkerManager().newCollection();//클러스터에 포함되지 않은 마커 클릭 리스너를 위해서

        Intent intent2 = getIntent();
        if (Intent.ACTION_VIEW.equals(intent2.getAction())) {
            //MarkerMaker(); //이게있으면 str 값이 변질됨

            kakaoId = intent2.getData().toString();
            int idx = kakaoId.indexOf("?");
            str = kakaoId.substring(idx + 1);

            if (str != null) {
                List<Address> list = null;
                try {
                    list = geocoder.getFromLocationName(str, 10); //위,경도 값으로 변환
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test", "입출력 오류");
                }

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
                        //사진
                        marker = normalMarkersCollection.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                }
            }else {
                Toast.makeText(MainActivity.this, "공유한 장소의 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.551036, 126.990899), 10)); //초기화면
            MarkerMaker();
        }


        //지도 터치해서 마커 생성
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng point) {

                if(marker!=null){
                    marker.remove(); }
                latitude = point.latitude;
                longitude = point.longitude;

                LatLng markerLocation = new LatLng(latitude, longitude);
                marker = normalMarkersCollection.addMarker(new MarkerOptions().position(markerLocation).title("핀 고정함")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                TryCatch();

            }

        });

        //마커 클릭시 정보창 나옴
        normalMarkersCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
                    bundle.putParcelable("장소사진",resizeBitmap);
                    bundle.putDouble("Long",longitude);
                    bundle.putDouble("Lat", latitude);
                    bundle.putString("UserId", tv_email.getText().toString());
                    info_fragment.setArguments(bundle); //정보창 fragment로 전달

                } else { //btn_info 버튼 두번 눌렀을때
                    frame.setVisibility(View.GONE);
                }
                return false;
            }
        });


        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                return false;
            }
        });

        //클러스터 포함된 마커클릭 리스너
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>(){

            @Override
            public boolean onClusterItemClick(MyItem myitem) {
                if (frame.getVisibility() == View.GONE) { //btn_info 버튼 한번 눌렀을때

                    frame.setVisibility((View.VISIBLE));
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Info_Fragment info_fragment = new Info_Fragment();
                    transaction.replace(R.id.frame, info_fragment);
                    transaction.commit();

                    //fragment text 변경
                    Bundle bundle = new Bundle(); //번들 객체 생성

                    bundle.putString("장소이름",myitem.getTitle());
                    bundle.putString("장소주소", myitem.getSnippet());
                    bundle.putParcelable("장소사진",myitem.getBitmap());
                    bundle.putDouble("Long",longitude);
                    bundle.putDouble("Lat", latitude);
                    bundle.putString("UserId", tv_email.getText().toString());
                    info_fragment.setArguments(bundle); //정보창 fragment로 전달

                } else { //btn_info 버튼 두번 눌렀을때
                    frame.setVisibility(View.GONE);
                }
                return true;
            }
        });

    }

    public void MarkerMaker(){ //firestore에 저장된 위경도 값 불러와서 앱 실행시, 마커 띄움
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("videos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int size = task.getResult().size(); //문서개수
                            ArrayList<Double> array = new ArrayList<>(); //위도 저장 배열
                            ArrayList<Double> array2 = new ArrayList<>(); //경도 저장 배열
                            ArrayList<String> array3 = new ArrayList<>(); //str 값 저장 배열

                            //firestore에서 불러와서 각 배열에 저장
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //Log.d(TAG, document.getId() + " => " + document.getData().get("lat")+"     "+document.getData().get("long") );
                                array.add((Double) document.getData().get("lat")); //위도저장
                                array2.add((Double) document.getData().get("long")); //경도저장
                                array3.add((String) document.getData().get("placeName")); //장소이름 저장
                                //Log.d(TAG, "=="+array.toString() + array2.toString() + array3.toString() );
                                //Log.d(TAG, "==>"+size);
                            }

                            ArrayList<Double> array4 = new ArrayList<>(); //arrry (위도)중복체크을 위한 배열
                            ArrayList<Double> array5 = new ArrayList<>(); //array2 (경도)중복체크을 위한 배열
                            for(int i=0; i<size; i++){
                                if(!array4.contains(array.get(i)) && !array5.contains(array2.get(i))){
                                    array4.add(array.get(i));
                                    array5.add(array2.get(i));
                                    if(array.get(i) != null && array2.get(i) !=null){
                                        latitude = array.get(i);
                                        longitude = array2.get(i);
                                    }
                                    str = array3.get(i);

                                    List<Address> list = null;
                                    try {
                                        list=geocoder.getFromLocation(latitude,longitude,10);
                                    }catch (IOException e){
                                        e.printStackTrace();
                                        Log.e("test","입출력 오류");
                                    }

                                    if (list != null) {
                                        if (list.size() == 0) {
                                            //입력한 주소의 위도경도 값이 없다면
                                            Toast.makeText(MainActivity.this, "해당되는 주소 정보는 없습니다", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Address address = list.get(0);
                                            address.setLatitude(latitude);//위도
                                            address.setLongitude(longitude);//경도
                                            str2 = address.getAddressLine(0) + "\n"; //상세주소
                                            //사진없음
                                        }
                                    }
                                    offsetItem = new MyItem(latitude,longitude,str,str2,resizeBitmap);
                                    mClusterManager.addItem(offsetItem);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }

    public void mCurrentLocation(View v) {
        //위치 정보 권한체크
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

        //현재위치 탐색
        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            if(marker!=null){
                                marker.remove(); }
                            //현재위치
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            LatLng myLocation = new LatLng(latitude, longitude);
                            marker = normalMarkersCollection.addMarker(new MarkerOptions().position(myLocation).title("현재 위치")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
                            TryCatch();


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



    private void TryCatch(){

        //final Geocoder geocoder = new Geocoder(MainActivity.this); //입력받은 주소 값 위도경도 값으로 변환해주는 거
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
                resizeBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.location);
            }
        }
    }

    public void shareKaKao() {

        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder(str,"https://firebasestorage.googleapis.com/v0/b/loginex-67236.appspot.com/o/%EC%83%98%ED%94%8C%EC%82%AC%EC%A7%84.jpg?alt=media&token=4bdded64-3fc5-4b31-b47c-df74ac68f6f3" ,
                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption(str2)
                        .build())

                .addButton(new ButtonObject("앱으로 이동", LinkObject.newBuilder()
                        .setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .setAndroidExecutionParams(str) //공유한 장소의 리뷰영상보기 값으로 바꿔주기
                        .setIosExecutionParams(str)
                        .build()))

                .build();


        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");


        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Toast.makeText(MainActivity.this, "존재하지 않은 페이지입니다,", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });

    }

}//end



