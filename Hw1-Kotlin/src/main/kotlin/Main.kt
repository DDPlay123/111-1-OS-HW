import kotlin.system.measureTimeMillis

val MatrixA = arrayOf(50, 80)
val MatrixB = arrayOf(80, 50)

fun main() {
    val cores = Runtime.getRuntime().availableProcessors()
    println("可用的核心數為:$cores")
    // do Initialize
    val matrixA = initMatrix().first
    val matrixB = initMatrix().second
    val matrixC = Array(matrixA.size) { Array(matrixB[0].size) { 0.00 } }
    // run For-loops
    repeat(3) {
        println("第${it + 1}次")
        runByForLoops(Triple(matrixA, matrixB, matrixC))
        runByMultiThread(Triple(matrixA, matrixB, matrixC), 50)
        runByMultiThread(Triple(matrixA, matrixB, matrixC), 10)
        runByMultiThread(Triple(matrixA, matrixB, matrixC), cores)
        println("-----------------------------------")
    }
}

private fun initMatrix(): Pair<Array<Array<Double>>, Array<Array<Double>>> {
    // Matrix A : 50 x 80
    val matrixA = Array(MatrixA[0]) { i ->
        Array(MatrixA[1]) { j ->
            (6.6 * (i + 1)) - (3.3 * (j + 1))
        }
    }
    // Matrix B : 80 x 50
    val matrixB = Array(MatrixB[0]) { i ->
        Array(MatrixB[1]) { j ->
            100 + (2.2 * (i + 1)) - (5.5 * (j + 1))
        }
    }
    return Pair(matrixA, matrixB)
}

private fun runByForLoops( // 一般迴圈解法
    matrix: Triple<Array<Array<Double>>, Array<Array<Double>>, Array<Array<Double>>>
) {
    val timeCost = measureTimeMillis {
        for (i in matrix.first.indices) // 0 ~ 49
            for (j in matrix.second[0].indices) // 0 ~ 49
                for (k in matrix.second.indices) // 0 ~ 79
                    matrix.third[i][j] += matrix.first[i][k] * matrix.second[k][j]
    }
    println("【A】時間:${timeCost}ms")
}

private fun runByMultiThread( // 使用執行緒解法
    matrix: Triple<Array<Array<Double>>, Array<Array<Double>>, Array<Array<Double>>>,
    thread: Int
) {
    val timeCost = measureTimeMillis {
        val threads = arrayOfNulls<Thread>(thread) // 定義多執行緒
        val rows = matrix.third.size / thread // 分配給每個執行緒的row

        for (i in 0 until thread) {
            var row = rows

            if (i == thread - 1) // 多餘的row留給最後一個執行緒
                row += matrix.third.size % thread

            val multiThread = MultiThread(matrix, rows * i, rows * i + row)
            threads[i] = Thread(multiThread)
            threads[i]!!.start() // 執行運算
        }

        for (i in 0 until thread)
            threads[i]!!.join() // 整合所有執行緒
    }
    println("【B-1】時間:${timeCost}ms")
}

class MultiThread(
    private val matrix: Triple<Array<Array<Double>>, Array<Array<Double>>, Array<Array<Double>>>,
    private val startRow: Int, private val endRow: Int // 起始行 和 結束行。
) : Runnable {
    override fun run() {
        var i: Int = startRow
        while (i < endRow && i < matrix.first.size) {
            for (j in 0 until matrix.second[0].size)
                for (k in 0 until matrix.second.size)
                    matrix.third[i][j] += matrix.first[i][k] * matrix.second[k][j]
            i++
        }
    }
}
