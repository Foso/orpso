package tictactoe.ui.home

import de.jensklingenberg.sheasy.model.Coordinate
import de.jensklingenberg.sheasy.model.Weapon

import react.RState
import tictactoe.model.ElementImage

interface HomeContract {
    interface View {
        fun setCellData(coordinate: Coordinate, playerValue: String)
        fun showError(error: String)
        fun setGameData(map: Array<Array<String>>)
        fun setPlayerId(id: Int)
        fun setgameStateText(text: String)
        fun setElement(warriors: List<ElementImage>)
        fun setOverlayList(overlays: List<ElementImage>)
        fun showChooseWeaponDialog()
        fun hideChooseWeaponDialog()

    }

    interface Presenter {
        fun onCreate()
        fun sendMessage(message: String)
        fun onCellClicked(coordinate: Coordinate)
        fun reset()
        fun joinGame()
        fun onWeaponChoosed(weapon: Weapon)
    }

    interface HomeViewState : RState {
        var imageList: List<ElementImage>
        var errorMessage: String
        var showSnackbar: Boolean
        var playerId: Int
        var gameStateText: String
        var showChooseWeaponModal: Boolean
        var overlayList: List<ElementImage>


    }
}