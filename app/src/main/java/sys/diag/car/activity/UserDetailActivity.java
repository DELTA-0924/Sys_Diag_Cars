package sys.diag.car.activity;

import static sys.diag.car.common.DataImageUtil.copyImageToInternalStorage;
import static sys.diag.car.common.DataImageUtil.deleteImagesByName;
import static sys.diag.car.common.Utility.NO_IMAGE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import sys.diag.car.R;
import sys.diag.car.common.DataImageUtil;
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
    private String token,refreshToken;
    private ProgressBar loadingUi;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail_profile);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        setUp();
        userViewModel.getCurrent().observe(this,result-> {
            if(result.status == Result.Status.SUCCESS)
                loadData(result.data);
        });
        userViewModel.getUserCars().observe(this,cars-> {
            if(!cars.isEmpty())
                carCount.setText(String.valueOf(cars.get(0).cars.size()));
        });

        carViewModel.getSynchronizeData().observe(this,result->{
            if(result.status == Result.Status.SUCCESS && result.data!=null){
                Toasty.success(UserDetailActivity.this, "Синхронизирован", Toast.LENGTH_SHORT).show();
                loadingUi.setVisibility(View.GONE);
            }
            else{
                Toasty.error(UserDetailActivity.this, result.message, Toast.LENGTH_SHORT).show();
                loadingUi.setVisibility(View.GONE);
            }
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
                DataImageUtil.deleteAllSavedImages(UserDetailActivity.this);
                Intent intent = new Intent(UserDetailActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    carViewModel.synchronizeData(token,UserDetailActivity.this.getFilesDir());
                    loadingUi.setVisibility(View.VISIBLE);
            }
        });
    }
    private void setUp(){
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack2);
        btnSync = findViewById(R.id.btnSync);
        avatar = findViewById(R.id.ivAvatars);

        carCount = findViewById(R.id.tvCarCountValue);
        userName = findViewById(R.id.tvName);
        email = findViewById(R.id.tvEmail);
        loadingUi = findViewById(R.id.loading2);
   }
   private void loadData(UserDto user){
        email.setText(user.getEmail());
        userName.setText(user.getName());
        token = user.getAccessToken();
        refreshToken = user.getRefreshToken();
        loadAvatar(user.getImagePath());
        userDto=user;
   }
   private void loadAvatar(String imagePath){


       if( imagePath!=null &&!imagePath.equals(NO_IMAGE)) {
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

            String imageFileName = userDto.getId()+"_User"+".jpg";
            deleteImagesByName(UserDetailActivity.this,imageFileName);
            String internalPath=copyImageToInternalStorage(imageUri,UserDetailActivity.this,imageFileName);
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

}
