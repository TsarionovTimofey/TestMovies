package com.example.testmovies.datamask

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.MutableLiveData

class DateInputMask(val input: EditText) {
    var block: String = "";
    fun listen() {
        input.addTextChangedListener(mDateEntryWatcher)
    }

    private val liveData: MutableLiveData<String> = MutableLiveData();
    fun getLiveData(): MutableLiveData<String> {
        return liveData
    }

    private val mDateEntryWatcher = object : TextWatcher {

        var edited = false
        val dividerCharacter = " / "

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }

            var working = getEditText()

            working = manageDateDivider(working, 4, start, before)
            working = manageDateDivider(working, 9, start, before)

            edited = true
            input.setText(working)
            input.setSelection(input.text.length)

            if (working.length == 14 && block != working) {
                block = working
                liveData.value = working
            }
            if (working.isEmpty() && liveData.value != "") {
                liveData.value = ""
                block = ""
            }
        }

        private fun manageDateDivider(working: String, position: Int, start: Int, before: Int): String {
            if (working.length == position) {
                return if (before <= position && start < position)
                    working + dividerCharacter
                else
                    working.dropLast(1)
            }
            return working
        }

        private fun getEditText(): String {
            return if (input.text.length >= 14)
                input.text.toString().substring(0, 14)
            else
                input.text.toString()
        }

        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
}