package mypoli.android.challenge

import mypoli.android.challenge.QuestPickerViewState.StateType.*
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.quest.Category
import mypoli.android.quest.Color
import mypoli.android.quest.Icon
import mypoli.android.quest.Quest
import mypoli.android.repeatingquest.entity.RepeatingPattern
import mypoli.android.repeatingquest.entity.RepeatingQuest
import org.threeten.bp.LocalDate

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/7/18.
 */

sealed class QuestPickerAction : Action {
    data class Load(val challengeId: String?) : QuestPickerAction()
    data class Loaded(val quests: List<Quest>, val repeatingQuests: List<RepeatingQuest>) : QuestPickerAction()
    data class Filter(val query: String) : QuestPickerAction()
    data class Check(val id: String, val isSelected: Boolean) : QuestPickerAction()
    object Save : QuestPickerAction()
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
                val quests = createPickerQuests(
                    listOf(
                        Quest(
                            id = "1",
                            name = "Run",
                            color = Color.GREEN,
                            icon = Icon.PIZZA,
                            category = Category("WELLNESS", Color.GREEN),
                            duration = 30,
                            reminder = null,
                            scheduledDate = LocalDate.now()
                        ),
                        Quest(
                            id = "2",
                            name = "Runing",
                            color = Color.ORANGE,
                            icon = Icon.MONEY,
                            category = Category("WELLNESS", Color.GREEN),
                            duration = 60,
                            reminder = null,
                            scheduledDate = LocalDate.now().plusDays(1)
                        )
                    ),
                    listOf(
                        RepeatingQuest(
                            id = "3",
                            name = "Runinja",
                            color = Color.BLUE_GREY,
                            icon = Icon.RESTAURANT,
                            category = Category("WELLNESS", Color.GREEN),
                            duration = 20,
                            repeatingPattern = RepeatingPattern.Daily()
                        )
                    )
                )
                subState.copy(
                    type = DATA_CHANGED,
                    allQuests = quests,
                    filteredQuests = quests
                )
            }

            is QuestPickerAction.Filter -> {
                val query = action.query.trim()
                when {
                    query.isEmpty() -> subState.copy(
                        type = DATA_CHANGED,
                        filteredQuests = subState.allQuests
                    )
                    query.length < MIN_FILTER_QUERY_LEN -> subState
                    else -> subState.copy(
                        type = DATA_CHANGED,
                        filteredQuests = filterQuests(
                            query,
                            subState.allQuests
                        )
                    )
                }
            }

            is QuestPickerAction.Check -> {
                subState.copy(
                    type = ITEM_SELECTED,
                    selectedQuests = if (action.isSelected) {
                        subState.selectedQuests + action.id
                    } else {
                        subState.selectedQuests - action.id
                    }
                )
            }
            else -> subState
        }

    private fun createPickerQuests(
        quests: List<Quest>,
        repeatingQuests: List<RepeatingQuest>
    ) =
        sortQuests(
            quests.map { PickerQuest.OneTime(it) } +
                repeatingQuests.map {
                    PickerQuest.Repeating(
                        it
                    )
                })

    private fun filterQuests(
        query: String,
        quests: List<PickerQuest>
    ) =
        sortQuests(
            quests.filter {
                it.name.toLowerCase().contains(query.toLowerCase())
            }
        )

    private fun sortQuests(result: List<PickerQuest>): List<PickerQuest> {
        return result.sortedWith(Comparator { q1, q2 ->
            val d1 = q1.date
            val d2 = q2.date
            if (d1 == null && d2 == null) {
                return@Comparator -1
            }

            if (d1 == null) {
                return@Comparator 1
            }

            if (d2 == null) {
                return@Comparator -1
            }

            if (d2.isAfter(d1)) {
                return@Comparator 1
            }

            return@Comparator if (d1.isAfter(d2)) {
                -1
            } else 0
        })
    }

    override fun defaultState() =
        QuestPickerViewState(
            type = LOADING,
            challengeId = "",
            allQuests = listOf(),
            filteredQuests = listOf(),
            selectedQuests = setOf()
        )


}

sealed class PickerQuest(open val id: String, open val name: String, open val date: LocalDate?) {
    data class OneTime(val quest: Quest) : PickerQuest(quest.id, quest.name, quest.scheduledDate)
    data class Repeating(val repeatingQuest: RepeatingQuest) :
        PickerQuest(repeatingQuest.id, repeatingQuest.name, repeatingQuest.repeatingPattern.start)
}

data class QuestPickerViewState(
    val type: QuestPickerViewState.StateType,
    val challengeId: String,
    val allQuests: List<PickerQuest>,
    val filteredQuests: List<PickerQuest>,
    val selectedQuests: Set<String>
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_CHANGED,
        ITEM_SELECTED
    }
}

