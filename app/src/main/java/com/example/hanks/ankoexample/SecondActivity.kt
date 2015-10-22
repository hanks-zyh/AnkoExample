package com.example.hanks.ankoexample

import android.app.Activity
import android.os.Bundle
import org.jetbrains.anko.*

/**
 * Created by hanks on 15-10-21.
 */
class SecondActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // content view
        verticalLayout {
            val name = editText()
            button("Say Hello") {
                onClick { toast("Hello, ${name.text}!") }
            }
        }

    }
}
