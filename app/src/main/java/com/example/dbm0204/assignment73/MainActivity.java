package com.example.dbm0204.assignment73;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.RejectedExecutionException;

import static android.R.attr.data;

public class MainActivity extends Activity implements View.OnClickListener {

    private final static int NEW_PICTURE=1001;
    private final static int SELECT_PICTURE=1002;
    private String selectedImagePath;
    private ImageView mImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b1 =(Button) findViewById(R.id.camera);
        b1.setOnClickListener(this);
        Button b2= (Button) findViewById(R.id.gallery);
        b2.setOnClickListener(this);
        mImage= (ImageView) findViewById(R.id.image);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,NEW_PICTURE);


            case R.id.gallery:
                Intent intent2 = new Intent();
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent2,
                        "Select Picture"), SELECT_PICTURE);
                break;

        }
    }

    public void OnActivityResult(int requestCode,int resultCode, Intent intent)
    {
        if(resultCode== RESULT_OK){
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = intent.getData();
                selectedImagePath = getPath(selectedImageUri);
                Uri selectedImage = intent.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                mImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            }
            else if(requestCode== NEW_PICTURE)
            {
                Bitmap thumbnail = (Bitmap) intent.getExtras().get("data");
                mImage.setImageBitmap(thumbnail);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
                try {
                    file.createNewFile();
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        }
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }
}



