package mypoli.android.challenge.list

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.ionicons_typeface_library.Ionicons
import kotlinx.android.synthetic.main.controller_challenge_list.view.*
import kotlinx.android.synthetic.main.item_challenge.view.*
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeViewController
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.view.*
import mypoli.android.common.view.recyclerview.SimpleViewHolder

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/05/2018.
 */
class ChallengeListViewController(args: Bundle? = null) :
    ReduxViewController<ChallengeListAction, ChallengeListViewState, ChallengeListReducer>(
        args
    ) {

    override val reducer = ChallengeListReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.controller_challenge_list, container, false
        )
        view.challengeList.layoutManager =
            LinearLayoutManager(container.context, LinearLayoutManager.VERTICAL, false)
        view.challengeList.adapter = ChallengeAdapter()

        view.addChallenge.setOnClickListener {
            val handler = FadeChangeHandler()
            rootRouter.pushController(
                RouterTransaction.with(AddChallengeViewController())
                    .pushChangeHandler(handler)
                    .popChangeHandler(handler)
            )
        }
        return view
    }

    override fun onCreateLoadAction() = ChallengeListAction.Load

    override fun onAttach(view: View) {
        super.onAttach(view)
        toolbarTitle = stringRes(R.string.drawer_challenges)
    }

    override fun render(state: ChallengeListViewState, view: View) {
        when (state) {
            is ChallengeListViewState.Changed -> {
                (view.challengeList.adapter as ChallengeAdapter).updateAll(
                    state.toViewModels(
                        view.context
                    )
                )
            }
        }
    }

    data class ChallengeViewModel(
        val id: String,
        val name: String,
        val icon: IIcon,
        @ColorRes val color: Int
    )

    inner class ChallengeAdapter(private var viewModels: List<ChallengeViewModel> = listOf()) :
        RecyclerView.Adapter<SimpleViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SimpleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_challenge,
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
            val vm = viewModels[position]
            val view = holder.itemView

            view.cName.text = vm.name

            view.cIcon.backgroundTintList =
                ColorStateList.valueOf(colorRes(vm.color))
            view.cIcon.setImageDrawable(
                IconicsDrawable(view.context)
                    .icon(vm.icon)
                    .colorRes(R.color.md_white)
                    .sizeDp(24)
            )
        }

        override fun getItemCount() = viewModels.size

        fun updateAll(viewModels: List<ChallengeViewModel>) {
            this.viewModels = viewModels
            notifyDataSetChanged()
        }
    }

    private fun ChallengeListViewState.Changed.toViewModels(context: Context): List<ChallengeListViewController.ChallengeViewModel> {
        return challenges.map {
            ChallengeViewModel(
                id = it.id,
                name = it.name,
                color = AndroidColor.valueOf(it.color.name).color500,
                icon = it.icon?.let { AndroidIcon.valueOf(it.name).icon }
                    ?: Ionicons.Icon.ion_android_clipboard
            )
        }
    }

}