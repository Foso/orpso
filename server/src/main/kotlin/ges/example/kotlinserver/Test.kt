package ges.example.kotlinserver

import com.squareup.moshi.Moshi
import de.jensklingenberg.sheasy.model.ClientCommandParser
import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.ServerCommand


fun main() {

    val gameArray = Array<Array<Int>>(3) { Array(3) { -1 } }

    gameArray[0][0] = 1
    gameArray[1][0] = 1
    gameArray[2][0] = 1


    fun checkRow(gameArray2 : Array<Array<Int>>): Boolean {
       return (0..2).any {id->
            if(gameArray2[id][0] != -1 &&
                gameArray2[id][0] == gameArray2[0][1]
                && gameArray2[id][1] == gameArray2[id][2]){
                return true
            }else{
                false
            }
        }
    }

    fun checkCol(gameArray2 : Array<Array<Int>>): Boolean {
        return (0..2).any {id->
            if(gameArray2[0][id] != -1 &&
                gameArray2[0][id] == gameArray2[1][id]
                && gameArray2[1][id] == gameArray2[2][id]){
                return true
            }else{
                false
            }
        }
    }
    fun checkDiag(gameArray2 : Array<Array<Int>>): Boolean{
        if(gameArray2[0][0] != -1 &&
            gameArray[0][0] == gameArray[1][1] &&
            gameArray[1][1] == gameArray[2][2]){
            return true
        }

        if(gameArray2[0][2] != -1 &&
            gameArray[0][2] == gameArray[1][1] &&
            gameArray[1][1] == gameArray[2][0]){
            return true
        }
        return false
    }

    fun checkWinner(gameArray2 : Array<Array<Int>>): Boolean {

        return checkRow(gameArray2) || checkCol(gameArray2)
    }

    val json = "{\"id\":8,\"state\":{\"type\":\"de.jensklingenberg.sheasy.model.GameState.Lobby\"}}"


    val stat = ClientCommandParser.getGameStateChangedCommand(json)
    println("Game is Won:"+checkWinner(gameArray))
}