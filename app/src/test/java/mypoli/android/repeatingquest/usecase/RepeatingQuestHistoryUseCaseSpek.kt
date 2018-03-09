package mypoli.android.repeatingquest.usecase

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import mypoli.android.TestUtil
import mypoli.android.quest.Quest
import mypoli.android.quest.RepeatingQuest
import mypoli.android.quest.data.persistence.QuestRepository
import mypoli.android.repeatingquest.entity.RepeatingPattern
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/2/18.
 */
class RepeatingQuestHistoryUseCaseSpek : Spek({

    describe("CreateRepeatingQuestHistoryUseCase") {

        fun executeUseCase(
            questRepo: QuestRepository,
            repeatingQuest: RepeatingQuest,
            start: LocalDate, end: LocalDate,
            currentDate: LocalDate = LocalDate.now()
        ) =
            CreateRepeatingQuestHistoryUseCase(questRepo,
                mock {
                    on { findById(any()) } doReturn repeatingQuest
                }).execute(
                CreateRepeatingQuestHistoryUseCase.Params(
                    "",
                    start,
                    end,
                    currentDate
                )
            )

        it("should require end to be after start") {
            val exec = {
                executeUseCase(
                    TestUtil.questRepoMock(),
                    TestUtil.repeatingQuest,
                    LocalDate.now(),
                    LocalDate.now().minusDays(1)
                )
            }
            exec shouldThrow IllegalArgumentException::class
        }

        it("should return completed") {
            val date = LocalDate.now()
            val questRepoMock = mock<QuestRepository> {
                on {
                    findCompletedForRepeatingQuestInPeriod(
                        any(),
                        any(),
                        any()
                    )
                } doReturn listOf(
                    TestUtil.quest.copy(
                        completedAtDate = date
                    )
                )
            }
            val result = executeUseCase(
                questRepoMock, TestUtil.repeatingQuest.copy(
                    repeatingPattern = RepeatingPattern.Daily()
                ), date, date
            ).data
            result[date].`should equal`(CreateRepeatingQuestHistoryUseCase.DateHistory.DONE_ON_SCHEDULE)
        }

        it("should return not completed") {
            val date = LocalDate.now()
            val questRepoMock = mock<QuestRepository> {
                on {
                    findCompletedForRepeatingQuestInPeriod(
                        any(),
                        any(),
                        any()
                    )
                } doReturn listOf<Quest>()
            }
            val result = executeUseCase(
                questRepoMock, TestUtil.repeatingQuest.copy(
                    repeatingPattern = RepeatingPattern.Daily()
                ), date, date
            ).data
            result[date].`should equal`(CreateRepeatingQuestHistoryUseCase.DateHistory.FAILED)
        }

        it("should return empty") {
            val date = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY))
            val questRepoMock = mock<QuestRepository> {
                on {
                    findCompletedForRepeatingQuestInPeriod(
                        any(),
                        any(),
                        any()
                    )
                } doReturn listOf<Quest>()
            }
            val result = executeUseCase(
                questRepoMock, TestUtil.repeatingQuest.copy(
                    repeatingPattern = RepeatingPattern.Weekly(setOf(DayOfWeek.MONDAY))
                ), date, date, date.minusDays(1)
            ).data
            result[date].`should equal`(CreateRepeatingQuestHistoryUseCase.DateHistory.EMPTY)
        }

        it("should return completed not on schedule") {
            val date = LocalDate.now().with(DayOfWeek.TUESDAY)
            val questRepoMock = mock<QuestRepository> {
                on {
                    findCompletedForRepeatingQuestInPeriod(
                        any(),
                        any(),
                        any()
                    )
                } doReturn listOf(
                    TestUtil.quest.copy(
                        completedAtDate = date
                    )
                )
            }
            val result = executeUseCase(
                questRepoMock, TestUtil.repeatingQuest.copy(
                    repeatingPattern = RepeatingPattern.Weekly(setOf(DayOfWeek.MONDAY))
                ), date, date
            ).data
            result[date].`should equal`(CreateRepeatingQuestHistoryUseCase.DateHistory.DONE_NOT_ON_SCHEDULE)
        }

        it("should return completed not on schedule after repeating quest end") {
            val today = LocalDate.now()
            val tomorrow = LocalDate.now().plusDays(1)
            val questRepoMock = mock<QuestRepository> {
                on {
                    findCompletedForRepeatingQuestInPeriod(
                        any(),
                        any(),
                        any()
                    )
                } doReturn listOf(
                    TestUtil.quest.copy(
                        completedAtDate = tomorrow
                    )
                )
            }
            val result = executeUseCase(
                questRepoMock, TestUtil.repeatingQuest.copy(
                    repeatingPattern = RepeatingPattern.Daily(today.minusDays(1), today)
                ), tomorrow, tomorrow
            ).data
            result[tomorrow].`should equal`(CreateRepeatingQuestHistoryUseCase.DateHistory.DONE_NOT_ON_SCHEDULE)
        }

        it("should return states for 2 dates") {
            val today = LocalDate.now()
            val tomorrow = LocalDate.now().plusDays(1)
            val questRepoMock = mock<QuestRepository> {
                on {
                    findCompletedForRepeatingQuestInPeriod(
                        any(),
                        any(),
                        any()
                    )
                } doReturn listOf(
                    TestUtil.quest.copy(
                        completedAtDate = tomorrow
                    )
                )
            }
            val result = executeUseCase(
                questRepoMock, TestUtil.repeatingQuest.copy(
                    repeatingPattern = RepeatingPattern.Daily()
                ), today, tomorrow
            ).data
            result.size.`should equal`(2)
            result[today].`should equal`(CreateRepeatingQuestHistoryUseCase.DateHistory.FAILED)
            result[tomorrow].`should equal`(CreateRepeatingQuestHistoryUseCase.DateHistory.DONE_ON_SCHEDULE)
        }
    }
})