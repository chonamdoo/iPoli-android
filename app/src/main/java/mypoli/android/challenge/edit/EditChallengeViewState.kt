package mypoli.android.challenge.edit

import mypoli.android.challenge.edit.EditChallengeViewState.StateType.LOADING
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.quest.Color
import mypoli.android.quest.Icon

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/12/18.
 */
sealed class EditChallengeAction : Action {

}

object EditChallengeReducer : BaseViewStateReducer<EditChallengeViewState>() {
    override val stateKey = key<EditChallengeViewState>()

    override fun reduce(
        state: AppState,
        subState: EditChallengeViewState,
        action: Action
    ): EditChallengeViewState {
        return subState
    }

    override fun defaultState() =
        EditChallengeViewState(
            type = LOADING,
            id = "",
            name = "",
            icon = null,
            color = Color.GREEN
        )


}

data class EditChallengeViewState(
    val type: EditChallengeViewState.StateType,
    val id: String,
    val name: String,
    val icon: Icon?,
    val color: Color
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_LOADED,
        COLOR_CHANGED,
        ICON_CHANGED,
        VALIDATION_ERROR_EMPTY_NAME,
    }
}