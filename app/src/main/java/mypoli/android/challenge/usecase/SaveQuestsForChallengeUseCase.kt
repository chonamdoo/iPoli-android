package mypoli.android.challenge.usecase

import mypoli.android.common.UseCase
import mypoli.android.quest.BaseQuest
import mypoli.android.quest.Quest
import mypoli.android.quest.RepeatingQuest
import mypoli.android.quest.data.persistence.QuestRepository
import mypoli.android.repeatingquest.persistence.RepeatingQuestRepository

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/8/18.
 */
class SaveQuestsForChallengeUseCase(
    private val questRepository: QuestRepository,
    private val repeatingQuestRepository: RepeatingQuestRepository
) : UseCase<SaveQuestsForChallengeUseCase.Params, Unit> {

    override fun execute(parameters: SaveQuestsForChallengeUseCase.Params) {

        val challengeId = parameters.challengeId

        val allQuests = parameters.allQuests

        val (quests, repeatingQuests) = allQuests
            .filter {
                parameters.selectedQuestIds.contains(
                    it.id
                )
            }.partition {
                it is Quest
            }

        quests.forEach {
            questRepository.save((it as Quest).copy(challengeId = challengeId))
        }
        repeatingQuests.forEach {
            repeatingQuestRepository.save((it as RepeatingQuest).copy(challengeId = challengeId))
        }
    }

    data class Params(
        val challengeId: String,
        val allQuests: List<BaseQuest>,
        val selectedQuestIds: Set<String>
    )

}