package com.example.our_capstone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RoomActivity  extends AppCompatActivity {                                   //메인클래스
    private static final String TAG = "AppCompatActivity";
    private String KEY;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {                                //메인함수
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent = getIntent();    //데이터 수신
        final String room_key = intent.getExtras().getString("room_key");
        KEY = room_key;
        imageView = (ImageView)findViewById(R.id.imageView);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);
        //방정보 따오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("rooms").document(room_key);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + "----------data: " + snapshot.getString("name"));
                    TextView textView = (TextView)findViewById(R.id.room_nm);                       //방이름 설정
                    textView.setText(snapshot.getString("name"));                             //방이름 설정
                    if(!snapshot.getString("photo").equals("default")){                       //방 메인사진 설정
                        loadImg(room_key,snapshot.getString("photo"));
                    }
                } else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });

        findViewById(R.id.change_rm_info).setOnClickListener(onClickListener);
        //findViewById(R.id.goto_our).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public  void onClick(View v){
            Intent intent = getIntent();    //데이터 수신
            String room_key = intent.getExtras().getString("room_key");
            switch (v.getId()){
                case R.id.change_rm_info:                                                           //73행에서 findView없으면 실행안댐
                    gotoRoomInfoSettingActivity(room_key);
                    break;
                /*case R.id.goto_our:                                                                 //74행에서 findView없으면 실행안댐
                    gotoOurActivity(room_key);
                    break;*/
            }
        }
    };
    private void gotoRoomInfoSettingActivity(String room_key) {
        Intent intent=new Intent(this,RoomInfoSettingActivity.class);
        intent.putExtra("room_key",room_key);
        startActivity(intent);
        RoomActivity.this.finish();                                                                 //어차피 방정보 변경은 여기로 돌아오는것을 함수로 해서 없에줘야함
    }
    private void gotoOurActivity(String room_key) {
        Intent intent=new Intent(this,OurActivity.class);
        intent.putExtra("room_key",room_key);
        startActivity(intent);
    }
    private void gotoMenuActivity(String room_key) {
        Intent intent=new Intent(this,MenuActivity.class);
        intent.putExtra("room_key",room_key);
        startActivity(intent);
        RoomActivity.this.finish();
    }
    private void gotoAlbumActivity(String room_key) {
        Intent intent=new Intent(this,AlbumActivity.class);
        intent.putExtra("room_key",room_key);
        startActivity(intent);
        RoomActivity.this.finish();
    }
    private void gotoChatActivity(String room_key) {
        Intent intent=new Intent(this,ChatActivity.class);
        intent.putExtra("room_key",room_key);
        startActivity(intent);
        RoomActivity.this.finish();
    }
    private void gotoMemberActivity(String room_key) {
        Intent intent=new Intent(this,MemberActivity.class);
        intent.putExtra("room_key",room_key);
        startActivity(intent);
        RoomActivity.this.finish();
    }
    private void loadImg(String room_key,String photo){
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = ref.child(room_key+"/"+photo);

        pathReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    Activity activity = RoomActivity.this;                                          //에러처리 떄문에 왠지는 모르겠음 ㅜㅜ
                    if (activity.isFinishing())                                                     //에러처리 떄문에 왠지는 모르겠음 ㅜㅜ
                        return;                                                                     //에러처리 떄문에 왠지는 모르겠음 ㅜㅜ

                    Glide.with(RoomActivity.this)
                            .load(task.getResult())
                            .into(imageView);
                } /*else {
                    // URL을 가져오지 못하면 토스트 메세지
                    Toast.makeText(RoomActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }
    BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            return true;

                        case R.id.nav_chat:
                            gotoChatActivity(KEY);
                            return true;

                        case R.id.nav_menu:
                            gotoMenuActivity(KEY);
                            return true;

                        case R.id.nav_album:
                            gotoAlbumActivity(KEY);
                            return true;

                        case R.id.nav_member:
                            gotoMemberActivity(KEY);
                            return true;

                    }
                    return false;
                }
            };

}
