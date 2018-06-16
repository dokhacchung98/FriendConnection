package com.example.admin.friendconnection;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

public class FetchAddressIntentService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//
//    public final class Constants {
//        public static final int SUCCESS_RESULT = 0;
//        public static final int FAILURE_RESULT = 1;
//        public static final String PACKAGE_NAME =
//                "com.google.android.gms.location.sample.locationaddress";
//        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
//        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
//                ".RESULT_DATA_KEY";
//        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
//                ".LOCATION_DATA_EXTRA";
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        if (intent == null) {
//            return;
//        }
//        String errorMessage = "";
//
//        // Get the location passed to this service through an extra.
//        Location location = intent.getParcelableExtra(
//                Constants.LOCATION_DATA_EXTRA);
//
//        // ...
//
//        List<Address> addresses = null;
//
//        try {
//            addresses = geocoder.getFromLocation(
//                    location.getLatitude(),
//                    location.getLongitude(),
//                    // In this sample, get just a single address.
//                    1);
//        } catch (IOException ioException) {
//            // Catch network or other I/O problems.
//            errorMessage = getString(R.string.service_not_available);
//            Log.e(TAG, errorMessage, ioException);
//        } catch (IllegalArgumentException illegalArgumentException) {
//            // Catch invalid latitude or longitude values.
//            errorMessage = getString(R.string.invalid_lat_long_used);
//            Log.e(TAG, errorMessage + ". " +
//                    "Latitude = " + location.getLatitude() +
//                    ", Longitude = " +
//                    location.getLongitude(), illegalArgumentException);
//        }
//
//        // Handle case where no address was found.
//        if (addresses == null || addresses.size() == 0) {
//            if (errorMessage.isEmpty()) {
//                errorMessage = getString(R.string.no_address_found);
//                Log.e(TAG, errorMessage);
//            }
//            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
//        } else {
//            Address address = addresses.get(0);
//            ArrayList<String> addressFragments = new ArrayList<String>();
//
//            // Fetch the address lines using getAddressLine,
//            // join them, and send them to the thread.
//            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
//                addressFragments.add(address.getAddressLine(i));
//            }
//            Log.i(TAG, getString(R.string.address_found));
//            deliverResultToReceiver(Constants.SUCCESS_RESULT,
//                    TextUtils.join(System.getProperty("line.separator"),
//                            addressFragments));
//        }
//    }
}
