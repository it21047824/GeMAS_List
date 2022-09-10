package com.example.gemaslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class ImageCropper extends AppCompatActivity {

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);

        readIntentData();

        String destinationUri = UUID.randomUUID().toString() + ".jpg";
        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(),destinationUri)))
                .withAspectRatio(5,7)
                .withMaxResultSize(330, 370)
                .start(ImageCropper.this);
    }

    private void readIntentData() {
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String uriString = intent.getStringExtra("DATA");
            uri = Uri.parse(uriString);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            Uri croppedUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", croppedUri+"");
            setResult(100, returnIntent);
            finish();
        }
    }
}