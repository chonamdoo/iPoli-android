package mypoli.android.challenge.edit

import mypoli.android.challenge.edit.EditChallengeViewState.StateType.*
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
 * on 3/12/18.
 */
sealed class EditChallengeAction : Action {
    data class Load(val challengeId: String) : EditChallengeAction()
    data class ChangeIcon(val icon: Icon?) : EditChallengeAction()
    data class ChangeColor(val color: Color) : EditChallengeAction()
    data class ChangeEndDate(val date: LocalDate) : EditChallengeAction()
}

object EditChallengeReducer : BaseViewStateReducer<EditChallengeViewState>() {
    override val stateKey = key<EditChallengeViewState>()

    override fun reduce(
        state: AppState,
        subState: EditChallengeViewState,
        action: Action
    ) =
        when (action) {
            is EditChallengeAction.Load -> {
                val dataState = state.dataState
//                val c = dataState.challenges.first { it.id == action.challengeId }
                val c = Challenge(
                    name = "Test",
                    color = Color.BLUE,
                    icon = Icon.STAR,
                    difficulty = Challenge.Difficulty.NORMAL,
                    end = LocalDate.now(),
                    motivations = listOf(
                        "ugabuga",
                        "drun drun",
                        "bla bla bla"
                    )
                )
                subState.copy(
                    type = DATA_LOADED,
                    id = action.challengeId,
                    name = c.name,
                    icon = c.icon,
                    color = c.color,
                    difficulty = c.difficulty,
                    end = c.end,
                    motivation1 = c.motivation1,
                    motivation2 = c.motivation2,
                    motivation3 = c.motivation3
                )
            }

            is EditChallengeAction.ChangeIcon -> {
                subState.copy(
                    type = ICON_CHANGED,
                    icon = action.icon
                )
            }

            is EditChallengeAction.ChangeColor -> {
                subState.copy(
                    type = COLOR_CHANGED,
                    color = action.color
                )
            }

            is EditChallengeAction.ChangeEndDate -> {
                subState.copy(
                    type = END_DATE_CHANGED,
                    end = action.date
                )
            }
            else -> subState
    }

    override fun defaultState() =
        EditChallengeViewState(
            type = LOADING,
            id = "",
            name = "",
            icon = null,
            color = Color.GREEN,
            difficulty = Challenge.Difficulty.NORMAL,
            end = LocalDate.now(),
            motivation1 = "",
            motivation2 = "",
            motivation3 = ""
        )


}

data class EditChallengeViewState(
    val type: EditChallengeViewState.StateType,
    val id: String,
    val name: String,
    val color: Color,
    val icon: Icon?,
    val difficulty: Challenge.Difficulty,
    val end: LocalDate,
    val motivation1: String,
    val motivation2: String,
    val motivation3: String
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_LOADED,
        COLOR_CHANGED,
        ICON_CHANGED,
        VALIDATION_ERROR_EMPTY_NAME,
        END_DATE_CHANGED
    }
}