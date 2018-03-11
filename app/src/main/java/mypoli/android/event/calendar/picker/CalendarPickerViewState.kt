package mypoli.android.event.calendar.picker

import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.DataLoadedAction
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.event.Calendar
import timber.log.Timber

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/11/2018.
 */

sealed class CalendarPickerAction : Action {
    object Load : CalendarPickerAction()
}

object CalendarPickerReducer : BaseViewStateReducer<CalendarPickerViewState>() {

    override val stateKey = key<CalendarPickerViewState>()

    override fun reduce(
        state: AppState,
        subState: CalendarPickerViewState,
        action: Action
    ) =
        when (action) {
            is DataLoadedAction.CalendarsChanged -> {
                Timber.d("AAA ${action.calendars}")
                CalendarPickerViewState.CalendarsLoaded(action.calendars.filter { it.isVisible })
            }
            else ->
                subState
        }

    override fun defaultState() = CalendarPickerViewState.Loading
}

sealed class CalendarPickerViewState : ViewState {

    object Loading : CalendarPickerViewState()
    data class CalendarsLoaded(val calendars: List<Calendar>) : CalendarPickerViewState()
}

