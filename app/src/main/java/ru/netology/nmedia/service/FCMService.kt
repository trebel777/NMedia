package ru.netology.nmedia.service


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {
        message.data[action]?.let { actionType ->
            val listAction = Action.values().map { it.name }
            if (!listAction.contains(actionType)) {
                handleUnknown(message)
                return
            }
            when (Action.valueOf(actionType)) {
                Action.LIKE -> handleLike(gson.fromJson(message.data[content], Like::class.java))
                Action.NEW_POST -> handleNewPost(
                    gson.fromJson(
                        message.data[content],
                        NewPost::class.java
                    )
                )
            }
            return
        }
        val  testInputPushValue = message.data.values.map {
            gson.fromJson(it, Test::class.java)
        }[0]
        val myId = AppAuth.getInstance().authStateFlow.value.id
        when {
            testInputPushValue.recipientId == myId -> {
                handleTestAction(testInputPushValue,"Персональная рассылка")
            }
            testInputPushValue.recipientId == null -> {
                handleTestAction(testInputPushValue,"Массовая рассылка")
            }
            testInputPushValue.recipientId == 0L  -> {
                println("сервер считает, что у нас анонимная аутентификация, переотправляем токен")
                AppAuth.getInstance().sendPushToken()
            }
            testInputPushValue.recipientId != 0L -> {
                println("сервер считает, что у на нашем устройстве другая аутентификация, переотправляем токен")
                AppAuth.getInstance().sendPushToken()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleTestAction(content: Test, message: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.avatar48dp)
            .setContentTitle(content.content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
    }

    @SuppressLint("MissingPermission")
    private fun handleLike(content: Like){
        val notification = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.avatar48dp)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
    @SuppressLint("MissingPermission")
    private fun handleNewPost(content: NewPost){
        val notification = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.avatar48dp)
            .setContentTitle(content.userName)
            .setContentText(
                getString(
                R.string.notification_new_post,
                content.userName
            )
        )
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(content.content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
    @SuppressLint("MissingPermission")
    private fun handleUnknown(message: RemoteMessage) {
        var str = ""
        for ((key, value) in message.data) {
            str += "$key = $value \n"
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.avatar48dp)
            .setContentTitle(getString(R.string.notification_unsupported))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(str))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}

enum class Action {
    LIKE,
    NEW_POST
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)
data class NewPost(
    val userName: String,
    val content: String,
)

data class Test(
    val recipientId: Long? = null,
    val content: String
)
