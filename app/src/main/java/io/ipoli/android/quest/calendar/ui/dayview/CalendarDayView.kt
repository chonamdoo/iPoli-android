package io.ipoli.android.quest.calendar.ui.dayview

import android.content.Context
import android.content.res.Resources
import android.database.DataSetObserver
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import io.ipoli.android.R
import io.ipoli.android.common.datetime.Time
import kotlinx.android.synthetic.main.calendar_hour_cell.view.*
import kotlinx.android.synthetic.main.view_calendar_day.view.*
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 9/2/17.
 */
class CalendarDayView : FrameLayout {

    sealed class Event {
        object Click : Event()
        data class Move(val y: Int) : Event()
    }

    sealed class State(open val yPosition: Int?, open val duration: Int?) {
        object View : State(null, null)
        data class MoveQuest(override val yPosition: Int, override val duration: Int) : State(yPosition, duration)
    }

    interface Consumer {
        fun consume(state: State)
    }

    class FSM(initialState: State, private val consumer: Consumer) {

        interface Action<in E : Event> {
            fun execute(state: State, event: E): State
        }

        private var currentState: State = initialState
        private val actions = mutableMapOf<Pair<KClass<*>, KClass<*>>, Action<*>>()

        init {
            consumer.consume(currentState)
        }

        fun <S : State, E : Event> transition(given: KClass<S>, on: KClass<E>, execute: (state: State, event: E) -> State) {
            val a = object : Action<E> {
                override fun execute(state: State, event: E): State {
                    return execute(state, event)
                }
            }
            actions[Pair(given, on)] = a
        }

        fun <E : Event> fire(event: E) {
            val actionKey = Pair(currentState::class, event::class)
            @Suppress("UNCHECKED_CAST")
            val a = actions[actionKey] as Action<E>
            currentState = a.execute(currentState, event)
            consumer.consume(currentState)
        }
    }

    private var dragView: View? = null

    private val fsm: FSM = FSM(State.View,
        object : Consumer {
            override fun consume(state: State) {
                when (state) {
                    is State.MoveQuest -> {
                        dragView?.setTopPosition(timeToPosition(positionToTimeMapper.timeAt(state.yPosition.toFloat(), 5)))
                    }
                }
                Timber.d("Consuming " + state)
            }

        })

    private val MIN_EVENT_DURATION = 10
    private val MAX_EVENT_DURATION = Time.h2Min(4)
    private var hourHeight: Float = 0f
    private var minuteHeight: Float = 0f
    private lateinit var dragImage: Drawable
    private var dragImageSize: Int = toPx(16)
    private val adapterViews = mutableListOf<View>()

    private lateinit var editModeBackground: View
    private lateinit var topDragView: View
    private lateinit var bottomDragView: View
    private lateinit var positionToTimeMapper: PositionToTimeMapper

    private var scheduledEventsAdapter: ScheduledEventsAdapter<*>? = null
    private var unscheduledEventsAdapter: UnscheduledEventsAdapter<*>? = null

    private val dataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            refreshEventsFromAdapter()
        }

        override fun onInvalidated() {
            removeAllViews()
        }
    }

    constructor(context: Context) : super(context) {
        initUi(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initUi(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initUi(attrs, defStyleAttr)
    }

    private fun initUi(attrs: AttributeSet?, defStyleAttr: Int) {
        setMainLayout()
        fetchStyleAttributes(attrs, defStyleAttr)
        val screenHeight = getScreenHeight()
        hourHeight = screenHeight / 6f
        minuteHeight = hourHeight / 60f

        positionToTimeMapper = PositionToTimeMapper(minuteHeight)

        setupScroll()
        setupHourCells()
        setupEditBackgroundView()
        setupUnscheduledQuests()

        topDragView = addDragView()
        bottomDragView = addDragView()

        fsm.transition(State.View::class, Event.Move::class, { s, e ->
            State.MoveQuest(e.y, 20)
        })

        fsm.transition(State.MoveQuest::class, Event.Move::class, { s, e ->
            State.MoveQuest(e.y, 20)
        })
    }

    private fun setupScroll() {
        scrollView.isVerticalScrollBarEnabled = false
    }

    private fun setMainLayout() {
        LayoutInflater.from(context).inflate(R.layout.view_calendar_day, this, true)
    }

    private fun fetchStyleAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarDayView, defStyleAttr, 0)
            dragImage = a.getDrawable(R.styleable.CalendarDayView_dragImage)
            dragImageSize = a.getDimensionPixelSize(R.styleable.CalendarDayView_dragImageSize, dragImageSize)
            a.recycle()
        }
    }

    private fun setupEditBackgroundView() {
        editModeBackground = View(context)
        editModeBackground.layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        editModeBackground.setBackgroundResource(R.color.md_dark_text_12)
        editModeBackground.visibility = View.GONE
        addView(editModeBackground)
    }

    private fun setupHourCells() {
        for (hour in 0..23) {
            val hourView = LayoutInflater.from(context).inflate(R.layout.calendar_hour_cell, this, false)
            if (hour > 0) {
                hourView.timeLabel.text = hour.toString() + ":00"
            }
            val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, hourHeight.toInt())
            layoutParams.topMargin = (hour * hourHeight).toInt()
            hourView.layoutParams = layoutParams
            eventContainer.addView(hourView)
        }
    }

    private fun addDragView(): View {
        val view = ImageView(context)
        view.layoutParams = LayoutParams(dragImageSize, dragImageSize)
        view.setImageDrawable(dragImage)
        view.visibility = View.GONE
        addView(view)
        return view
    }

    private fun setupUnscheduledQuests() {
        unscheduledQuests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        unscheduledQuests.isNestedScrollingEnabled = false
    }

    fun setUnscheduledQuestsAdapter(adapter: UnscheduledEventsAdapter<*>) {
        unscheduledEventsAdapter = adapter
        unscheduledQuests.adapter = adapter
    }

    fun setScheduledEventsAdapter(adapter: ScheduledEventsAdapter<*>) {
        this.scheduledEventsAdapter?.unregisterDataSetObserver(dataSetObserver)
        this.scheduledEventsAdapter = adapter
        this.scheduledEventsAdapter?.registerDataSetObserver(dataSetObserver)
        addEventsFromAdapter()
    }

    private fun addEventsFromAdapter() {
//        removeAllViews()
        val a = scheduledEventsAdapter!!
        for (i in 0 until a.count) {
            val adapterView = a.getView(i, null, eventContainer)
            val event = a.getItem(i)
            adapterView.setPositionAndHeight(
                event.startMinute * minuteHeight,
                (event.duration * minuteHeight).toInt())
            adapterViews.add(i, adapterView)
            eventContainer.addView(adapterView)
        }
    }

    private fun refreshEventsFromAdapter() {
        val a = scheduledEventsAdapter!!
        val eventsInViewCount = childCount
        val eventsInAdapterCount = a.count
        val reuseCount = Math.min(eventsInViewCount, eventsInAdapterCount)

        for (i in 0 until reuseCount) {
            a.getView(i, getChildAt(i), eventContainer)
        }

        if (eventsInViewCount < eventsInAdapterCount) {
            for (i in eventsInViewCount until eventsInAdapterCount) {
                eventContainer.addView(a.getView(i, null, eventContainer), i)
            }
        } else if (eventsInViewCount > eventsInAdapterCount) {
            removeViews(eventsInAdapterCount, eventsInViewCount)
        }
    }

    fun scheduleEvent(adapterView: View) {
        scheduledEventsAdapter?.onStartEdit(adapterView)
        interceptTouch = true
        val dragView = addAndPositionDragView(adapterView)
        dragView.post {
            this.dragView = dragView
            setupDragViews(dragView)
            editModeBackground.bringToFront()
            showViews(editModeBackground, topDragView, bottomDragView)
            setDragListener(dragView, adapterView.height / 2)
        }

    }

    private fun addAndPositionDragView(adapterView: View): View {
        TransitionManager.beginDelayedTransition(this)
        val dragView = LayoutInflater.from(context).inflate(R.layout.item_calendar_drag, this, false)
        dragView.setPositionAndHeight(adapterView.top.toFloat(), getMinutesHeight(15).toInt())
        addView(dragView)
        return dragView
    }

    private fun setDragListener(dragView: View, initialOffset: Int) {
        var startOffset = initialOffset
        setOnTouchListener { _, e ->

            val topVR = Rect()
            topDragView.getGlobalVisibleRect(topVR)

            val action = e.actionMasked
            if (action == MotionEvent.ACTION_DOWN) {

                startOffset = e.rawY.toInt() - dragView.topLocationOnScreen
                val visibleRect = Rect()
                dragView.getGlobalVisibleRect(visibleRect)

                if (topVR.contains(e.rawX.toInt(), e.rawY.toInt())) {
                    Timber.d("Topeka")
                } else if (!visibleRect.contains(e.rawX.toInt(), e.rawY.toInt())) {
                    stopEditMode(dragView)
                } else {

                }
            }
            if (action == MotionEvent.ACTION_MOVE) {

//                if (topVR.contains(e.rawX.toInt(), e.rawY.toInt())) {
//                    Timber.d("Topeka")

//                val dy = e.rawY - topLocationOnScreen // - startOffset
//
//                val topPosition = timeToPosition(positionToTimeMapper.timeAt(dy, 5))
//
//                val bottomPos = timeToPosition(positionToTimeMapper.timeAt(dragView.bottom.toFloat()))
//
//                val heightPx = (bottomPos - topPosition).toInt()
//                if (isValidHeightForEvent(heightPx)) {
//                    topDragView.setTopPosition(topPosition - dragImageSize / 2)
//                    dragView.setPositionAndHeight(topPosition, heightPx)
//                }

//                } else {
//                    Timber.d("Not timber")
                val dy = e.rawY - topLocationOnScreen - startOffset
                fsm.fire(Event.Move(dy.toInt()))
//
//                    dragView.setTopPosition(timeToPosition(positionToTimeMapper.timeAt(dy, 5)))
//
//                    val calendarPosition = dragView.topLocationOnScreen - scrollView.topLocationOnScreen.toFloat()
//
//                    if (calendarPosition >= 0) {
//                        scheduledEventsAdapter?.onStartTimeChanged(dragView, positionToTimeMapper.timeAt(calendarPosition + scrollView.scrollY))
//                    }
//                    topDragView.setTopPosition(dragView.top.toFloat() - dragImageSize / 2)
//                    bottomDragView.setTopPosition(dragView.bottom.toFloat() - dragImageSize / 2)
//                }
            }

            if (action == MotionEvent.ACTION_UP) {
                setOnTouchListener(null)
                interceptTouch = false

                dragView.setOnTouchListener { _, motionEvent ->
                    Timber.d("Touching drag view")
                    true
                }
            }
            true
        }
    }

    private var interceptTouch = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return interceptTouch
    }

//    fun startEditMode(editView: View) {
//        editModeBackground.bringToFront()
//        setupEditView(editView)
//        setupDragViews(editView)
//        TransitionManager.beginDelayedTransition(this)
//        showViews(editModeBackground, topDragView, bottomDragView)
//        scheduledEventsAdapter?.onStartEdit(editView)
//    }

    private fun setupDragViews(dragView: View) {
        setupTopDragView(dragView)
        setupBottomDragView(dragView)
    }

    private fun setupBottomDragView(editView: View) {
        bottomDragView.elevation = editView.elevation
        bottomDragView.bringToFront()
        positionBottomDragView(editView)
        setBottomDragViewListener(bottomDragView, editView)
    }

    private fun setupTopDragView(editView: View) {
        topDragView.elevation = editView.elevation
        topDragView.bringToFront()
        positionTopDragView(editView)
        setTopDragViewListener(topDragView, editView)
    }

    private fun positionBottomDragView(editView: View) {
        val lp = bottomDragView.layoutParams as MarginLayoutParams
        lp.topMargin = editView.bottom - dragImageSize / 2
        lp.marginStart = editView.left + editView.width / 2 - dragImageSize / 2
        bottomDragView.layoutParams = lp
    }

    private fun positionTopDragView(editView: View) {
        val lp = topDragView.layoutParams as MarginLayoutParams
        lp.topMargin = editView.top - dragImageSize / 2
        lp.marginStart = editView.left + editView.width / 2 - dragImageSize / 2
        topDragView.layoutParams = lp
    }

    private fun stopEditMode(editView: View) {
        setOnTouchListener(null)
        interceptTouch = false
        editView.setTopPosition(getAdjustedYPosFor(editView, rangeLength = 5))
        TransitionManager.beginDelayedTransition(this)
        hideViews(editModeBackground, topDragView, bottomDragView)
        scheduledEventsAdapter?.onStopEdit(editView)
    }

    private fun setBottomDragViewListener(bottomDragView: View, editView: View) {
        var lastY = 0f
        bottomDragView.setOnTouchListener { _, e ->
            if (e.actionMasked == MotionEvent.ACTION_DOWN) {
                lastY = e.y
            }

            if (e.actionMasked == MotionEvent.ACTION_MOVE) {
                val dy = e.y - lastY
                val height = editView.height + dy.toInt()
                if (isValidHeightForEvent(height)) {
                    editView.changeHeight(height)
                    bottomDragView.changePosition(dy)
                    lastY = e.y
                }
            }

            true
        }
    }

    private fun setTopDragViewListener(topDragView: View, editView: View) {
        var lastY = 0f
        topDragView.setOnTouchListener { _, e ->
            if (e.actionMasked == MotionEvent.ACTION_DOWN) {
                lastY = e.y
            }

            if (e.actionMasked == MotionEvent.ACTION_MOVE) {
                val dy = e.y - lastY
                val height = editView.height - dy.toInt()
                if (isValidHeightForEvent(height)) {
                    editView.changePositionAndHeight(dy, height)
                    topDragView.changePosition(dy)

                    Timber.d("New start time " + positionToTimeMapper.timeAt((topDragView.layoutParams as MarginLayoutParams).topMargin.toFloat()))
                    lastY = e.y
                }
            }

            true
        }
    }

    private fun View.changePositionAndHeight(yDelta: Float, height: Int) =
        changeLayoutParams<MarginLayoutParams> {
            it.topMargin += yDelta.toInt()
            it.height = height
        }

    private fun View.setPositionAndHeight(yPosition: Float, height: Int) =
        changeLayoutParams<MarginLayoutParams> {
            it.topMargin = yPosition.toInt()
            it.height = height
        }

    private fun View.setTopPosition(yPosition: Float) =
        changeLayoutParams<MarginLayoutParams> { it.topMargin = yPosition.toInt() }

    private fun View.changePosition(yDelta: Float) =
        changePosition(yDelta.toInt())

    private fun View.changePosition(yDelta: Int) =
        changeLayoutParams<MarginLayoutParams> { it.topMargin += yDelta }

    private fun View.changeHeight(height: Int) =
        changeLayoutParams<MarginLayoutParams> { it.height = height }

    private fun <T : ViewGroup.LayoutParams> View.changeLayoutParams(cb: (layoutParams: T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val lp = layoutParams as T
        cb(lp)
        layoutParams = lp
    }

    private val View.topLocationOnScreen: Int
        get() {
            val location = IntArray(2)
            getLocationOnScreen(location)
            return location[1]
        }

    private fun isValidHeightForEvent(height: Int): Boolean =
        getMinutesFor(height) in MIN_EVENT_DURATION..MAX_EVENT_DURATION

    private fun getAdjustedYPosFor(view: View, rangeLength: Int): Float =
        timeToPosition(positionToTimeMapper.timeAt((view.layoutParams as MarginLayoutParams).topMargin.toFloat(), rangeLength))

    private fun getScreenHeight(): Int {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        return metrics.heightPixels
    }

    private fun timeToPosition(time: Time): Float =
        time.hours * hourHeight + getMinutesHeight(time.getMinutes())

    private fun getMinutesHeight(minutes: Int): Float =
        minuteHeight * minutes

    private fun getMinutesFor(height: Int): Int =
        (height / minuteHeight).toInt()

    private fun showViews(vararg views: View) =
        views.forEach { it.visibility = View.VISIBLE }

    private fun hideViews(vararg views: View) =
        views.forEach { it.visibility = View.GONE }

    private fun toPx(dp: Int): Int =
        (dp * Resources.getSystem().displayMetrics.density).toInt()
}