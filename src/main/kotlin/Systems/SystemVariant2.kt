package Systems

import NonlinearSystem
import Vector2D
import kotlin.math.sqrt

// Вариант 2:
// f1(x1, x2) = sqrt(sqrt(x1) + sqrt(x2) - 2*x1) - 0.5*(sqrt(x1) + sqrt(x2))
// f2(x1, x2) = x1 - 0.01*sqrt(x2) - 0.2
class SystemVariant2 : NonlinearSystem {

    override fun f1(x: Vector2D): Double {
        // Область определения:
        // x1 >= 0, x2 >= 0,
        // sqrt(x1) + sqrt(x2) - 2*x1 >= 0
        if (x.x1 < 0.0 || x.x2 < 0.0) return Double.NaN

        val s1 = sqrt(x.x1)
        val s2 = sqrt(x.x2)
        val inner = s1 + s2
        val radicand = inner - 2.0 * x.x1

        if (radicand < 0.0) return Double.NaN

        return sqrt(radicand) - 0.5 * inner
    }

    override fun f2(x: Vector2D): Double {
        // sqrt(x2) определён только при x2 >= 0
        if (x.x2 < 0.0) return Double.NaN
        return x.x1 - 0.01 * sqrt(x.x2) - 0.2
    }

    override fun analyticJacobian(x: Vector2D): Array<DoubleArray> {
        // Для аналитических производных нужны x1 > 0, x2 > 0,
        // а также radicand > 0, потому что в производных есть деление
        // на sqrt(x1), sqrt(x2), sqrt(radicand).
        if (x.x1 <= 0.0 || x.x2 <= 0.0) {
            return nanJacobian()
        }

        val s1 = sqrt(x.x1)
        val s2 = sqrt(x.x2)
        val inner = s1 + s2
        val radicand = inner - 2.0 * x.x1

        if (radicand <= 0.0) {
            return nanJacobian()
        }

        val r = sqrt(radicand)

        // f1 = sqrt(radicand) - 0.5*(sqrt(x1)+sqrt(x2))
        // radicand = sqrt(x1) + sqrt(x2) - 2*x1
        val df1dx1 = (1.0 / (2.0 * r)) * (1.0 / (2.0 * s1) - 2.0) - 1.0 / (4.0 * s1)
        val df1dx2 = (1.0 / (2.0 * r)) * (1.0 / (2.0 * s2)) - 1.0 / (4.0 * s2)

        // f2 = x1 - 0.01*sqrt(x2) - 0.2
        val df2dx1 = 1.0
        val df2dx2 = -0.01 / (2.0 * s2)

        return arrayOf(
            doubleArrayOf(df1dx1, df1dx2),
            doubleArrayOf(df2dx1, df2dx2)
        )
    }

    private fun nanJacobian(): Array<DoubleArray> {
        return arrayOf(
            doubleArrayOf(Double.NaN, Double.NaN),
            doubleArrayOf(Double.NaN, Double.NaN)
        )
    }
}