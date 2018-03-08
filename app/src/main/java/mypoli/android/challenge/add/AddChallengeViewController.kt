package mypoli.android.challenge.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mypoli.android.R
import mypoli.android.common.redux.android.ReduxViewController

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/8/18.
 */
class AddChallengeViewController(args: Bundle?) :
    ReduxViewController<AddChallengeAction, AddChallengeViewState, AddChallengeReducer>(args) {

    override val reducer = AddChallengeReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_add_challenge, container, false)
        return view
    }

    override fun render(state: AddChallengeViewState, view: View) {
    }
}