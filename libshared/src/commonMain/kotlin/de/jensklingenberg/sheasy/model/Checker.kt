package de.jensklingenberg.sheasy.model

fun checkWinner(attackWeapon: Weapon, defenseWeapon: Weapon): MatchState {

    return when (defenseWeapon) {
        is Weapon.Scissors -> {
            when (attackWeapon) {
                is Weapon.Rock -> {
                    MatchState.WIN
                }
                is Weapon.Scissors -> {
                    MatchState.DRAW
                }
                else -> {
                    MatchState.LOOSE
                }
            }
        }
        is Weapon.Paper -> {
            when (attackWeapon) {
                is Weapon.Scissors -> {
                    MatchState.WIN
                }
                is Weapon.Paper -> {
                    MatchState.DRAW
                }
                else -> {
                    MatchState.LOOSE
                }
            }

        }
        is Weapon.Rock -> {
            when (attackWeapon) {
                is Weapon.Paper -> {
                    MatchState.WIN
                }
                is Weapon.Rock -> {
                    MatchState.DRAW
                }
                else -> {
                    MatchState.LOOSE
                }
            }
        }
        is Weapon.Trap -> {
            MatchState.LOOSE
        }
        is Weapon.Flag -> {
            MatchState.WIN
        }
        is Weapon.Hidden -> {
            MatchState.WIN
        }
    }
}