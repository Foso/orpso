package ges.example.kotlinserver

import ges.example.kotlinserver.server.ChatApplication
import java.io.File


data class ChatSession(val id: String)


class Actor(val name: String)

class Arrow(val type: String = "->")

class Config(val autoNumber: Boolean = false) {


    fun toPlantUml(): String {
        val autoNumberText = if (autoNumber) {
            "autonumber"
        } else {
            ""
        }

        return """
              $autoNumberText
          """.trimIndent()
    }
}

class PlantUml(var config: Config? = null, val nodes: List<PlantUmlNode>) {

    fun write() {
        File("/home/jens/Code/2020/JK/orpso/server/docs/Test.puml").writeText(printUml())

    }

    fun printUml(): String {

        val configText: String = config?.toPlantUml() ?: ""

        val cmdText = nodes.joinToString(separator = "\n") {
            it.toPlantUml()
        }

        return """
        @startuml
        $configText
        $cmdText
        @enduml
    """
    }
}

fun main() {
     ChatApplication()
    val jens = Actor("Jens")
    val bob = Actor("Bob")

    val nodeList = arrayListOf<PlantUmlNode>(
        send("Bob", "Alice", "My name",Arrow("-->")),
        send(bob, "Alice", "My name"),
        divider("Repetition"),
        divider(""),

        divider("Repetition"),
        send(jens, "Bob", "Another authentication Request")
    )

    val uml = PlantUml(config = Config(), nodes = nodeList)
    uml.write()


}

fun send(from: Actor, to: String, note: String): PlantUmlNode {
    return send("\"" + from.name + "\"", to, note)
}


private fun divider(text: String = ""): PlantUmlNode =
    object : PlantUmlNode {
        override fun toPlantUml(): String {
            return "== $text =="
        }
    }


private fun send(from: String, to: String, note: String, arrow: Arrow = Arrow()): PlantUmlNode =
    object : PlantUmlNode {
        override fun toPlantUml(): String {
            return "$from ${arrow.type} $to: $note"
        }
    }


interface PlantUmlNode {
    fun toPlantUml(): String
}
