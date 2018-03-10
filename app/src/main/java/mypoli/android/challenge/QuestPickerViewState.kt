package mypoli.android.challenge

import android.content.res.ColorStateList
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.ionicons_typeface_library.Ionicons
import kotlinx.android.synthetic.main.item_quest_picker.view.*
import mypoli.android.R
import mypoli.android.challenge.QuestPickerViewState.StateType.*
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.view.AndroidColor
import mypoli.android.common.view.AndroidIcon
import mypoli.android.common.view.visible
import mypoli.android.quest.*
import mypoli.android.repeatingquest.entity.RepeatingPattern
import org.threeten.bp.LocalDate

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/7/18.
 */

sealed class QuestPickerAction : Action {
    data class Load(val challengeId: String? = null) : QuestPickerAction()
    data class Loaded(val quests: List<Quest>, val repeatingQuests: List<RepeatingQuest>) : QuestPickerAction()
    data class Filter(val query: String) : QuestPickerAction()
    data class Check(val id: String, val isSelected: Boolean) : QuestPickerAction()
    object Save : QuestPickerAction()
    object Next : QuestPickerAction()
}

object QuestPickerReducer : BaseViewStateReducer<QuestPickerViewState>() {

    override val stateKey = key<QuestPickerViewState>()

    val MIN_FILTER_QUERY_LEN = 3

    override fun reduce(
        state: AppState,
        subState: QuestPickerViewState,
        action: Action
    ) =
        when (action) {
            is QuestPickerAction.Loaded -> {
                val quests = createPickerQuests(
                    listOf(
                        Quest(
                            id = "1",
                            name = "Run",
                            color = Color.GREEN,
                            icon = Icon.PIZZA,
                            category = Category("WELLNESS", Color.GREEN),
                            duration = 30,
                            reminder = null,
                            scheduledDate = LocalDate.now()
                        ),
                        Quest(
                            id = "2",
                            name = "Runing",
                            color = Color.ORANGE,
                            icon = Icon.MONEY,
                            category = Category("WELLNESS", Color.GREEN),
                            duration = 60,
                            reminder = null,
                            scheduledDate = LocalDate.now().plusDays(1)
                        )
                    ),
                    listOf(
                        RepeatingQuest(
                            id = "3",
                            name = "Runinja",
                            color = Color.BLUE_GREY,
                            icon = Icon.RESTAURANT,
                            category = Category("WELLNESS", Color.GREEN),
                            duration = 20,
                            repeatingPattern = RepeatingPattern.Daily()
                        )
                    )
                )
                subState.copy(
                    type = DATA_CHANGED,
                    allQuests = quests,
                    filteredQuests = quests
                )
            }

            is QuestPickerAction.Filter -> {
                val query = action.query.trim()
                when {
                    query.isEmpty() -> subState.copy(
                        type = DATA_CHANGED,
                        filteredQuests = subState.allQuests
                    )
                    query.length < MIN_FILTER_QUERY_LEN -> subState
                    else -> subState.copy(
                        type = DATA_CHANGED,
                        filteredQuests = filterQuests(
                            query,
                            subState.allQuests
                        )
                    )
                }
            }

            is QuestPickerAction.Check -> {
                subState.copy(
                    type = ITEM_SELECTED,
                    selectedQuests = if (action.isSelected) {
                        subState.selectedQuests + action.id
                    } else {
                        subState.selectedQuests - action.id
                    }
                )
            }
            else -> subState
        }

    private fun createPickerQuests(
        quests: List<Quest>,
        repeatingQuests: List<RepeatingQuest>
    ) =
        sortQuests(
            quests.map { PickerQuest.OneTime(it) } +
                repeatingQuests.map {
                    PickerQuest.Repeating(
                        it
                    )
                })

    private fun filterQuests(
        query: String,
        quests: List<PickerQuest>
    ) =
        sortQuests(
            quests.filter {
                it.name.toLowerCase().contains(query.toLowerCase())
            }
        )

    private fun sortQuests(result: List<PickerQuest>): List<PickerQuest> {
        return result.sortedWith(Comparator { q1, q2 ->
            val d1 = q1.date
            val d2 = q2.date
            if (d1 == null && d2 == null) {
                return@Comparator -1
            }

            if (d1 == null) {
                return@Comparator 1
            }

            if (d2 == null) {
                return@Comparator -1
            }

            if (d2.isAfter(d1)) {
                return@Comparator 1
            }

            return@Comparator if (d1.isAfter(d2)) {
                -1
            } else 0
        })
    }

    override fun defaultState() =
        QuestPickerViewState(
            type = LOADING,
            challengeId = "",
            allQuests = listOf(),
            filteredQuests = listOf(),
            selectedQuests = setOf()
        )


}

sealed class PickerQuest(
    open val baseQuest: BaseQuest,
    open val id: String,
    open val name: String,
    open val date: LocalDate?
) {
    data class OneTime(val quest: Quest) :
        PickerQuest(quest, quest.id, quest.name, quest.scheduledDate)

    data class Repeating(val repeatingQuest: RepeatingQuest) :
        PickerQuest(
            repeatingQuest,
            repeatingQuest.id,
            repeatingQuest.name,
            repeatingQuest.repeatingPattern.start
        )
}

data class QuestPickerViewState(
    val type: QuestPickerViewState.StateType,
    val challengeId: String,
    val allQuests: List<PickerQuest>,
    val filteredQuests: List<PickerQuest>,
    val selectedQuests: Set<String>
) : ViewState {
    enum class StateType {
        LOADING,
        DATA_CHANGED,
        ITEM_SELECTED
    }
}

data class QuestViewModel(
    val id: String,
    val name: String,
    @ColorRes val color: Int,
    val icon: IIcon,
    val isRepeating: Boolean,
    val isSelected: Boolean
)

class QuestAdapter(
    private var viewModels: List<QuestViewModel> = listOf(),
    private var checkListener: (String, Boolean) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun getItemCount() = viewModels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vm = viewModels[position]
        val view = holder.itemView
        view.questName.text = vm.name
        view.questIcon.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(view.context, vm.color))
        view.questIcon.setImageDrawable(
            IconicsDrawable(view.context)
                .icon(vm.icon)
                .colorRes(R.color.md_white)
                .sizeDp(24)
        )
        view.questRepeatIndicator.visible = vm.isRepeating

        view.questCheck.setOnCheckedChangeListener(null)
        view.questCheck.isChecked = vm.isSelected
        view.questCheck.setOnCheckedChangeListener { _, isChecked ->
            checkListener(vm.id, isChecked)
        }
        view.setOnClickListener {
            view.questCheck.isChecked = !view.questCheck.isChecked
        }
    }

    fun updateAll(viewModels: List<QuestViewModel>) {
        this.viewModels = viewModels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_quest_picker,
                parent,
                false
            )
        )
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

fun QuestPickerViewState.toViewModels() =
    filteredQuests.map {
        when (it) {
            is PickerQuest.OneTime -> {
                val quest = it.quest
                QuestViewModel(
                    id = quest.id,
                    name = quest.name,
                    color = AndroidColor.valueOf(quest.color.name).color500,
                    icon = quest.icon?.let { AndroidIcon.valueOf(it.name).icon }
                        ?: Ionicons.Icon.ion_android_clipboard,
                    isRepeating = false,
                    isSelected = selectedQuests.contains(it.id)
                )
            }
            is PickerQuest.Repeating -> {
                val rq = it.repeatingQuest
                QuestViewModel(
                    id = rq.id,
                    name = rq.name,
                    color = AndroidColor.valueOf(rq.color.name).color500,
                    icon = rq.icon?.let { AndroidIcon.valueOf(it.name).icon }
                        ?: Ionicons.Icon.ion_android_clipboard,
                    isRepeating = true,
                    isSelected = selectedQuests.contains(it.id)
                )
            }
        }
    }