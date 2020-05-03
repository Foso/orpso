import challenge.ui.bottomBar
import tictactoe.ui.home.home
import challenge.ui.toolbar
import kotlinext.js.requireAll
import react.dom.footer
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window


class Application {

    companion object{

    }

    init {
        window.onload = {
            requireAll(kotlinext.js.require.context("kotlin", true, js("/\\.css$/")))
            render(document.getElementById("root")) {
                toolbar()
                home()
                footer {
                    bottomBar()
                }
            }
        }
    }

}
