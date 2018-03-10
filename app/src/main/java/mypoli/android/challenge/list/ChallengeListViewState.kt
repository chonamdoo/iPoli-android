package mypoli.android.challenge.list

import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.DataLoadedAction
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.quest.Color
import mypoli.android.quest.Icon
import org.threeten.bp.LocalDate

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/05/2018.
 */

sealed class ChallengeListAction : Action {
    object Load : ChallengeListAction()
}

object ChallengeListReducer : BaseViewStateReducer<ChallengeListViewState>() {

    override fun reduce(
        state: AppState,
        subState: ChallengeListViewState,
        action: Action
    ) = when (action) {
        is ChallengeListAction.Load -> {
            createState(
                listOf(
                    Challenge(
                        name = "Test challenge",
                        color = Color.RED,
                        icon = Icon.HEART,
                        difficulty = Challenge.Difficulty.HELL,
                        end = LocalDate.now().plusWeeks(1)
                    ),
                    Challenge(
                        name = "Run 5k marathon",
                        color = Color.GREEN,
                        icon = Icon.RUN,
                        difficulty = Challenge.Difficulty.HELL,
                        end = LocalDate.now().plusWeeks(1)
                    )
                )
            )
//            createState(state.dataState.challenges)
        }

        is DataLoadedAction.ChallengesChanged -> {
            createState(action.challenges)
        }

        else -> subState
    }

    private fun createState(challenges: List<Challenge>): ChallengeListViewState {
        return when {
            challenges.isEmpty() -> ChallengeListViewState.Empty
            else -> ChallengeListViewState.Changed(challenges)
        }
    }


    override fun defaultState() = ChallengeListViewState.Loading

    override val stateKey = key<ChallengeListViewState>()
}

sealed class ChallengeListViewState : ViewState {
    object Loading : ChallengeListViewState()
    object Empty : ChallengeListViewState()

    data class Changed(val challenges: List<Challenge>) : ChallengeListViewState()
}