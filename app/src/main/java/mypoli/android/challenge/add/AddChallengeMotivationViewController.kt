package mypoli.android.challenge.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.controller_add_challenge_motivation.view.*
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeMotivationViewState.StateType.DATA_LOADED
import mypoli.android.challenge.add.AddChallengeMotivationViewState.StateType.INITIAL
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.redux.android.ReduxViewController

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/9/18.
 */
sealed class AddChallengeMotivationAction : Action {
    object Load : AddChallengeMotivationAction()
    data class Next(val motivationList: List<String>) : AddChallengeMotivationAction()
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
                val parentState = state.stateFor(AddChallengeViewState::class.java)
                val motivationList = parentState.motivationList
                subState.copy(
                    type = DATA_LOADED,
                    motivation1 = if (motivationList.isNotEmpty()) motivationList[0] else "",
                    motivation2 = if (motivationList.size > 1) motivationList[1] else "",
                    motivation3 = if (motivationList.size > 2) motivationList[2] else ""
                )
            }
            else -> subState
        }
    }

    override fun defaultState() =
        AddChallengeMotivationViewState(
            type = INITIAL,
            motivation1 = "",
            motivation2 = "",
            motivation3 = ""
        )

}

data class AddChallengeMotivationViewState(
    val type: AddChallengeMotivationViewState.StateType,
    val motivation1: String,
    val motivation2: String,
    val motivation3: String
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
        val view = inflater.inflate(R.layout.controller_add_challenge_motivation, container, false)
        return view
    }

    override fun onCreateLoadAction() =
        AddChallengeMotivationAction.Load


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
                view.motivation1.setText(state.motivation1)
                view.motivation2.setText(state.motivation2)
                view.motivation3.setText(state.motivation3)
            }
        }
    }
}