package io.ipoli.android.quest.calendar.addquest

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.mikepenz.entypo_typeface_library.Entypo
import com.mikepenz.iconics.IconicsDrawable
import io.ipoli.android.R
import io.ipoli.android.common.ViewUtils
import io.ipoli.android.common.datetime.Time
import io.ipoli.android.common.di.ControllerModule
import io.ipoli.android.common.mvi.MviViewController
import io.ipoli.android.common.mvi.ViewStateRenderer
import io.ipoli.android.common.view.*
import io.ipoli.android.quest.calendar.addquest.StateType.*
import io.ipoli.android.reminder.view.picker.ReminderPickerDialogController
import io.ipoli.android.reminder.view.picker.ReminderViewModel
import kotlinx.android.synthetic.main.controller_add_quest.view.*
import org.threeten.bp.LocalDate
import space.traversal.kapsule.Injects
import space.traversal.kapsule.required

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 11/2/17.
 */
class AddQuestViewController(args: Bundle? = null) :
    MviViewController<AddQuestViewState, AddQuestViewController, AddQuestPresenter, AddQuestIntent>(args),
    Injects<ControllerModule>,
    ViewStateRenderer<AddQuestViewState> {

    private val presenter by required { addQuestPresenter }

    override fun createPresenter() = presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = inflater.inflate(R.layout.controller_add_quest, container, false)

        view.questName.setOnEditTextImeBackListener(object : EditTextImeBackListener {
            override fun onImeBack(ctrl: EditTextBackEvent, text: String) {
                close()
            }
        })

        view.questName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                send(SaveQuestIntent(view.questName.text.toString()))
            }
            true
        }

        view.duration.setImageDrawable(IconicsDrawable(view.context)
            .icon(Entypo.Icon.ent_hour_glass)
            .colorRes(R.color.md_black)
            .alpha(122)
            .sizeDp(22))

        view.scheduleDate.setOnClickListener {
            send(PickDateIntent)
        }

        view.startTime.setOnClickListener {
            send(PickTimeIntent)
        }

        view.duration.setOnClickListener {
            send(PickDurationIntent)
        }

        view.color.setOnClickListener {
            send(PickColorIntent)
        }

        view.icon.setOnClickListener {
            send(PickIconIntent)
        }

        view.reminder.setOnClickListener {
            send(PickReminderIntent)
        }

        view.done.setOnClickListener {
            send(SaveQuestIntent(view.questName.text.toString()))
        }

        return view
    }

    override fun render(state: AddQuestViewState, view: View) {
        colorSelectedIcons(state, view)

        when (state.type) {
            PICK_DATE -> {
                val date = state.date ?: LocalDate.now()
                DatePickerDialog(view.context, R.style.Theme_iPoli_AlertDialog,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        send(DatePickedIntent(year, month + 1, dayOfMonth))
                    }, date.year, date.month.value - 1, date.dayOfMonth).show()
            }

            PICK_TIME -> {
                val startTime = state.time ?: Time.now()
                val dialog = TimePickerDialog(view.context,
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        send(TimePickedIntent(Time.at(hour, minute)))
                    }, startTime.hours, startTime.getMinutes(), false)
                dialog.setButton(Dialog.BUTTON_NEUTRAL, view.context.getString(R.string.do_not_know), { _, _ ->
                    send(TimePickedIntent(null))
                })
                dialog.show()
            }

            PICK_DURATION ->
                DurationPickerDialogController(object : DurationPickerDialogController.DurationPickedListener {
                    override fun onDurationPicked(minutes: Int) {
                        send(DurationPickedIntent(minutes))
                    }

                }, state.duration).showDialog(router, "pick_duration_tag")

            PICK_COLOR ->
                ColorPickerDialogController(object : ColorPickerDialogController.ColorPickedListener {
                    override fun onColorPicked(color: AndroidColor) {
                        send(ColorPickedIntent(color))
                    }

                }, state.color).showDialog(router, "pick_color_tag")

            PICK_ICON ->
                IconPickerDialogController({ icon ->
                    send(IconPickedIntent(icon))
                }, state.icon).showDialog(router, "pick_icon_tag")

            PICK_REMINDER ->
                ReminderPickerDialogController(object : ReminderPickerDialogController.ReminderPickedListener {
                    override fun onReminderPicked(reminder: ReminderViewModel?) {
                        send(ReminderPickedIntent(reminder))
                    }
                }, state.reminder).showDialog(router, "pick_reminder_tag")

            VALIDATION_ERROR_EMPTY_NAME ->
                view.questName.error = "Think of a name"

            QUEST_SAVED -> {
                resetForm(view)
            }

            DEFAULT -> {
            }
        }
    }

    private fun resetForm(view: View) {
        view.questName.setText("")
        listOf<ImageView>(
            view.scheduleDate,
            view.startTime,
            view.duration,
            view.color,
            view.icon,
            view.reminder)
            .forEach { resetSelectedIcon(it) }
    }

    private fun colorSelectedIcons(state: AddQuestViewState, view: View) {
        state.date?.let {
            colorSelectedIcon(view.scheduleDate)
        }

        if (state.time != null) {
            colorSelectedIcon(view.startTime)
        } else {
            view.startTime.drawable.setTintList(null)
        }

        state.duration?.let {
            colorSelectedIcon(view.duration)
        }

        state.color?.let {
            colorSelectedIcon(view.color)
        }

        state.icon?.let {
            colorSelectedIcon(view.icon)
        }

        if (state.reminder != null) {
            colorSelectedIcon(view.reminder)
        } else {
            view.reminder.drawable.setTintList(null)
        }
    }

    private fun resetSelectedIcon(view: ImageView) {
        // 0.48 = 122
        view.drawable.alpha = 122
        view.drawable.setTintList(null)
    }

    private fun colorSelectedIcon(view: ImageView) {
        view.drawable.alpha = 255
        view.drawable.setTint(colorRes(R.color.colorAccent))
    }

    override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeEnded(changeHandler, changeType)
        view!!.questName.requestFocus()
        ViewUtils.showKeyboard(view!!.questName.context, view!!.questName)
    }

    private fun close() {
        resetForm(view!!)
        router.popController(this)
    }

}