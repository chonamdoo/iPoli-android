package mypoli.android.challenge.add

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.ionicons_typeface_library.Ionicons
import kotlinx.android.synthetic.main.controller_add_challenge_summary.view.*
import kotlinx.android.synthetic.main.item_challenge_summary_quest.view.*
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeSummaryViewState.StateType.INITIAL
import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.view.visible
import mypoli.android.quest.BaseQuest
import mypoli.android.quest.Icon
import org.threeten.bp.LocalDate

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/10/18.
 */
sealed class AddChallengeSummaryAction : Action

object AddChallengeSummaryReducer : BaseViewStateReducer<AddChallengeSummaryViewState>() {
    override val stateKey = key<AddChallengeSummaryViewState>()

    override fun reduce(
        state: AppState,
        subState: AddChallengeSummaryViewState,
        action: Action
    ): AddChallengeSummaryViewState {
        return subState
    }

    override fun defaultState() =
        AddChallengeSummaryViewState(
            type = INITIAL,
            name = "",
            icon = null,
            difficulty = Challenge.Difficulty.NORMAL,
            end = LocalDate.now(),
            motivationList = listOf(),
            quests = listOf()
        )
}

data class AddChallengeSummaryViewState(
    val type: AddChallengeSummaryViewState.StateType,
    val name: String,
    val icon: Icon?,
    val difficulty: Challenge.Difficulty,
    val end: LocalDate,
    val motivationList: List<String>,
    val quests: List<BaseQuest>
) : ViewState {
    enum class StateType {
        INITIAL,
        DATA_CHANGED
    }
}

class AddChallengeSummaryViewController(args: Bundle? = null) :
    ReduxViewController<AddChallengeSummaryAction, AddChallengeSummaryViewState, AddChallengeSummaryReducer>(
        args
    ) {
    override val reducer = AddChallengeSummaryReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_add_challenge_summary, container, false)
        view.challengeQuests.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.challengeQuests.adapter = QuestAdapter(
            listOf(
                QuestViewModel(Ionicons.Icon.ion_android_clipboard, "workout everyday", true),
                QuestViewModel(Ionicons.Icon.ion_document, "Run", false),
                QuestViewModel(Ionicons.Icon.ion_android_done, "Read a book", true)
            )
        )
        return view
    }

    override fun render(state: AddChallengeSummaryViewState, view: View) {
    }

    data class QuestViewModel(
        val icon: IIcon,
        val name: String,
        val isRepeating: Boolean
    )

    inner class QuestAdapter(private var viewModels: List<QuestViewModel> = listOf()) :
        RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount() = viewModels.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val vm = viewModels[position]
            val view = holder.itemView
            view.questIcon.setImageDrawable(
                IconicsDrawable(view.context)
                    .icon(vm.icon)
                    .colorRes(R.color.md_white)
                    .sizeDp(24)
            )
            view.questName.text = vm.name
            view.repeatingIndicator.visible = vm.isRepeating
        }

        fun updateAll(viewModels: List<QuestViewModel>) {
            this.viewModels = viewModels
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_challenge_summary_quest,
                    parent,
                    false
                )
            )

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}