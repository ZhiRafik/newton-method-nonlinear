package Systems

import NonlinearSystem
import Vector2D
import kotlin.math.sqrt

// Вариант 1
class SystemVariant1 : NonlinearSystem {
    override fun f1(x: Vector2D) = 3 * sqrt(x.x1.coerceAtLeast(0.0)) - 2 * x.x2 + 8
    override fun f2(x: Vector2D) = x.x1 + x.x2 - 4

    override fun jacobian(x: Vector2D): Array<DoubleArray> {
        val df1dx1 = 3.0 / (2.0 * sqrt(x.x1.coerceAtLeast(1e-9)))
        val df1dx2 = -2.0
        val df2dx1 = 1.0
        val df2dx2 = 1.0
        return arrayOf(doubleArrayOf(df1dx1, df1dx2), doubleArrayOf(df2dx1, df2dx2))
    }
}