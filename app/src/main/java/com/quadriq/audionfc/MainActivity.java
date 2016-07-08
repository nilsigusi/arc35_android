package com.quadriq.audionfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.acs.audiojack.AudioJackReader;

public class MainActivity extends AppCompatActivity {


    private final BroadcastReceiver mHeadsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                boolean plugged = (intent.getIntExtra("state", 0) == 1);

                /* Mute the audio output if the reader is unplugged. */
                //mReader.setMute(!plugged);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        Acr3x acr = new Acr3x(mAudioManager);
        acr.start(new Acr3xNotifListener() {
            @Override
            public void onUUIDAavailable(String uuid) {
                Log.d("onUUID", uuid);
            }

            @Override
            public void onFirmwareVersionAvailable(String firmwareVersion) {
                Log.d("onFirmwareVersion", firmwareVersion);

            }
        });

//        acr.read(new Acr3xNotifListener() {
//            @Override
//            public void onUUIDAavailable(String uuid) {
//                Log.d("onUUID", uuid);
//            }
//
//            @Override
//            public void onFirmwareVersionAvailable(String firmwareVersion) {
//                Log.d("onFirmwareVersion", firmwareVersion);
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
