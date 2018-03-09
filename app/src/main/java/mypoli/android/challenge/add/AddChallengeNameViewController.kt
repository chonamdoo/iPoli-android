package mypoli.android.challenge.add

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import kotlinx.android.synthetic.main.controller_add_challenge_name.view.*
import kotlinx.android.synthetic.main.view_no_elevation_toolbar.view.*
import mypoli.android.R
import mypoli.android.challenge.add.AddChallengeNameViewState.StateType.*
import mypoli.android.challenge.entity.Challenge
import mypoli.android.common.AppState
import mypoli.android.common.BaseViewStateReducer
import mypoli.android.common.mvi.ViewState
import mypoli.android.common.redux.Action
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.view.*
import mypoli.android.quest.Color
import mypoli.android.quest.Icon

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/8/18.
 */

sealed class AddChallengeNameAction : Action {
    data class ChangeColor(val color: Color) : AddChallengeNameAction()
    data class ChangeIcon(val icon: Icon?) : AddChallengeNameAction()
    data class ChangeDifficulty(val position: Int) : AddChallengeNameAction()
    data class Next(val name: String) : AddChallengeNameAction()
}

object AddChallengeNameReducer : BaseViewStateReducer<AddChallengeNameViewState>() {

    override val stateKey = key<AddChallengeNameViewState>()


    override fun reduce(
        state: AppState,
        subState: AddChallengeNameViewState,
        action: Action
    ) =
        when (action) {
            is AddChallengeNameAction.ChangeColor ->
                subState.copy(
                    type = COLOR_CHANGED,
                    color = action.color
                )

            is AddChallengeNameAction.ChangeIcon ->
                subState.copy(
                    type = ICON_CHANGED,
                    icon = action.icon
                )

            is AddChallengeNameAction.ChangeDifficulty ->
                subState.copy(
                    type = DIFFICULTY_CHANGED,
                    difficulty = Challenge.Difficulty.values()[action.position]
                )
            else -> subState
    }

    override fun defaultState() =
        AddChallengeNameViewState(
            type = INITIAL,
            name = "",
            color = Color.GREEN,
            icon = null,
            difficulty = Challenge.Difficulty.NORMAL
        )
}


data class AddChallengeNameViewState(
    val type: AddChallengeNameViewState.StateType,
    val name: String,
    val color: Color,
    val icon: Icon?,
    val difficulty: Challenge.Difficulty
) : ViewState {
    enum class StateType {
        INITIAL,
        COLOR_CHANGED,
        ICON_CHANGED,
        DIFFICULTY_CHANGED
    }
}

class AddChallengeNameViewController(args: Bundle? = null) :
    ReduxViewController<AddChallengeNameAction, AddChallengeNameViewState, AddChallengeNameReducer>(
        args
    ) {
    override val reducer = AddChallengeNameReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_add_challenge_name, container, false)
        setToolbar(view.toolbar)
        toolbarTitle = "New challenge"

        view.challengeDifficulty.background.setColorFilter(
            colorRes(R.color.md_white),
            PorterDuff.Mode.SRC_ATOP
        )
        view.challengeDifficulty.adapter = ArrayAdapter<String>(
            view.context,
            R.layout.item_add_challenge_difficulty_item,
            R.id.spinnerItemId,
            view.resources.getStringArray(R.array.difficulties)
        )
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        showBackButton()
    }

    override fun render(state: AddChallengeNameViewState, view: View) = when (state.type) {
        AddChallengeNameViewState.StateType.INITIAL -> {
            renderColor(view, state)
            renderIcon(view, state)

            view.challengeDifficulty.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        dispatch(AddChallengeNameAction.ChangeDifficulty(position))
                    }

                }
            
            view.challengeNext.dispatchOnClick(AddChallengeNameAction.Next(view.challengeName.text.toString()))
        }

        COLOR_CHANGED -> {
            renderColor(view, state)
        }

        ICON_CHANGED -> {
            renderIcon(view, state)
        }

        DIFFICULTY_CHANGED -> {
        }
    }

    private fun renderIcon(
        view: View,
        state: AddChallengeNameViewState
    ) {
        view.challengeIcon.setCompoundDrawablesWithIntrinsicBounds(
            IconicsDrawable(view.context)
                .icon(state.iicon)
                .colorRes(R.color.md_white)
                .sizeDp(24),
            null, null, null
        )

        view.challengeIcon.setOnClickListener {
            IconPickerDialogController({ icon ->
                dispatch(AddChallengeNameAction.ChangeIcon(icon))
            }, state.icon?.androidIcon).showDialog(
                router,
                "pick_icon_tag"
            )

        }
    }

    private fun renderColor(
        view: View,
        state: AddChallengeNameViewState
    ) {
        colorLayout(view, state)
        view.challengeColor.setOnClickListener {
            ColorPickerDialogController({
                dispatch(AddChallengeNameAction.ChangeColor(it.color))
            }, state.color.androidColor).showDialog(
                router,
                "pick_color_tag"
            )
        }
    }

    private fun colorLayout(
        view: View,
        state: AddChallengeNameViewState
    ) {
        val color500 = colorRes(state.color.androidColor.color500)
        val color700 = colorRes(state.color.androidColor.color700)
        view.appbar.setBackgroundColor(color500)
        view.toolbar.setBackgroundColor(color500)
        view.rootContainer.setBackgroundColor(color500)
        activity?.window?.navigationBarColor = color500
        activity?.window?.statusBarColor = color700
        view.challengeDifficulty.setPopupBackgroundResource(state.color.androidColor.color500)

    }

    private val AddChallengeNameViewState.iicon: IIcon
        get() = icon?.androidIcon?.icon ?: GoogleMaterial.Icon.gmd_local_florist

}