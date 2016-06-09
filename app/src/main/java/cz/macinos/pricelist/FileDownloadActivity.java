package cz.macinos.pricelist;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cz.macinos.pricelist.list.PricelistActivity;

/**
 * Downloads a selected file from Google Drive.
 */
public class FileDownloadActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "FileDownloadActivity";

    /**
     * File name for pricelist database storing.
     */
    private static final String DB_FILENAME = "pricelist-db";

    public static final String EXISTING_FILE_ID = " ";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    private DriveId driveId;

    /**
     * Called when activity gets visible. A connection to Drive services need to
     * be initiated as soon as the activity is visible. Registers
     * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "*****************Getting the intent");
        Intent i = getIntent();
        driveId = (DriveId) i.getParcelableExtra("DRIVE_ID");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        Drive.DriveApi.fetchDriveId(getGoogleApiClient(), EXISTING_FILE_ID).setResultCallback(idCallback);
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    final private ResultCallback<DriveIdResult> idCallback = new ResultCallback<DriveIdResult>() {
        @Override
        public void onResult(DriveIdResult result) {
            new RetrieveDriveFileContentsAsyncTask(FileDownloadActivity.this).execute(result.getDriveId());
        }
    };

    final private class RetrieveDriveFileContentsAsyncTask extends ApiClientAsyncTask<DriveId, Boolean, String> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackgroundConnected(DriveId... params) {
            String pricelistContents = null;
            DriveFile file = driveId.asDriveFile();
            DriveContentsResult driveContentsResult = file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            BufferedReader reader;
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                reader = new BufferedReader(new InputStreamReader(driveContents.getInputStream(), "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    Log.i(TAG, "*** LINE READ: " + line);
                    builder.append(line+"\n");
                }
                pricelistContents = builder.toString();
            } catch (IOException e) {
                Log.e(TAG, "IOException while reading from the stream", e);
            }

            //write the selected file pricelistContents to pricelist database
            try {
                Log.i(TAG, "*************Creating database...");
                FileOutputStream fos = openFileOutput(DB_FILENAME, Context.MODE_PRIVATE);
                fos.write(pricelistContents.getBytes());
                fos.close();

                // Save the loaded pricelist to shared preferences database (shared among activities, but not among other apps (MODE_PRIVATE))
                SharedPreferences sp = getSharedPreferences(getString(R.string.preference_database), MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString(getString(R.string.preference_database), pricelistContents);
                ed.commit();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFoundException while creating pricelist database file", e);
            } catch (IOException e) {
                Log.e(TAG, "IOException while writing to pricelist database file", e);
            }

            driveContents.discard(getGoogleApiClient());
            return pricelistContents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                showMessage("Error while reading from the file");
                return;
            }
            showMessage(getString(R.string.file_loaded));

            Intent i = new Intent(FileDownloadActivity.this, PricelistActivity.class);
            //i.putExtra("PRICELIST", result);
            startActivity(i);

            finish();
        }
    }
}
