package com.unforgivendevelopment.riltester;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = "RILTester";

    private EditText CmdRespText = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void sendRawRILRequest(View view) {
        try {
            CmdRespText = (EditText) findViewById(R.id.edit_response);
            CmdRespText.setText("---Wait response---");

            // public int invokeOemRilRequestRaw(byte[] oemReq, byte[] oemResp)
            TelephonyManager tmInstance = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            Class tm = Class.forName(tmInstance.getClass().getName());

            //Class[] methodParameters = new Class[]{byte[].class, byte[].class};

            byte[] input = { (byte) 0x00, (byte) 0x43 };// your input goes here

            byte[] oemResponse = new byte[1024]; // get your results here
            String oemOutputStr = new String("");

            Object[] params = new Object[]{input, oemResponse};
            Method method = tm.getClass().getDeclaredMethod("invokeOemRilRequestRaw", byte[].class, byte[].class);
            //int result = (Integer) method.invoke(tm, params);
            int result = (Integer)method.invoke(tmInstance, params);

            int size = oemResponse.length;

            log("oemResponse length=[" + Integer.toString(size) + "]");
            oemOutputStr += "oemResponse length=[" + Integer.toString(size) + "]" +
                    "\n";
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    byte myByte = oemResponse[i];
                    int myInt = (int) (myByte & 0xFF);
                    log("oemResponse[" + Integer.toString(i) + "]=[0x" +
                            Integer.toString(myInt, 16) + "]");
                    oemOutputStr += "oemResponse[" + Integer.toString(i) + "]=[0x" +
                            Integer.toString(myInt, 16) + "]" + "\n";
                }
            }
            CmdRespText.setText( "" + oemOutputStr );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void log(String msg)
    {
        Log.d(LOG_TAG, "[RILTESTER] " + msg);
    }

}
