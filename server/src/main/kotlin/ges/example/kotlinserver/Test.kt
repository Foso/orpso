package ges.example.kotlinserver

import de.jensklingenberg.sheasy.model.*


/**
 * Schere schlägt Papier
 * Papier schlägt Stein
 * Stein schlägt Schere
 */


fun moveChar(from: Coordinate, toCoordinate: Coordinate, elementList: MutableList<Warrior>) {
    val fromChar = elementList.find { it.coordinate == from }

    val toChar = elementList.find { it.coordinate == toCoordinate }

    if (fromChar == null) {
        return
    } else {
        if (toChar == null) {
            elementList.remove(fromChar)
            elementList.add(fromChar.copy(coordinate = toCoordinate))
        }
    }


}

val ROWS = 6
val COLS = 7
fun main() {

    val check = checkWinner(Weapon.Rock, Weapon.Scissors)

    val elementList = mutableListOf<Warrior>()

    elementList.add(Warrior(Player(0, "X", ""), Weapon.Scissors, Coordinate(0, 0)))

    printMap(elementList)
    moveChar(Coordinate(0, 0), Coordinate(0, 1), elementList)
    printMap(elementList)
    moveChar(Coordinate(0, 1), Coordinate(0, 2), elementList)
    printMap(elementList)

}

private fun printMap(elementList: MutableList<Warrior>) {
    val gameArray = Array(ROWS) { Array(COLS) { "" } }

    elementList.forEach {
        val coord = it.coordinate
        gameArray[coord.y][coord.x] = "W"
    }

    gameArray.forEachIndexed { index, strings ->


        println(strings.joinToString(separator = "|") {
            val value = if (it == "") {
                "-"
            } else {
                it
            }
            value
        })


    }
    println("__________________________________________________________")


}
