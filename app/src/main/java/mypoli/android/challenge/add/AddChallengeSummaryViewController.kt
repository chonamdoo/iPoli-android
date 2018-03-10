package mypoli.android.challenge.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeSummaryViewState.StateType.INITIAL
import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.quest.BaseQuest
import mypoli.android.quest.Icon
import org.threeten.bp.LocalDate

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/10/18.
 */
sealed class AddChallengeSummaryAction : Action

object AddChallengeSummaryReducer : BaseViewStateReducer<AddChallengeSummaryViewState>() {
    override val stateKey = key<AddChallengeSummaryViewState>()

    override fun reduce(
        state: AppState,
        subState: AddChallengeSummaryViewState,
        action: Action
    ): AddChallengeSummaryViewState {
        return subState
    }

    override fun defaultState() =
        AddChallengeSummaryViewState(
            type = INITIAL,
            name = "",
            icon = null,
            difficulty = Challenge.Difficulty.NORMAL,
            end = LocalDate.now(),
            motivationList = listOf(),
            quests = listOf()
        )
}

data class AddChallengeSummaryViewState(
    val type: AddChallengeSummaryViewState.StateType,
    val name: String,
    val icon: Icon?,
    val difficulty: Challenge.Difficulty,
    val end: LocalDate,
    val motivationList: List<String>,
    val quests: List<BaseQuest>
) : ViewState {
    enum class StateType {
        INITIAL,
        DATA_CHANGED
    }
}

class AddChallengeSummaryViewController(args: Bundle? = null) :
    ReduxViewController<AddChallengeSummaryAction, AddChallengeSummaryViewState, AddChallengeSummaryReducer>(
        args
    ) {
    override val reducer = AddChallengeSummaryReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_add_challenge_summary, container, false)
        return view
    }

    override fun render(state: AddChallengeSummaryViewState, view: View) {
    }
}