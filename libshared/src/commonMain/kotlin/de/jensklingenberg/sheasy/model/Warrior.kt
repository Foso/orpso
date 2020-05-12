package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable

@Serializable
data class Warrior(val owner: Player, val weapon: Weapon, val coordinate: Coordinate, val weaponRevealed: Boolean = false)