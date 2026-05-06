import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel
import kotlin.math.abs

/*
- Зеленая область: Это набор всех начальных точек (x1, x2),
 из которых метод Ньютона смог успешно найти корень системы менее чем за 30 итераций, не выходя за границы

- Черная область: Это «зона смерти». Если выбрать начальную точку в черной зоне,
 метод Ньютона либо уйдет в бесконечность, либо зациклится, либо вылетит за пределы квадрата 10×10.

- Желтая и Голубая линии: Это графики функций f1 = 0 и f2 = 0
 Точка, где они пересекаются внутри зеленой области — это и есть искомый корень системы
 */
// Визуализация
class PlotPanel(val system: NonlinearSystem) : JPanel() {
    private val solver = NewtonSolver(system)
    private val maxIterations = 30
    private val step = 2 // Шаг сетки в пикселях

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        val w = width
        val h = height

        // 1. Отрисовка диаграммы сходимости (Задний план)
        for (i in 0 until w step step) {
            for (j in 0 until h step step) {
                // Пересчет экранных координат (i, j) в математические (x1, x2) от -10 до 10
                val x1 = (i - w / 2.0) / (w / 20.0)
                val x2 = (h / 2.0 - j) / (h / 20.0)

                val (result, iterations) = solver.solve(Vector2D(x1, x2), maxIterations)

                if (result == null) {
                    g2.color = Color.BLACK // Не сошлось или вылет
                } else {
                    // Цветовая шкала: чем меньше итераций, тем светлее
                    val ratio = iterations.toFloat() / maxIterations
                    val green = (255 * (1 - ratio)).toInt().coerceIn(50, 255)
                    g2.color = Color(0, green, 0) // Оттенки зеленого
                }
                // Рисуем квадрат сетки
                g2.fillRect(i, j, step, step)
            }
        }

        // 2. Отрисовка осей координат (поверх сетки)
        g2.color = Color.DARK_GRAY
        g2.drawLine(0, h / 2, w, h / 2)
        g2.drawLine(w / 2, 0, w / 2, h)

        // Добавим разметку
        for (marker in -10..10) {
            val markX = w / 2 + (marker * w / 20)
            val markY = h / 2 - (marker * h / 20)
            g2.drawLine(markX, h/2 - 5, markX, h/2 + 5)
            g2.drawLine(w/2 - 5, markY, w/2 + 5, markY)
        }

        // 3. Отрисовка линий функций (Передний план)
        drawFunctionLines(g2, w, h)
    }

    private fun drawFunctionLines(g2: Graphics2D, w: Int, h: Int) {
        for (i in 0 until w) {
            for (j in 0 until h) {
                val x1 = (i - w / 2.0) / (w / 20.0)
                val x2 = (h / 2.0 - j) / (h / 20.0)
                val v = Vector2D(x1, x2)

                // Используем яркие цвета, чтобы они были видны на зеленом/черном фоне
                if (abs(system.f1(v)) < 0.1) {
                    g2.color = Color.YELLOW // f1=0
                    g2.drawRect(i, j, 1, 1)
                }
                if (abs(system.f2(v)) < 0.1) {
                    g2.color = Color.CYAN // f2=0
                    g2.drawRect(i, j, 1, 1)
                }
            }
        }
    }
}