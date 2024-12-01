package com.example.chatapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chatapplication.R;
import com.example.chatapplication.adapters.UserAdapter;
import com.example.chatapplication.databinding.ActivityUsersBinding;
import com.example.chatapplication.models.User;
import com.example.chatapplication.utilities.Constants;
import com.example.chatapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.chatapplication.listeners.UserListeners;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListeners{

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUsers();
        SetListeners();
    }
    private void SetListeners() {
        binding.ImageBack.setOnClickListener(v -> {
            Log.d("SetListeners", "Back button clicked");
            onBackPressed();  // Updated back handling
        });
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size() > 0){
                            UserAdapter userAdapter = new UserAdapter(users,this);
                            binding.RecyclerView.setAdapter(userAdapter);
                            binding.RecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showerror();
                        }
                    }else{
                        showerror();
                    }
                });
    }
    private void showerror(){
        binding.TextErrorMessage.setText(String.format("%s", "No users available"));
        binding.TextErrorMessage.setVisibility(View.VISIBLE);
    }
    private void loading(Boolean isloading){
        if(isloading){
            binding.ProgressBar.setVisibility(View.VISIBLE);
}else{
            binding.ProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}