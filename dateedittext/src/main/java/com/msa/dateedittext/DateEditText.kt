package com.msa.dateedittext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.Gravity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * This class Allows the user to enter a date as a text and then verify it based on date format, divider character
 * and min or max date with other options
 */
class DateEditText : TextInputEditText {


    enum class DateFormat(val value: String) {
        DDMMyyyy("ddMMyyyy"),
        MMyy("MMyy"),
    }

    enum class DividerCharacter(val value: String) {
        Minus("-"),
        Slash("/"),
    }


    private var dividerCharacter = DividerCharacter.Minus
    private var dateFormat = DateFormat.DDMMyyyy

    var maxDate: Date? = null
        set(value) {
            validateMinMaxDate(minDate, value)
            field = value
        }

    var minDate: Date? = null
        set(value) {
            validateMinMaxDate(value, maxDate)
            field = value
        }

    var autoCorrect: Boolean = false
    var helperTextEnabled = false
    var helperTextHighlightedColor = Color.BLUE


    private val dateLength: Int
        get() {
            return if (dateFormat == DateFormat.DDMMyyyy) {
                10
            } else {
                5
            }
        }

    private val firstDividerPosition = 2
    private val nextDividerPosition = 5
    private var edited = false
    private var valueWithError: String? = null



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
     * init edit text with given arguments from AttributeSet
     * @param attrs: AttributeSet
     */
    @SuppressLint("RtlHardcoded")
    private fun initDateEditText(attrs: AttributeSet? = null) {
        gravity = Gravity.LEFT
        isCursorVisible = false
        setOnClickListener { setSelection(text?.length ?: 0) }
        inputType = InputType.TYPE_CLASS_NUMBER
        if (attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DateEditText, 0, 0)
        getDividerCharacter(typedArray)
        getDateFormat(typedArray)
        getMaxDate(typedArray)
        getMinDate(typedArray)
        autoCorrect = typedArray.getBoolean(R.styleable.DateEditText_autoCorrect, true)
        helperTextEnabled = typedArray.getBoolean(R.styleable.DateEditText_helperTextEnabled, false)
        helperTextHighlightedColor = typedArray.getColor(R.styleable.DateEditText_helperTextHighlightedColor, Color.BLUE)
        validateMinMaxDate(minDate, maxDate)
        typedArray.recycle()
    }

    /**
     * get Date Format using from array and set edit text hint based on date format
     * @param typedArray: TypedArray
     */
    private fun getDateFormat(typedArray: TypedArray) {
        val dateFormat = typedArray.getInt(R.styleable.DateEditText_dateFormat, 0)
        if (dateFormat == 0) {
            this.dateFormat = DateFormat.DDMMyyyy
        } else if (dateFormat == 1) {
            this.dateFormat = DateFormat.MMyy
        }
        if (hint.isNullOrEmpty()){
            hint = getDateFormatFromDivider()
        }
    }

    /**
     *  get Divider Character from typed array
     *  @param typedArray: TypedArray
     */
    private fun getDividerCharacter(typedArray: TypedArray) {
        val dividerCharacter = typedArray.getInt(R.styleable.DateEditText_dividerCharacter, 0)
        if (dividerCharacter == 0) {
            this.dividerCharacter = DividerCharacter.Slash
        } else if (dividerCharacter == 1) {
            this.dividerCharacter = DividerCharacter.Minus
        }
    }

    /**
     *  get max date from typed array and throw Exception if the date is invalid
     *  @param typedArray: TypedArray
     *  @throws IllegalArgumentException
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
     *   get min date from typed array and throw an Exception if the date is invalid
     *   @param typedArray: TypedArray
     *   @throws IllegalArgumentException
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
     * validate min and max date, throw an Exception if the date is invalid
     *   @param date: String
     *   @throws IllegalArgumentException
     */
    private fun validateMinMaxDate(date: String) {
        if (dateFormat == DateFormat.MMyy) {
            if (date.length != 5) {
                throw IllegalArgumentException("Invalid date")
            }
            val month = date.substring(0, 2).toInt()
            if (month > 12 || month <= 0) {
                throw IllegalArgumentException("Invalid date")
            }
        } else if (dateFormat == DateFormat.DDMMyyyy) {
            if (date.length != 10) {
                throw IllegalArgumentException("Invalid date")
            }

            val day = date.substring(0, 2).toInt()
            val month = date.substring(3, 5).toInt()
            val year = date.substring(6, 10).toInt()
            val isLeapYear = (year % 100 != 0 || year % 400 != 0)

            if (month > 12 || month <= 0) {
                throw IllegalArgumentException("Invalid date")
            }
            if (day > 31 || day == 0) {
                throw IllegalArgumentException("Invalid date")
            } else if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
                throw IllegalArgumentException("Invalid date")
            } else if (month == 2 && day == 31) {
                throw IllegalArgumentException("Invalid date")
            } else if (month == 2 && day == 29 && !isLeapYear) {
                throw IllegalArgumentException("Invalid date")
            }
        }
    }

    /**
     *  validate min and max date, throw an Exception if the min date >= max date
     *   @throws IllegalArgumentException
     */
    private fun validateMinMaxDate(minDate: Date?, maxDate: Date?) {
        val mMinDate = minDate ?: return
        val mMaxDate = maxDate ?: return
        if (mMinDate >= mMaxDate) {
            throw IllegalArgumentException("min date must be smaller than max date")
        }
    }

    /**
     * user should call this function from activity or any android ui components to start listening and updating the edit text
     */
    fun listen() {
        addTextChangedListener(dateTextWatcher)
    }

    /**
     * When an object of this type is attached to an Editable, its methods will be called when the text is changed. "from https://developer.android.com/reference/android/text/TextWatcher"
     * All updates and validation of text are done here
     */
    private val dateTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }
            var value = getEditText()
            value = validate(value)
            if (valueWithError != null && before < count){
                edited = true
                setText(valueWithError)
                setSelection(text?.length ?: 0)
                valueWithError = null
                return
            }
            setError(value= null,errorMessage = null)
            value = manageDateDivider(value, firstDividerPosition, start, before)
            if (dateFormat == DateFormat.DDMMyyyy) {
                value = manageDateDivider(value, nextDividerPosition, start, before)
            }
            edited = true
            setText(value)
            setSelection(text?.length ?: 0)
            renderHelperText(value = value)
        }

        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }

    /**
     * validate user text input
     * @param value: String (user text input)
     */
    private fun validate(value: String): String {
        if (dateFormat == DateFormat.MMyy) {
            return validateMMyyDateFormat(value)
        } else if (dateFormat == DateFormat.DDMMyyyy) {
            return validateddMMyyyyDateFormat(value)

        }
        return value
    }

    /**
     * validate user text input for DDMMyyyy Date Format
     * @param  value: String (user text input)
     * @return the corrected value
     */
    private fun validateddMMyyyyDateFormat(value: String): String {
        var mValue = value

        // validate day
        if (mValue.length >= 2) {
            val day = mValue.substring(0, 2).toInt()
            if (day > 31 || day == 0) {
                if (autoCorrect){
                    mValue = "31"
                }else{
                    setError(value=  mValue.substring(0, 2), errorMessage = context.getString(R.string.invalid_day))
                }
            }
        }

        // validate month
        if (mValue.length >= 5) {
            val month = mValue.substring(3, 5).toInt()
            if (month > 12 || month == 0) {
                if (autoCorrect){
                    mValue = mValue.replace(month.toString(), "12", false)
                }else{
                    setError(value=  mValue.substring(0,5), errorMessage =  context.getString(R.string.invalid_month))
                }
            }
            // validate day of month
            val day = mValue.substring(0, 2).toInt()
            if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
                if (autoCorrect){
                    mValue = mValue.replace(day.toString(), "30", false)
                }else{
                    setError(value=  mValue.substring(0,5), errorMessage =  context.getString(R.string.invalid_day_of_month))
                }
            } else if (month == 2 && day == 31) {
                if (autoCorrect){
                    mValue = mValue.replace(day.toString(), "29", false)
                }else{
                    setError(value=  mValue.substring(0,5), errorMessage = context.getString(R.string.invalid_day_of_month))
                }
            }
        }

        // validate max && min date
        if (mValue.length == 10) {
            val year = mValue.substring(6, 10).toInt()
            if (maxDate != null) {
                val maxDate = maxDate!!
                val format = getDateFormatFromDivider()
                val inputDate = value.toDate(format = format)
                if (inputDate != null && inputDate > maxDate) {
                    if (autoCorrect){
                        mValue = maxDate.toString(format = format)
                    }else{
                        setError(value=  mValue, errorMessage =  context.getString(R.string.invalid_date_max))
                    }
                }
            }
            if (minDate != null) {
                val minDate = minDate!!
                val format = getDateFormatFromDivider()
                val inputDate = value.toDate(format = format)
                if (inputDate != null && inputDate < minDate) {
                    if (autoCorrect){
                        mValue = minDate.toString(format = format)
                    }else{
                        setError(value=  mValue, errorMessage =  context.getString(R.string.invalid_date_min))
                    }
                }
            }
            //not Leap year
            if (year % 100 != 0 || year % 400 != 0) {
                val month = mValue.substring(3, 5).toInt()
                val day = mValue.substring(0, 2).toInt()
                if (month == 2 && day >= 28) {
                    if (autoCorrect){
                        mValue = mValue.replace(day.toString(), "28", false)
                    }else{
                        setError(value=  mValue, errorMessage =  context.getString(R.string.invalid_day_of_month_leap_year))
                    }
                }
            }
        }
        return mValue
    }

    /**
     * validate user text input for MMyy Date Format
     * @param  value: String (user text input)
     * @return the corrected value
     */
    private fun validateMMyyDateFormat(value: String): String {
        var mValue = value

        // validate month
        if (mValue.length >= 2) {
            val month = mValue.substring(0, 2).toInt()
            if (month > 12 || month == 0) {
                if (autoCorrect) {
                    mValue = "12"
                } else {
                    setError(value=  month.toString(), errorMessage =  context.getString(R.string.invalid_month))
                }
            }
        }
        // validate year
        if (mValue.length == 5) {
            if (maxDate != null) {
                val maxDate = maxDate!!
                val format = getDateFormatFromDivider()
                val inputDate = value.toDate(format = format)
                if (inputDate != null && inputDate > maxDate) {
                    if (autoCorrect){
                        mValue = maxDate.toString(format = format)
                    }else{
                        setError(value=  mValue, errorMessage =  context.getString(R.string.invalid_date_max))
                    }
                }
            }
            if (minDate != null) {
                val minDate = minDate!!
                val format = getDateFormatFromDivider()
                val inputDate = value.toDate(format = format)
                if (inputDate != null && inputDate < minDate) {
                    if (autoCorrect) {
                        mValue = minDate.toString(format = format)
                    }else{
                        setError(value=  mValue, errorMessage = context.getString(R.string.invalid_date_min))
                    }
                }
            }
        }
        return mValue
    }

    /**
     * get edit text based on dateFormat length
     */
    private fun getEditText(): String {
        return if ((text?.length ?: 0) >= dateLength)
            text.toString().substring(0, dateLength)
        else
            text.toString()
    }

    /**
     * @author Ícaro Mota from stackoverflow
     * add or remove divider character to the user text input
     * @param working: user text input
     * @param position: the  position where we want to insert the divider character on  user text input
     * @param start: start editing position
     * @param before: length before editing
     * @return the text after add ir removing divider character from user text input
     */
    private fun manageDateDivider(working: String, position: Int, start: Int, before: Int): String {
        if (working.length == position) {
            return if (before <= position && start < position)
                working + dividerCharacter.value
            else
                working.dropLast(1)
        }
        return working
    }

    /**
     * get date format based on divider character
     * @return date format with divider character
     */
    private fun getDateFormatFromDivider(): String {
        return when (dateFormat) {
            DateFormat.MMyy -> dateFormat.value.substring(0, 2) + dividerCharacter.value + dateFormat.value.substring(
                2,
                4
            )
            DateFormat.DDMMyyyy -> dateFormat.value.substring(
                0,
                2
            ) + dividerCharacter.value + dateFormat.value.substring(
                2,
                4
            ) + dividerCharacter.value + dateFormat.value.substring(4, 8)
        }
    }

    /**
     * set error to edit text
     * @param errorMessage error message will render
     * @param value the value which has the error
     */
    private fun setError(errorMessage: String?, value: String?){
        valueWithError = value
        if (parent.parent is TextInputLayout){
            val parentTextInputLayout = parent.parent as TextInputLayout
            parentTextInputLayout.error = errorMessage
            if  (errorMessage == null){
                parentTextInputLayout.isErrorEnabled = false
            }
        }else{
            error = errorMessage
        }
    }

    /**
     * render helper text if the parent of edit text is TextInputLayout
     * @param value user text input after validation process
     */
    private fun renderHelperText(value: String) {

        if (parent.parent is TextInputLayout && helperTextEnabled){
            val textInputLayout = parent.parent as TextInputLayout
            if (value.isEmpty()){
                textInputLayout.helperText = null
                return
            }
            textInputLayout.isHelperTextEnabled = true
            val hint = getDateFormatFromDivider()
            val spannableString = SpannableString(hint)
            val foregroundSpan = ForegroundColorSpan(helperTextHighlightedColor)
            spannableString.setSpan(
                foregroundSpan,
                0,
                value.length ,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textInputLayout.helperText = spannableString
        }
    }
}

