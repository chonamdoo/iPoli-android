package mypoli.android.challenge

import mypoli.android.challenge.QuestPickerViewState.StateType.LOADING
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 3/7/18.
 */

sealed class QuestPickerAction : Action {

}

object QuestPickerReducer : BaseViewStateReducer<QuestPickerViewState>() {

    override val stateKey = key<QuestPickerViewState>()


    override fun reduce(
        state: AppState,
        subState: QuestPickerViewState,
        action: Action
    ) =
        when (action) {
            else -> subState
        }

    override fun defaultState(): QuestPickerViewState {
        return QuestPickerViewState(type = LOADING)
    }


}

data class QuestPickerViewState(
    val type: QuestPickerViewState.StateType
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_LOADED
    }
}