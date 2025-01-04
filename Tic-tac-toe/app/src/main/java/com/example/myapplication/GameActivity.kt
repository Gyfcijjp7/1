package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var buttons: List<Button>
    private lateinit var turnIndicator: TextView
    private lateinit var playerScore: TextView
    private lateinit var aiScore: TextView
    private lateinit var resetButton: Button
    private lateinit var backButton: Button
    private lateinit var gameResult: TextView

    private var isPlayerTurn = true
    private var isGameOver = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        buttons = listOf(
            findViewById(R.id.button_1),
            findViewById(R.id.button_2),
            findViewById(R.id.button_3),
            findViewById(R.id.button_4),
            findViewById(R.id.button_5),
            findViewById(R.id.button_6),
            findViewById(R.id.button_7),
            findViewById(R.id.button_8),
            findViewById(R.id.button_9)
        )
        turnIndicator = findViewById(R.id.turn_indicator)
        playerScore = findViewById(R.id.player_score)
        aiScore = findViewById(R.id.ai_score)
        resetButton = findViewById(R.id.reset_button)
        backButton = findViewById(R.id.back_button)
        gameResult = findViewById(R.id.game_result)

        buttons.forEach { button ->
            button.setOnClickListener {
                if (isPlayerTurn && !isGameOver) {
                    onPlayerMove(button)
                }
            }
        }

        resetButton.setOnClickListener { resetGame() }
        backButton.setOnClickListener { finish() }
    }

    private fun onPlayerMove(button: Button) {
        if (button.text.isEmpty()) {
            button.text = "X"
            if (checkGameState()) return
            switchTurnToAI()
        }
    }

    private fun switchTurnToAI() {
        isPlayerTurn = false
        turnIndicator.text = "Ход: Нолики"

        handler.postDelayed({
            if (!isGameOver) {
                makeAIMove()
            }
        }, 1000)
    }

    private fun makeAIMove() {
        val emptyButtons = buttons.filter { it.text.isEmpty() }
        if (emptyButtons.isNotEmpty()) {
            val button = emptyButtons.random()
            button.text = "O"
            if (checkGameState()) return
        }
        isPlayerTurn = true
        turnIndicator.text = "Ход: Крестики"
    }

    private fun resetGame() {
        buttons.forEach {
            it.text = ""
            it.isEnabled = true
            it.setTextColor(resources.getColor(android.R.color.white))
        }
        gameResult.visibility = View.GONE
        isPlayerTurn = true
        isGameOver = false
        turnIndicator.text = "Ход: Крестики"
    }

    private fun checkGameState(): Boolean {
        val winningCombinations = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )

        for (combination in winningCombinations) {
            val (a, b, c) = combination
            if (buttons[a].text.isNotEmpty() &&
                buttons[a].text == buttons[b].text &&
                buttons[a].text == buttons[c].text
            ) {
                highlightWinningCombination(combination)
                showGameResult(buttons[a].text.toString())
                updateScore(buttons[a].text.toString())
                return true
            }
        }

        if (buttons.all { it.text.isNotEmpty() }) {
            showGameResult("Ничья!")
            return true
        }
        return false
    }

    private fun highlightWinningCombination(combination: List<Int>) {
        val lightRedColor = Color.parseColor("#FF6666")
        combination.forEach { index ->
            buttons[index].setTextColor(lightRedColor)
        }
    }

    private fun showGameResult(winner: String) {
        val message = if (winner == "X") "ВЫ победили!" else if (winner == "O") "ИИ победил!" else "Ничья!"
        gameResult.text = message
        gameResult.visibility = View.VISIBLE

        buttons.forEach { it.isEnabled = true }
    }

    private fun updateScore(winner: String) {
        if (winner == "X") {
            val currentScore = playerScore.text.toString().toInt()
            playerScore.text = (currentScore + 1).toString()
        } else if (winner == "O") {
            val currentScore = aiScore.text.toString().toInt()
            aiScore.text = (currentScore + 1).toString()
        }
        isGameOver = true
    }
}