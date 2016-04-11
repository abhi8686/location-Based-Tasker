package com.example.fsdf;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import android.provider.Settings;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;

public class TaskManager extends BroadcastReceiver {
	private double rad2deg(double rad) {
		
		  return (rad * 180 / Math.PI);
		
		}
	private double deg2rad(double deg) {
		
		  return (deg * Math.PI / 180.0);
	
		}
	SQLiteDatabase mydatabase;
	 NotificationManager NM;
    @SuppressWarnings("deprecation")
	@Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
    	mydatabase = arg0.openOrCreateDatabase("geomind",0,null);
    	 mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Store(Job VARCHAR,Latitude double, Longitude double,wifi int, bluetooth int, vibrate int, silent int, ring int,radius int);");
		Cursor cursor = mydatabase.rawQuery("Select * from Store",null);
		Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();
		GPSTracker gps = new GPSTracker(arg0);
		if(gps.canGetLocation()){
			double lat = 	gps.getLatitude(); // returns latitude
			double longt = 	gps.getLongitude();
			if(cursor!=null && cursor.getCount()>0)
			{

				cursor.moveToFirst();

				do {
					double lon1 = cursor.getDouble(cursor.getColumnIndexOrThrow("Longitude"));
					double lat1 = cursor.getDouble(cursor.getColumnIndexOrThrow("Latitude"));
		        	double theta = lon1 - longt;
		        	
					double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat)) * Math.cos(deg2rad(theta));

					dist = Math.acos(dist);
		        	
		        	  dist = rad2deg(dist);
		        	
		        	  dist = dist * 60 * 1.1515;
		        	
		        	 
		        	    dist = dist * 1.609344;
		        	
		        	   
		        	    if(dist < cursor.getInt(8)) {
							Log.d("radius",String.valueOf(cursor.getInt(8)));
							if (cursor.getInt(cursor.getColumnIndexOrThrow("wifi")) > 0) {
								WifiManager wifiManager = (WifiManager) arg0.getSystemService(Context.WIFI_SERVICE);
								if (!wifiManager.isWifiEnabled()) {
									wifiManager.setWifiEnabled(true);
								}
								Log.d("wifi", "enabled");
							}
							if (cursor.getInt(cursor.getColumnIndexOrThrow("wifi")) < 1) {
								WifiManager wifiManager = (WifiManager) arg0.getSystemService(Context.WIFI_SERVICE);
								if (wifiManager.isWifiEnabled()) {
									wifiManager.setWifiEnabled(false);
								}
								Log.d("wifi", "disabled");

							}
							if (cursor.getInt(cursor.getColumnIndexOrThrow("bluetooth")) > 0) {
								final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
								bluetooth.enable();
								Log.d("bluetooth", "enabled");
							}
							if (cursor.getInt(cursor.getColumnIndexOrThrow("bluetooth")) == 0) {

								final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
								bluetooth.disable();
								Log.d("bluetooth", "disabled");
							}

							if (cursor.getInt(cursor.getColumnIndexOrThrow("vibrate")) > 0) {
								AudioManager manager = (AudioManager) arg0.getSystemService(Context.AUDIO_SERVICE);
								manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
								Log.d("vibrate ", "on");
							}
							if (cursor.getInt(cursor.getColumnIndexOrThrow("silent")) > 0) {
								final AudioManager mode = (AudioManager) arg0.getSystemService(Context.AUDIO_SERVICE);
								mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
								Log.d("silent ", "on");
							}
							if (cursor.getInt(cursor.getColumnIndexOrThrow("ring")) > 0) {
								final AudioManager mode = (AudioManager) arg0.getSystemService(Context.AUDIO_SERVICE);
								mode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							}



						}


				} while (cursor.moveToNext());
			}


    }

}
    
}
