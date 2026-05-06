import Systems.SystemVariant1
import Systems.SystemVariant2
import javax.swing.*

fun main() {
    val options = arrayOf("Вариант 1", "Вариант 2")
    val choice = JOptionPane.showOptionDialog(null, "Выберите систему уравнений", "Задание 3",
        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0])

    // Если пользователь закрыл окно выбора варианта
    if (choice == -1) return

    val selectedSystem = if (choice == 0) SystemVariant1() else SystemVariant2()
    val solver = NewtonSolver(selectedSystem)

    val frame = JFrame("Графический анализ (f1-синий, f2-красный)")
    val plotPanel = PlotPanel(selectedSystem)
    frame.add(plotPanel)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(605, 630)
    frame.isVisible = true

    // Цикл для многократного ввода
    while (true) {
        val input = JOptionPane.showInputDialog(
            frame,
            "Введите начальное приближение через пробел (x1 x2):\nНажмите 'Esc' для выхода.",
            "Расчет методом Ньютона",
            JOptionPane.QUESTION_MESSAGE
        )

        // Если нажата кнопка "Отмена" или "Закрыть" (крестик) — выходим из цикла
        if (input == null) break

        if (input.trim().isEmpty()) continue

        try {
            val coords = input.trim().split(Regex("\\s+")).map { it.toDouble() }
            if (coords.size < 2) throw Exception("Нужно два числа")

            val startPoint = Vector2D(coords[0], coords[1])
            val (result, iterations) = solver.solve(startPoint)

            if (result != null) {
                val successMsg = """
                    Начальная точка: (${startPoint.x1}, ${startPoint.x2})
                    Корень найден: (${String.format("%.4f", result.x1)}, ${String.format("%.4f", result.x2)})
                    Итераций: $iterations
                """.trimIndent()

                JOptionPane.showMessageDialog(frame, successMsg)
                println(successMsg)
            } else {
                val failMsg = "Метод разошелся или вышел за границы за $iterations итераций."
                JOptionPane.showMessageDialog(frame, failMsg, "Ошибка сходимости", JOptionPane.WARNING_MESSAGE)
                println("Точка (${startPoint.x1}, ${startPoint.x2}): $failMsg")
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(frame, "Ошибка ввода! Введите два числа через пробел (например: 1.0 2.0)", "Ошибка", JOptionPane.ERROR_MESSAGE)
        }
    }

    frame.dispose()
    System.exit(0)
}