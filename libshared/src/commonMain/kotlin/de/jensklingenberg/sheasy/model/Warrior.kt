package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable

interface Element

class Background : Element

@Serializable
data class Warrior(val owner: Player, val weapon: Weapon, val coordinate: Coordinate, val weaponRevealed: Boolean = false) :Element