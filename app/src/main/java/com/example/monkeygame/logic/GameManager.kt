package com.example.monkeygame.logic

class GameManager(private val lifeCount: Int = 3) {
    var hitsTaken: Int = 0
        private set

    var score: Int = 0
        private set

    var bananasEaten: Int = 0
        private set

    val rows: Int = 6
    val cols: Int = 5

    // 0 = Empty, 1 = Barrel, 2 = Banana
    private val gameMatrix = Array(rows) { IntArray(cols) }

    // for obstacle spawn
    private var isEvenTick = false

    fun getItem(row: Int, col: Int): Int {
        return gameMatrix[row][col]
    }

    fun setItem(row: Int, col: Int, value: Int) {
        gameMatrix[row][col] = value
    }

    val isGameOver: Boolean
        get() = hitsTaken == lifeCount

    val monkeyRow: Int = rows - 1
    var monkeyCol: Int = cols / 2

    fun movePlayer(move: Int){
        val newPos: Int = monkeyCol + move
        if (newPos >= 0 && newPos <= cols-1)
            monkeyCol = newPos
    }

    fun checkCollision(): Int {
        val monkeyPos = gameMatrix[monkeyRow][monkeyCol]

        // monkey collided with a barrel
        if (monkeyPos == 1) {
            hitsTaken++
            gameMatrix[monkeyRow][monkeyCol] = 0
            return 1
        }
        // monkey collided with a banana
        else if (monkeyPos == 2) {
            bananasEaten++
            if (hitsTaken > 0) {
                hitsTaken--
            }
            gameMatrix[monkeyRow][monkeyCol] = 0
            return 2
        }

        return 0
    }


    fun moveObstacles() {
        score++

        isEvenTick = !isEvenTick

        // move all the rows by 1
        // start from bottom to avoid overwrite
        var i = rows - 1
        while (i > 0) {
            for (j in 0..<cols) {
                gameMatrix[i][j] = gameMatrix[i - 1][j]
            }
            i--
        }

        // empty the top row
        for (j in 0..<cols) {
            gameMatrix[0][j] = 0
        }

        // random new barrel/banana
        if (isEvenTick) {
            val randomCol = (0..<cols).random()
            // 0.1 chance for banana, 0.9 chance for barrel
            val isBanana = (0..9).random() < 1

            if (isBanana) {
                gameMatrix[0][randomCol] = 2
            } else {
                gameMatrix[0][randomCol] = 1
            }
        }
    }

}