package ges.example.kotlinserver

import de.jensklingenberg.sheasy.model.*


/**
 * Schere schlägt Papier
 * Papier schlägt Stein
 * Stein schlägt Schere
 */


fun moveChar(from: Coord, toCoord: Coord, elementList: MutableList<Warrior>) {
    val fromChar = elementList.find { it.coord == from }

    val toChar = elementList.find { it.coord == toCoord }

    if (fromChar == null) {
        return
    } else {
        if (toChar == null) {
            elementList.remove(fromChar)
            elementList.add(fromChar.copy(coord = toCoord))
        }
    }


}

val ROWS = 6
val COLS = 7
fun main() {

    val check = checkWinner(Weapon.Rock(),Weapon.Schere())

    val elementList = mutableListOf<Warrior>()

    elementList.add(Warrior(Player(0, "X", ""), Weapon.Schere(), Coord(0, 0)))

    printMap(elementList)
    moveChar(Coord(0, 0), Coord(0, 1), elementList)
    printMap(elementList)
    moveChar(Coord(0, 1), Coord(0, 2), elementList)
    printMap(elementList)

}

private fun printMap(elementList: MutableList<Warrior>) {
    val gameArray = Array(ROWS) { Array(COLS) { "" } }

    elementList.forEach {
        val coord = it.coord
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
