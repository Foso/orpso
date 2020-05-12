package tictactoe.ui.home

import challenge.usecase.MessageUseCase
import components.materialui.Modal
import de.jensklingenberg.sheasy.model.Coordinate
import de.jensklingenberg.sheasy.model.Weapon
import kotlinx.css.Contain
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
        showSnackbar = false
        gameStateText = "Hallo"
        overlayList = emptyList()
        showChooseWeaponModal=false
    }

    override fun componentDidMount() {
        presenter.onCreate()
    }


    override fun RBuilder.render() {

        messageUseCase.showErrorSnackbar(this, state.errorMessage, snackbarVisibility())

        div {
            Modal {
                attrs {
                    this.open=state.showChooseWeaponModal
                }

                div {
                    div{
                        img {
                            attrs {
                                height = "50"
                                width = "50"
                                onClickFunction = {
                                    // presenter.onCellClicked(Coordinate(rowIndex, colIndex))
                                    presenter.onWeaponChoosed(Weapon.Scissors)
                                }

                                src =
                                    "images/scissors_blue.svg"

                            }
                        }
                        +"Scissors"

                    }

                    div{
                        img {
                            attrs {
                                height = "50"
                                width = "50"
                                onClickFunction = {
                                    // presenter.onCellClicked(Coordinate(rowIndex, colIndex))
                                    presenter.onWeaponChoosed(Weapon.Rock)
                                }

                                src =
                                    "images/rock_blue.svg"

                            }
                        }
                        +"Rock"

                    }

                    div{
                        img {
                            attrs {
                                height = "50"
                                width = "50"
                                onClickFunction = {
                                    // presenter.onCellClicked(Coordinate(rowIndex, colIndex))
                                    presenter.onWeaponChoosed(Weapon.Paper)
                                }

                                src =
                                    "images/paper_blue.svg"

                            }
                        }
                        +"Paper"

                    }
                }


            }
        }



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
                                                presenter.onCellClicked(Coordinate(rowIndex, colIndex))
                                            }

                                            val overlayItem = state.overlayList.find {
                                                it.coordinate == Coordinate(
                                                    rowIndex,
                                                    colIndex
                                                )
                                            }
                                            src =  overlayItem?.imgPath ?: ""
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
                                            val elementItem = state.imageList.find {
                                                it.coordinate == Coordinate(
                                                    rowIndex,
                                                    colIndex
                                                )
                                            }
                                            src = elementItem?.imgPath ?: ""

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

    override fun setCellData(coordinate: Coordinate, playerValue: String) {


    }

    override fun showError(error: String) {
        setState {
            showSnackbar = true
            errorMessage = error
        }
    }

    override fun setGameData(map: Array<Array<String>>) {

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

    override fun setOverlayList(overlays: List<ElementImage>) {
        setState {
            this.overlayList = overlays
        }
    }

    override fun showChooseWeaponDialog() {
        setState {
            this.showChooseWeaponModal=true
        }
    }

    override fun hideChooseWeaponDialog() {
        setState {
            this.showChooseWeaponModal=false
        }
    }
}


fun RBuilder.home() = child(HomeView::class) {}




