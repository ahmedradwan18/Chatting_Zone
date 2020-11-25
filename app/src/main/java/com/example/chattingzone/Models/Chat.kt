package com.example.chattingzone.Models

data class Chat(
    var sender: String = "", var message: String = "", var receiver: String = "",
    var url: String = "", var messageID: String = "", var isSeen: Boolean = false
)
