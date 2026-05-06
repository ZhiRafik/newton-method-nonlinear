package Systems

import NonlinearSystem
import Vector2D
import kotlin.math.sqrt

// Вариант 2
class SystemVariant2 : NonlinearSystem {
    override fun f1(x: Vector2D): Double {
        val inner = sqrt(x.x1.coerceAtLeast(0.0)) + sqrt(x.x2.coerceAtLeast(0.0))
        return sqrt((inner - 2 * x.x1).coerceAtLeast(0.0)) - 0.5 * inner
    }
    override fun f2(x: Vector2D) = x.x1 - 0.01 * sqrt(x.x2.coerceAtLeast(0.0)) - 0.2

    override fun jacobian(x: Vector2D): Array<DoubleArray> {
        val h = 1e-7
        val df1dx1 = (f1(Vector2D(x.x1 + h, x.x2)) - f1(x)) / h
        val df1dx2 = (f1(Vector2D(x.x1, x.x2 + h)) - f1(x)) / h
        val df2dx1 = (f2(Vector2D(x.x1 + h, x.x2)) - f2(x)) / h
        val df2dx2 = (f2(Vector2D(x.x1, x.x2 + h)) - f2(x)) / h
        return arrayOf(doubleArrayOf(df1dx1, df1dx2), doubleArrayOf(df2dx1, df2dx2))
    }
}