package tictactoe.ui.home

import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.Status
import react.RState

interface HomeContract {
    interface View {
        fun setCellData(coord: Coord, playerValue: String)
        fun showError(error: String)
        fun setGameData(map: Array<Array<String>>)
        fun setPlayerId(id: Int)
    }

    interface Presenter {
        fun onCreate()
        fun sendMessage(message: String)
        fun onCellClicked(coord: Coord)
        fun reset()
        fun joinGame()
    }

    interface HomeViewState : RState {
        var map: Array<Array<String>>
        var errorMessage: String
        var status: Status
        var showSnackbar: Boolean
        var playerId: Int

    }
}