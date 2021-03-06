package com.example.tasks.view

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.tasks.R
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.model.TaskModel
import com.example.tasks.viewmodel.TaskFormViewModel
import kotlinx.android.synthetic.main.activity_register.button_save
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var mViewModel: TaskFormViewModel

    private var mTaskId = 0
    private var mPriorityListId: MutableList<Int> = arrayListOf()
    private val mDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        mViewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)

        // Inicializa eventos
        listeners()
        observe()

        listPriorities()

        loadDataFromActivity()
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras

        if (bundle != null) {
            mTaskId = bundle.getInt(TaskConstants.BUNDLE.TASKID)
            mViewModel.load(mTaskId)
        }

        button_save.text = getString(R.string.update_task)
    }

    private fun listPriorities() {
        mViewModel.listPriorities()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_save -> {
                handleSave()
            }

            R.id.button_date -> {
                showDatePicker()
            }
        }
    }

    private fun handleSave() {
        val newTask = TaskModel().apply {
            this.id = mTaskId
            this.description = edit_description.text.toString()
            this.dueDate = button_date.text.toString()
            this.complete = check_complete.isChecked
            this.priorityId = mPriorityListId[spinner_priority.selectedItemPosition]
        }

        mViewModel.store(newTask)
    }

    private fun showDatePicker() {
        val calender = Calendar.getInstance()
        DatePickerDialog(
            this,
            this,
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun observe() {
        mViewModel.priorityList.observe(this, androidx.lifecycle.Observer {
            /**
             * Fill the list with de Iterator data and then set into the Spinner by an adapter
             */
            val list: MutableList<String> = arrayListOf()

            for (item in it) {
                list.add(item.description)
                mPriorityListId.add(item.id)
            }

            spinner_priority.adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        })

        mViewModel.validation.observe(this, androidx.lifecycle.Observer {
            if (it.sucess()) {
                if (mTaskId == 0) {
                    Toast.makeText(this, getString(R.string.task_created), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, getString(R.string.task_updated), Toast.LENGTH_SHORT)
                        .show()
                }
                finish()
            } else {
                Toast.makeText(this, it.failure(), Toast.LENGTH_SHORT).show()
            }
        })

        mViewModel.taskData.observe(this, androidx.lifecycle.Observer {
            // Set description
            edit_description.setText(it.description)
            // Set Complete Checkbox
            check_complete.isChecked = it.complete
            // Set formated Date
            val deformatedDate = SimpleDateFormat("yyyyy-MM-dd").parse(it.dueDate)
            button_date.text = mDateFormat.format(deformatedDate)
            // Set formated Date
            spinner_priority.setSelection(getIndex(it.priorityId))
        })
    }

    // Find the correct position in the spinner of the current task id
    private fun getIndex(priorityId: Int): Int {
        var index = 0
        for (i in 0 until mPriorityListId.count()) {
            if (mPriorityListId[i] == priorityId) {
                index = i
                break
            }
        }
        return index
    }

    private fun listeners() {
        button_save.setOnClickListener(this)
        button_date.setOnClickListener(this)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calender = Calendar.getInstance()
        calender.set(year, month, dayOfMonth)

        button_date.text = mDateFormat.format(calender.time)
    }

}
