package com.example.quiz

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    var isShowingAnswers = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toggleIcon = findViewById<ImageView>(R.id.toggle_choices_visibility)

        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        // Récupère les trois choix
        val choice1 = findViewById<View>(R.id.choice_1)
        val choice2 = findViewById<View>(R.id.choice_2)
        val choice3 = findViewById<View>(R.id.choice_3)

// Un choix ne peut être touché qu'une seule fois
        var answered = false

        choice1.setOnClickListener {
            if (!answered) {
                answered = true
                choice1.setBackgroundColor(getResources().getColor(R.color.choice_wrong, null))
                choice3.setBackgroundColor(getResources().getColor(R.color.choice_correct, null))
            }
        }

        choice2.setOnClickListener {
            if (!answered) {
                answered = true
                choice2.setBackgroundColor(getResources().getColor(R.color.choice_wrong, null))
                choice3.setBackgroundColor(getResources().getColor(R.color.choice_correct, null))
            }
        }

        choice3.setOnClickListener {
            if (!answered) {
                answered = true
                choice3.setBackgroundColor(getResources().getColor(R.color.choice_correct, null))
            }
        }
        toggleIcon.setOnClickListener {
            isShowingAnswers = !isShowingAnswers

            val visibility = if (isShowingAnswers) View.VISIBLE else View.GONE
            findViewById<View>(R.id.choice_1).visibility = visibility
            findViewById<View>(R.id.choice_2).visibility = visibility
            findViewById<View>(R.id.choice_3).visibility = visibility

            if (isShowingAnswers) {
                toggleIcon.setImageResource(R.drawable.ic_visibility_off)
            } else {
                toggleIcon.setImageResource(R.drawable.ic_visibility)
            }
        }
    }
}
