package mypoli.android.challenge

import mypoli.android.challenge.QuestPickerViewState.StateType.DATA_CHANGED
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
    data class Filter(val text : String) : QuestPickerAction()

}

object QuestPickerReducer : BaseViewStateReducer<QuestPickerViewState>() {

    override val stateKey = key<QuestPickerViewState>()

    val MIN_FILTER_QUERY_LEN = 3

    override fun reduce(
        state: AppState,
        subState: QuestPickerViewState,
        action: Action
    ) =
        when (action) {
            is QuestPickerAction.Loaded -> {
                subState.copy(
                    type = DATA_CHANGED,
                    quests = action.quests,
                    repeatingQuests = action.repeatingQuests,
                    resultQuests = filterQuests(subState.quests, subState.repeatingQuests)
                )
            }

            is QuestPickerAction.Filter -> {
                val text = action.text.trim()
                if(text.isEmpty()) {
                    subState.copy(
                        type = DATA_CHANGED,
                        resultQuests = filterQuests(subState.quests, subState.repeatingQuests)
                    )
                } else if(text.length < MIN_FILTER_QUERY_LEN) {
                    subState
                } else {
                    subState.copy(
                        type = DATA_CHANGED,
                        resultQuests = filterQuests(subState.quests, subState.repeatingQuests)
                    )
                }
            }
            else -> subState
        }

    private fun filterQuests(
        quests: List<Quest>,
        repeatingQuests: List<RepeatingQuest>
    ): List<PickerQuest> {
        return listOf()
    }

    override fun defaultState() =
        QuestPickerViewState(
            type = LOADING,
            challengeId = "",
            quests = listOf(),
            repeatingQuests = listOf(),
            resultQuests = listOf()
        )


}

sealed class PickerQuest {
    data class OneTime(val quest : Quest) : PickerQuest()
    data class Repeating(val repeatingQuest: RepeatingQuest) : PickerQuest()
}

data class QuestPickerViewState(
    val type: QuestPickerViewState.StateType,
    val challengeId: String,
    val quests : List<Quest>,
    val repeatingQuests: List<RepeatingQuest>,
    val resultQuests: List<PickerQuest>
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_CHANGED
    }
}

