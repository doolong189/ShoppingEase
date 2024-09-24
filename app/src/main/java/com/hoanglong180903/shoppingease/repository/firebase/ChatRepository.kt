package com.hoanglong180903.shoppingease.repository.firebase

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hoanglong180903.shoppingease.model.MessageModel
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.Date

class ChatRepository(val application: Application) {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val isSuccessful = MutableLiveData<Boolean>()
    fun sendMessage(
        messageTxt: String,
        senderUid: String,
        date: Long,
        senderRoom: String,
        receiverRoom: String,
        token: String,
        context: Context,
        name: String
    ) {
        val message = MessageModel(
            messageText = messageTxt,
            senderId = senderUid,
            timestamp = date
        )
        val randomKey: String = database.reference.push().key!!
        val lastMgsObj = HashMap<String, Any>()
        lastMgsObj["lastMsg"] = message.messageText
        lastMgsObj["lastMsgTime"] = date
        database.reference.child("Chats").child(senderRoom).updateChildren(lastMgsObj)
        database.reference.child("Chats").child(receiverRoom).updateChildren(lastMgsObj)
        database.reference.child("Chats")
            .child(senderRoom)
            .child("Messages")
            .child(randomKey)
            .setValue(message)
            .addOnSuccessListener {
                database.reference.child("Chats")
                    .child(receiverRoom)
                    .child("Messages")
                    .child(randomKey)
                    .setValue(message)
                    .addOnSuccessListener {
                        Log.d("sendMessage", "gui tin nhan thanh cong")
//                        sendNotification(name, messageTxt, token, context)
                    }
            }
            .addOnFailureListener {
                Log.d("sendMessage", "gui tin nhan khong thanh cong")
            }
    }

    fun fetchMessage(
        onSuccess: (List<MessageModel>) -> Unit,
        onFailure: (DatabaseError) -> Unit,
        senderRoom: String
    ) {
        database.getReference("Chats")
            .child(senderRoom)
            .child("Messages").addValueEventListener(object : ValueEventListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<MessageModel>()
                    snapshot.children.forEach { child ->
                        val user = child.getValue(MessageModel::class.java)
                        user?.let { messageList.add(it) }
                        Log.e("___user", user.toString())
                    }
                    onSuccess(messageList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error)
                }
            })
    }

    fun sendPhotoGallery(
        data: Intent,
        messageTxt: String,
        senderUid: String,
        senderRoom: String,
        receiverRoom: String,
        token: String,
        context: Context,
        name: String
    ) {
        val selectedImage: Uri = data.data!!
        val calendar = Calendar.getInstance()
        val reference: StorageReference =
            storage.reference.child("Chats").child(calendar.timeInMillis.toString() + "")
        reference.putFile(selectedImage).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener { uri ->
                    val filePath = uri.toString()
                    val messageTxt: String = messageTxt
                    val date = Date()
                    val message = MessageModel(
                        messageText = messageTxt,
                        senderId = senderUid,
                        messageImageUrl = filePath,
                        timestamp = date.time
                    )
                    val randomKey = database.reference.push().key
                    val lastMgsSender = HashMap<String, Any>()
                    lastMgsSender["lastMsg"] = "You just send an image"
                    lastMgsSender["lastMsgTime"] = date.time
                    val lastMgsReceiver = HashMap<String, Any>()
                    lastMgsReceiver["lastMsg"] = "You just receive an image"
                    lastMgsReceiver["lastMsgTime"] = date.time
                    database.reference.child("Chats").child(senderRoom)
                        .updateChildren(lastMgsSender)
                    database.reference.child("Chats").child(receiverRoom)
                        .updateChildren(lastMgsReceiver)
                    database.reference.child("Chats")
                        .child(senderRoom)
                        .child("Messages")
                        .child(randomKey!!)
                        .setValue(message)
                        .addOnSuccessListener {
                            database.reference.child("Chats")
                                .child(receiverRoom)
                                .child("Messages")
                                .child(randomKey)
                                .setValue(message)
                                .addOnSuccessListener {
                                    Log.d("send photo", "send photo successful")
//                                    sendNotification(name, "Photo", token, context)
                                }
                                .addOnFailureListener {
                                    Log.d("send photo", it.localizedMessage)
                                }
                            isSuccessful.value = task.isSuccessful
                        }
                        .addOnFailureListener {
                            isSuccessful.value = false
                        }
                }
            }
        }
    }

    fun sendCameraPhoto(
        data: Uri,
        messageTxt: String,
        senderUid: String,
        senderRoom: String,
        receiverRoom: String,
        token: String,
        context: Context,
        name: String
    ) {
        val selectedImage: Uri = data
        val calendar = Calendar.getInstance()
        val reference: StorageReference =
            storage.reference.child("Chats").child(calendar.timeInMillis.toString() + "")
        reference.putFile(selectedImage).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener { uri ->
                    val filePath = uri.toString()
                    val messageTxt: String = messageTxt
                    val date = Date()
                    val message = MessageModel(
                        messageText = messageTxt,
                        senderId = senderUid,
                        messageImageUrl = filePath,
                        timestamp = date.time
                    )
                    val randomKey = database.reference.push().key
                    val lastMgsSender = HashMap<String, Any>()
                    lastMgsSender["lastMsg"] = "You just send an image"
                    lastMgsSender["lastMsgTime"] = date.time
                    val lastMgsReceiver = HashMap<String, Any>()
                    lastMgsReceiver["lastMsg"] = "You just receive an image"
                    lastMgsReceiver["lastMsgTime"] = date.time
                    database.reference.child("Chats").child(senderRoom)
                        .updateChildren(lastMgsSender)
                    database.reference.child("Chats").child(receiverRoom)
                        .updateChildren(lastMgsReceiver)
                    database.reference.child("Chats")
                        .child(senderRoom)
                        .child("Messages")
                        .child(randomKey!!)
                        .setValue(message)
                        .addOnSuccessListener {
                            database.reference.child("Chats")
                                .child(receiverRoom)
                                .child("Messages")
                                .child(randomKey)
                                .setValue(message)
                                .addOnSuccessListener {
                                    Log.d("send photo", "send photo successful")
//                                    sendNotification(name, "Photo", token, context)
                                }
                                .addOnFailureListener {
                                    Log.d("send photo", it.localizedMessage)
                                }
                            isSuccessful.value = task.isSuccessful
                        }
                }
            }
        }
    }

    fun sendQuestionProduct(
        messageTxt: String,
        senderUid: String,
        senderRoom: String,
        receiverRoom: String,
        image: String
    ) {
        val filePath = image
        val messageTxt: String = messageTxt
        val date = Date()
        val message = MessageModel(
            messageText = messageTxt,
            senderId = senderUid,
            messageImageUrl = filePath,
            timestamp = date.time
        )
        val randomKey = database.reference.push().key
        val lastMgsSender = HashMap<String, Any>()
        lastMgsSender["lastMsg"] = "You just send an image"
        lastMgsSender["lastMsgTime"] = date.time
        val lastMgsReceiver = HashMap<String, Any>()
        lastMgsReceiver["lastMsg"] = "You just receive an image"
        lastMgsReceiver["lastMsgTime"] = date.time
        database.reference.child("Chats").child(senderRoom).updateChildren(lastMgsSender)
        database.reference.child("Chats").child(receiverRoom).updateChildren(lastMgsReceiver)
        database.reference.child("Chats")
            .child(senderRoom)
            .child("Messages")
            .child(randomKey!!)
            .setValue(message)
            .addOnSuccessListener {
                database.reference.child("Chats")
                    .child(receiverRoom)
                    .child("Messages")
                    .child(randomKey)
                    .setValue(message)
                    .addOnSuccessListener {
                        Log.d("send photo", "send photo successful")
                    }
                    .addOnFailureListener {
                        Log.d("send photo", it.localizedMessage)
                    }
            }
            .addOnFailureListener {
                isSuccessful.value = false
            }
    }


//    private fun sendNotification(name: String, message: String, token: String,context : Context) {
//        try {
//            val queue = Volley.newRequestQueue(context)
//            val url = "https://fcm.googleapis.com/fcm/send"
//            val data = JSONObject()
//            data.put("title", name)
//            data.put("body", message)
//            val notificationData = JSONObject()
//            notificationData.put("notification", data)
//            notificationData.put("to", token)
//            val request: JsonObjectRequest =
//                object : JsonObjectRequest(url, notificationData, Response.Listener { },
//                    Response.ErrorListener { error ->
//                        Log.e("notification", "" + error.localizedMessage)
//                    }) {
//                    @Throws(AuthFailureError::class)
//                    override fun getHeaders(): Map<String, String> {
//                        val map = java.util.HashMap<String, String>()
//                        val key =
//                            "Key=AAAAzCFOfys:APA91bE0zt9-aHqk3_s7EU7RIBjFcCDmteRzc0RGaa7jyWHnkZVpEBSlAKKO34Zu3ntJi0s6AruTHh3_du8fahM1ZWVSZMVG7747_n_Lv7a_7FtP-8VCNPYTFaC78aTNO06b_49eefuw"
//                        map["Content-Type"] = "application/json"
//                        map["Authorization"] = key
//                        return map
//                    }
//                }
//            queue.add(request)
//        } catch (ex: Exception) {
//        }
//    }
}

