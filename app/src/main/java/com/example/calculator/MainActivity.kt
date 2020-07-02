package com.example.calculator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var lastNumeric : Boolean = false
    var lastDot : Boolean = false
    var stack = ArrayList<String>()
    var parCount : Int = 0
    var parFlag : Boolean = false
    var wrongFlag = false
    var Equal : Boolean = false
    var negativeFlag : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    fun onDigit(view : View){

        if (Equal && !wrongFlag) {
            onClear(view)
        }
        //Toast.makeText(this@MainActivity, "I am Clicked",Toast.LENGTH_SHORT).show()
        var new :String = ((view as Button).text).toString()
        literals(new)

        temp.text = stack.toString()


        if (wrongFlag && !Equal){
            Toast.makeText(this@MainActivity, "Wrong Expression",Toast.LENGTH_SHORT).show()
            wrongFlag = false
        }else if (wrongFlag && Equal){
            result.append((view as Button).text)
            wrongFlag = false
        }else
            result.append((view as Button).text)
        Equal = false


    }

    fun onClear (view : View){
        result.setText("")
        stack.clear()
        lastNumeric = false
        lastDot = false
        wrongFlag = false
        parCount = 0
        parFlag = false
        negativeFlag = false
        temp.setText("")
        temp1.setText("")
    }

    fun onEqual(view : View) {
        Equal = true
        if (parCount == 0 && stack.size != 0 && lastNumeric) {
            var stack1 = ArrayList<String>()
            var stack2 = ArrayList<Double>()
            stack1.add("$")
            var i: Int = 0

            while (i != stack.size) {
                if (stack[i] !in "+-*/()") {
                    stack2.add(stack[i].toDouble())
                } else {

                    var size = stack.size
                    var size1 = stack1.size
                    var size2 = stack2.size

                    if (stack[i] == "(") {
                        stack1.add(stack[i])
                    } else if (stack[i] == ")") {

                        if (stack1[size1 - 1] != "(") {

                            while (stack1[size1 - 1] != "(") {
                                calculateAndDelete( stack1, stack2)
                                size1--
                                size2 -= 2

                            }

                        }
                        stack1.removeAt(size1 - 1)

                    } else {

                        var previous: String = stack1[size1 - 1]

                        if (precedence(previous, stack[i])) {
                            stack1.add(stack[i])
                        } else {
                            calculateAndDelete(stack1, stack2)
                            stack1.add(stack[i])
                        }
                    }
                }
                i++
                temp1.append("${stack1.toString()}   ${stack2.toString()} \n")

            }

            var size1 = stack1.size
            while (stack1.size > 1) {
                calculateAndDelete( stack1, stack2)
                temp1.append("${stack1.toString()}   ${stack2.toString()} \n")
            }

            temp1.setMovementMethod(ScrollingMovementMethod())
            result.text = removeZero(stack2[0].toString())

        }else{
            wrongFlag = true
            Toast.makeText(this@MainActivity, "Wrong/Empty Expression",Toast.LENGTH_SHORT).show()
        }
    }

    private fun operation(opt1 : Double , opt2 : Double , op : String) : Double{
        var res : Double = 0.0
        try {
            when (op) {
                "*" -> res = opt1 * opt2
                "/" -> res = opt1 / opt2
                "+" -> res = opt1 + opt2
                "-" -> res = opt1 - opt2
            }
        }catch (e : TypeNotPresentException){
            e.printStackTrace()
        }
        return res
    }

    private fun precedence(op1 : String , op2: String) : Boolean{

        if (op1 in "+-" && op2 in "*/")
            return true
        if (op1 in "$(" && op2 in "-+/*")
            return true

        return false
    }

    private fun calculateAndDelete(
        array1 : ArrayList<String>,array2 : ArrayList<Double>){

        var size1 = array1.size
        var size2 = array2.size

        var opt1 = array2[size2-2]
        var opt2 = array2[size2-1]
        var op = array1[size1-1]

        array1.removeAt(size1-1)

        array2.removeAt(size2-1)

        array2.removeAt(size2-2)

        size1 --
        size2 -= 2

        var res = operation(opt1,opt2,op)

        array2.add(res)

    }

    fun literals(new : String){
        if (new.length != 0){

            if (!(lastNumeric || parFlag)){

                if (new == "("){
                    stack.add(new)
                    parCount ++
                }else if (new == (")") && parCount == 0){
                    wrongFlag = true
                }else if (new == "-" && !negativeFlag){
                    stack.add(new)
                    lastNumeric = true
                    negativeFlag = true
                }else if (new !in "+-/*)."){
                    stack.add(new)
                    lastNumeric = true
                    parFlag = false
                    negativeFlag = false
                }else if (new == "." && !(lastDot)){
                    stack.add("0.")
                    lastNumeric = true
                    lastDot = true
                    negativeFlag = true
                }else{
                    wrongFlag = true
                }
            }else if (lastNumeric && !(parFlag)){
                if (new == ")" && parCount >0){
                    stack.add(new)
                    parFlag = true
                    lastNumeric = false
                    lastDot = false
                    parCount --
                }else if (new !in "-+/*()."){
                    literalHelper(new)
                    negativeFlag = false
                }else if (new == "." && !lastDot){
                    literalHelper(new)
                    lastDot = true
                }else if (new == "-" && negativeFlag){
                    wrongFlag = true
                }else if (new in "-+/*" && !negativeFlag){
                    stack.add(new)
                    lastNumeric = false
                    parFlag = false
                    lastDot = false
                }else{
                    wrongFlag = true
                }
            }else if (!lastNumeric && parFlag){
                if (new in "+-*/"){
                    stack.add(new)
                    parFlag = false
                }else if (new == ")" && parCount > 0 ){
                    parCount --
                    stack.add(new)
                }else{
                    wrongFlag = true
                }
            }
        }else{
            wrongFlag = true
        }

    }

    fun literalHelper(new : String){
        var size = stack.size
        var previous = stack[size - 1]
        stack.removeAt(size - 1)
        size --
        previous += new
        stack.add(previous)
    }

    private fun removeZero(res : String): String{
        var value = res
        if (res.contains(".0"))
            value = res.substring(0,res.length-2)
        return  value
    }

    fun remove(view : View) {
        if (stack.size >0 && !Equal){
        var size = stack.size
        if (size > 0){
            var previous = stack[size-1]
            stack.removeAt(size-1)
            if (previous.length != 1){
                if (previous.length == 2 && previous[1] in "."){
                    if (size == 1){
                        lastNumeric = false
                        lastDot = false
                    }else if (stack[size - 2] in "+-/*("){
                        lastDot = false
                        lastNumeric = false
                    }
                }else{
                previous = previous.substring(0,(previous.length-1))
                stack.add(previous)
                if (previous == "-"){
                    negativeFlag = true
                    lastNumeric = false
                }}
            }else{

                if (previous == "("){
                    parCount --
                    if (size > 1){
                        lastNumeric = false
                    }
                }else if (previous == ")"){
                    parCount ++
                    lastNumeric = true
                }else if(previous in "+/*" ){
                    if (stack[size -2] in ")"){
                        parFlag = true
                    }else{
                        lastNumeric = true
                    }
                }else if (previous == "-"){
                    if (size == 1){
                        lastNumeric = false
                    }else {
                        if (stack[size-2] in "+-/*"){
                            lastNumeric = false
                            negativeFlag = false
                        }else{
                            lastNumeric = true
                        }

                    }
                }else if (previous !in "+-*/()"){
                    lastNumeric = false
                    }
                }
            }

            var current = result.text
            result.text = current.substring(0,(current.length-1))
            temp.text = stack.toString()

    }else{
        Toast.makeText(this@MainActivity, "Wrong Action",Toast.LENGTH_SHORT).show()
    }


    }
    fun showHide(view: View){
        if (temp.getVisibility() == View.GONE){
            temp1.setVisibility(View.VISIBLE)
            temp.setVisibility(View.VISIBLE)
        }else{
            temp1.setVisibility(View.GONE)
            temp.setVisibility(View.GONE)
        }
    }
}