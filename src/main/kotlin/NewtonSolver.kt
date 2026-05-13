import kotlin.math.abs
import kotlin.math.sqrt

class NewtonSolver(
    private val system: NonlinearSystem,
    private val jacobianMode: JacobianMode
) {
    private fun good(v: Double): Boolean = !v.isNaN() && !v.isInfinite()

    private fun values(x: Vector2D): DoubleArray {
        return doubleArrayOf(system.f1(x), system.f2(x))
    }

    private fun norm(a: Double, b: Double): Double {
        return sqrt(a * a + b * b)
    }

    private fun insideArea(x: Vector2D): Boolean {
        return abs(x.x1) <= 10.0 && abs(x.x2) <= 10.0
    }



    private fun fixTinyNegativeCoordinate(x: Vector2D): Vector2D {
        val tolerance = 1e-12

        val boundaryValue = if (jacobianMode == JacobianMode.NUMERIC) {
            0.0
        } else {
            1e-14
        }

        val x1 = if (x.x1 < 0.0 && abs(x.x1) < tolerance) {
            boundaryValue
        } else {
            x.x1
        }

        val x2 = if (x.x2 < 0.0 && abs(x.x2) < tolerance) {
            boundaryValue
        } else {
            x.x2
        }

        return Vector2D(x1, x2)
    }

    private fun inverse2x2(j: Array<DoubleArray>): Array<DoubleArray>? {
        val a = j[0][0]
        val b = j[0][1]
        val c = j[1][0]
        val d = j[1][1]

        if (!good(a) || !good(b) || !good(c) || !good(d)) return null

        val det = a * d - b * c
        if (!good(det) || abs(det) < 1e-12) return null

        return arrayOf(
            doubleArrayOf(d / det, -b / det),
            doubleArrayOf(-c / det, a / det)
        )
    }

    fun solve(start: Vector2D, maxIter: Int = 30, eps: Double = 1e-8): NewtonResult {
        if (!insideArea(start)) {
            return NewtonResult(
                null,
                0,
                listOf(start),
                false,
                "Начальная точка вне области [-10; 10] x [-10; 10]"
            )
        }

        var x = fixTinyNegativeCoordinate(start)

        val path = mutableListOf<Vector2D>()
        val usedPoints = mutableListOf<Vector2D>()

        path.add(x)
        usedPoints.add(x)

        for (iter in 1..maxIter) {
            val f = values(x)

            if (!good(f[0]) || !good(f[1])) {
                return NewtonResult(
                    null,
                    iter - 1,
                    path,
                    false,
                    "Функция не определена в текущей точке"
                )
            }

            val currentNorm = norm(f[0], f[1])

            if (currentNorm < eps) {
                return NewtonResult(
                    x,
                    iter - 1,
                    path,
                    true,
                    "Критерий ||F(X)|| < eps"
                )
            }

            val j = system.jacobian(x, jacobianMode)

            val invJ = inverse2x2(j)
                ?: return NewtonResult(
                    null,
                    iter - 1,
                    path,
                    false,
                    "Матрица Якоби вырождена или не определена"
                )

            val dx1 = -(invJ[0][0] * f[0] + invJ[0][1] * f[1])
            val dx2 = -(invJ[1][0] * f[0] + invJ[1][1] * f[1])

            if (!good(dx1) || !good(dx2)) {
                return NewtonResult(
                    null,
                    iter - 1,
                    path,
                    false,
                    "Поправка Ньютона не определена"
                )
            }

            var lambda = 1.0
            var accepted: Vector2D? = null

            while (lambda >= 1e-8) {
                val rawCandidate = Vector2D(
                    x.x1 + lambda * dx1,
                    x.x2 + lambda * dx2
                )

                // Вот тут используется fixTinyNegativeCoordinate
                val candidate = fixTinyNegativeCoordinate(rawCandidate)

                if (insideArea(candidate)) {
                    val candidateF = values(candidate)

                    if (good(candidateF[0]) && good(candidateF[1])) {
                        val candidateNorm = norm(candidateF[0], candidateF[1])

                        if (candidateNorm < currentNorm || candidateNorm < eps) {
                            accepted = candidate
                            break
                        }
                    }
                }

                lambda /= 2.0
            }

            val nextX = accepted
                ?: return NewtonResult(
                    null,
                    iter - 1,
                    path,
                    false,
                    "Не удалось подобрать допустимый шаг Ньютона"
                )

            val nextF = values(nextX)

            if (!good(nextF[0]) || !good(nextF[1])) {
                return NewtonResult(
                    null,
                    iter,
                    path,
                    false,
                    "Функция не определена после шага Ньютона"
                )
            }

            val nextNorm = norm(nextF[0], nextF[1])
            val stepNorm = norm(nextX.x1 - x.x1, nextX.x2 - x.x2)

            val hasCycle = usedPoints.any { old ->
                norm(nextX.x1 - old.x1, nextX.x2 - old.x2) < eps && nextNorm >= eps
            }

            if (hasCycle) {
                return NewtonResult(
                    null,
                    iter,
                    path,
                    false,
                    "Обнаружено зацикливание"
                )
            }

            path.add(nextX)
            usedPoints.add(nextX)

            if (nextNorm < eps || stepNorm < eps) {
                return NewtonResult(
                    nextX,
                    iter,
                    path,
                    true,
                    "Критерий сходимости выполнен"
                )
            }

            x = nextX
        }

        return NewtonResult(
            null,
            maxIter,
            path,
            false,
            "Превышено максимальное число итераций"
        )
    }
}