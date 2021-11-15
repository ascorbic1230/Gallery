package com.example.demo01;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class AskPermissionActivity extends Activity {

    private static final int PERMISSION_CODE = 101;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        } else {
            startMainActivity();
        }
    }

    public static boolean hasPermissions(Context context, String[] listPermissions) {
        if (context != null && listPermissions != null) {
            for (String permission : listPermissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Read storage permission granted", Toast.LENGTH_SHORT).show();
                startMainActivity();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
            }
        }
    }
    public void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}