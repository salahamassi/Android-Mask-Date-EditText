package com.msa.dateedittext

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
// TODO write comments
/**
 *
 */
class DateEditText : EditText {


    companion object {
        const val ddMMyyyyDateFormat = "ddMMyyyy"
        const val MMyyDateFormat = "MMyy"
        const val minus = "-"
        const val slash = "/"
    }


    var dividerCharacter = "/"
    var dateFormat = ddMMyyyyDateFormat
    var maxDate: Date? = null
    var minDate: Date? = null
    var dateLength = 10

    private val firstDividerPosition = 2
    private val nextDividerPosition = 5
    private var edited = false


    constructor(context: Context?) : super(context) {
        initDateEditText()

    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initDateEditText(attrs = attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initDateEditText(attrs = attrs)
    }

    /**
     *
     */
    private fun initDateEditText(attrs: AttributeSet? = null) {
        isCursorVisible = false
        setOnClickListener { setSelection(text.length) }
        inputType = InputType.TYPE_CLASS_NUMBER
        if (attrs == null) { return }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DateEditText, 0, 0)
        getDividerCharacter(typedArray)
        getDateFormat(typedArray)
        getMaxDate(typedArray)
        getMinDate(typedArray)
        typedArray.recycle()
    }

    /**
     *
     */
    private fun getDateFormat(typedArray: TypedArray) {
        val dateFormat = typedArray.getInt(R.styleable.DateEditText_dateFormat, 0)
        if (dateFormat == 0) {
            this.dateFormat = ddMMyyyyDateFormat
            dateLength = 10
        } else if (dateFormat == 1) {
            this.dateFormat = MMyyDateFormat
            dateLength = 5
        }
        hint = getDateFormatFromDivider()
    }

    /**
     *
     */
    private fun getDividerCharacter(typedArray: TypedArray) {
        val dividerCharacter = typedArray.getInt(R.styleable.DateEditText_dividerCharacter, 0)
        if (dividerCharacter == 0) {
            this.dividerCharacter = slash
        }else if (dividerCharacter == 1) {
            this.dividerCharacter = minus
        }
    }

    /**
     *
     */
    private fun getMaxDate(typedArray: TypedArray) {
        val maxDateString = typedArray.getString(R.styleable.DateEditText_maxDate) ?: return
        val format = getDateFormatFromDivider()
        validateMinMaxDate(date = maxDateString)
        try {
            maxDate = SimpleDateFormat(format, Locale.getDefault()).parse(maxDateString)
        } catch (e: ParseException) {
            throw IllegalArgumentException("max date must be entered as a format and divider character")
        }
    }

    /**
     *
     */
    private fun getMinDate(typedArray: TypedArray) {
        val minDateString = typedArray.getString(R.styleable.DateEditText_minDate) ?: return
        val format = getDateFormatFromDivider()
        validateMinMaxDate(date = minDateString)
        try {
            minDate = SimpleDateFormat(format, Locale.getDefault()).parse(minDateString)
        } catch (e: ParseException) {
            throw IllegalArgumentException("min date must be entered as a format and divider character")
        }
    }

    /**
     *
     */
    private  fun validateMinMaxDate(date: String){
        if (dateFormat == MMyyDateFormat){
            if (date.length != 5) {
                throw IllegalArgumentException("Invalid date")
            }
            val month = date.substring(0,2).toInt()
            if (month > 12 || month <= 0) {
                throw IllegalArgumentException("Invalid date")
            }
        }else if (dateFormat == ddMMyyyyDateFormat){
            if (date.length != 10){
                throw IllegalArgumentException("Invalid date")
            }

            val day = date.substring(0,2).toInt()
            val month = date.substring(3,5).toInt()
            val year = date.substring(6, 10).toInt()
            val isLeapYear =  (year % 100 != 0 || year % 400 != 0)

            if (month > 12 || month <= 0) {
                throw IllegalArgumentException("Invalid date")
            }
            if (day > 31 || day == 0) {
                throw IllegalArgumentException("Invalid date")
            } else if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
                throw IllegalArgumentException("Invalid date")
            } else if (month == 2 && day == 31) {
                throw IllegalArgumentException("Invalid date")
            }else if (month == 2 && day == 29 && !isLeapYear) {
                throw IllegalArgumentException("Invalid date")
            }
        }
    }

    /**
     *
     */
    fun listen() {
        addTextChangedListener(dateTextWatcher)
    }

    /**
     *
     */
    private val dateTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }
            var value = getEditText()
            value = validate(value)
            value = manageDateDivider(value, firstDividerPosition, start, before)
            if (dateFormat == ddMMyyyyDateFormat) {
                value = manageDateDivider(value, nextDividerPosition, start, before)
            }
            edited = true
            setText(value)
            setSelection(text.length)
        }

        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }

    /**
     *
     */
    private fun validate(value: String): String {
        if (dateFormat == MMyyDateFormat) {
            return validateMMyyDateFormat(value)
        } else if (dateFormat == ddMMyyyyDateFormat) {
            return validateddMMyyyyDateFormat(value)

        }
        return value
    }

    /**
     *
     */
    private fun validateddMMyyyyDateFormat(value: String): String {
        var mValue = value
        if (mValue.length == 2) { // validate day
            val day = mValue.toInt()
            if (day > 31 || day == 0) {
                mValue = "31"
            }
        } else if (mValue.length == 5) { // validate month
            val month = mValue.substring(3, 5).toInt()
            if (month > 12 || month == 0) {
                mValue = mValue.replace(month.toString(), "12", false)
            }
            // validate day of month
            val day = mValue.substring(0, 2).toInt()
            if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
                mValue = mValue.replace(day.toString(), "30", false)
            } else if (month == 2 && day == 31) {
                mValue = mValue.replace(day.toString(), "29", false)
            }
        } else if (mValue.length == 10) { // validate max && min date
            val year = mValue.substring(6, 10).toInt()
            if (maxDate != null) {
                val maxDate = maxDate!!
                val format = getDateFormatFromDivider()
                val inputDate = value.toDate(format = format)
                if (inputDate != null && inputDate > maxDate) {
                    mValue = maxDate.toString(format = format)
                }
            }
            if (minDate != null) {
                val minDate = minDate!!
                val format = getDateFormatFromDivider()
                val inputDate = value.toDate(format = format)
                if (inputDate != null && inputDate < minDate) {
                    mValue = minDate.toString(format = format)
                }
            }
            //not Leap year
            if (year % 100 != 0 || year % 400 != 0) {
                val month = mValue.substring(3, 5).toInt()
                val day = mValue.substring(0, 2).toInt()
                if (month == 2 && day >= 28) {
                    mValue = mValue.replace(day.toString(), "28", false)
                }
            }
        }
        return mValue
    }

    /**
     *
     */
    private fun validateMMyyDateFormat(value: String): String {
        var mValue = value
        if (mValue.length == 2) { // validate month
            val month = mValue.toInt()
            if (month > 12 || month == 0) {
                mValue = "12"
            }
        } else if (mValue.length == 5) { // validate year
            if (maxDate != null) {
                val maxDate = maxDate!!
                val format = getDateFormatFromDivider()
                val inputDate = value.toDate(format = format)
                if (inputDate != null && inputDate > maxDate) {
                    mValue = maxDate.toString(format = format)
                }
            }
            if (minDate != null) {
                val minDate = minDate!!
                val format = getDateFormatFromDivider()
                val inputDate = value.toDate(format = format)
                if (inputDate != null && inputDate < minDate) {
                    mValue = minDate.toString(format = format)
                }
            }
        }
        return mValue
    }

    /**
     *
     */
    private fun getEditText(): String {
        return if (text.length >= dateLength)
            text.toString().substring(0, dateLength)
        else
            text.toString()
    }

    /**
     *
     */
    private fun manageDateDivider(working: String, position: Int, start: Int, before: Int): String {
        if (working.length == position) {
            return if (before <= position && start < position)
                working + dividerCharacter
            else
                working.dropLast(1)
        }
        return working
    }

    /**
     *
     */
    private fun getDateFormatFromDivider(): String {
        return when (dateFormat) {
            MMyyDateFormat -> dateFormat.substring(0, 2) + dividerCharacter + dateFormat.substring(2, 4)
            ddMMyyyyDateFormat -> dateFormat.substring(0, 2) + dividerCharacter + dateFormat.substring(
                2,
                4
            ) + dividerCharacter + dateFormat.substring(4, 8)
            else -> dateFormat
        }
    }
}

