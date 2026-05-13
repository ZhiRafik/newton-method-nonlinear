// Интерфейс для системы двух нелинейных уравнений
interface NonlinearSystem {
    fun f1(x: Vector2D): Double
    fun f2(x: Vector2D): Double

    // Аналитическая матрица Якоби
    fun analyticJacobian(x: Vector2D): Array<DoubleArray>

    // Численная матрица Якоби через конечные разности.
    // Сначала пробуем центральную разность, если точка выходит из области определения — одностороннюю.
    fun numericJacobian(x: Vector2D, h: Double = 1e-6): Array<DoubleArray> {
        fun good(v: Double): Boolean = !v.isNaN() && !v.isInfinite()

        fun derivative(functionIndex: Int, variableIndex: Int): Double {
            val f: (Vector2D) -> Double = if (functionIndex == 0) ::f1 else ::f2
            val base = f(x)

            if (!good(base)) return Double.NaN

            val plus = if (variableIndex == 0) {
                Vector2D(x.x1 + h, x.x2)
            } else {
                Vector2D(x.x1, x.x2 + h)
            }

            val minus = if (variableIndex == 0) {
                Vector2D(x.x1 - h, x.x2)
            } else {
                Vector2D(x.x1, x.x2 - h)
            }

            val fPlus = f(plus)
            val fMinus = f(minus)

            return when {
                good(fPlus) && good(fMinus) -> (fPlus - fMinus) / (2.0 * h)
                good(fPlus) -> (fPlus - base) / h
                good(fMinus) -> (base - fMinus) / h
                else -> Double.NaN
            }
        }

        return arrayOf(
            doubleArrayOf(derivative(0, 0), derivative(0, 1)),
            doubleArrayOf(derivative(1, 0), derivative(1, 1))
        )
    }

    fun jacobian(x: Vector2D, mode: JacobianMode): Array<DoubleArray> {
        return when (mode) {
            JacobianMode.ANALYTIC -> analyticJacobian(x)
            JacobianMode.NUMERIC -> numericJacobian(x)
        }
    }
}