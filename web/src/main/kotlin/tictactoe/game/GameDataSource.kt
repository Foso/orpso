package tictactoe.game

import com.badoo.reaktive.observable.Observable
import de.jensklingenberg.sheasy.model.*

interface GameDataSource {
    fun getPlayer():Player?
    fun prepareGame()
    fun join()
    fun observeGameState(): Observable<GameState>
    fun requestReset()
    fun observePlayer(): Observable<Int>
    fun observeMap(): Observable<List<Warrior>>
    fun onMoveChar(fromCoordinate: Coordinate, toCoordinate: Coordinate)
    fun onSelectedDrawWeapon(weapon: Weapon)
    fun observeNextTurn(): Observable<Player>
    fun addFlag(coordinate: Coordinate)
    fun addTrap(coordinate: Coordinate)
    fun startGame()
}

