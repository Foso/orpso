package tictactoe.ui.home

import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.Status
import react.RState
import tictactoe.model.ElementImage

interface HomeContract {
    interface View {
        fun setCellData(coord: Coord, playerValue: String)
        fun showError(error: String)
        fun setGameData(map: Array<Array<String>>)
        fun setPlayerId(id: Int)
        fun setgameStateText(text:String)
        fun setElement(warriors:List<ElementImage>)
        fun setOverlayList(overlays:List<Coord>)

    }

    interface Presenter {
        fun onCreate()
        fun sendMessage(message: String)
        fun onCellClicked(coord: Coord)
        fun reset()
        fun joinGame()
    }

    interface HomeViewState : RState {
        var imageList : List<ElementImage>
        var gameArray: Array<Array<String>>
        var errorMessage: String
        var status: Status
        var showSnackbar: Boolean
        var playerId: Int
        var gameStateText:String

        var overlayArray: Array<Array<String>>
        var overlayList : List<Coord>


    }
}