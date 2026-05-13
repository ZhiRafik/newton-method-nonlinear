import Systems.SystemVariant1
import Systems.SystemVariant2
import javax.swing.JFrame
import javax.swing.JOptionPane
import kotlin.math.abs



fun formatPoint(p: Vector2D): String {
    return "(${String.format("%.12e", p.x1)}; ${String.format("%.12e", p.x2)})"
}

fun main() {
    val systemOptions = arrayOf("Вариант 2", "Вариант 1")

    val systemChoice = JOptionPane.showOptionDialog(
        null,
        "Выберите систему уравнений",
        "Задание 3",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        systemOptions,
        systemOptions[0]
    )

    if (systemChoice == -1) return

    val selectedSystem = if (systemOptions[systemChoice] == "Вариант 1") SystemVariant1() else SystemVariant2()

    val jacobianOptions = arrayOf(
        "Аналитическая матрица Якоби",
        "Численная матрица Якоби"
    )

    val jacobianChoice = JOptionPane.showOptionDialog(
        null,
        "Выберите способ вычисления матрицы Якоби",
        "Матрица Якоби",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        jacobianOptions,
        jacobianOptions[0]
    )

    if (jacobianChoice == -1) return

    val jacobianMode = if (jacobianChoice == 0) {
        JacobianMode.ANALYTIC
    } else {
        JacobianMode.NUMERIC
    }

    val selectedModeText = if (jacobianMode == JacobianMode.ANALYTIC) {
        "аналитический Якоби"
    } else {
        "численный Якоби"
    }

    val solver = NewtonSolver(selectedSystem, jacobianMode)

    val frame = JFrame("Графический анализ: f1=0 желтый, f2=0 голубой, $selectedModeText")

    val plotPanel = PlotPanel(selectedSystem, jacobianMode)

    frame.add(plotPanel)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(650, 680)
    frame.isVisible = true

    while (true) {
        val input = JOptionPane.showInputDialog(
            frame,
            "Введите начальное приближение через пробел (x1 x2):\nНажмите Esc для выхода.",
            "Расчет методом Ньютона",
            JOptionPane.QUESTION_MESSAGE
        ) ?: break

        if (input.trim().isEmpty()) continue

        try {
            val coords = input
                .trim()
                .split(Regex("\\s+"))
                .map { it.replace(',', '.').toDouble() }

            if (coords.size < 2) throw IllegalArgumentException()

            val startPoint = Vector2D(coords[0], coords[1])

            val result = solver.solve(startPoint)

            val iterationsText = result.path.mapIndexed { index, point ->
                "X^($index) = ${formatPoint(point)}"
            }.joinToString("\n")

            val message = if (result.converged && result.root != null) {
                """
                Начальная точка: ${formatPoint(startPoint)}
                Способ Якоби: $selectedModeText
                Корень найден: ${formatPoint(result.root)}
                Итераций: ${result.iterations}
                Причина остановки: ${result.message}

                Последовательные приближения:
                $iterationsText
                """.trimIndent()
            } else {
                """
                Начальная точка: ${formatPoint(startPoint)}
                Способ Якоби: $selectedModeText
                Метод не сошелся.
                Итераций: ${result.iterations}
                Причина: ${result.message}

                Последовательные приближения:
                $iterationsText
                """.trimIndent()
            }

            JOptionPane.showMessageDialog(frame, message)

            println(message)
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                frame,
                "Ошибка ввода! Введите два числа через пробел, например: 0.1 4.0",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    frame.dispose()
    System.exit(0)
}