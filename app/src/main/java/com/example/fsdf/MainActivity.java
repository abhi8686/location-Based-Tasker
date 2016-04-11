package com.example.fsdf;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class MainActivity extends FragmentActivity {
	private GoogleMap googleMap;
	 SQLiteDatabase mydatabase;
	LatLng position;
	private Button button;
	final Context context = this;
	PendingIntent pendingIntent;
	private AlarmManager manager;
    CheckBox silent, wifi,bluetooth, ring,vibrate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent alarmIntent = new Intent(this, TaskManager.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
        	 
			@Override
			public void onClick(View arg0) {
 
				// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.prompts, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
 
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
 
				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
                silent = ((CheckBox) promptsView.findViewById(R.id.silent));
                wifi = ((CheckBox) promptsView.findViewById(R.id.wifi));
                ring = ((CheckBox) promptsView.findViewById(R.id.ring));
                vibrate = ((CheckBox) promptsView.findViewById(R.id.vibrate));
                bluetooth = ((CheckBox) promptsView.findViewById(R.id.bluetooth));
                final EditText radius = ((EditText) promptsView.findViewById(R.id.radius));
 
				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Done",
					  new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog,int id) {
						// get user input and set it to result
						// edit text
					    	mydatabase = openOrCreateDatabase("geomind",MODE_PRIVATE,null);
					        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Store(Job VARCHAR,Latitude double, Longitude double,wifi int, bluetooth int, vibrate int, silent int, ring int, radius int);");
					        mydatabase.execSQL("INSERT INTO Store VALUES('"+userInput.getText()+"',"+position.latitude+","+position.longitude+","+(wifi.isChecked()?1:0)+", "+(bluetooth.isChecked()?1:0)+", "+(vibrate.isChecked()?1:0)+", "+(silent.isChecked()?1:0)+", "+(ring.isChecked()?1:0)+", "+radius.getText()+");");
					        mydatabase.close();
					        Toast.makeText(
		              	               MainActivity.this,
		              	                "Task added",
		              	                Toast.LENGTH_SHORT).show();
					    }
					  })
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
 
			}

		
			
		});
        
        
    }
    protected void onPause()
    {


            super.onPause();
            googleMap.clear();

    }
    protected void onResume() {
    	 double lat,longt;
        super.onResume();
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;
       
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
      //  Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
         lat = 	gps.getLatitude(); // returns latitude
       longt = 	gps.getLongitude();
      
       //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat + "\nLong: " + longt, Toast.LENGTH_LONG).show();  
        
        }
        else
        {
        	 lat = 13.3470;
        	 longt = 74.7880;
        	 
        }
        position = new LatLng(lat,longt);
        if (googleMap !=null){
        	CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(lat, longt)).zoom(15).build();
     
    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

  		  googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longt)).draggable(true)
  		      .title("Drag to the desired location"));
            Toast.makeText(getApplicationContext(), "Drag the marker to desired location.", Toast.LENGTH_SHORT).show();
  		  googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

              @Override
              public void onMarkerDragStart(Marker marker) {
                  
              }

              @Override
              public void onMarkerDragEnd(Marker marker) {

               position = marker.getPosition(); //
              	Toast.makeText(
              	                MainActivity.this,
              	                "Lat " + position.latitude
              	                        ,
              	                Toast.LENGTH_SHORT).show();
            	Toast.makeText(
      	                MainActivity.this,
      	                 "Long " + position.longitude,
      	                Toast.LENGTH_SHORT).show();
              }

              @Override
              public void onMarkerDrag(Marker marker) {

              }
          });

  		}
  	else
  	{
  		  Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
  	}
    }
 
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId())
//        {
//
//        case R.id.menu_view:
//            Intent i = new Intent(MainActivity.this, Second.class);
//            startActivity(i);

//            default:
//            	return super.onOptionsItemSelected(item);    }
        int id=item.getItemId();
        if (id == R.id.about) {

            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(context);
            View aboutView = li.inflate(R.layout.about, null);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(aboutView);



            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("Alright!",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text
                                    dialog.cancel();
                                }
                            });



            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return true;
        }
       
        return super.onOptionsItemSelected(item);

    }


}    