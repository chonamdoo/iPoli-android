package mypoli.android.challenge.list

import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/05/2018.
 */

sealed class ChallengeListAction : Action {
    object LoadData : ChallengeListAction()
}

object ChallengeListReducer : BaseViewStateReducer<ChallengeListViewState>() {
    override fun reduce(
        state: AppState,
        subState: ChallengeListViewState,
        action: Action
    ) = when (action) {
        is ChallengeListAction.LoadData -> {
            createState(subState, state.dataState.challenges)
        }
        else -> subState
    }

    private fun createState(
        subState: ChallengeListViewState,
        challenges: List<Challenge>
    ) =
        subState.copy(
            type = ChallengeListViewState.StateType.CHANGED,
            challenges = challenges,
            showEmptyView = challenges.isEmpty()
        )

    override fun defaultState() =
        ChallengeListViewState(
            type = ChallengeListViewState.StateType.LOADING,
            challenges = listOf(),
            showEmptyView = false
        )

    override val stateKey = key<ChallengeListViewState>()
}

data class ChallengeListViewState(
    val type: StateType,
    val challenges: List<Challenge>,
    val showEmptyView: Boolean
) : ViewState {
    enum class StateType {
        LOADING,
        CHANGED
    }
}