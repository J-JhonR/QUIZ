package com.example.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // Accès à la base de données
    lateinit var flashcardDatabase: FlashcardDatabase

    // Copie locale de toutes les cartes, relue depuis la base
    private var cards = mutableListOf<Flashcard>()

    private var currentIndex = 0         // carte affichée
    private var correctIndex = 0         // position de la bonne réponse parmi les 4 choix
    private var answered = false         // true dès qu'un choix est touché
    private var isShowingAnswers = true  // choix visibles ou cachés

    private lateinit var questionView: TextView
    private lateinit var choiceViews: List<TextView>
    private lateinit var toggleIcon: ImageView

    // Reçoit la carte créée dans AddCardActivity
    private val addCardLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val question = data?.getStringExtra("question").orEmpty()
            val answer = data?.getStringExtra("answer").orEmpty()
            val wrong1 = data?.getStringExtra("wrong1").orEmpty()
            val wrong2 = data?.getStringExtra("wrong2").orEmpty()
            val wrong3 = data?.getStringExtra("wrong3").orEmpty()

            if (question.isNotBlank() && answer.isNotBlank() &&
                wrong1.isNotBlank() && wrong2.isNotBlank() && wrong3.isNotBlank()) {
                // 1. Écrire la carte dans la base
                flashcardDatabase.insertCard(Flashcard(question, answer, wrong1, wrong2, wrong3))
                // 2. Relire toutes les cartes depuis la base
                cards = flashcardDatabase.getAllCards().toMutableList()
                // 3. Afficher la nouvelle carte (la dernière)
                currentIndex = cards.size - 1
                showCard()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ouvre la base, ajoute les cartes de départ si elle est vide, puis charge la liste
        flashcardDatabase = FlashcardDatabase(this)
        if (flashcardDatabase.getAllCards().isEmpty()) {
            seedStarterCards()
        }
        cards = flashcardDatabase.getAllCards().toMutableList()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Flashcards"

        questionView = findViewById(R.id.flashcard_question)
        toggleIcon = findViewById(R.id.toggle_choices_visibility)
        choiceViews = listOf(
            findViewById<TextView>(R.id.choice_1),
            findViewById<TextView>(R.id.choice_2),
            findViewById<TextView>(R.id.choice_3),
            findViewById<TextView>(R.id.choice_4)
        )

        // Un toucher sur un choix
        choiceViews.forEachIndexed { position, choice ->
            choice.setOnClickListener { onChoiceClicked(position) }
        }

        // Bouton œil : affiche ou cache les choix
        toggleIcon.setOnClickListener {
            isShowingAnswers = !isShowingAnswers
            val visibility = if (isShowingAnswers) View.VISIBLE else View.GONE
            choiceViews.forEach { it.visibility = visibility }
            toggleIcon.setImageResource(
                if (isShowingAnswers) R.drawable.ic_visibility_off else R.drawable.ic_visibility
            )
        }

        // Bouton Suivant : carte suivante, puis retour à la première
        findViewById<TextView>(R.id.next_button).setOnClickListener {
            currentIndex = (currentIndex + 1) % cards.size
            showCard()
        }

        // Bouton Add : ouvre AddCardActivity
        findViewById<ImageView>(R.id.add_button).setOnClickListener {
            addCardLauncher.launch(Intent(this, AddCardActivity::class.java))
        }

        toggleIcon.setImageResource(R.drawable.ic_visibility_off)
        showCard()
    }

    // Enregistre les 4 cartes de départ dans la base (premier lancement seulement)
    private fun seedStarterCards() {
        flashcardDatabase.insertCard(Flashcard("Quel est le 44e président des États-Unis ?", "Barack Obama",
            "Bill Clinton", "George H. W. Bush", "Jimmy Carter"))
        flashcardDatabase.insertCard(Flashcard("Quelle est la capitale d'Haïti ?", "Port-au-Prince",
            "Artibonite", "Port-de-Paix", "Cayes"))
        flashcardDatabase.insertCard(Flashcard("Quel est le plus grand océan du monde ?", "Pacifique",
            "Indien", "Atlantique", "Arctique"))
        flashcardDatabase.insertCard(Flashcard("Combien de continents y a-t-il ?", "Sept",
            "Huit", "Trois", "Dix"))
    }

    // Affiche la carte courante avec ses 4 choix mélangés
    private fun showCard() {
        val card = cards[currentIndex]
        answered = false
        questionView.text = card.question

        // La bonne réponse et les 3 fausses, mélangées
        val options = listOf(
            card.answer,
            card.wrongAnswer1.orEmpty(),
            card.wrongAnswer2.orEmpty(),
            card.wrongAnswer3.orEmpty()
        ).shuffled()
        correctIndex = options.indexOf(card.answer)

        val visibility = if (isShowingAnswers) View.VISIBLE else View.GONE
        choiceViews.forEachIndexed { i, choice ->
            choice.text = options[i]
            choice.setBackgroundColor(ContextCompat.getColor(this, R.color.choice_default))
            choice.visibility = visibility
        }
    }

    // Colore le choix touché et la bonne réponse
    private fun onChoiceClicked(position: Int) {
        if (answered) return
        answered = true

        choiceViews[correctIndex].setBackgroundColor(
            ContextCompat.getColor(this, R.color.choice_correct)
        )
        if (position != correctIndex) {
            choiceViews[position].setBackgroundColor(
                ContextCompat.getColor(this, R.color.choice_wrong)
            )
        }
    }
}