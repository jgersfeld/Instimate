package com.tag.photocaptureandgallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.takeimage.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static com.example.takeimage.R.id.caption;

public class MainActivity extends Activity {

	private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
	Button btnSelect;
	Button calculate;
	ImageView ivImage;
	String userChoosenTask;
	EditText userCaption;
	EditText userLocation; //DONE
	TimePicker userTime; //WORK ON THIS SOME MORE
	EditText userNumTagged; //DONE
	EditText userNumFollowers; //DONE
	int numFollowers;
	int likes;
	int hour;
	int hashtag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
		btnSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
		calculate = (Button) findViewById(R.id.calculate);
		calculate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				displayLikes();
			}
		});
		ivImage = (ImageView) findViewById(R.id.ivImage);
		userCaption = (EditText) findViewById(caption);
		userTime = (TimePicker) findViewById(R.id.timePicker);
		userLocation = (EditText) findViewById(R.id.location);
		userNumTagged = (EditText) findViewById(R.id.tagged);
		userNumFollowers = (EditText) findViewById(R.id.numberOfFollowers);
		likes = 0;
		hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if(userChoosenTask.equals("Take Photo"))
						cameraIntent();
					else if(userChoosenTask.equals("Choose from Library"))
						galleryIntent();
				} else {
					//code for deny
				}
				break;
		}
	}

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				boolean result=Utility.checkPermission(MainActivity.this);

				if (items[item].equals("Take Photo")) {
					userChoosenTask ="Take Photo";
					if(result)
						cameraIntent();

				} else if (items[item].equals("Choose from Library")) {
					userChoosenTask ="Choose from Library";
					if(result)
						galleryIntent();

				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	private void galleryIntent()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
	}

	private void cameraIntent()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE)
				onSelectFromGalleryResult(data);
			else if (requestCode == REQUEST_CAMERA)
				onCaptureImageResult(data);
		}
	}

	private void onCaptureImageResult(Intent data) {
		Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

		File destination = new File(Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".jpg");

		FileOutputStream fo;
		try {
			destination.createNewFile();
			fo = new FileOutputStream(destination);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ivImage.setImageBitmap(thumbnail);
	}

	@SuppressWarnings("deprecation")
	private void onSelectFromGalleryResult(Intent data) {
		Bitmap bm=null;
		if (data != null) {
			try {
				bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ivImage.setImageBitmap(bm);
	}
	public int calculateBaseScore() {
		numFollowers = Integer.parseInt(userNumFollowers.getText().toString());
		if(numFollowers <= 50) {
			return 10;
		}
		else if (numFollowers <=100) {
			return 30;
		}
		else if(numFollowers <=150) {
			return 50;
		}
		else if(numFollowers <=250) {
			return 75;
		}
		else if(numFollowers <=300) {
			return 90;
		}
		else if(numFollowers <=400) {
			return 100;
		}
		else if(numFollowers <=500) {
			return 110;
		}
		else if(numFollowers <=600) {
			return 200;
		}
		else if(numFollowers <=700) {
			return 700/3;
		}
		else if(numFollowers <=800) {
			return 800/3;
		}
		else if(numFollowers <=900) {
			return 300;
		}
		else if(numFollowers <= 1000) {
			return 1000/3;
		}
		return 1;

	}

	public int calculateHashtag() {
		hashtag = 0;
		String caption = userCaption.getText().toString();
		String delims = "[ ]+";
		String[] tokens = caption.split(delims);

		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].contains("#"))
				hashtag++;
		}

		return hashtag;
	}

	public double calculateTime() {
		double timePercent = 0;
		if (hour == 0) {
			timePercent = 1.030;
		}
		if(hour == 1) {
			timePercent = 1.011;
		}
		if(hour == 2) {
			timePercent = 0.896;
		}
		if(hour == 3) {
			timePercent = .724;
		}
		if(hour == 4) {
			timePercent = .542;
		}
		if (hour == 5) {
			timePercent = .372;
		}
		if(hour == 6) {
			timePercent = .257;
		}
		if(hour == 7) {
			timePercent = .194;
		}
		if(hour == 8) {
			timePercent = .152;
		}
		if(hour == 9) {
			timePercent = .155;
		}
		if(hour == 10) {
			timePercent = .204;
		}
		if(hour == 11) {
			timePercent = .320;
		}
		if(hour == 12) {
			timePercent = .481;
		}
		if(hour == 13) {
			timePercent = .657;
		}
		if(hour == 14) {
			timePercent = .811;
		}
		if (hour == 15) {
			timePercent = .921;
		}
		if(hour == 16) {
			timePercent = 1.011;
		}
		if(hour == 17) {
			timePercent = 1.055;
		}
		if(hour == 18) {
			timePercent = .994;
		}
		if(hour == 19) {
			timePercent = .976;
		}
		if (hour == 20) {
			timePercent = .994;
		}
		if(hour == 21) {
			timePercent = .994;
		}
		if(hour == 22) {
			timePercent = .976;
		}
		if(hour == 23) {
			timePercent = 1.008;
		}

		return timePercent;
	}

	public void displayLikes() {
		int baseScore =	this.calculateBaseScore();
		likes = baseScore;
		if(userLocation.getText()!=null) {
			likes += baseScore*.0035;
		}
		if(userNumTagged != null) {
			likes += baseScore*.0277;
		}
		hashtag = this.calculateHashtag();
		likes += hashtag * .0069;
		likes += baseScore * this.calculateTime();

		Toast.makeText(getApplicationContext(), "You have a predicted amount of " + likes + " likes!", Toast.LENGTH_LONG).show();
	}



}
