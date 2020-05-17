package ges.example.kotlinserver.game

import de.jensklingenberg.sheasy.model.Coordinate
import de.jensklingenberg.sheasy.model.Player
import de.jensklingenberg.sheasy.model.Weapon

interface RpsGameContract {
    interface RpsGameServer {
        fun sendBroadcast(data: String)
        fun sendData(playerId: Int, data: String)
        fun onPlayerAdded(sessionId: String, player: Player)

    }

    interface Presenter {
        fun onReset()
        fun onAddPlayer(sessionId: String)
        fun onMoveChar(playerId: Int, fromCoordinate: Coordinate, toCoordinate: Coordinate)
        fun onReceivedSelectedDrawWeapon(playerId: Int, weapon: Weapon)
        fun shuffle(playerId: Int)
        fun addFlag(playerId: Int, coordinate: Coordinate)
        fun addTrap(playerId: Int, coordinate: Coordinate)
        fun onPlayerReady(playerId: Int)
    }
}