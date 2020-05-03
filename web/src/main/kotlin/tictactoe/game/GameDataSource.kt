package tictactoe.game

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.observable.Observable
import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.GameState

interface GameDataSource {
    fun prepareGame()
    fun join()
    fun makeAMove(coord: Coord): Completable
    fun observeGameChanges(): Observable<Array<Array<String>>>
    fun observeGameState(): Observable<GameState>
    fun requestReset()
    fun observePlayer(): Observable<Int>
}

