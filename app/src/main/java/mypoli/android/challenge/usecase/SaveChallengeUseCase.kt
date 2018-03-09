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

        val c = challengeRepository.save(
            Challenge(
                name = parameters.name,
                color = parameters.color,
                icon = parameters.icon,
                difficulty = parameters.difficulty,
                end = parameters.end
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
        val name: String,
        val color: Color,
        val icon: Icon,
        val difficulty: Challenge.Difficulty,
        val end: LocalDate,
        val allQuests: List<BaseQuest>,
        val selectedQuestIds: Set<String>
    )
}