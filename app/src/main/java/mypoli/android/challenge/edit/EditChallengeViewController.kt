package mypoli.android.challenge.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mypoli.android.R
import mypoli.android.common.redux.android.ReduxViewController

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/12/18.
 */
class EditChallengeViewController(args : Bundle? = null) :
    ReduxViewController<EditChallengeAction, EditChallengeViewState, EditChallengeReducer>(args) {
    override val reducer = EditChallengeReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_edit_challenge, container, false)
        return view
    }

    override fun render(state: EditChallengeViewState, view: View) {
    }


}