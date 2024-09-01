package com.example.myapplication

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
private const val INITIAL_TIP_PERCENT = 15
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false)

        if(isDarkModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.activity_main)
        val toggleButton = findViewById<ToggleButton>(R.id.toggleMode)
        toggleButton.isChecked = isDarkModeOn

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("isDarkModeOn", true)

            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("isDarkModeOn", false)
            }
            editor.apply()

        }
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvPercentLabel = findViewById(R.id.tvPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        fun updateTipDescription(tipPercent: Int) {
            val tipDescription = when(tipPercent){
                in 0..9 -> "Poor"
                in 10..14 -> "Acceptable"
                in 15..19 -> "Good"
                in 20..24 -> "Great"
                else -> "Amazing"
            }
            tvTipDescription.text = tipDescription
        }
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvPercentLabel.text = "$INITIAL_TIP_PERCENT"


        seekBarTip.setOnSeekBarChangeListener(/* l = */ object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvPercentLabel.text = "$progress"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}


            private fun updateTipDescription(tipPercent: Int) {
                val tipDescription = when(tipPercent){
                    in 0..9 -> "Poor"
                    in 10..14 -> "Acceptable"
                    in 15..19 -> "Good"
                    in 20..24 -> "Great"
                    else -> "Amazing"
                }
                tvTipDescription.text = tipDescription
                val color = ArgbEvaluator().evaluate(
                    tipPercent.toFloat()/seekBarTip.max,
                    ContextCompat.getColor(this@MainActivity, R.color.color_worst_tip),
                    ContextCompat.getColor(this@MainActivity, R.color.color_best_tip)
                ) as Int
                tvTipDescription.setTextColor(color)
            }
            private fun computeTipAndTotal() {
                if(etBaseAmount.text.isEmpty()){
                    tvTipAmount.text = ""
                    tvTotalAmount.text = ""
                    return
                }
                val baseAmount = etBaseAmount.text.toString().toDouble()
                val tipPercent = seekBarTip.progress
                val tipAmount = baseAmount*tipPercent/100
                val totalAmount = baseAmount + tipAmount
                tvTipAmount.text = "%.2f".format(tipAmount)
                tvTotalAmount.text = "%.2f".format(totalAmount)
            }

        })
        etBaseAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()

            }

            private fun computeTipAndTotal() {
                val baseAmount = etBaseAmount.text.toString().toDouble()
                val tipPercent = seekBarTip.progress
                val tipAmount = baseAmount*tipPercent/100
                val totalAmount = baseAmount + tipAmount
                tvTipAmount.text = tipAmount.toString()
                tvTotalAmount.text = totalAmount.toString()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}