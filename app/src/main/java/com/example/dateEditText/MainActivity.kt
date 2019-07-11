package com.example.dateEditText

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstDateEditText.listen()
        secondDateEditText.listen()
        thirdDateEditText.listen()
        fourthDateEditText.listen()
        fifthDateEditText.listen()
    }
}
