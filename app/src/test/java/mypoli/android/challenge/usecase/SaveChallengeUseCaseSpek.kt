package mypoli.android.challenge.usecase

import mypoli.android.TestUtil
import mypoli.android.challenge.entity.Challenge
import mypoli.android.quest.Color
import mypoli.android.quest.Icon
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.threeten.bp.LocalDate

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/09/2018.
 */
class SaveChallengeUseCaseSpek : Spek({

    describe("SaveChallengeUseCase") {

        fun executeUseCase(
            params: SaveChallengeUseCase.Params
        ) =
            SaveChallengeUseCase(TestUtil.challengeRepoMock()).execute(params)

        it("should not accept Challenge without name") {
            val exec =
                {
                    executeUseCase(
                        SaveChallengeUseCase.Params(
                            name = "",
                            color = Color.BLUE,
                            icon = Icon.STAR,
                            difficulty = Challenge.Difficulty.NORMAL,
                            end = LocalDate.now()
                        )
                    )
                }
            exec shouldThrow IllegalArgumentException::class
        }
    }
})