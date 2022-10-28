package com.RedRose.gemaslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class ImageCropper extends AppCompatActivity {

    private Uri uri;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);

        readIntentData();

        String destinationUri = UUID.randomUUID().toString() + ".jpg";
        if (type != null) {
            if (type.equals("banner")){
                UCrop.of(uri, Uri.fromFile(new File(getCacheDir(),destinationUri)))
                        .withAspectRatio(35,18)
                        .start(ImageCropper.this);
            }
        } else {
            UCrop.of(uri, Uri.fromFile(new File(getCacheDir(),destinationUri)))
                    .withAspectRatio(5,7)
                    .withMaxResultSize(330, 370)
                    .start(ImageCropper.this);
        }
    }

    private void readIntentData() {
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String uriString = intent.getStringExtra("DATA");
            uri = Uri.parse(uriString);
            type = intent.getStringExtra("TYPE");
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