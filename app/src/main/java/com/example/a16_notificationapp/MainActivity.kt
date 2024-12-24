package com.example.a16_notificationapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.core.content.res.ResourcesCompat
import com.example.a16_notificationapp.databinding.ActivityMainBinding


/*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val CHANNEL_ID = "Meri channel ki ID Jese Delivery notification"
    private val NOTIFICATION_ID = 69

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sendNotification()
    }

    private fun sendNotification(){

        val nm:NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "New channel lmfao!", NotificationManager.IMPORTANCE_HIGH))

        // Below is code for obtaining image which would be displayed on notification, don't use "SVG"!!
        // Because we want the image in "Bitmap" form, and there is a "bit-map" conversion factory which
        // is internally called, that cannot convert SVG to Bitmap use only PNG (Recommended) and JPG if u want to
        var pngInDrawableForm = ResourcesCompat.getDrawable(resources, R.drawable.img, null)

        var drawableToBitmap = (pngInDrawableForm as BitmapDrawable).getBitmap()

        var notification = Notification.Builder(this)
            .setLargeIcon(drawableToBitmap)
            .setSmallIcon(R.drawable.img)
            .setContentText("This is the heading")
            .setSubText("This is the content msg")
            .setChannelId(CHANNEL_ID)
            .build()

        nm.notify(NOTIFICATION_ID, notification)
    }
}
*/

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val CHANNEL_ID = "delivery_notification_channel"
    private val NOTIFICATION_ID = 69

    private val BASIC_NOTIFICATION = 0
    private val NOTIFICATION_WITH_INTENT_TO_GO_TO_SECOND_ACTIVITY = 1
    private val NOTIFICATION_WITH_BIG_PICTURE_STYLE = 2
    private val NOTIFICATION_WITH_INBOX_STYLE = 3


    val REQ_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listener()
    }

    private fun listener(){

        binding.btnBasicNotification.setOnClickListener{
            requestNotificationPermission(BASIC_NOTIFICATION)
        }

        binding.btnIntentSecondActivityNotification.setOnClickListener{
            requestNotificationPermission(NOTIFICATION_WITH_INTENT_TO_GO_TO_SECOND_ACTIVITY)
        }

        binding.btnBigPictureStyle.setOnClickListener{
            requestNotificationPermission(NOTIFICATION_WITH_BIG_PICTURE_STYLE)
        }

        binding.btnInboxStyle.setOnClickListener{
            requestNotificationPermission(NOTIFICATION_WITH_INBOX_STYLE)
        }
    }

    private fun requestNotificationPermission(notificationType: Int) {

        // In newer android versions it is mandatory to take user permission before sending him notifications!
        // That is y on phone u get pop-up to allow notifications then only notificatinos are allowed on phone
        // So this function checks if phone user has a high Android version or not, and if he has a high android
        // version, it requests for the permission to allow the sending of notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            } else {
                sendNotification(notificationType)
            }
        } else {
            sendNotification(notificationType)      // For older android versions it is simply sendNotifications()    :)
        }
    }

    private fun sendNotification(notificationType: Int) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Delivery Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        nm.createNotificationChannel(channel)

        // Convert Drawable to Bitmap safely
        // Below is code for obtaining image which would be displayed on notification, don't use "SVG"!!
        // Because we want the image in "Bitmap" form, and there is a "bit-map" conversion factory which
        // is internally called, that cannot convert SVG to Bitmap use only PNG (Recommended) and JPG if u want to
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.img, null)
        val bitmap = (drawable as? BitmapDrawable)?.bitmap

        // Build Notification
        val notification = notificationBuilder(notificationType, bitmap)

        // Send notification to user using notification manager
        // Now this "NOTIFICATION_ID" is basically "id" for a notification, if ID is same for two notifications,
        // then they will be displayed as a single notification, but if ID if differenet, they'll be separate notifications
        // E.g. in whatsapp, when multiple msgs come from a single person they are all consolidated in a single notification,
        // but if different people are msging, then they are shown as separate notifications (As they have different IDS :)
        nm.notify(NOTIFICATION_ID, notification)
    }


    private fun notificationBuilder(notificationType : Int, bitmap : Bitmap?): Notification
    {
        return when(notificationType){

            BASIC_NOTIFICATION -> {
                Notification.Builder(this, CHANNEL_ID)
                    .setLargeIcon(bitmap)                               // This img is displayed on right side (Not mandatory) (U will see this in some notifications & in some u won't)
                    .setSmallIcon(R.mipmap.ic_launcher)                 // Use a valid small icon   // This is usually "icon" of the "app" it is displayed on left top corner..
                    .setContentTitle("This is the heading")             // Title of notification
                    .setContentText("This is the most basic notification")
                    .build()
            }

            NOTIFICATION_WITH_INTENT_TO_GO_TO_SECOND_ACTIVITY -> {

                val intent = Intent(applicationContext, SecondActivity::class.java)

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)     //  Use of this line : If the activity you're launching (SecondActivity) already exists in the activity stack,
                // all activities on top of it will be removed (cleared), and the existing instance of SecondActivity will be brought to the foreground.
                // If instance of (SecondActivity) is not there in back-stack, it will create a new instance


                val pendingIntent = PendingIntent.getActivity(this, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                                // If a PendingIntent with the same request code (REQ_CODE) already exists,
                                // its extras or data are updated with the new intent’s extras or data
                                // this is because of "  PendingIntent.FLAG_UPDATE_CURRENT  "
                                // This prevents creating multiple PendingIntent objects for the same action

                    // PendingIntent.FLAG_IMMUTABLE ---> Immutable (FLAG_IMMUTABLE): The PendingIntent cannot be altered after creation.
                    //Use this when you don’t need to modify the intent later.


                Notification.Builder(this, CHANNEL_ID)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("This is notification..")
                    .setContentText("with intent to move to second activity")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)        // Dismiss notification on click (so notification is removed after it has been selected)
                    .build()
            }

            NOTIFICATION_WITH_BIG_PICTURE_STYLE -> {
                val bigPictureStyle = Notification.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(bitmap)
                    .setBigContentTitle("Image sent by Raman")                      // When u expand the img to see the "big picture"
                    .setSummaryText("This msg will be visible on expanding img")    // these two text msgs will be visible instead of
                                                                                    // "setContentTitle" and "setContentText"

                Notification.Builder(this, CHANNEL_ID)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Title when not expanded")
                    .setContentText("Text-content when not expanded")
                    .setStyle(bigPictureStyle)      // "setStyle()" function is used to leverage styles when a notification is expanded
                    .build()                        // on expansion code related to this style would be visible
            }

            NOTIFICATION_WITH_INBOX_STYLE -> {
                val inboxStyle = Notification.InboxStyle()
                    .addLine("This is line-1")
                    .addLine("This is line-2")
                    .addLine("This is line-3")
                    .addLine("This is line-4")
                    .addLine("This is line-5")
                    .addLine("This is line-6")
                    .addLine("This is line-7")
                    .addLine("This is line-8")
                    .addLine("This is line-9")
                    .addLine("This is line-10")
                    .setBigContentTitle("This is title on expansion")
                    .setSummaryText("This is content on expansion")

                Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher) // Use a valid small icon
                    .setContentTitle("Title when not expanded")
                    .setContentText("content when not expanded")
                    .setStyle(inboxStyle)                       // This style would be visible on expansion
                    .build()
            }

            else -> {
                Notification.Builder(this, CHANNEL_ID)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.mipmap.ic_launcher) // Use a valid small icon
                    .setContentTitle("This is the heading")
                    .setContentText("This is the most basic notification")
                    .build()
            }
        }
    }
}




