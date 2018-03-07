package mypoli.android.challenge

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.ionicons_typeface_library.Ionicons
import kotlinx.android.synthetic.main.controller_quest_picker.view.*
import kotlinx.android.synthetic.main.item_quest_picker.view.*
import mypoli.android.R
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.view.colorRes
import mypoli.android.common.view.visible

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 3/7/18.
 */
class QuestPickerViewController(args: Bundle? = null) :
    ReduxViewController<QuestPickerAction, QuestPickerViewState, QuestPickerReducer>(args) {

    override val reducer = QuestPickerReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_quest_picker, null)

        view.questList.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.questList.adapter = QuestAdapter(listOf(
            QuestViewModel("", "Run 3 km", R.color.md_green_500, Ionicons.Icon.ion_android_clipboard, false, false),
            QuestViewModel("", "Eat every day", R.color.md_red_500, Ionicons.Icon.ion_clipboard, true, false),
            QuestViewModel("", "Read", R.color.md_blue_500, Ionicons.Icon.ion_clipboard, false, true)
        ))
        return view
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