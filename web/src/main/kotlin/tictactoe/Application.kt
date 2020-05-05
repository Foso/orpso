import challenge.ui.toolbar
import kotlinext.js.requireAll
import react.dom.footer
import react.dom.render
import tictactoe.ui.home.home
import kotlin.browser.document
import kotlin.browser.window


class Application {

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
