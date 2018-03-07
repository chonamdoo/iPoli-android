package mypoli.android.challenge

import mypoli.android.challenge.QuestPickerViewState.StateType.DATA_CHANGED
import mypoli.android.challenge.QuestPickerViewState.StateType.LOADING
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
import timber.log.Timber

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 3/7/18.
 */

sealed class QuestPickerAction : Action {
    data class Load(val challengeId: String) : QuestPickerAction()
    data class Loaded(val quests: List<Quest>, val repeatingQuests: List<RepeatingQuest>) : QuestPickerAction()
    data class Filter(val query: String) : QuestPickerAction()

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
                            id = "",
                            name = "Run",
                            color = Color.GREEN,
                            icon = Icon.PIZZA,
                            category = Category("WELLNESS", Color.GREEN),
                            duration = 30,
                            reminder = null,
                            scheduledDate = LocalDate.now()
                        ),
                        Quest(
                            id = "",
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
                            id = "",
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
                if (query.length < MIN_FILTER_QUERY_LEN) {
                    subState
                } else {
                    subState.copy(
                        type = DATA_CHANGED,
                        filteredQuests = filterQuests(
                            query,
                            subState.allQuests
                        )
                    )
                }
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
    ): List<PickerQuest> {
        return sortQuests(quests.filter {
            Timber.d("AAA ${it.name}")
            it.name.toLowerCase().contains(query.toLowerCase()) })
    }

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
            filteredQuests = listOf()
        )


}

sealed class PickerQuest(open val name: String, open val date: LocalDate?) {
    data class OneTime(val quest: Quest) : PickerQuest(quest.name, quest.scheduledDate)
    data class Repeating(val repeatingQuest: RepeatingQuest) :
        PickerQuest(repeatingQuest.name, repeatingQuest.repeatingPattern.start)
}

data class QuestPickerViewState(
    val type: QuestPickerViewState.StateType,
    val challengeId: String,
    val allQuests: List<PickerQuest>,
    val filteredQuests: List<PickerQuest>
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_CHANGED
    }
}

