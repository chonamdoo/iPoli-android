package mypoli.android.challenge.usecase

import mypoli.android.common.UseCase
import mypoli.android.quest.Quest
import mypoli.android.quest.RepeatingQuest
import mypoli.android.quest.data.persistence.QuestRepository
import mypoli.android.repeatingquest.persistence.RepeatingQuestRepository

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/09/2018.
 */
class RemoveQuestFromChallengeUseCase(
    private val questRepository: QuestRepository,
    private val repeatingQuestRepository: RepeatingQuestRepository
) :
    UseCase<RemoveQuestFromChallengeUseCase.Params, RemoveQuestFromChallengeUseCase.Result> {

    override fun execute(parameters: Params) =
        when (parameters) {
            is Params.WithQuestId -> {
                require(parameters.id.isNotEmpty())
                val quest = questRepository.findById(parameters.id)
                require(quest != null)
                Result.ChangedQuest(removeQuestFromChallenge(quest!!))
            }

            is Params.WithRepeatingQuestId -> {
                require(parameters.id.isNotEmpty())
                val rq = repeatingQuestRepository.findById(parameters.id)
                require(rq != null)

                questRepository
                    .findAllForRepeatingQuest(rq!!.id)
                    .forEach {
                        removeQuestFromChallenge(it)
                    }

                Result.ChangedRepeatingQuest(
                    repeatingQuestRepository.save(
                        rq.copy(
                            challengeId = null
                        )
                    )
                )
            }
        }

    private fun removeQuestFromChallenge(q: Quest) =
        questRepository.save(
            q.copy(
                challengeId = null
            )
        )


    sealed class Params {
        data class WithQuestId(val id: String) : Params()
        data class WithRepeatingQuestId(val id: String) : Params()
    }

    sealed class Result {
        data class ChangedQuest(val quest: Quest) : Result()
        data class ChangedRepeatingQuest(val quest: RepeatingQuest) : Result()
    }


}