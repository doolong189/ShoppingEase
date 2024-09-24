package com.hoanglong180903.shoppingease.model

class MessageModel(
    val messageId: String = "",
    var messageText: String = "",
    var senderId: String = "",
    var messageImageUrl: String = "",
    val timestamp: Long = 0) {
}