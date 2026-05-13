package Systems

import NonlinearSystem
import Vector2D
import kotlin.math.sqrt

// Вариант 1:
// f1(x1, x2) = 3 * sqrt(x1) - 2 * x2 + 8
// f2(x1, x2) = x1 + x2 - 4
class SystemVariant1 : NonlinearSystem {

    override fun f1(x: Vector2D): Double {
        if (x.x1 < 0.0) return Double.NaN
        return 3.0 * sqrt(x.x1) - 2.0 * x.x2 + 8.0
    }

    override fun f2(x: Vector2D): Double {
        return x.x1 + x.x2 - 4.0
    }

    override fun analyticJacobian(x: Vector2D): Array<DoubleArray> {
        // Производная sqrt(x1) по x1 не существует при x1 <= 0.
        if (x.x1 <= 0.0) {
            return arrayOf(
                doubleArrayOf(Double.NaN, Double.NaN),
                doubleArrayOf(Double.NaN, Double.NaN)
            )
        }

        val df1dx1 = 3.0 / (2.0 * sqrt(x.x1))
        val df1dx2 = -2.0

        val df2dx1 = 1.0
        val df2dx2 = 1.0

        return arrayOf(
            doubleArrayOf(df1dx1, df1dx2),
            doubleArrayOf(df2dx1, df2dx2)
        )
    }
}