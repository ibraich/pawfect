package com.example.pawfect

class Message {
    var senderId: String = ""
    var receiverId: String = ""
    var messageText: String = ""
    var timestamp: String = ""
    var timeMillis: Long = 0L
    var isRead: Boolean = false

    constructor()

    constructor(timestamp: String, messageText: String) {
        this.timestamp = timestamp
        this.messageText = messageText
    }

    constructor(
        senderId: String,
        receiverId: String,
        messageText: String,
        timestamp: String,
        timeMillis: Long,
        isRead: Boolean
    ) {
        this.senderId = senderId
        this.receiverId = receiverId
        this.messageText = messageText
        this.timestamp = timestamp
        this.timeMillis = timeMillis
        this.isRead = isRead
    }
}
