package tictactoe.ui.home

import challenge.usecase.MessageUseCase
import components.materialui.Modal
import de.jensklingenberg.sheasy.model.Coordinate
import de.jensklingenberg.sheasy.model.Weapon
import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement
import react.*
import react.dom.*
import tictactoe.model.ElementImage
import kotlin.browser.document
import kotlin.browser.window
import kotlin.lazy

fun getImage(path: String): HTMLImageElement {
    val image = window.document.createElement("img") as HTMLImageElement
    image.src = path
    return image
}


class MyCanvas : RComponent<RProps, RState>() {

    init {

    }

    override fun componentDidMount() {
        val canvas2 = document.createElement("canvas") as HTMLCanvasElement
        val context2 = canvas2.getContext("2d") as CanvasRenderingContext2D
        context2.canvas.width = window.innerWidth.toInt();
        context2.canvas.height = 300
        document.body?.appendChild(canvas2)


        context2.drawImage(getImage("http://try.kotlinlang.org/static/images/canvas/Kotlin-logo.png"), 50.0, 50.0)

    }

    override fun RBuilder.render() {

        canvas("mycan") {
            attrs {
                height = "300"

            }

            img {
                attrs {
                    height = "50"
                    width = "50"

                    src =
                        "images/scissors_blue.svg"

                }
            }
        }


        //   val tcan = can.getContext("2d") as CanvasRenderingContext2D


    }

}


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
        showChooseWeaponModal = false
    }

    override fun componentDidMount() {
        presenter.onCreate()
    }


    override fun RBuilder.render() {

        messageUseCase.showErrorSnackbar(this, state.errorMessage, snackbarVisibility())

        drawModal()

        div("imagesGrid") {

            //mCanvas()

            toolbar()

            div("container") {
                gameField()

                gameOverlay()
            }

            button {
                +"Shuffle"
            }

            button {
                attrs {
                    text("Start Game")
                    onClickFunction = {
                        presenter.startGame()
                    }
                }
            }

            p {
                +state.gameStateText
            }

        }
    }

    private fun RDOMBuilder<DIV>.toolbar() {
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
    }

    private fun RBuilder.drawModal() {
        div {
            Modal {
                attrs {
                    this.open = state.showChooseWeaponModal
                }

                div {
                    div {
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

                    div {
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

                    div {
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
                                            src = overlayItem?.imgPath ?: ""
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
            this.showChooseWeaponModal = true
        }
    }

    override fun hideChooseWeaponDialog() {
        setState {
            this.showChooseWeaponModal = false
        }
    }
}


fun RBuilder.home() = child(HomeView::class) {}

fun RBuilder.mCanvas() = child(MyCanvas::class) {}



