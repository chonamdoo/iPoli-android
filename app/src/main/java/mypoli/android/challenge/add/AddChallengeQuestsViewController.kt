package mypoli.android.challenge.add

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import kotlinx.android.synthetic.main.list_quest_picker.view.*
import mypoli.android.R
import mypoli.android.challenge.*
import mypoli.android.common.redux.android.ReduxViewController

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/10/18.
 */
class AddChallengeQuestsViewController(args: Bundle? = null) :
    ReduxViewController<QuestPickerAction, QuestPickerViewState, QuestPickerReducer>(args) {
    override val reducer = QuestPickerReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.controller_add_challenge_quests, container, false)
        view.questList.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.questList.adapter = QuestAdapter(listOf(), { id, isChecked ->
            dispatch(QuestPickerAction.Check(id, isChecked))
        })
        return view
    }

    override fun onCreateLoadAction() = QuestPickerAction.Load()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.challenge_quest_picker_menu, menu)
        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = false

            override fun onQueryTextChange(newText: String): Boolean {
                dispatch(QuestPickerAction.Filter(newText))
                return true
            }
        })
    }

    override fun render(state: QuestPickerViewState, view: View) {
        when (state.type) {
            QuestPickerViewState.StateType.DATA_CHANGED -> {
                (view.questList.adapter as QuestAdapter).updateAll(state.toViewModels())
            }
        }
    }
}