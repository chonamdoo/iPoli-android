package mypoli.android.challenge.show

import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.DataLoadedAction
import mypoli.android.common.datetime.datesBetween
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.quest.*
import mypoli.android.repeatingquest.entity.RepeatingPattern
import org.threeten.bp.LocalDate
import java.util.*

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/05/2018.
 */

sealed class ChallengeAction : Action {
    data class Load(val challengeId: String) : ChallengeAction()
    data class Remove(val challengeId: String) : ChallengeAction()
    data class RemoveQuestFromChallenge(val questIndex: Int) : ChallengeAction()
}

object ChallengeReducer : BaseViewStateReducer<ChallengeViewState>() {
    override fun reduce(
        state: AppState,
        subState: ChallengeViewState,
        action: Action
    ): ChallengeViewState {
        return when (action) {
            is ChallengeAction.Load -> {
                val dataState = state.dataState
                val c =
                    dataState.challenges.firstOrNull { it.id == action.challengeId }

                c?.let {
                    createChangedState(it)
                } ?: ChallengeViewState.Loading(action.challengeId)
            }

            is DataLoadedAction.ChallengesChanged -> {
                val c = action.challenges.firstOrNull { it.id == subState.id }

                c?.let {
                    createChangedState(it)
                } ?: ChallengeViewState.Removed
            }
            else -> subState
        }
    }

    private fun createChangedState(challenge: Challenge) =
        ChallengeViewState.Changed(
            id = challenge.id,
            name = challenge.name,
            color = challenge.color,
            completedCount = 18,
            totalCount = 35,
            progressPercent = ((18.0 / 35) * 100).toInt(),
            xAxisLabelCount = 5,
            chartData = LocalDate.now().minusDays(30).datesBetween(LocalDate.now()).map {
                it to 5
            }.toMap().toSortedMap(),
            quests = listOf(
                RepeatingQuest(
                    id = "3",
                    name = "Runinja",
                    color = Color.BLUE_GREY,
                    icon = Icon.RESTAURANT,
                    category = Category("WELLNESS", Color.GREEN),
                    duration = 20,
                    repeatingPattern = RepeatingPattern.Daily()
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
                ),
                Quest(
                    id = "1",
                    name = "Run",
                    color = Color.GREEN,
                    icon = Icon.PIZZA,
                    category = Category("WELLNESS", Color.GREEN),
                    duration = 30,
                    reminder = null,
                    scheduledDate = LocalDate.now(),
                    completedAtDate = LocalDate.now()
                )
            )
        )

    override fun defaultState() = ChallengeViewState.Loading("")

    override val stateKey = key<ChallengeViewState>()
}

sealed class ChallengeViewState(open val id: String = "") : ViewState {

    data class Loading(override val id: String) : ChallengeViewState(id)

    data class Changed(
        override val id: String,
        val name: String,
        val color: Color,
        val completedCount: Int,
        val totalCount: Int,
        val progressPercent: Int,
        val xAxisLabelCount: Int,
        val chartData: SortedMap<LocalDate, Int>,
        val quests: List<BaseQuest>
    ) : ChallengeViewState(id)

    object Removed : ChallengeViewState()
}