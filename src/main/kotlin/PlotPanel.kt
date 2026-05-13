import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.ceil

// Визуализация графиков f1 = 0, f2 = 0 и диаграммы сходимости.
class PlotPanel(
    private val system: NonlinearSystem,
    private val jacobianMode: JacobianMode
) : JPanel() {
    private val solver = NewtonSolver(system, jacobianMode)
    private val maxIterations = 30
    private val gridN = 160

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2 = g as Graphics2D
        val w = width
        val h = height

        drawConvergenceDiagram(g2, w, h)
        drawAxes(g2, w, h)
        drawFunctionLines(g2, w, h)
    }

    private fun toScreenX(x1: Double, w: Int): Int {
        return ((x1 + 10.0) / 20.0 * w).toInt()
    }

    private fun toScreenY(x2: Double, h: Int): Int {
        return ((10.0 - x2) / 20.0 * h).toInt()
    }

    private fun drawConvergenceDiagram(g2: Graphics2D, w: Int, h: Int) {
        val mathStep = 20.0 / gridN
        val cellW = ceil(w.toDouble() / gridN).toInt()
        val cellH = ceil(h.toDouble() / gridN).toInt()

        for (i in 0 until gridN) {
            for (j in 0 until gridN) {
                val x1 = -10.0 + i * mathStep
                val x2 = -10.0 + j * mathStep

                val result = solver.solve(Vector2D(x1, x2), maxIterations)

                g2.color = if (!result.converged) {
                    Color.BLACK
                } else {
                    val ratio = result.iterations.toFloat() / maxIterations
                    val green = (255 * (1.0f - ratio)).toInt().coerceIn(50, 255)
                    Color(0, green, 0)
                }

                val px = toScreenX(x1 - mathStep / 2.0, w)
                val py = toScreenY(x2 + mathStep / 2.0, h)

                g2.fillRect(px, py, cellW + 1, cellH + 1)
            }
        }
    }

    private fun drawAxes(g2: Graphics2D, w: Int, h: Int) {
        g2.color = Color.DARK_GRAY

        g2.drawLine(0, h / 2, w, h / 2)
        g2.drawLine(w / 2, 0, w / 2, h)

        for (marker in -10..10) {
            val markX = toScreenX(marker.toDouble(), w)
            val markY = toScreenY(marker.toDouble(), h)

            g2.drawLine(markX, h / 2 - 5, markX, h / 2 + 5)
            g2.drawLine(w / 2 - 5, markY, w / 2 + 5, markY)
        }
    }

    private fun drawFunctionLines(g2: Graphics2D, w: Int, h: Int) {
        for (i in 0 until w) {
            for (j in 0 until h) {
                val x1 = -10.0 + 20.0 * i / w
                val x2 = 10.0 - 20.0 * j / h

                val v = Vector2D(x1, x2)

                val value1 = system.f1(v)
                val value2 = system.f2(v)

                if (!value1.isNaN() && !value1.isInfinite() && abs(value1) < 0.1) {
                    g2.color = Color.YELLOW
                    g2.drawRect(i, j, 1, 1)
                }

                if (!value2.isNaN() && !value2.isInfinite() && abs(value2) < 0.1) {
                    g2.color = Color.CYAN
                    g2.drawRect(i, j, 1, 1)
                }
            }
        }
    }
}