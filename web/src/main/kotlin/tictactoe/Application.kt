import challenge.ui.toolbar
import kotlinext.js.requireAll
import react.dom.footer
import react.dom.render
import tictactoe.game.GameApiHandler
import tictactoe.game.GameDataSource
import tictactoe.game.GameRepository
import tictactoe.ui.home.home
import kotlin.browser.document
import kotlin.browser.window


class Application {


    companion object{
        private val gameApiHandler = GameApiHandler()
        val gameDataSource: GameDataSource = GameRepository(gameApiHandler)

    }
    init {
        window.onload = {
            requireAll(kotlinext.js.require.context("kotlin", true, js("/\\.css$/")))
            render(document.getElementById("root")) {
                toolbar()
                home()
                footer {
                    //bottomBar()
                }
            }
        }
    }

}
