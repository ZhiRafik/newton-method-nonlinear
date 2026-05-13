data class NewtonResult(
    val root: Vector2D?,
    val iterations: Int,
    val path: List<Vector2D>,
    val converged: Boolean,
    val message: String
)