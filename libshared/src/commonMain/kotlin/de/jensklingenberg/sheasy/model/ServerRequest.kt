package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable


@Serializable
enum class ServerRequestTypes {
    PLAYEREVENT,
    MESSAGE,
    ERROR,
    UNKNOWN,
    RESET,

    MOVECHAR
}

@Serializable
sealed class PlayerRequestEvent {

    @Serializable
    class JoinGameRequest : PlayerRequestEvent()

    @Serializable
    class AddFlag(val coordinate: Coordinate) : PlayerRequestEvent()

    @Serializable
    class AddTrap(val coordinate: Coordinate) : PlayerRequestEvent()

    @Serializable
    class ShuffleElements : PlayerRequestEvent()

    @Serializable
    class StartGame : PlayerRequestEvent()

    @Serializable
    class SelectedDrawWeapon(val weapon: Weapon) : PlayerRequestEvent()

    @Serializable
    class MoveCharRequest(val fromCoordinate: Coordinate, val toCoordinate: Coordinate) : PlayerRequestEvent()
}

@Serializable
sealed class ServerRequest(val id: Int) {

    @Serializable
    class PlayerRequest(val playerRequestEvent: PlayerRequestEvent) : ServerRequest(
        ServerRequestTypes.PLAYEREVENT.ordinal
    )

    @Serializable
    class ResetRequest : ServerRequest(ServerRequestTypes.RESET.ordinal)

    @Serializable
    class MessageRequest : ServerRequest(ServerRequestTypes.MESSAGE.ordinal)

}

