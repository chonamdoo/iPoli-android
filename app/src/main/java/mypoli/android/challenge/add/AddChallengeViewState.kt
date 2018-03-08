package mypoli.android.challenge.add

import mypoli.android.challenge.PickerQuest
import mypoli.android.challenge.add.AddChallengeViewState.StateType.LOADING
import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.quest.Color
import mypoli.android.quest.Icon
import org.threeten.bp.LocalDate

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
        AddChallengeViewState(
            type = LOADING,
            name = "",
            color = null,
            icon = null,
            difficulty = Challenge.Difficulty.MEDIUM,
            end = LocalDate.now(),
            motivationList = listOf(),
            quests = listOf()
        )
}


data class AddChallengeViewState(
    val type: AddChallengeViewState.StateType,
    val name: String,
    val color: Color?,
    val icon: Icon?,
    val difficulty: Challenge.Difficulty,
    val end: LocalDate,
    val motivationList: List<String>,
    val quests: List<PickerQuest>
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_CHANGED
    }
}