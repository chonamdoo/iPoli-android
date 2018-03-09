package mypoli.android.event.persistence

import android.content.ContentUris
import android.provider.CalendarContract
import android.provider.CalendarContract.Instances
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.launch
import mypoli.android.common.datetime.Time
import mypoli.android.common.datetime.minutes
import mypoli.android.common.persistence.CollectionRepository
import mypoli.android.event.Event
import mypoli.android.myPoliApp
import org.threeten.bp.LocalDate
import java.util.*

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/09/2018.
 */

interface EventRepository : CollectionRepository<Event> {
    fun listenForScheduledBetween(
        calendarIds: Set<Int>,
        start: LocalDate,
        end: LocalDate
    ): ReceiveChannel<List<Event>>
}

class AndroidCalendarEventRepository : EventRepository {

    companion object {

        private val INSTANCE_PROJECTION = arrayOf(
            Instances.EVENT_ID,
            Instances.BEGIN,
            Instances.END,
            Instances.START_MINUTE,
            Instances.END_MINUTE,
            Instances.TITLE,
            Instances.EVENT_LOCATION,
            Instances.DURATION,
            Instances.CALENDAR_TIME_ZONE
        )

        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_BEGIN_INDEX = 1
        private const val PROJECTION_END_INDEX = 2
        private const val PROJECTION_START_MIN_INDEX = 3
        private const val PROJECTION_END_MIN_INDEX = 4
        private const val PROJECTION_TITLE_INDEX = 5
        private const val PROJECTION_LOCATION_INDEX = 6
        private const val PROJECTION_DURATION_INDEX = 7
        private const val PROJECTION_TIME_ZONE_INDEX = 8
    }


    override fun listenForScheduledBetween(
        calendarIds: Set<Int>,
        start: LocalDate,
        end: LocalDate
    ): ReceiveChannel<List<Event>> {

        val beginTime = Calendar.getInstance()
        beginTime.set(start.year, start.monthValue, start.dayOfMonth, 0, 0, 0)

        val prevDayEnd = end.minusDays(1)

        val endTime = Calendar.getInstance()
        endTime.set(prevDayEnd.year, prevDayEnd.monthValue, prevDayEnd.dayOfMonth, 23, 59, 59)


        val builder = Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, beginTime.timeInMillis)
        ContentUris.appendId(builder, endTime.timeInMillis)

        val selection = (CalendarContract.Events.CALENDAR_ID + " = ?")
        val selectionArgs = calendarIds.map { it.toString() }.toTypedArray()

        val events = mutableListOf<Event>()

        myPoliApp.instance.contentResolver.query(
            builder.build(),
            INSTANCE_PROJECTION,
            selection,
            selectionArgs,
            null
        ).use {
            while (it.moveToNext()) {
                val eventStartTime = Time.of(it.getInt(PROJECTION_START_MIN_INDEX))
                val eventEndTime = Time.of(it.getInt(PROJECTION_END_MIN_INDEX))
                events.add(
                    Event(
                        id = it.getString(PROJECTION_ID_INDEX),
                        name = it.getString(PROJECTION_TITLE_INDEX),
                        start = eventStartTime,
                        duration = (eventEndTime - eventStartTime).toMinuteOfDay().minutes
                    )
                )
            }
        }

        val channel = Channel<List<Event>>()
        launch(CommonPool) {
            channel.send(events)
        }

        return channel
    }

    override fun save(entity: Event): Event {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: String): Event? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listenById(id: String): ReceiveChannel<Event?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listenForAll(): ReceiveChannel<List<Event>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(entity: Event) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun undoRemove(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}