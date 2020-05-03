package tictactoe.ui.home

import challenge.usecase.MessageUseCase
import de.jensklingenberg.sheasy.model.Coord
import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.RDOMBuilder
import react.dom.button
import react.dom.div
import react.setState

class HomeView : RComponent<RProps, HomeContract.HomeViewState>(), HomeContract.View {
    private val messageUseCase = MessageUseCase()

    private val presenter: HomeContract.Presenter by lazy {
        HomePresenter(this)
    }


    override fun HomeContract.HomeViewState.init() {
        map = Array<Array<String>>(3) { Array(3) { "-" } }
        showSnackbar = false

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

            firstRow()

            secondRow()
            thirdRow()


        }


    }

    private fun snackbarVisibility(): Boolean {
        return state.showSnackbar

    }

    private fun RDOMBuilder<DIV>.firstRow() {
        div {
            button {
                attrs {
                    text(state.map[0][0])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(0, 0))
                    }
                }
            }
            button {
                attrs {
                    text(state.map[0][1])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(0, 1))
                    }
                }
            }
            button {
                attrs {
                    text(state.map[0][2])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(0, 2))
                    }
                }
            }
        }
    }

    private fun RDOMBuilder<DIV>.secondRow() {
        div {
            button {
                attrs {
                    text(state.map[1][0])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(1, 0))
                    }
                }
            }
            button {
                attrs {
                    text(state.map[1][1])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(1, 1))
                    }
                }
            }
            button {
                attrs {
                    text(state.map[1][2])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(1, 2))
                    }
                }
            }
        }
    }

    private fun RDOMBuilder<DIV>.thirdRow() {
        div {
            button {
                attrs {
                    text(state.map[2][0])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(2, 0))
                    }
                }
            }
            button {
                attrs {
                    text(state.map[2][1])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(2, 1))
                    }
                }
            }
            button {
                attrs {
                    text(state.map[2][2])
                    onClickFunction = {
                        presenter.onCellClicked(Coord(2, 2))
                    }
                }
            }
        }
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


}


fun RBuilder.home() = child(HomeView::class) {}




