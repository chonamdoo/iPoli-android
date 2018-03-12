package mypoli.android.challenge.list

import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.DataLoadedAction
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action

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
    ) =
        when (action) {
            is ChallengeListAction.Load ->
                createState(state.dataState.challenges)

            is DataLoadedAction.ChallengesChanged ->
                createState(action.challenges)

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