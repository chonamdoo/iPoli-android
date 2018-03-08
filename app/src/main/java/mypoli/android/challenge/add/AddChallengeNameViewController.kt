package mypoli.android.challenge.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeNameViewState.StateType.LOADING
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.redux.android.ReduxViewController

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/8/18.
 */

sealed class AddChallengeNameAction : Action

object AddChallengeNameReducer : BaseViewStateReducer<AddChallengeNameViewState>() {

    override val stateKey = key<AddChallengeNameViewState>()


    override fun reduce(
        state: AppState,
        subState: AddChallengeNameViewState,
        action: Action
    ): AddChallengeNameViewState {
        return subState
    }

    override fun defaultState() =
        AddChallengeNameViewState(type = LOADING)
}


data class AddChallengeNameViewState(
    val type: AddChallengeNameViewState.StateType
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_CHANGED
    }
}


class AddChallengeNameViewController(args: Bundle? = null) :
    ReduxViewController<AddChallengeNameAction, AddChallengeNameViewState, AddChallengeNameReducer>(
        args
    ) {
    override val reducer = AddChallengeNameReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_add_challenge_name, container, false)
        return view
    }

    override fun render(state: AddChallengeNameViewState, view: View) {
    }


}