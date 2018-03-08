package mypoli.android.challenge

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.ionicons_typeface_library.Ionicons
import kotlinx.android.synthetic.main.controller_quest_picker.view.*
import kotlinx.android.synthetic.main.item_quest_picker.view.*
import kotlinx.android.synthetic.main.view_default_toolbar.view.*
import mypoli.android.R
import mypoli.android.challenge.QuestPickerViewState.StateType.DATA_CHANGED
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.view.*

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 3/7/18.
 */
class QuestPickerViewController(args: Bundle? = null) :
    ReduxViewController<QuestPickerAction, QuestPickerViewState, QuestPickerReducer>(args) {

    override val reducer = QuestPickerReducer

    private lateinit var challengeId: String

    constructor(
        challengeId: String
    ) : this() {
        this.challengeId = challengeId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.controller_quest_picker, null)
        setToolbar(view.toolbar)
        toolbarTitle = "Choose quests"
        view.questList.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.questList.adapter = QuestAdapter()
        return view
    }

    override fun onCreateLoadAction() = QuestPickerAction.Load(challengeId)

    override fun onAttach(view: View) {
        super.onAttach(view)
        showBackButton()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.quest_picker_menu, menu)
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

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                router.popCurrentController()
                true
            }

            R.id.actionSave -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun render(state: QuestPickerViewState, view: View) {
        when (state.type) {
            DATA_CHANGED -> {
                (view.questList.adapter as QuestAdapter).updateAll(state.toViewModels())
            }
        }

    }

    data class QuestViewModel(
        val id: String,
        val name: String,
        @ColorRes val color: Int,
        val icon: IIcon,
        val isRepeating: Boolean,
        val isSelected : Boolean
    )

    inner class QuestAdapter(private var viewModels: List<QuestViewModel> = listOf()) :
        RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount() = viewModels.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val vm = viewModels[position]
            val view = holder.itemView
            view.questName.text = vm.name
            view.questIcon.backgroundTintList =
                ColorStateList.valueOf(colorRes(vm.color))
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
                dispatch(QuestPickerAction.Check(vm.id, isChecked))
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private fun QuestPickerViewState.toViewModels() =
        filteredQuests.map {
            when (it) {
                is PickerQuest.OneTime -> {
                    val quest = it.quest
                    QuestPickerViewController.QuestViewModel(
                        id = quest.id,
                        name = quest.name,
                        color = quest.color.androidColor.color500,
                        icon = quest.icon?.androidIcon?.icon ?: Ionicons.Icon.ion_android_clipboard,
                        isRepeating = false,
                        isSelected = selectedQuests.contains(it.id)
                    )
                }
                is PickerQuest.Repeating -> {
                    val rq = it.repeatingQuest
                    QuestPickerViewController.QuestViewModel(
                        id = rq.id,
                        name = rq.name,
                        color = rq.color.androidColor.color500,
                        icon = rq.icon?.androidIcon?.icon ?: Ionicons.Icon.ion_android_clipboard,
                        isRepeating = true,
                        isSelected = selectedQuests.contains(it.id)
                    )
                }
            }
        }
}