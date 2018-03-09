package mypoli.android.challenge.add

import mypoli.android.challenge.PickerQuest
import mypoli.android.challenge.add.AddChallengeViewState.StateType.*
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
    ) =
        when (action) {
            is AddChallengeNameAction.Next -> {
                val s = state.stateFor(AddChallengeNameViewState::class.java)
                subState.copy(
                    type = CHANGE_PAGE,
                    adapterPosition = subState.adapterPosition + 1,
                    name = action.name,
                    color = s.color,
                    icon = s.icon,
                    difficulty = s.difficulty
                )
            }

            is AddChallengeMotivationAction.Next ->
                subState.copy(
                    type = CHANGE_PAGE,
                    adapterPosition = subState.adapterPosition + 1,
                    motivationList = action.motivationList
                )

            AddChallengeMotivationAction.Back ->
                subState.copy(
                    type = CHANGE_PAGE,
                    adapterPosition = subState.adapterPosition - 1
                )

            AddChallengeNameAction.Back ->
                subState.copy(
                    type = CLOSE
                )

            else -> subState
    }

    override fun defaultState() =
        AddChallengeViewState(
            type = LOADING,
            adapterPosition = 0,
            name = "",
            color = Color.GREEN,
            icon = null,
            difficulty = Challenge.Difficulty.NORMAL,
            end = LocalDate.now(),
            motivationList = listOf(),
            quests = listOf()
        )
}


data class AddChallengeViewState(
    val type: AddChallengeViewState.StateType,
    val adapterPosition: Int,
    val name: String,
    val color: Color,
    val icon: Icon?,
    val difficulty: Challenge.Difficulty,
    val end: LocalDate,
    val motivationList: List<String>,
    val quests: List<PickerQuest>
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_CHANGED,
        CHANGE_PAGE,
        CLOSE
    }
}