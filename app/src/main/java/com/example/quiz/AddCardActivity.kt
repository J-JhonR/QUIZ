package com.example.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_card)

        // Barre du haut
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Ajouter une carte"

        // Les champs de saisie
        val editQuestion = findViewById<EditText>(R.id.edit_question)
        val editAnswer = findViewById<EditText>(R.id.edit_answer)
        val editWrong1 = findViewById<EditText>(R.id.wrong_1)
        val editWrong2 = findViewById<EditText>(R.id.wrong_2)
        val editWrong3 = findViewById<EditText>(R.id.wrong_3)

        // Bouton Save : vérifie les champs, renvoie les données, ferme l'écran
        findViewById<ImageView>(R.id.save_button).setOnClickListener {
            val question = editQuestion.text.toString().trim()
            val answer = editAnswer.text.toString().trim()
            val wrong1 = editWrong1.text.toString().trim()
            val wrong2 = editWrong2.text.toString().trim()
            val wrong3 = editWrong3.text.toString().trim()

            if (question.isEmpty() || answer.isEmpty() ||
                wrong1.isEmpty() || wrong2.isEmpty() || wrong3.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent()
            resultIntent.putExtra("question", question)
            resultIntent.putExtra("answer", answer)
            resultIntent.putExtra("wrong1", wrong1)
            resultIntent.putExtra("wrong2", wrong2)
            resultIntent.putExtra("wrong3", wrong3)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Bouton Cancel : ferme l'écran sans rien renvoyer
        findViewById<ImageView>(R.id.cancel_button).setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}