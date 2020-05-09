package ges.example.kotlinserver.game

import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.Player

interface RpsGameContract {
    interface RpsGameServer {
        fun sendBroadcast(data: String)
        fun sendData(playerId: Int, data: String)
        fun onPlayerAdded(sessionId: String, player: Player)
    }

    interface Presenter {
        fun onMakeMove(playerId: Int, coord: Coord)
        fun onReset()
        fun onAddPlayer(sessionId: String)
        fun onMoveChar(playerId: Int,fromCoord: Coord,toCoord: Coord)
    }
}