package mypoli.android.challenge

import mypoli.android.challenge.QuestPickerViewState.StateType.DATA_LOADED
import mypoli.android.challenge.QuestPickerViewState.StateType.LOADING
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.quest.Quest
import mypoli.android.repeatingquest.entity.RepeatingQuest

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 3/7/18.
 */

sealed class QuestPickerAction : Action {
    data class Load(val challengeId: String) : QuestPickerAction()
    data class Loaded(val quests: List<Quest>, val repeatingQuests: List<RepeatingQuest>) : QuestPickerAction()

}

object QuestPickerReducer : BaseViewStateReducer<QuestPickerViewState>() {

    override val stateKey = key<QuestPickerViewState>()


    override fun reduce(
        state: AppState,
        subState: QuestPickerViewState,
        action: Action
    ) =
        when (action) {
            is QuestPickerAction.Loaded -> {
                subState.copy(
                    type = DATA_LOADED,
                    quests = action.quests,
                    repeatingQuests = action.repeatingQuests
                )
            }
            else -> subState
        }

    override fun defaultState() =
        QuestPickerViewState(
            type = LOADING,
            challengeId = "",
            quests = listOf(),
            repeatingQuests = listOf()
        )


}

data class QuestPickerViewState(
    val type: QuestPickerViewState.StateType,
    val challengeId: String,
    val quests : List<Quest>,
    val repeatingQuests: List<RepeatingQuest>
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_LOADED
    }
}