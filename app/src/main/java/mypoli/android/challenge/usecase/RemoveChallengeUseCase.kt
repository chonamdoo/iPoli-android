package mypoli.android.challenge.usecase

import mypoli.android.challenge.persistence.ChallengeRepository
import mypoli.android.common.UseCase

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/12/2018.
 */
class RemoveChallengeUseCase(
    private val challengeRepository: ChallengeRepository
) : UseCase<RemoveChallengeUseCase.Params, Unit> {

    override fun execute(parameters: Params) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    data class Params(val challengeId: String)
}