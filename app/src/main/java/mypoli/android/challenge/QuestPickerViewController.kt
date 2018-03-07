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
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.view.*
import timber.log.Timber

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
        view.questList.adapter = QuestAdapter(listOf(
            QuestViewModel("", "Run 3 km", R.color.md_green_500, Ionicons.Icon.ion_android_clipboard, false, false),
            QuestViewModel("", "Eat every day", R.color.md_red_500, Ionicons.Icon.ion_clipboard, true, false),
            QuestViewModel("", "Read", R.color.md_blue_500, Ionicons.Icon.ion_clipboard, false, true)
        ))
        return view
    }

    override fun onCreateLoadAction(): QuestPickerAction? {
        return QuestPickerAction.Load(challengeId)
    }

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
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Timber.d("AAAA $newText")
//                if (StringUtils.isEmpty(newText)) {
//                    filter("") { quests -> adapter.setQuests(quests) }
//                    return true
//                }
//
//                if (newText.trim { it <= ' ' }.length < MIN_FILTER_QUERY_LEN) {
//                    return true
//                }
//                filter(newText.trim { it <= ' ' }) { quests -> adapter.setQuests(quests) }
                return true
            }
        })
    }

    override fun render(state: QuestPickerViewState, view: View) {

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

            view.questCheck.isChecked = vm.isSelected
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


}