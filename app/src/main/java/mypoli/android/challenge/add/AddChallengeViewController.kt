package mypoli.android.challenge.add

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import kotlinx.android.synthetic.main.controller_add_challenge.view.*
import mypoli.android.R
import mypoli.android.challenge.QuestPickerViewController
import mypoli.android.common.redux.android.ReduxViewController
import timber.log.Timber

/**
 * Created by Polina Zhelyazkova <polina@mypoli.fun>
 * on 3/8/18.
 */
class AddChallengeViewController(args: Bundle? = null) :
    ReduxViewController<AddChallengeAction, AddChallengeViewState, AddChallengeReducer>(args) {

    override val reducer = AddChallengeReducer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.controller_add_challenge, container, false)

        view.pager.adapter = AddChallengePagerAdapter(this)

        return view
    }

    override fun render(state: AddChallengeViewState, view: View) {

    }

    class AddChallengePagerAdapter(
        controller: Controller
    ) :
        RouterPagerAdapter(controller) {
        override fun configureRouter(router: Router, position: Int) {
            Timber.d("AAA position $position")
            if (!router.hasRootController()) {
                Timber.d("AAA position false")
                when (position) {
                    0 -> router.setRoot(RouterTransaction.with(AddChallengeNameViewController()))
                    1 -> router.setRoot(RouterTransaction.with(QuestPickerViewController("")))
                }
            }
        }

        override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

        override fun getCount() = 2
    }
}