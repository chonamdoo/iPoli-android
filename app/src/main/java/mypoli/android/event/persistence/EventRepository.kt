package mypoli.android.event.persistence

import android.content.ContentUris
import android.database.Cursor
import android.graphics.Color
import android.provider.CalendarContract
import android.provider.CalendarContract.Instances
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import mypoli.android.common.datetime.Time
import mypoli.android.common.datetime.instant
import mypoli.android.common.datetime.minutes
import mypoli.android.common.persistence.CollectionRepository
import mypoli.android.event.Event
import mypoli.android.myPoliApp
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.util.*


/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/09/2018.
 */

interface EventRepository : CollectionRepository<Event> {
    fun findScheduledBetween(
        calendarIds: Set<Int>,
        start: LocalDate,
        end: LocalDate
    ): List<Event>
}

class AndroidCalendarEventRepository : EventRepository {

    companion object {

        val GOOGLE_CALENDAR_COLOR_MAPPING = mapOf(
            -509406 to -2818048,
            -509406 to -2818048,
            -370884 to -765666,
            -35529 to -1086464,
            -21178 to -1010944,
            -339611 to -606426,
            -267901 to -1784767,
            -4989844 to -4142541,
            -8662712 to -8604862,
            -15292571 to -16023485,
            -12396910 to -16738680,
            -7151168 to -13388167,
            -6299161 to -16540699,
            -6306073 to -12417548,
            -11958553 to -12627531,
            -6644481 to -8812853,
            -4613377 to -5005861,
            -5997854 to -6395473,
            -3312410 to -7461718,
            -3365204 to -5434281,
            -618062 to -2614432,
            -3118236 to -1672077,
            -5475746 to -8825528,
            -4013374 to -10395295,
            -3490369 to -5792882,
            -2350809 to -2818048,
            -18312 to -765666,
            -272549 to -606426,
            -11421879 to -16023485,
            -8722497 to -13388167,
            -12134693 to -16540699,
            -11238163 to -12627531,
            -5980676 to -8812853,
            -2380289 to -7461718,
            -30596 to -1672077,
            -1973791 to -10395295,
            -2883584 to -2818048,
            -831459 to -765666,
            -1152256 to -1086464,
            -1076736 to -1010944,
            -672219 to -606426,
            -1914036 to -1784767,
            -4208334 to -4142541,
            -8670655 to -8604862,
            -16089278 to -16023485,
            -16738937 to -16738680,
            -16606492 to -16540699,
            -12483341 to -12417548,
            -12624727 to -12627531,
            -8878646 to -8812853,
            -5071654 to -5005861,
            -7527511 to -7461718,
            -5500074 to -5434281,
            -2680225 to -2614432,
            -1737870 to -1672077,
            -8891321 to -8825528,
            -10263709 to -10395295
        )

        private val INSTANCE_PROJECTION = arrayOf(
            Instances.EVENT_ID,
            Instances.BEGIN,
            Instances.END,
            Instances.START_MINUTE,
            Instances.END_MINUTE,
            Instances.TITLE,
            Instances.EVENT_LOCATION,
            Instances.DURATION,
            Instances.CALENDAR_TIME_ZONE,
            Instances.DISPLAY_COLOR
        )

        fun getDisplayColor(color: Int): Int {

            // taken from https://stackoverflow.com/questions/19775686/android-calendar-color-from-calendar-color-differ-from-real-calendar-color

            if (GOOGLE_CALENDAR_COLOR_MAPPING.containsKey(color)) {
                return GOOGLE_CALENDAR_COLOR_MAPPING[color]!!
            }
            if (GOOGLE_CALENDAR_COLOR_MAPPING.containsValue(color)) {
                return color
            }
            val colorData = FloatArray(3)
            Color.colorToHSV(color, colorData)
            if (colorData[2] > 0.79f) {
                colorData[1] = Math.min(colorData[1] * 1.3f, 1.0f)
                colorData[2] = colorData[2] * 0.8f
            }
            return Color.HSVToColor(Color.alpha(color), colorData)
        }

        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_BEGIN_INDEX = 1
        private const val PROJECTION_END_INDEX = 2
        private const val PROJECTION_START_MIN_INDEX = 3
        private const val PROJECTION_END_MIN_INDEX = 4
        private const val PROJECTION_TITLE_INDEX = 5
        private const val PROJECTION_LOCATION_INDEX = 6
        private const val PROJECTION_DURATION_INDEX = 7
        private const val PROJECTION_TIME_ZONE_INDEX = 8
        private const val PROJECTION_DISPLAY_COLOR = 9
    }

    override fun findScheduledBetween(
        calendarIds: Set<Int>,
        start: LocalDate,
        end: LocalDate
    ): List<Event> {

        val beginTime = Calendar.getInstance()
        beginTime.set(start.year, start.monthValue - 1, start.dayOfMonth, 0, 0, 0)

        val endTime = Calendar.getInstance()
        endTime.set(end.year, end.monthValue - 1, end.dayOfMonth, 23, 59, 59)

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
                events.add(createEvent(it))
            }
        }

        return events
    }

    private fun createEvent(cursor: Cursor): Event {
        val eventStartTime = Time.of(cursor.getInt(PROJECTION_START_MIN_INDEX))
        val eventEndTime = Time.of(cursor.getInt(PROJECTION_END_MIN_INDEX))
        val tz = ZoneId.of(cursor.getString(PROJECTION_TIME_ZONE_INDEX))
        return Event(
            id = cursor.getString(PROJECTION_ID_INDEX),
            name = cursor.getString(PROJECTION_TITLE_INDEX),
            startTime = eventStartTime,
            duration = (eventEndTime - eventStartTime).toMinuteOfDay().minutes,
            startDate = cursor.getLong(PROJECTION_BEGIN_INDEX).instant.atZone(tz).toLocalDate(),
            endDate = cursor.getLong(PROJECTION_END_INDEX).instant.atZone(tz).toLocalDate(),
            color = getDisplayColor(cursor.getInt(PROJECTION_DISPLAY_COLOR))
        )
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