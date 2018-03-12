package mypoli.android.challenge.usecase

import mypoli.android.challenge.persistence.ChallengeRepository
import mypoli.android.common.UseCase
import mypoli.android.quest.data.persistence.QuestRepository
import mypoli.android.repeatingquest.persistence.RepeatingQuestRepository

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/12/2018.
 */
class RemoveChallengeUseCase(
    private val challengeRepository: ChallengeRepository,
    private val questRepository: QuestRepository,
    private val repeatingQuestRepository: RepeatingQuestRepository,
    private val removeQuestFromChallengeUseCase: RemoveQuestFromChallengeUseCase
) : UseCase<RemoveChallengeUseCase.Params, Unit> {

    override fun execute(parameters: Params) {
        val c = challengeRepository.findById(parameters.challengeId)
        require(c != null)
        questRepository
            .findAllForChallenge(c!!.id)
            .forEach {
                removeQuestFromChallengeUseCase.execute(
                    RemoveQuestFromChallengeUseCase.Params.WithQuestId(
                        it.id
                    )
                )
            }

        repeatingQuestRepository
            .findAllForChallenge(c.id)
            .forEach {
                removeQuestFromChallengeUseCase.execute(
                    RemoveQuestFromChallengeUseCase.Params.WithRepeatingQuestId(
                        it.id
                    )
                )
            }
        challengeRepository.remove(c)
    }

    data class Params(val challengeId: String)
}