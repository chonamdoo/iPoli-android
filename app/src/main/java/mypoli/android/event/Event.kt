package mypoli.android.event

import mypoli.android.common.datetime.Duration
import mypoli.android.common.datetime.Minute
import mypoli.android.common.datetime.Time
import mypoli.android.quest.Entity
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/09/2018.
 */
data class Event(
    override val id: String = "",
    val name: String,
    val startTime: Time,
    val duration: Duration<Minute>,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val color: Int,
    val isRepeating: Boolean,
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now()
) : Entity {
    val endTime: Time
        get() = startTime.plus(duration.intValue)
}