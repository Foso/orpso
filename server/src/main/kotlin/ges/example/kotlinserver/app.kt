package ges.example.kotlinserver

import ges.example.kotlinserver.server.ChatApplication


data class ChatSession(val id: String)


fun main() {
    ChatApplication()
}

