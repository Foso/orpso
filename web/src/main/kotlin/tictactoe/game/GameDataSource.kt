package tictactoe.game

import com.badoo.reaktive.observable.Observable
import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.GameState
import de.jensklingenberg.sheasy.model.Warrior

interface GameDataSource {
    fun prepareGame()
    fun join()
    fun makeAMove(coord: Coord)
    fun observeGameChanges(): Observable<Array<Array<String>>>
    fun observeGameState(): Observable<GameState>
    fun requestReset()
    fun observePlayer(): Observable<Int>
    fun observeMap(): Observable<List<Warrior>>
    fun onMoveChar(fromCoord: Coord,toCoord: Coord)

}

