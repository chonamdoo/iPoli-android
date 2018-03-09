package mypoli.android.challenge.add

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.controller_add_challenge_end_date.view.*
import mypoli.android.R
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.quest.Color
import org.threeten.bp.LocalDate

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/9/18.
 */

sealed class AddChallengeEndDateAction : Action

object AddChallengeEndDateReducer : BaseViewStateReducer<AddChallengeEndDateViewState>() {
    override val stateKey = key<AddChallengeEndDateViewState>()


    override fun reduce(
        state: AppState,
        subState: AddChallengeEndDateViewState,
        action: Action
    ): AddChallengeEndDateViewState {
        return subState
    }

    override fun defaultState() =
        AddChallengeEndDateViewState(
            type = AddChallengeEndDateViewState.StateType.INITIAL,
            color = Color.GREEN,
            endDate = LocalDate.now()
        )
}

data class AddChallengeEndDateViewState(
    val type: AddChallengeEndDateViewState.StateType,
    val color: Color,
    val endDate: LocalDate
) : ViewState {
    enum class StateType {
        INITIAL,
        DATA_LOADED
    }
}

class AddChallengeEndDateViewController(args: Bundle? = null) :
    ReduxViewController<AddChallengeEndDateAction, AddChallengeEndDateViewState, AddChallengeEndDateReducer>(
        args
    ) {

    override val reducer = AddChallengeEndDateReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_add_challenge_end_date, container, false)
        view.dateList.layoutManager =
            LinearLayoutManager(container.context, LinearLayoutManager.VERTICAL, false)
        view.dateList.adapter = DateAdapter()
        return view
    }

    override fun render(state: AddChallengeEndDateViewState, view: View) {
    }

    data class DateViewModel(
        val text: String,
        val date: LocalDate
    )

    inner class DateAdapter(private var viewModels: List<DateViewModel> = listOf()) :
        RecyclerView.Adapter<ViewHolder>() {


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val vm = viewModels[position]
        }

        fun updateAll(viewModels: List<DateViewModel>) {
            this.viewModels = viewModels
            notifyDataSetChanged()
        }

        override fun getItemCount() = viewModels.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_repeating_pattern_month_day,
                    parent,
                    false
                )
            )

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}