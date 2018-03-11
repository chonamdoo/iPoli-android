package mypoli.android.challenge.entity

import mypoli.android.common.datetime.Time
import mypoli.android.quest.Color
import mypoli.android.quest.Entity
import mypoli.android.quest.Icon
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/05/2018.
 */
data class Challenge(
    override val id: String = "",
    val name: String,
    val color: Color,
    val icon: Icon? = null,
    val difficulty: Difficulty,
    val end: LocalDate,
    val motivations: List<String>,
    val experience: Int? = null,
    val coins: Int? = null,
    val completedAtDate: LocalDate? = null,
    val completedAtTime: Time? = null,
    val nextDate: LocalDate? = null,
    val nextStartTime: Time? = null,
    val nextDuration: Int? = null,
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now()
) : Entity {

    enum class Difficulty {
        EASY, NORMAL, HARD, HELL
    }

    val nextEndTime: Time?
        get() = nextStartTime?.plus(nextDuration!!)

}