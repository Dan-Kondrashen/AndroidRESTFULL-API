package ru.kondrashin.myworkingproject

import android.app.Dialog
import android.content.DialogInterface

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.CollationKey
import java.util.*

class DatePickerFragment : DialogFragment() {
    companion object {
        private const val ARG_DATE = "date"
        const val EXTRA_DATE = "ru.kondrashin.myworkingproject.date"

        fun newInstance(date: String?) = DatePickerFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_DATE, date)
            }
        }

    }
    private lateinit var datePicker: DatePicker



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE)
        val calendar = Calendar.getInstance()
        calendar.time = date as Date
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val view = layoutInflater.inflate(R.layout.dialog_date, null)

        datePicker = view.findViewById(R.id.dialog_date_date_picker)
        datePicker.init(year, month, day, null)
        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .setTitle(R.string.date_start_title)
            .setPositiveButton(android.R.string.ok) { dialogInterface: DialogInterface, i: Int ->
                val date = GregorianCalendar(datePicker.year, datePicker.month, datePicker.dayOfMonth).time
                val date2 = DateFormat.format("Год: yyyy Месяц: MM День: dd", date)
                sendResult(BlogFragment.REQUEST_DATE, date)
            }

            .create()


    }
    private fun sendResult(requestKey: String, date: Date) {
        setFragmentResult(
            requestKey,
            bundleOf(EXTRA_DATE to date)
        )
    }


}