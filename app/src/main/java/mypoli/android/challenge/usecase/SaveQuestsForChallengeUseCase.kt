package mypoli.android.challenge.usecase

import mypoli.android.common.UseCase
import mypoli.android.quest.Quest
import mypoli.android.quest.data.persistence.QuestRepository
import mypoli.android.repeatingquest.entity.RepeatingQuest
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
        parameters.quests.forEach {
            questRepository.save(it.copy(challengeId = challengeId))
        }
        parameters.repeatingQuests.forEach {
            repeatingQuestRepository.save(it.copy(challengeId = challengeId))
        }
    }

    data class Params(
        val challengeId: String,
        val quests: List<Quest>,
        val repeatingQuests: List<RepeatingQuest>
    )

}