package com.example.chatapplication.activities;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.chatapplication.utilities.Constants;
import com.example.chatapplication.utilities.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chatapplication.databinding.ActivitySignupBinding;

import com.example.chatapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }
    private void setListeners() {
        binding.textSignIn.setOnClickListener(v-> getOnBackPressedDispatcher().onBackPressed());
        binding.textSignUp.setOnClickListener(v->{
            if(isValidSignUpDetails()){
                signUp();
            }
        });
        binding.LayoutImage.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImage.launch(intent);
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void signUp(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.NameInput.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.EmailInput.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.PasswordInput.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    PreferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    PreferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    PreferenceManager.putString(Constants.KEY_NAME, binding.NameInput.getText().toString());
                    PreferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showToast("Error: " + e.getMessage());
                });
    }
    private String EncodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    Uri imageUri = result.getData().getData();
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.ImageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodedImage = EncodedImage(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );
    private Boolean isValidSignUpDetails(){
        if(encodedImage == null){
            showToast("Select profile image");
            return false;
        }
        else if(binding.NameInput.getText().toString().trim().isEmpty()){
            showToast("Enter name");
            return false;
        }
        else if(binding.EmailInput.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.EmailInput.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        }
        else if(binding.PasswordInput.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }
        else if(binding.PasswordInput.getText().toString().trim().length() < 6){
            showToast("Password must be at least 6 characters");
            return false;
        }
        else if(binding.PasswordVerifyInput.getText().toString().trim().isEmpty()){
            showToast("Enter password again");
            return false;
        }
        else if(!binding.PasswordInput.getText().toString().trim().equals(binding.PasswordVerifyInput.getText().toString().trim())){
            showToast("Passwords do not match");
            return false;
        }
        else{
            return true;
        }

    }
    public void loading (Boolean isloading){
        if(isloading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.textSignUp.setVisibility(View.INVISIBLE);
        }
        else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textSignUp.setVisibility(View.VISIBLE);
        }

    }

}