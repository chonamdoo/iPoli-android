package mypoli.android.challenge.sideeffect

import mypoli.android.challenge.QuestPickerAction
import mypoli.android.challenge.QuestPickerViewState
import mypoli.android.challenge.add.AddChallengeSummaryAction
import mypoli.android.challenge.add.AddChallengeViewState
import mypoli.android.challenge.edit.EditChallengeAction
import mypoli.android.challenge.edit.EditChallengeViewState
import mypoli.android.challenge.usecase.SaveChallengeUseCase
import mypoli.android.challenge.usecase.SaveQuestsForChallengeUseCase
import mypoli.android.common.AppSideEffect
import mypoli.android.common.AppState
import mypoli.android.common.redux.Action
import mypoli.android.quest.Quest
import mypoli.android.quest.RepeatingQuest
import space.traversal.kapsule.required

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/7/18.
 */
class ChallengeSideEffect : AppSideEffect() {

    private val questRepository by required { questRepository }
    private val repeatingQuestRepository by required { repeatingQuestRepository }
    private val saveQuestsForChallengeUseCase by required { saveQuestsForChallengeUseCase }
    private val saveChallengeUseCase by required { saveChallengeUseCase }

    override suspend fun doExecute(action: Action, state: AppState) {
        when (action) {
            is QuestPickerAction.Load -> {
                dispatch(QuestPickerAction.Loaded(listOf<Quest>(), listOf<RepeatingQuest>()))
            }

            is QuestPickerAction.Save -> {
                val pickerState = state.stateFor(QuestPickerViewState::class.java)
                val challengeId = pickerState.challengeId

                val quests = pickerState.allQuests.map {
                    it.baseQuest
                }

                saveQuestsForChallengeUseCase.execute(
                    SaveQuestsForChallengeUseCase.Params(
                        challengeId,
                        quests,
                        pickerState.selectedQuests
                    )
                )
            }

            is AddChallengeSummaryAction.Save -> {
                val s = state.stateFor(AddChallengeViewState::class.java)
                saveChallengeUseCase.execute(
                    SaveChallengeUseCase.Params(
                        name = s.name,
                        color = s.color,
                        icon = s.icon,
                        difficulty = s.difficulty,
                        end = s.end,
                        motivations = s.motivationList,
                        allQuests = s.allQuests,
                        selectedQuestIds = s.selectedQuestIds
                    )
                )
            }

            is EditChallengeAction.Save -> {
                val s = state.stateFor(EditChallengeViewState::class.java)
                saveChallengeUseCase.execute(
                    SaveChallengeUseCase.Params(
                        name = s.name,
                        color = s.color,
                        icon = s.icon,
                        difficulty = s.difficulty,
                        end = s.end,
                        motivations = listOf(s.motivation1, s.motivation2, s.motivation3)
                    )
                )
            }
        }

    }

    override fun canHandle(action: Action) =
        action is QuestPickerAction
            || action is AddChallengeSummaryAction
            || action is EditChallengeAction

}