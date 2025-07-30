package com.example.janayejackson_gymlog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.janayejackson_gymlog.database.GymLogRepository;
import com.example.janayejackson_gymlog.database.entities.User;
import com.example.janayejackson_gymlog.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private GymLogRepository repository;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = GymLogRepository.getRepository(getApplication());

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(!verifyUser()){
                    toastMaker("Invalid credentials");
                }else {
                    Intent intent = MainActivity.mainActivityIntentFactory(getApplicationContext(), 0);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean verifyUser(){
        String username = binding.userNameLoginEditText.getText().toString();
        if(username.isEmpty()){
            toastMaker("username should not be blank");
            return false;
        }
        user = repository.getUserBuUserName(username);
        if(user != null){
            String password = binding.passwordLoginEditText.getText().toString();
            if(password.equals(user.getPassword())){
                return true;
            }else{
                toastMaker("Invalid password");
                return false;
            }
        }
        toastMaker(String.format("No %s found", username));
        return false;

    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}