package mypoli.android

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import mypoli.android.challenge.entity.Challenge
import mypoli.android.challenge.persistence.ChallengeRepository
import mypoli.android.pet.Pet
import mypoli.android.pet.PetAvatar
import mypoli.android.player.AuthProvider
import mypoli.android.player.Player
import mypoli.android.player.persistence.PlayerRepository
import mypoli.android.quest.*
import mypoli.android.quest.data.persistence.QuestRepository
import mypoli.android.repeatingquest.entity.RepeatingPattern
import mypoli.android.repeatingquest.persistence.RepeatingQuestRepository
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

/**
 * Created by Venelin Valkov <venelin@ipoli.io>
 * on 12/1/17.
 */
object TestUtil {
    fun player() = Player(
        level = 1,
        coins = 10,
        gems = 0,
        username = "",
        displayName = "",
        experience = 10,
        authProvider = AuthProvider.Guest(""),
        pet = Pet(
            "",
            avatar = PetAvatar.ELEPHANT,
            healthPoints = 30,
            moodPoints = Pet.AWESOME_MIN_MOOD_POINTS - 1
        )
    )

    fun playerRepoMock(player: Player) = mock<PlayerRepository> {
        on { find() } doReturn player
        on { save(any()) } doAnswer { invocation ->
            invocation.getArgument(0)
        }
    }

    fun questRepoMock() = mock<QuestRepository> {
        on { save(any()) } doAnswer { invocation ->
            invocation.getArgument(0)
        }
    }

    fun repeatingQuestRepoMock() = mock<RepeatingQuestRepository> {
        on { save(any()) } doAnswer { invocation ->
            invocation.getArgument(0)
        }
    }

    fun challengeRepoMock() = mock<ChallengeRepository> {
        on { save(any()) } doAnswer { invocation ->
            invocation.getArgument(0)
        }
    }

    val firstDateOfWeek: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
    val lastDateOfWeek: LocalDate = LocalDate.now().with(DayOfWeek.SUNDAY)

    val quest = Quest(
        name = "Test",
        color = Color.BLUE,
        category = Category("test", Color.BLUE),
        duration = 60,
        scheduledDate = LocalDate.now()
    )

    val repeatingQuest = RepeatingQuest(
        name = "Test",
        repeatingPattern = RepeatingPattern.Daily(start = firstDateOfWeek),
        color = Color.BLUE,
        category = Category("test", Color.BLUE),
        duration = 60
    )

    val challenge = Challenge(
        name = "Test",
        color = Color.BLUE,
        icon = Icon.STAR,
        difficulty = Challenge.Difficulty.NORMAL,
        end = LocalDate.now()
    )
}