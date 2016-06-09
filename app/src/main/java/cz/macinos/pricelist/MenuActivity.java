package cz.macinos.pricelist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cz.macinos.pricelist.list.PricelistActivity;

/**
 * First activity, where user can load new pricelist or return to existing one.
 */
public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "pricelist.MenuActivity";

    RelativeLayout layout;
    Button gdiskButton;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Create");
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout) findViewById(R.id.relative_layout);
        gdiskButton = (Button) findViewById(R.id.btn_gd_open_file);
        backButton = (Button) findViewById(R.id.btn_back_to_pricelist);

        gdiskButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, GoogleDriveActivity.class);
                startActivity(i);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, PricelistActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"Start");

        if (pricelistNotCreated()) {
            backButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"Stop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Destroy");
    }

    /**
     * Checks if pricelist has been already created or not.
     * @return TRUE if it is NOT created, FALSE otherwise.
     */
    private boolean pricelistNotCreated() {
        //get pricelist from shared preferences
        SharedPreferences sp = getSharedPreferences(getString(R.string.preference_database), MODE_PRIVATE);
        String rawPricelist = sp.getString(getString(R.string.preference_database), null);
        Log.i(TAG, "Raw pricelist: " + rawPricelist);
        if (rawPricelist == null) {
            return true;
        }

        return false;
    }

    private void showToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
