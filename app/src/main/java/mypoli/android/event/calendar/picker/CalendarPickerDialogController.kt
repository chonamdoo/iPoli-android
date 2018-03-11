package mypoli.android.event.calendar.picker

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_calendar_picker.view.*
import mypoli.android.R
import mypoli.android.common.view.ReduxDialogController
import mypoli.android.common.view.recyclerview.SimpleViewHolder

/**
 * Created by Venelin Valkov <venelin@mypoli.fun>
 * on 03/11/2018.
 */
class CalendarPickerDialogController :
    ReduxDialogController<CalendarPickerAction, CalendarPickerViewState, CalendarPickerReducer> {
    override val reducer = CalendarPickerReducer

    private lateinit var pickedCalendarsListener: (List<Int>) -> Unit

    constructor(args: Bundle? = null) : super(args)

    constructor(
        pickedCalendarsListener: (List<Int>) -> Unit
    ) : this() {
        this.pickedCalendarsListener = pickedCalendarsListener
    }

    override fun onCreateLoadAction() = CalendarPickerAction.Load

    override fun onCreateDialog(
        dialogBuilder: AlertDialog.Builder,
        contentView: View,
        savedViewState: Bundle?
    ): AlertDialog {
        return dialogBuilder
            .setPositiveButton("OK") { _, _ -> pickedCalendarsListener(listOf()) }
            .create()
    }

    override fun onCreateContentView(inflater: LayoutInflater, savedViewState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_calendar_picker, null)

        view.calendarList.layoutManager = LinearLayoutManager(activity!!)
        view.calendarList.setHasFixedSize(true)

        return view
    }

    override fun render(state: CalendarPickerViewState, view: View) {
    }

    data class CalendarViewModel(val name: String, val color: Int, val isSelected: Boolean)

    inner class CalendarAdapter(private var viewModels: List<CalendarViewModel> = listOf()) :
        RecyclerView.Adapter<SimpleViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SimpleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_calendar_picker,
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {

        }

        fun updateAll(viewModels: List<CalendarViewModel>) {
            this.viewModels = viewModels
            notifyDataSetChanged()
        }

        override fun getItemCount() = viewModels.size
    }
}