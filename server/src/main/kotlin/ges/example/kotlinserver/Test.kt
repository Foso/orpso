package ges.example.kotlinserver

import com.squareup.moshi.Moshi
import de.jensklingenberg.sheasy.model.ClientCommandParser
import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.ServerCommand

open class Weapon
class Schere : Weapon()
class Papier: Weapon()
class Stein: Weapon()

/**
 * Schere schlägt Papier
 * Papier schlägt Stein
 * Stein schlägt Schere
 */


fun check(weapon1: Weapon,weapon2: Weapon){

}

fun main() {

    val gameArray = Array<Array<Int>>(3) { Array(3) { -1 } }

    gameArray[0][0] = 1
    gameArray[1][0] = 1
    gameArray[2][0] = 1




}