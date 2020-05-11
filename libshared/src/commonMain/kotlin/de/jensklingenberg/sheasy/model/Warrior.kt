package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable

@Serializable
data class Warrior(val owner: Player, val weapon: Weapon, val coord: Coord, val weaponRevealed: Boolean = false)