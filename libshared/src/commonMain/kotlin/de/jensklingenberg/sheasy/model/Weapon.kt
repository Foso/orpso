package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Weapon() {
    @Serializable
    class Scissors : Weapon()

    @Serializable
    class Papier : Weapon()

    @Serializable
    class Rock : Weapon()

    @Serializable
    class Trap : Weapon()

    @Serializable
    class Flag : Weapon()

    @Serializable
    class Hidden : Weapon()
}

fun getWeaponImagePath(id: Int, weapon: Weapon): String {

    fun schere(id: Int): String = when (id) {
        0 -> "images/scissors_blue.svg"
        1 -> "images/scissors_red.svg"
        else -> ""
    }

    fun rock(id: Int): String = when (id) {
        0 -> "images/rock_blue.svg"
        1 -> "images/rock_red.svg"
        else -> ""

    }

    fun hidden(id: Int): String {
        return when (id) {
            0 -> "images/player_blue.svg"
            1 -> "images/player_red.svg"
            else -> ""
        }
    }

    fun paper(id: Int): String {
        return when (id) {
            0 -> "images/paper_blue.svg"
            1 -> "images/paper_red.svg"
            else -> ""
        }
    }

    fun flag(id: Int): String {
        return when (id) {
            0 -> "images/flag_blue.svg"
            1 -> "images/flag_red.svg"
            else -> ""
        }
    }

    fun trap(id: Int): String {
        return when (id) {
            0 -> "images/trap_blue.svg"
            1 -> "images/trap_red.svg"
            else -> ""
        }
    }

    return when (weapon) {
        is Weapon.Scissors -> schere(id)
        is Weapon.Papier -> paper(id)
        is Weapon.Rock -> rock(id)
        is Weapon.Trap -> trap(id)
        is Weapon.Flag -> flag(id)
        is Weapon.Hidden -> hidden(id)
    }
}