package sys.diag.car.activity;

import static sys.diag.car.common.DataImageUtil.copyImageToInternalStorage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import dagger.hilt.android.AndroidEntryPoint;
import sys.diag.car.R;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.UserDto;
import sys.diag.car.viewmodels.CarViewModel;
import sys.diag.car.viewmodels.UserViewModel;
@AndroidEntryPoint
public class UserDetailActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private UserViewModel userViewModel;
    private CarViewModel carViewModel;
    private ImageView avatar;
    private AppCompatButton btnLogout,btnBack,btnSync;
    private TextView joinDate,carCount,email,userName;
    private UserDto userDto;
    private String token;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail_profile);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        setUp();
        userViewModel.getCurrent().observe(this,user-> loadData(user));
        userViewModel.getUserCars().observe(this,cars-> {
            carCount.setText(String.valueOf(cars.get(0).cars.size()));
            Log.e("CAR",String.valueOf(cars.get(0).cars.size()));
        });


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userViewModel.Logout(userDto);



            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BTNSync","CLick");
                    carViewModel.synchronizeData(token);

            }
        });
    }
    private void setUp(){
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack2);
        btnSync = findViewById(R.id.btnSync);
        avatar = findViewById(R.id.ivAvatars);
        joinDate = findViewById(R.id.tvJoinDateValue);
        carCount = findViewById(R.id.tvCarCountValue);
        userName = findViewById(R.id.tvName);
        email = findViewById(R.id.tvEmail);
   }
   private void loadData(UserDto user){
        email.setText(user.getEmail());
        userName.setText(user.getName());
        token = user.getAccessToken();
        loadAvatar(user.getImagePath());
        userDto=user;
   }
   private void loadAvatar(String imagePath){


       if( imagePath!=null &&!imagePath.equals("No_Data")) {
           Picasso.get()
                   .load( "file://"+imagePath)
                   .placeholder(R.drawable.img_place_holder)
                   .error(R.drawable.img_error)
                   .into(avatar);
           Log.e("IMG","Image has but something went wrong");
       }else {
           Picasso.get()
                   .load(R.drawable.img_place_holder)
                   .into(avatar);
           Log.e("IMG","Image null ");
       }
   }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String internalPath=copyImageToInternalStorage(imageUri,UserDetailActivity.this);
            userDto.setImagePath(internalPath);
            if(userDto.getImagePath()!=null)
                Log.e("IMG","Image has but something went wrong");
            try {
                userViewModel.Edit(userDto);
            }catch(Exception ex){
                Log.e("Add Avatar",ex.getMessage());
            }
        }
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
