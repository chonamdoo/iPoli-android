package mypoli.android.challenge.add

import mypoli.android.challenge.add.AddChallengeViewState.StateType.LOADING
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/8/18.
 */
sealed class AddChallengeAction : Action

object AddChallengeReducer : BaseViewStateReducer<AddChallengeViewState> () {

    override val stateKey = key<AddChallengeViewState>()


    override fun reduce(
        state: AppState,
        subState: AddChallengeViewState,
        action: Action
    ): AddChallengeViewState {
        return subState
    }

    override fun defaultState() =
        AddChallengeViewState(type = LOADING)
}


data class AddChallengeViewState(
    val type: AddChallengeViewState.StateType
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_CHANGED
    }
}