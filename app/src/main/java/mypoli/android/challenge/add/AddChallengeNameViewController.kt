package mypoli.android.challenge.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.controller_add_challenge_name.view.*
import kotlinx.android.synthetic.main.view_no_elevation_toolbar.view.*
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeNameViewState.StateType.INITIAL
import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.view.colorRes
import mypoli.android.common.view.setToolbar
import mypoli.android.common.view.showBackButton
import mypoli.android.common.view.toolbarTitle
import mypoli.android.quest.Color
import mypoli.android.quest.Icon

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
        AddChallengeNameViewState(
            type = INITIAL,
            name = "",
            color = Color.GREEN,
            icon = null,
            difficulty = Challenge.Difficulty.NORMAL
        )
}


data class AddChallengeNameViewState(
    val type: AddChallengeNameViewState.StateType,
    val name: String,
    val color: Color,
    val icon: Icon?,
    val difficulty: Challenge.Difficulty
) : ViewState {
    enum class StateType {
        INITIAL
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
        setToolbar(view.toolbar)
        toolbarTitle = "New challenge"
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        showBackButton()
    }

    override fun render(state: AddChallengeNameViewState, view: View) {
        when (state.type) {
            AddChallengeNameViewState.StateType.INITIAL -> {
                colorLayout(view, state)
            }
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
    }

}