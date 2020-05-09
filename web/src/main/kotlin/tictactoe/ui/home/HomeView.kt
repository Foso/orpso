package tictactoe.ui.home

import challenge.usecase.MessageUseCase
import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.Warrior
import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.*
import react.setState

class HomeView : RComponent<RProps, HomeContract.HomeViewState>(), HomeContract.View {
    private val messageUseCase = MessageUseCase()

    private val presenter: HomeContract.Presenter by lazy {
        HomePresenter(this)
    }


    override fun HomeContract.HomeViewState.init() {
        elementList = emptyList()
        gameArray = Array(6) { Array(7) { "" } }
        overlayArray = Array(6) { Array(7) { "" } }
        showSnackbar = false
        gameStateText = "Hallo"
        overlayList = emptyList()
    }

    override fun componentDidMount() {
        presenter.onCreate()
    }


    override fun RBuilder.render() {

        messageUseCase.showErrorSnackbar(this, state.errorMessage, snackbarVisibility())


        div("imagesGrid") {
            attrs {
                style = kotlinext.js.js {
                    margin = "0"
                }
            }


            button {
                attrs {
                    text("Join Game")
                    onClickFunction = {
                        presenter.joinGame()
                    }
                }
            }

            button {
                attrs {
                    text("Reset")
                    onClickFunction = {
                        presenter.reset()
                    }
                }
            }

            button {
                attrs {
                    text("Player: " + state.playerId)
                    onClickFunction = {

                    }
                }
            }

            div("container") {
                gameField()

                gameOverlay()
            }

            p {
                +state.gameStateText
            }

        }
    }

    private fun RDOMBuilder<DIV>.gameOverlay() {
        div("overlay") {
            table("mytable") {

                tbody {
                    (0..GameSettings.ROWS-1).forEach {rowIndex->

                        tr {

                            (0..GameSettings.COLS-1).forEach {colIndex->
                                        td {
                                            img {
                                                attrs {
                                                    height = "50"
                                                    width = "50"
                                                    onClickFunction = {
                                                        presenter.onCellClicked(Coord(rowIndex, colIndex))
                                                    }


                                                    src = if (state.overlayList.find { it == Coord(rowIndex, colIndex) }!=null) {

                                                        "images/Letter_o.svg"
                                                    }else{
                                                        ""
                                                    }
                                                }
                                            }
                                        }

                                }
                            }






                    }
                }
            }
        }
    }

    private fun RDOMBuilder<DIV>.gameField() {
        div("gameDiv") {
            table("mytable2") {
                tbody {
                    state.gameArray.forEachIndexed { index, columns ->
                        tr {
                            columns.forEachIndexed { index2, _ ->
                                td {
                                    img {
                                        attrs {
                                            height = "50"
                                            width = "50"
                                            if (state.elementList.find { it.coord == Coord(index, index2) }!=null) {

                                                src = "images/player.png"
                                            }else{
                                                src = ""
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun snackbarVisibility(): Boolean {
        return state.showSnackbar

    }

    override fun setCellData(coord: Coord, playerValue: String) {
        setState {
            this.gameArray[coord.y][coord.x] = playerValue
        }

    }

    override fun showError(error: String) {
        setState {
            showSnackbar = true
            errorMessage = error
        }
    }

    override fun setGameData(map: Array<Array<String>>) {
        setState {
            // this.gameArray = map
            this.overlayArray = Array(6) { Array(7) { "" } }
        }
    }

    override fun setPlayerId(id: Int) {
        setState {
            this.playerId = id
        }
    }

    override fun setgameStateText(text: String) {
        setState {
            this.gameStateText = text
        }
    }


    override fun setElement(warriors: List<Warrior>) {
        setState {
            this.elementList=warriors
        }
    }

    override fun setOverlayList(overlays: List<Coord>) {
        setState {
            this.overlayList=overlays
        }
    }
}


fun RBuilder.home() = child(HomeView::class) {}




