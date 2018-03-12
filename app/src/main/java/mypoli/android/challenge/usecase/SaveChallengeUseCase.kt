package mypoli.android.challenge.usecase

import mypoli.android.challenge.entity.Challenge
import mypoli.android.challenge.persistence.ChallengeRepository
import mypoli.android.common.UseCase
import mypoli.android.quest.BaseQuest
import mypoli.android.quest.Color
import mypoli.android.quest.Icon
import org.threeten.bp.LocalDate

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/09/2018.
 */
class SaveChallengeUseCase(
    private val challengeRepository: ChallengeRepository,
    private val saveQuestsForChallengeUseCase: SaveQuestsForChallengeUseCase
) :
    UseCase<SaveChallengeUseCase.Params, Challenge> {

    override fun execute(parameters: Params): Challenge {

        require(parameters.name.isNotEmpty())

        return if (parameters.id.isNotEmpty()) {
            updateChallenge(parameters)
        } else {
            saveNewChallenge(parameters)
        }
    }

    private fun updateChallenge(parameters: Params): Challenge {
        val c = challengeRepository.findById(parameters.id)
        require(c != null)

        return challengeRepository.save(
            c!!.copy(
                name = parameters.name,
                color = parameters.color,
                icon = parameters.icon,
                difficulty = parameters.difficulty,
                end = parameters.end,
                motivations = parameters.motivations
            )
        )
    }

    private fun saveNewChallenge(parameters: Params): Challenge {
        val c = challengeRepository.save(
            Challenge(
                name = parameters.name,
                color = parameters.color,
                icon = parameters.icon,
                difficulty = parameters.difficulty,
                end = parameters.end,
                motivations = parameters.motivations
            )
        )

        saveQuestsForChallengeUseCase.execute(
            SaveQuestsForChallengeUseCase.Params(
                c.id,
                parameters.allQuests,
                parameters.selectedQuestIds
            )
        )

        return c
    }

    data class Params(
        val id: String = "",
        val name: String,
        val color: Color,
        val icon: Icon?,
        val difficulty: Challenge.Difficulty,
        val end: LocalDate,
        val motivations: List<String>,
        val allQuests: List<BaseQuest> = listOf(),
        val selectedQuestIds: Set<String> = setOf()
    )
}