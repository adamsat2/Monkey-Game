package com.example.monkeygame.logic

class GameManager(private val lifeCount: Int = 3) {
    var hitsTaken: Int = 0
        private set

    val rows: Int = 5
    val cols: Int = 3

    // 0 = Empty
    // 1 = Barrel
    private val gameMatrix = Array(rows) { IntArray(cols) }

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
        // correct answer and update score
        val newPos: Int = monkeyCol + move
        if (newPos >= 0 && newPos <= cols-1)
            monkeyCol = newPos
    }

    fun checkCollision() {
        // a barrel hit the monkey, update hitsTaken
        if (gameMatrix[monkeyRow][monkeyCol] == 1) {
            hitsTaken++
            gameMatrix[monkeyRow][monkeyCol] = 0
        }
    }

    fun moveObstacles() {
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

        // random new barrel
        val randomCol = (0..<cols).random()
        gameMatrix[0][randomCol] = 1

        checkCollision()
    }

}