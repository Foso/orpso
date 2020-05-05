package tictactoe.ui.home

import challenge.usecase.MessageUseCase
import de.jensklingenberg.sheasy.model.Coord
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
        map = Array(3) { Array(3) { "-" } }
        newMap = Array(3) { Array(3) { "-" } }

        showSnackbar = false
        gameStateText = "Hallo"

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
                div("content") {
                    table("mytable2") {
                        tbody {
                            state.map.forEachIndexed { index, columns ->
                                tr {
                                    columns.forEachIndexed { index2, _ ->
                                        td {
                                            img {
                                                attrs {
                                                    height = "50"
                                                    width = "50"
                                                    if (state.map[index][index2] != "-") {
                                                        src = when (state.map[index][index2]) {
                                                            "0" -> "images/ximg.png"
                                                            "1" -> "images/oimg.png"
                                                            else -> ""
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

                div("overlay") {
                    table("mytable") {

                        tbody {
                            state.map.forEachIndexed { index, columns ->
                                tr {
                                    columns.forEachIndexed { index2, _ ->
                                        td {
                                            img {
                                                attrs {
                                                    height = "50"
                                                    width = "50"
                                                    onClickFunction = {
                                                        presenter.onCellClicked(Coord(index, index2))
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

            p {
                +state.gameStateText
            }

        }
    }


    private fun snackbarVisibility(): Boolean {
        return state.showSnackbar

    }


    override fun setCellData(coord: Coord, playerValue: String) {
        setState {
            this.map[coord.y][coord.x] = playerValue
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
            this.map = map
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


}


fun RBuilder.home() = child(HomeView::class) {}




