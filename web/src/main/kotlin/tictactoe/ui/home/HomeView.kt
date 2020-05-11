package tictactoe.ui.home

import challenge.usecase.MessageUseCase
import de.jensklingenberg.sheasy.model.Coord
import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.*
import react.setState
import tictactoe.model.ElementImage

class HomeView : RComponent<RProps, HomeContract.HomeViewState>(), HomeContract.View {
    private val messageUseCase = MessageUseCase()

    private val presenter: HomeContract.Presenter by lazy {
        HomePresenter(this)
    }


    override fun HomeContract.HomeViewState.init() {
        imageList = emptyList()
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
                    (0 until GameSettings.ROWS).forEach { rowIndex ->

                        tr {

                            (0 until GameSettings.COLS).forEach { colIndex ->
                                td {
                                    img {
                                        attrs {
                                            height = "50"
                                            width = "50"
                                            onClickFunction = {
                                                presenter.onCellClicked(Coord(rowIndex, colIndex))
                                            }

                                            val overlayItem = state.overlayList.find {
                                                it == Coord(
                                                    rowIndex,
                                                    colIndex
                                                )
                                            }
                                            src = if (overlayItem
                                                != null
                                            ) {

                                                "images/Letter_o.svg"
                                            } else {
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
                    (0 until GameSettings.ROWS).forEach { rowIndex ->
                        tr {
                            (0 until GameSettings.COLS).forEach { colIndex ->
                                td {
                                    img {
                                        attrs {
                                            height = "50"
                                            width = "50"
                                            val elementItem =state.imageList.find {
                                                it.coord == Coord(
                                                    rowIndex,
                                                    colIndex
                                                )
                                            }
                                            src = elementItem?.imgPath?:""

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


    override fun setElement(warriors: List<ElementImage>) {
        setState {
            this.imageList = warriors
        }
    }

    override fun setOverlayList(overlays: List<Coord>) {
        setState {
            this.overlayList = overlays
        }
    }
}


fun RBuilder.home() = child(HomeView::class) {}




