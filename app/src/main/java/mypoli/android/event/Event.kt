package mypoli.android.event

import mypoli.android.common.datetime.Duration
import mypoli.android.common.datetime.Minute
import mypoli.android.common.datetime.Time
import mypoli.android.quest.Entity
import org.threeten.bp.Instant

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/09/2018.
 */
data class Event(
    override val id: String = "",
    val name: String,
    val start: Time,
    val duration: Duration<Minute>,
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now()
) : Entity