// Интерфейс для системы уравнений
interface NonlinearSystem {
    fun f1(x: Vector2D): Double
    fun f2(x: Vector2D): Double
    fun jacobian(x: Vector2D): Array<DoubleArray> // Аналитическая матрица Якоби
}