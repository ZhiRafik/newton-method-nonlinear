import kotlin.math.abs
import kotlin.math.sqrt

class NewtonSolver(val system: NonlinearSystem) {

    // Метод Ньютона
    fun solve(start: Vector2D, maxIter: Int = 30, eps: Double = 1e-6): Pair<Vector2D?, Int> {
        var x = start
        for (i in 1..maxIter) {
            val f = doubleArrayOf(system.f1(x), system.f2(x))
            val j = system.jacobian(x)

            // Определитель матрицы Якоби
            val det = j[0][0] * j[1][1] - j[0][1] * j[1][0]
            if (abs(det) < 1e-12) {
                return null to i
            } // Матрица вырождена

            // Обратная матрица и вычисление поправки Y
            val dx1 = ( -f[0] * j[1][1] + f[1] * j[0][1] ) / det
            val dx2 = ( -f[1] * j[0][0] + f[0] * j[1][0] ) / det

            val nextX = Vector2D(x.x1 + dx1, x.x2 + dx2)

            // Проверка выхода за границы [-10, 10] (согласно заданию)
            if (abs(nextX.x1) > 10.5 || abs(nextX.x2) > 10.5) {
                return null to i
            }

            // Критерий окончания
            if (sqrt(dx1*dx1 + dx2*dx2) < eps) {
                return nextX to i
            }

            x = nextX
        }
        return null to maxIter // Не сошлось за 30 итераций
    }
}