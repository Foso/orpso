package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable


@Serializable
enum class ServerRequestTypes {
    PLAYEREVENT,
    MESSAGE,
    ERROR,
    UNKNOWN,
    RESET,
    JOINGAME,
    MOVECHAR
}

@Serializable
sealed class PlayerRequestEvent {
    @Serializable
    class SelectedDrawWeapon(val weapon: Weapon) : PlayerRequestEvent()
}

@Serializable
sealed class ServerRequest(val id: Int) {

    @Serializable
    class JoinGameRequest : ServerRequest(ServerRequestTypes.JOINGAME.ordinal)


    @Serializable
    class PlayerRequest(val playerRequestEvent: PlayerRequestEvent) : ServerRequest(
        ServerRequestTypes.PLAYEREVENT.ordinal
    )

    @Serializable
    class MoveCharRequest(val fromCoordinate: Coordinate, val toCoordinate: Coordinate) : ServerRequest(
        ServerRequestTypes.MOVECHAR.ordinal
    )

    @Serializable
    class ResetRequest : ServerRequest(ServerRequestTypes.RESET.ordinal)

    @Serializable
    class MessageRequest : ServerRequest(ServerRequestTypes.MESSAGE.ordinal)

}

