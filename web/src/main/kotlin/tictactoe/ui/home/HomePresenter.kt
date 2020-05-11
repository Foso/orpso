package tictactoe.ui.home

import com.badoo.reaktive.observable.subscribe
import de.jensklingenberg.sheasy.model.*
import tictactoe.game.GameDataSource
import tictactoe.game.GameRepository
import tictactoe.model.ElementImage
import kotlin.browser.window

class GameSettings{
    companion object{
        val ROWS = 6
        val COLS = 7
    }
}

class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {
    private val elementList = mutableListOf<Warrior>()

    private val gameDataSource: GameDataSource = GameRepository()
    var selectedWarrior: Warrior? = null
    var overlayArrows: MutableList<Coord> = mutableListOf()

    override fun onCreate() {
        gameDataSource.prepareGame()

        gameDataSource.observeGameState().subscribe(onNext = { state ->
            when (state) {
                GameState.NewGame -> {
                }
                GameState.Started -> {
                    view.setgameStateText("GAME STARTED")
                }
                is GameState.Ended -> {
                    window.alert("PLAYER ${state.winnerID} HAS WON " + state.isWon)
                    view.setgameStateText("GAME ENDED")
                }
                is GameState.Lobby -> {
                    view.setgameStateText("WAITING FOR OPPONENTS")
                }
                is GameState.DrawEvent -> {
                    window.alert("CHOOSE WEAPON")


                }
                is GameState.GameUpdate -> {

                }
            }
        })

        gameDataSource.observeGameChanges().subscribe(onNext = {
            view.setGameData(it)
        })

        gameDataSource.observeMap().subscribe(onNext = { it ->
            if (it.isNotEmpty()) {
                elementList.clear()
                elementList.addAll(it)
                val imgList = elementList.map {
                    ElementImage(
                        getWeaponImagePath(it.owner.id, it.weapon),
                        it.coord
                    )

                }
                view.setElement(imgList)
            }
        })

        gameDataSource

        // createList(elementList)


    }


    override fun sendMessage(message: String) {
        //myWebSocket?.send(message)
    }

    override fun onCellClicked(coord: Coord) {
        val selectedOverlay = overlayArrows.find { it == coord }
        if(selectedOverlay!=null){
            if(selectedWarrior!=null){
                console.log("Move from: "+selectedWarrior?.coord+ " to "+coord)
                gameDataSource.onMoveChar(selectedWarrior!!.coord,coord)

                view.setOverlayList(overlayArrows)
                overlayArrows.clear()
            }
        }else{
            overlayArrows.clear()
            val selected = elementList.find { it.coord == coord }

            if(selected?.weapon is Weapon.Trap ||
                selected?.weapon is Weapon.Flag
               // || selected?.weapon is Weapon.Hidden
            ){
                return
            }

            if (selected != null) {
                selectedWarrior = selected

                //Left
                if(selectedWarrior?.coord?.x!=0){
                    overlayArrows.add(coord.copy(x = coord.x - 1))
                }

                //Right
                if(selectedWarrior?.coord?.x!=GameSettings.COLS){
                    overlayArrows.add(coord.copy(x = coord.x + 1))
                }

                //Top
                if(selectedWarrior?.coord?.y!=0){
                    overlayArrows.add(coord.copy(coord.y-1))
                }

                //Bottom
                if(selectedWarrior?.coord?.y!=GameSettings.ROWS){
                    overlayArrows.add(coord.copy(coord.y+1))
                }

                view.setOverlayList(overlayArrows)
            }
        }

        //gameDataSource.makeAMove(coord)
    }

    override fun reset() {

        gameDataSource.requestReset()
    }

    override fun joinGame() {
        gameDataSource.join()
    }

}