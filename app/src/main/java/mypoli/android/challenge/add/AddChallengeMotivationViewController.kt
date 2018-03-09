package mypoli.android.challenge.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.controller_add_challenge_motivation.view.*
import kotlinx.android.synthetic.main.view_no_elevation_toolbar.view.*
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeMotivationViewState.StateType.DATA_LOADED
import mypoli.android.challenge.add.AddChallengeMotivationViewState.StateType.INITIAL
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

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/9/18.
 */
sealed class AddChallengeMotivationAction : Action {
    object Load : AddChallengeMotivationAction()
    data class Next(val motivationList: List<String>) : AddChallengeMotivationAction()
    object Back : AddChallengeMotivationAction()
}


object AddChallengeMotivationReducer : BaseViewStateReducer<AddChallengeMotivationViewState>() {

    override val stateKey = key<AddChallengeMotivationViewState>()

    override fun reduce(
        state: AppState,
        subState: AddChallengeMotivationViewState,
        action: Action
    ): AddChallengeMotivationViewState {
        return when (action) {
            AddChallengeMotivationAction.Load -> {
                subState.copy(
                    type = DATA_LOADED,
                    color = state.stateFor(AddChallengeViewState::class.java).color
                )
            }
            else -> subState
        }
    }

    override fun defaultState() =
        AddChallengeMotivationViewState(
            type = INITIAL,
            color = Color.GREEN
        )

}

data class AddChallengeMotivationViewState(
    val type: AddChallengeMotivationViewState.StateType,
    val color: Color
) : ViewState {
    enum class StateType {
        INITIAL,
        DATA_LOADED
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
    ): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.controller_add_challenge_motivation, container, false)
        setToolbar(view.toolbar)
        toolbarTitle = "Thoughts to motivate you later"
        return view
    }

    override fun onCreateLoadAction() =
        AddChallengeMotivationAction.Load

    override fun onAttach(view: View) {
        super.onAttach(view)
        showBackButton()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            dispatch(AddChallengeMotivationAction.Back)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun render(state: AddChallengeMotivationViewState, view: View) {
        when (state.type) {
            INITIAL -> {
                view.challengeNext.setOnClickListener {
                    dispatch(
                        AddChallengeMotivationAction.Next(
                            listOf(
                                view.motivation1.text.toString(),
                                view.motivation2.text.toString(),
                                view.motivation3.text.toString()
                            )
                        )
                    )
                }
            }
            DATA_LOADED -> {
                colorLayout(view, state)
            }
        }
    }

    private fun colorLayout(
        view: View,
        state: AddChallengeMotivationViewState
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