package mypoli.android.challenge.sideeffect

import mypoli.android.challenge.PickerQuest
import mypoli.android.challenge.QuestPickerAction
import mypoli.android.challenge.QuestPickerViewState
import mypoli.android.challenge.usecase.SaveQuestsForChallengeUseCase
import mypoli.android.common.AppSideEffect
import mypoli.android.common.AppState
import mypoli.android.common.redux.Action
import mypoli.android.quest.Quest
import mypoli.android.repeatingquest.entity.RepeatingQuest
import space.traversal.kapsule.required

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/7/18.
 */
class ChallengeSideEffect : AppSideEffect() {

    private val questRepository by required { questRepository }
    private val repeatingQuestRepository by required { repeatingQuestRepository }
    private val saveQuestsForChallengeUseCase by required { saveQuestsForChallengeUseCase }

    override suspend fun doExecute(action: Action, state: AppState) {
        when (action) {
            is QuestPickerAction.Load -> {
                dispatch(QuestPickerAction.Loaded(listOf<Quest>(), listOf<RepeatingQuest>()))
            }

            is QuestPickerAction.Save -> {
                val pickerState = state.stateFor(QuestPickerViewState::class.java)
                val challengeId = pickerState.challengeId
                val quests = mutableListOf<Quest>()
                val repeatingQuests = mutableListOf<RepeatingQuest>()
                pickerState.allQuests
                    .filter {
                        pickerState.selectedQuests.contains(
                            it.id
                        )
                    }.forEach {
                        when (it) {
                            is PickerQuest.OneTime -> quests.add(it.quest)
                            is PickerQuest.Repeating -> repeatingQuests.add(it.repeatingQuest)
                        }
                    }

                saveQuestsForChallengeUseCase.execute(
                    SaveQuestsForChallengeUseCase.Params(
                        challengeId,
                        quests,
                        repeatingQuests
                    )
                )
            }
        }

    }

    override fun canHandle(action: Action) =
        action is QuestPickerAction

}