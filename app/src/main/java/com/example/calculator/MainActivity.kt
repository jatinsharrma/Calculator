package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    var lastNumeric : Boolean = false
    var lastDot : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    fun onDigit(view : View){
        //Toast.makeText(this@MainActivity, "I am Clicked",Toast.LENGTH_SHORT).show()
        result.append((view as Button).text)
        lastNumeric = true
    }

    fun onClear (view : View){
        result.setText("")
        lastNumeric = false
        lastDot = false
    }

    fun onDecimalPoint(view : View){
        if (lastNumeric && !lastDot) {
            result.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onEqual(view : View){
        if(lastNumeric){
            var tvValue = result.text.toString()
            var prefix = ""

            try{
                if(tvValue.startsWith("-")){
                    prefix = "-"
                    tvValue = tvValue.substring(1)
                }
                if (tvValue.contains("-")){
                    val splitValue = tvValue.split("-")
                    var one = splitValue[0]
                    var two = splitValue[1]
                    if (!prefix.isEmpty())
                        one = prefix + one
                    result.text = removeZero((one.toDouble() - two.toDouble()).toString())

                }else if (tvValue.contains("+")){
                    val splitValue = tvValue.split("+")
                    var one = splitValue[0]
                    var two = splitValue[1]
                    if (!prefix.isEmpty())
                        one = prefix + one
                    result.text = removeZero((one.toDouble() + two.toDouble()).toString())

                }else if (tvValue.contains("*")){
                    val splitValue = tvValue.split("*")
                    var one = splitValue[0]
                    var two = splitValue[1]
                    if (!prefix.isEmpty())
                        one = prefix + one
                    result.text = removeZero((one.toDouble() * two.toDouble()).toString())

                }else if (tvValue.contains("/")){
                    val splitValue = tvValue.split("/")
                    var one = splitValue[0]
                    var two = splitValue[1]
                    if (!prefix.isEmpty())
                        one = prefix + one
                    result.text = removeZero((one.toDouble() / two.toDouble()).toString())

                }
            } catch (e:ArithmeticException){
                e.printStackTrace()
            }

        }

    }

    private fun removeZero(res : String): String{
        var value = res
        if (res.contains(".0"))
            value = res.substring(0,res.length-2)
        return  value
    }
    fun onOperator(view : View){
        if (lastNumeric && !isOperatorAdded((result.text.toString()))){
            result.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    private fun isOperatorAdded(value : String) : Boolean{
        return if (value.startsWith("-")){
            false}
        else{
            value.contains("/") || value.contains("*") || value.contains("-") || value.contains("+")
        }

    }
}