package mypoli.android.event.persistence

import android.database.Cursor
import android.provider.CalendarContract
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import mypoli.android.common.persistence.CollectionRepository
import mypoli.android.common.view.ColorUtil
import mypoli.android.event.Calendar
import mypoli.android.myPoliApp

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/11/2018.
 */
interface CalendarRepository : CollectionRepository<Calendar> {

    fun findAll(): List<Calendar>
}

class AndroidCalendarRepository : CalendarRepository {

    companion object {

        private val CALENDAR_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE
        )

        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_NAME_INDEX = 1
        private const val PROJECTION_COLOR_INDEX = 2
        private const val PROJECTION_VISIBLE_INDEX = 3
    }

    override fun findAll(): List<Calendar> {

        val calendars = mutableListOf<Calendar>()

        myPoliApp.instance.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI.buildUpon().build(),
            CALENDAR_PROJECTION,
            null,
            null,
            null
        ).use {
            while (it.moveToNext()) {
                calendars.add(createCalendar(it))
            }
        }

        return calendars
    }

    private fun createCalendar(cursor: Cursor) =
        Calendar(
            id = cursor.getString(PROJECTION_ID_INDEX),
            name = cursor.getString(PROJECTION_NAME_INDEX),
            color = ColorUtil.fromGoogleCalendarDisplayColor(cursor.getInt(PROJECTION_COLOR_INDEX)),
            isVisible = cursor.getInt(PROJECTION_VISIBLE_INDEX) == 1
        )

    override fun save(entity: Calendar): Calendar {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: String): Calendar? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listenById(id: String): ReceiveChannel<Calendar?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listenForAll(): ReceiveChannel<List<Calendar>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(entity: Calendar) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun undoRemove(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}