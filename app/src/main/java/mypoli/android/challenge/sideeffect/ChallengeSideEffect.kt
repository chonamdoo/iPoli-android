package mypoli.android.challenge.sideeffect

import mypoli.android.challenge.QuestPickerAction
import mypoli.android.common.AppSideEffect
import mypoli.android.common.AppState
import mypoli.android.common.redux.Action
import mypoli.android.quest.Quest
import mypoli.android.repeatingquest.entity.RepeatingQuest
import space.traversal.kapsule.required

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 3/7/18.
 */
class ChallengeSideEffect : AppSideEffect() {

    private val questRepository by required { questRepository }
    private val repeatingQuestRepository by required { repeatingQuestRepository }

    override suspend fun doExecute(action: Action, state: AppState) {
        if (action is QuestPickerAction.Load) {
            dispatch(QuestPickerAction.Loaded(listOf<Quest>(), listOf<RepeatingQuest>()))
        }

    }

    override fun canHandle(action: Action) =
        action is QuestPickerAction.Load

}