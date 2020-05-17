package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PlayerResponseEvent {
    @Serializable
    class JOINED(val yourPlayer: Player) : PlayerResponseEvent()

    @Serializable
    class NEXTPLAYER(val nextPlayer: Player) : PlayerResponseEvent()
}