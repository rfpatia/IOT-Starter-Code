package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class ValueActivity extends AppCompatActivity {

    private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "ARG_DEVICEID";

    private TextView tv;
    private ParticleDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);
        tv = (TextView) findViewById(R.id.value);

        //...
        // Do network work on background thread
        /********
         * This code is to program on what will happen when the app start, for example, getting variable to get the status of the vacumn cleaner robot is it on or off
         * or subscribe event where it will automatically change the status of the robot in the ui when the status is change from the hardware.
         */
        Async.executeAsync(ParticleCloud.get(ValueActivity.this), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));

                long subscriptionId;  // save this for later, for unsubscribing
                subscriptionId = device.subscribeToEvents(
                        "the name of the subscription set in the hardware",  // the first argument, "eventNamePrefix", is optional
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {

                                /************
                                 * one of the format to handle subscription
                                if(event.dataPayload.equals("ON0")) {
                                    ledStatus1 = true;
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            findViewById(R.id.button1).setBackgroundColor(0xFF00FF00);
                                        }
                                    })
                                }*/
                            }

                            public void onEventError(Exception e) {
                                Log.d("sometag", "Event error: ", e);
                            }
                        });

                return null;

            }

            @Override
            public void onSuccess(Object i) { // this goes on the main thread
                //This is where you are going to change the ui if it success
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                e.printStackTrace();
            }
        });




        /*************
         * This code is to handle a program when the button is pressed
         */
        findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
                // Do network work on background thread
                Async.executeAsync(ParticleCloud.get(ValueActivity.this), new Async.ApiWork<ParticleCloud, Object>() {
                    @Override
                    public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                        device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                        /************
                         * start programming here. basically this is where you are going to get data from cloud
                         */
                        Object variable;
                        try {
                            variable = device.getVariable("val");
                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            Toaster.l(ValueActivity.this, e.getMessage());
                            variable = -1;
                        }
                        return variable;
                        //end of progrram
                    }

                    @Override
                    public void onSuccess(Object i) { // this goes on the main thread
                        //This is where you are going to change the ui if it success
                        tv.setText(i.toString());
                    }

                    @Override
                    public void onFailure(ParticleCloudException e) {
                        e.printStackTrace();
                    }
                });
            }
        });





    }

    public static Intent buildIntent(Context ctx, Integer value, String deviceid) {
        Intent intent = new Intent(ctx, ValueActivity.class);
        intent.putExtra(ARG_VALUE, value);
        intent.putExtra(ARG_DEVICEID, deviceid);

        return intent;
    }


}
