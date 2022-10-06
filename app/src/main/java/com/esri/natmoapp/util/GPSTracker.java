package com.esri.natmoapp.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;

public class GPSTracker extends Service implements LocationListener {

	private static final Location TODO = null;
	private final Context mContext;
	private static GPSTracker mGpsTracker;
	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location, locationgps, locationnetwork; // location
	double latitude; // latitude
	double longitude; // longitude
	String provider_tag;

	DecimalFormat df = new DecimalFormat("#.########");
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES_Network = 0; // 10 meters

	// Declaring a Location Manager
	public LocationManager locationManager_Gps, locationManager_Network;
	public Boolean ProviderStatus = false;

	public GPSTracker(Context context) {
		this.mContext = context;
		initializeLocation();
	}

	public static GPSTracker getInstance(Context context) {
		if (mGpsTracker == null)
			mGpsTracker = new GPSTracker(context);

		return mGpsTracker;
	}

	public boolean isGPS() {
		boolean flag = false;
		// getting GPS status
		isGPSEnabled = locationManager_Gps.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (isGPSEnabled) {
			flag = true;

		}
		return flag;
	}

	@SuppressLint("WrongConstant")
	public void initializeLocation() {
		locationManager_Gps = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
		locationManager_Network = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
		// getting GPS status
		isGPSEnabled = locationManager_Gps.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		isNetworkEnabled = locationManager_Network.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!isGPSEnabled && !isNetworkEnabled) {
			// no network provider is enabled
		} else {
			this.canGetLocation = true;
			// First get location from Network Provider

			// if GPS Enabled get lat/long using GPS Services
			if (isGPSEnabled) {
				if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				locationManager_Gps.requestLocationUpdates(
						LocationManager.GPS_PROVIDER,
						0,
						0, this);
				Log.d("GPS Enabled", "GPS Enabled");
			}
			if (isNetworkEnabled) {
				locationManager_Network.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER,
						MIN_DISTANCE_CHANGE_FOR_UPDATES_Network,
						0, this);
				Log.d("Network", "Network");
			}
		}
	}

	public Location getLocation() {
		try {
			if (locationManager_Gps != null) {

				if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return TODO;
				}
				locationgps = locationManager_Gps
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				if (locationgps != null) {
					location = locationgps;
					provider_tag = locationgps.getProvider();
					latitude = locationgps.getLatitude();
					longitude = locationgps.getLongitude();
				} else if (locationManager_Network != null) {
					if (isNetworkEnabled) {
						locationnetwork = locationManager_Network
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (locationnetwork != null) {
							location = locationnetwork;
							provider_tag = locationnetwork.getProvider();
							latitude = locationnetwork.getLatitude();
							longitude = locationnetwork.getLongitude();
						}
					}
				}
			} else {
				Toast.makeText(mContext, "Searching Location Please Waite..", Toast.LENGTH_LONG).show();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	public Location getLocationUsingGPS() {
		if (locationManager_Gps != null) {

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return TODO;
			}
			locationgps = locationManager_Gps
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (locationgps != null) {
				location = locationgps;
				provider_tag = locationgps.getProvider();
				latitude = locationgps.getLatitude();
				longitude = locationgps.getLongitude();
			} else {
				getLocationUsingNetwork();
			}
		}

		return locationgps;
	}

	public Location getLocationUsingNetwork() {
		try {
			if (isNetworkEnabled) {
				if (locationManager_Network != null) {
					if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
						return TODO;
					}
					locationnetwork = locationManager_Network
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if (locationnetwork != null) {
						location = locationnetwork;
						provider_tag = locationnetwork.getProvider();
						latitude = locationnetwork.getLatitude();
						longitude = locationnetwork.getLongitude();
					}
				}
			} else {
				latitude = 0.0;
				longitude = 0.0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return locationnetwork;
	}

	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS() {
		if (locationManager_Gps != null) {
			locationManager_Gps.removeUpdates(GPSTracker.this);

		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}


	public String getProvider_tag() {
		return provider_tag;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showGPSSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		alertDialog.setTitle("GPS Disabled");

		alertDialog.setMessage("GPS is Disabled.Go to the settings to Enable GPS");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location updatelocation) {

		try {
			if (updatelocation != null) {
				location = updatelocation;
				provider_tag = location.getProvider();
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			} else {
				if (locationManager_Gps == null || locationManager_Network == null) {
					initializeLocation();
				}
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				updatelocation = locationManager_Gps.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (updatelocation != null) {
					location = updatelocation;
					provider_tag = location.getProvider();
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				} else {
					if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
						return;
					}
					updatelocation = locationManager_Network.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if(updatelocation!=null)
				{
					location=updatelocation;
					provider_tag=updatelocation.getProvider();
					latitude=updatelocation.getLatitude();
					longitude=updatelocation.getLongitude();
				}
			}
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
	
	
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}