package sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kaya on 06/01/17.
 */

public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    SharedPreferences.Editor editor;



    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        //editor = context.getSharedPreferences("received_message", MODE_PRIVATE).edit();

        String senderNum = "0";
        String message = "0";

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    senderNum = phoneNumber;
                    message = currentMessage.getDisplayMessageBody();


                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);








                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    //Toast toast = Toast.makeText(context,
                      //      "senderNum: "+ senderNum + ", message: " + message, duration);
                    //toast.show();

                } // end for loop
            } // bundle is null





        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }


        Bundle extras = intent.getExtras();
        Intent i = new Intent("broadCastName");
        // Data you need to pass to activity
        i.putExtra("phone", senderNum);
        i.putExtra("mesaj", message);

        context.sendBroadcast(i);

        //editor.putString("phone",senderNum);
        //editor.putString("mesaj",message);
        //editor.commit();


    }


}