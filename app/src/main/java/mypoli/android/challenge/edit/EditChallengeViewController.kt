package mypoli.android.challenge.edit

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.controller_edit_challenge.view.*
import mypoli.android.R
import mypoli.android.challenge.edit.EditChallengeViewState.StateType.DATA_LOADED
import mypoli.android.common.redux.android.ReduxViewController
import mypoli.android.common.text.DateFormatter
import mypoli.android.common.view.*

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/12/18.
 */
class EditChallengeViewController(args : Bundle? = null) :
    ReduxViewController<EditChallengeAction, EditChallengeViewState, EditChallengeReducer>(args) {

    override val reducer = EditChallengeReducer

    private lateinit var challengeId: String

    constructor(
        challengeId: String
    ) : this() {
        this.challengeId = challengeId
    }

    override fun onCreateLoadAction() =
        EditChallengeAction.Load(challengeId)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.controller_edit_challenge, container, false)
        setToolbar(view.toolbar)
        toolbarTitle = ""
        view.toolbarTitle.text = stringRes(R.string.title_edit_challenge)
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        showBackButton()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_challenge_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                router.popCurrentController()
                true
            }
            R.id.actionSave -> {
//                dispatch(EditRepeatingQuestAction.Save)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun render(state: EditChallengeViewState, view: View) {
        when (state.type) {
            DATA_LOADED -> {
                view.challengeName.setText(state.name)
                if (state.motivation1.isNotEmpty()) {
                    view.challengeMotivation1Value.visibility = View.VISIBLE
                    view.challengeMotivation1Value.text = state.motivation1
                } else {
                    view.challengeMotivation1Value.visibility = View.GONE
                }
                if (state.motivation2.isNotEmpty()) {
                    view.challengeMotivation2Value.visibility = View.VISIBLE
                    view.challengeMotivation2Value.text = state.motivation2
                } else {
                    view.challengeMotivation2Value.visibility = View.GONE
                }
                if (state.motivation3.isNotEmpty()) {
                    view.challengeMotivation3Value.visibility = View.VISIBLE
                    view.challengeMotivation3Value.text = state.motivation3
                } else {
                    view.challengeMotivation3Value.visibility = View.GONE
                }
                view.challengeEndDateValue.text = state.formattedDate
                view.challengeDifficultyValue.text = state.difficultyText
                view.challengeIconIcon.setImageDrawable(state.iconDrawable)
                colorLayout(view, state)
            }
        }
    }

    private fun colorLayout(
        view: View,
        state: EditChallengeViewState
    ) {
        val color500 = colorRes(state.color500)
        val color700 = colorRes(state.color700)
        view.appbar.setBackgroundColor(color500)
        view.toolbar.setBackgroundColor(color500)
        view.toolbarCollapsingContainer.setContentScrimColor(color500)
        activity?.window?.navigationBarColor = color500
        activity?.window?.statusBarColor = color700
    }

    private val EditChallengeViewState.color500: Int
        get() = color.androidColor.color500

    private val EditChallengeViewState.color700: Int
        get() = color.androidColor.color700

    private val EditChallengeViewState.formattedDate: String
        get() = DateFormatter.format(view!!.context, end)

    private val EditChallengeViewState.difficultyText: String
        get() = view!!.resources.getStringArray(R.array.difficulties)[difficulty.ordinal]

    private val EditChallengeViewState.iconDrawable: Drawable
        get() =
            if (icon == null) {
                ContextCompat.getDrawable(view!!.context, R.drawable.ic_icon_black_24dp)!!
            } else {
                val androidIcon = icon.androidIcon
                IconicsDrawable(view!!.context)
                    .icon(androidIcon.icon)
                    .colorRes(androidIcon.color)
                    .sizeDp(24)
            }
}