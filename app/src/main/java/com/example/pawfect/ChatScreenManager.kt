package com.example.pawfect

object ChatScreenManager {
    private var currentChatId: String? = null

    fun setChatOpen(chatId: String?) {
        currentChatId = chatId
    }

    fun isChatOpen(chatId: String): Boolean {
        return currentChatId == chatId
    }
}
