package avill.ladv.chordo.util.tools

import android.Manifest
import android.Manifest.permission_group
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object MyImplicitIntentManager {
    fun urlIntent(activity: Activity, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendText(activity: Activity, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            //Intent.setType = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT).show()
        }
    }

    fun playStore(activity: Activity) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.adobe.reader"))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT).show()
        }
    }

    fun youtube(activity: Activity) {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:5X7WWVTrBvM"))
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=5X7WWVTrBvM"))
            activity.startActivity(intent)
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    var SELECT_VIDEO = 555
    var VIDEO_PERMISSION = 5454
    fun pickVideo(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                VIDEO_PERMISSION
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            // set type
            intent.setType("video/*")
            // start activity result
            activity.startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                SELECT_VIDEO
            )
        }
    }

    fun senPhoneNumber(activity: Activity, phone: String) {
        // Create an implicit intent to view a webpage
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:+$phone"))
        // Verify that the intent resolves to an activity
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            activity.startActivity(intent)
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //    <uses-feature
    //        android:name="android.hardware.telephony"
    //        android:required="false" />
    //    <uses-permission android:name="android.permission.CALL_PHONE"/>
    var CALL = 21
    fun call(activity: Activity, phone: String) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL
            )
            return
        }
        // Create an implicit intent to view a webpage
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:+$phone"))
        // Verify that the intent resolves to an activity
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            activity.startActivity(intent)
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun sendEmail(activity: Activity, email: String, subject: String?, content: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        // add three fields to intent using putExtra function
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        // set type of intent
        intent.setType("message/rfc822")
        // startActivity with intent with chooser as Email client using createChooser function
        // Verify that the intent resolves to an activity
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            activity.startActivity(Intent.createChooser(intent, "Choose an Email client :"))
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun openSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        // Verify that the intent resolves to an activity
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            activity.startActivity(intent)
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun pickImage(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        // Verify that the intent resolves to an activity
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            activity.startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                SELECT_PICTURE
            )
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    var SELECT_PICTURE = 654

    //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    //<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    fun pickImageAndReturn(activity: Activity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        // Verify that the intent resolves to an activity
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            //activity.startActivity(intent);
            activity.startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                SELECT_PICTURE
            )
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    var SELECT_FILE = 654

    //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    //<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    fun pickFileAndReturn(activity: Activity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("file/*")
        // Verify that the intent resolves to an activity
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            //activity.startActivity(intent);
            activity.startActivityForResult(
                Intent.createChooser(intent, "Select the File"),
                SELECT_FILE
            )
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun googleMaps(activity: Activity, latitude: String, longitude: String) {
        /*String uri = String.format("geo:"+latitude+","+longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.maps");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // Start the activity if it resolves successfully
            activity.startActivity(intent);
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT).show();
        }*/
        val uri = "https://www.google.com.tw/maps/place/$latitude,$longitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        if (intent.resolveActivity(activity.packageManager) != null) {
            // Start the activity if it resolves successfully
            activity.startActivity(intent)
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //<uses-permission android:name="android.permission.SEND_SMS" />
    fun sendMsg(activity: Activity, phone: String?, content: String?) {
        val smsVIntent = Intent(Intent.ACTION_VIEW)
        // prompts only sms-mms clients
        smsVIntent.setType("vnd.android-dir/mms-sms")
        // extra fields for number and message respectively
        smsVIntent.putExtra("address", phone)
        smsVIntent.putExtra("sms_body", content)
        try {
            activity.startActivity(smsVIntent)
        } catch (ex: Exception) {
            Toast.makeText(
                activity, "Your sms has failed...",
                Toast.LENGTH_LONG
            ).show()
            ex.printStackTrace()
        }
    }

    var PERMISSION_REQUEST_CODE = 4444
    fun sendMessage(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it")
                val permissions = arrayOf(Manifest.permission.SEND_SMS)
                activity.requestPermissions(permissions, PERMISSION_REQUEST_CODE)
            }
        }
        try {
            // Get the default instance of the SmsManager
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                "4857974518",
                null,
                "Hola soy tu yo del pasado!",
                null,
                null
            )
            Toast.makeText(
                activity, "Your sms has successfully sent!",
                Toast.LENGTH_LONG
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                activity, "Your sms has failed...",
                Toast.LENGTH_LONG
            ).show()
            Log.e(MyImplicitIntentManager::class.java.simpleName, ex.message + "")
            ex.printStackTrace()
        }
    }

    fun openContacts(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        activity.startActivityForResult(intent, 1)
    }

    fun openWhatsapp(activity: Activity) {
        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        sendIntent.setType("text/plain")
        sendIntent.setPackage("com.whatsapp")
        activity.startActivity(Intent.createChooser(sendIntent, ""))
        //startActivity(sendIntent);
    }

    //TODO: CAMERA----------------------------------------------------------------------------------
    var CAMERA_REQUEST = 2394
    var CAMERA_RESULT = 209

    //    <!--camera-->
    //    <uses-permission android:name="android.permission.CAMERA" />
    //    <uses-feature android:name="android.hardware.camera" />
    fun openCameraPhoto(activity: Activity) {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.CAMERA),
                CAMERA_RESULT
            )
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activity.startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }

    //FIXME-----------------------------------------------------------------------------------------
    var REQUEST_VIDEO_CAPTURE = 22
    fun openCameraVideo(activity: Activity) {
        // check permission
        if (ContextCompat.checkSelfPermission(
                activity.applicationContext,
                permission_group.CAMERA
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // permission granted
            // continue the action
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.CAMERA),
                CAMERA_RESULT
            )
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            activity.startActivityForResult(cameraIntent, REQUEST_VIDEO_CAPTURE)
        }
    }
}
