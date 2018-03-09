package mypoli.android.challenge.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.controller_add_challenge_name.view.*
import kotlinx.android.synthetic.main.view_no_elevation_toolbar.view.*
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeMotivationViewState.StateType.INITIAL
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.view.colorRes

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/9/18.
 */
sealed class AddChallengeMotivationAction : Action


object AddChallengeMotivationReducer : BaseViewStateReducer<AddChallengeMotivationViewState>() {

    override val stateKey = key<AddChallengeMotivationViewState>()

    override fun reduce(
        state: AppState,
        subState: AddChallengeMotivationViewState,
        action: Action
    ): AddChallengeMotivationViewState {
        return subState
    }

    override fun defaultState() =
        AddChallengeMotivationViewState(
            type = INITIAL
        )

}

data class AddChallengeMotivationViewState(
    val type: AddChallengeMotivationViewState.StateType
) : ViewState {
    enum class StateType {
        INITIAL
    }
}

class AddChallengeMotivationViewController(args: Bundle? = null) :
    ReduxViewController<AddChallengeMotivationAction, AddChallengeMotivationViewState, AddChallengeMotivationReducer>(
        args
    ) {
    override val reducer = AddChallengeMotivationReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View =
        inflater.inflate(R.layout.controller_add_challenge_motivation, container, false)

    override fun render(state: AddChallengeMotivationViewState, view: View) {
        when (state.type) {

        }
    }

    private fun colorLayout(
        view: View,
        state: AddChallengeNameViewState
    ) {
        val color500 = colorRes(state.color.androidColor.color500)
        val color700 = colorRes(state.color.androidColor.color700)
        view.appbar.setBackgroundColor(color500)
        view.toolbar.setBackgroundColor(color500)
        view.rootContainer.setBackgroundColor(color500)
        activity?.window?.navigationBarColor = color500
        activity?.window?.statusBarColor = color700
        view.challengeDifficulty.setPopupBackgroundResource(state.color.androidColor.color500)

    }
}