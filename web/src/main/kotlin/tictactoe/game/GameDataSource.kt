package tictactoe.game

import com.badoo.reaktive.observable.Observable
import de.jensklingenberg.sheasy.model.Coordinate
import de.jensklingenberg.sheasy.model.GameState
import de.jensklingenberg.sheasy.model.Warrior
import de.jensklingenberg.sheasy.model.Weapon

interface GameDataSource {
    fun prepareGame()
    fun join()
    fun observeGameState(): Observable<GameState>
    fun requestReset()
    fun observePlayer(): Observable<Int>
    fun observeMap(): Observable<List<Warrior>>
    fun onMoveChar(fromCoordinate: Coordinate, toCoordinate: Coordinate)
    fun onSelectedDrawWeapon(weapon: Weapon)
}

