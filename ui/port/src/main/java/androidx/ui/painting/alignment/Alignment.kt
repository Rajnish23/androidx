package androidx.ui.painting.alignment

import androidx.ui.engine.geometry.Offset
import androidx.ui.engine.geometry.Rect
import androidx.ui.engine.geometry.Size
import androidx.ui.lerpDouble
import androidx.ui.text.TextDirection
import androidx.ui.toStringAsFixed
import androidx.ui.truncDiv

// / A point within a rectangle.
// /
// / `Alignment(0.0, 0.0)` represents the center of the rectangle. The distance
// / from -1.0 to +1.0 is the distance from one side of the rectangle to the
// / other side of the rectangle. Therefore, 2.0 units horizontally (or
// / vertically) is equivalent to the width (or height) of the rectangle.
// /
// / `Alignment(-1.0, -1.0)` represents the top left of the rectangle.
// /
// / `Alignment(1.0, 1.0)` represents the bottom right of the rectangle.
// /
// / `Alignment(0.0, 3.0)` represents a point that is horizontally centered with
// / respect to the rectangle and vertically below the bottom of the rectangle by
// / the height of the rectangle.
// /
// / [Alignment] use visual coordinates, which means increasing [x] moves the
// / point from left to right. To support layouts with a right-to-left
// / [TextDirection], consider using [AlignmentDirectional], in which the
// / direction the point moves when increasing the horizontal value depends on
// / the [TextDirection].
// /
// / A variety of widgets use [Alignment] in their configuration, most
// / notably:
// /
// /  * [Align] positions a child according to a [Alignment].
// /
// / See also:
// /
// /  * [AlignmentDirectional], which has a horizontal coordinate orientation
// /    that depends on the [TextDirection].
// /  * [AlignmentGeometry], which is an abstract type that is agnostic as to
// /    whether the horizontal direction depends on the [TextDirection].
class Alignment(
        // / The distance fraction in the horizontal direction.
        // /
        // / A value of -1.0 corresponds to the leftmost edge. A value of 1.0
        // / corresponds to the rightmost edge. Values are not limited to that range;
        // / values less than -1.0 represent positions to the left of the left edge,
        // / and values greater than 1.0 represent positions to the right of the right
        // / edge.
    val x: Double,
        // / The distance fraction in the vertical direction.
        // /
        // / A value of -1.0 corresponds to the topmost edge. A value of 1.0
        // / corresponds to the bottommost edge. Values are not limited to that range;
        // / values less than -1.0 represent positions above the top, and values
        // / greater than 1.0 represent positions below the bottom.
    val y: Double

) : AlignmentGeometry() {

    init {
        assert(x != null)
        assert(y != null)
    }

    companion object {
        // / The top left corner.
        val topLeft = Alignment(-1.0, -1.0)

        // / The center point along the top edge.
        val topCenter = Alignment(0.0, -1.0)

        // / The top right corner.
        val topRight = Alignment(1.0, -1.0)

        // / The center point along the left edge.
        val centerLeft = Alignment(-1.0, 0.0)

        // / The center point, both horizontally and vertically.
        val center = Alignment(0.0, 0.0)

        // / The center point along the right edge.
        val centerRight = Alignment(1.0, 0.0)

        // / The bottom left corner.
        val bottomLeft = Alignment(-1.0, 1.0)

        // / The center point along the bottom edge.
        val bottomCenter = Alignment(0.0, 1.0)

        // / The bottom right corner.
        val bottomRight = Alignment(1.0, 1.0)

        // / Linearly interpolate between two [Alignment]s.
        // /
        // / If either is null, this function interpolates from [Alignment.center].
        // /
        // / The `t` argument represents position on the timeline, with 0.0 meaning
        // / that the interpolation has not started, returning `a` (or something
        // / equivalent to `a`), 1.0 meaning that the interpolation has finished,
        // / returning `b` (or something equivalent to `b`), and values in between
        // / meaning that the interpolation is at the relevant point on the timeline
        // / between `a` and `b`. The interpolation can be extrapolated beyond 0.0 and
        // / 1.0, so negative values and values greater than 1.0 are valid (and can
        // / easily be generated by curves such as [Curves.elasticInOut]).
        // /
        // / Values for `t` are usually obtained from an [Animation<double>], such as
        // / an [AnimationController].
        fun lerp(a: Alignment?, b: Alignment?, t: Double): Alignment? {
            assert(t != null)
            if (a == null && b == null)
                return null
            if (a == null)
                return Alignment(lerpDouble(0.0, b!!.x, t), lerpDouble(0.0, b.y, t))
            if (b == null)
                return Alignment(lerpDouble(a.x, 0.0, t), lerpDouble(a.y, 0.0, t))
            return Alignment(lerpDouble(a.x, b.x, t), lerpDouble(a.y, b.y, t))
        }

        internal fun _stringify(x: Double, y: Double): String {
            if (x == -1.0 && y == -1.0)
                return "topLeft"
            if (x == 0.0 && y == -1.0)
                return "topCenter"
            if (x == 1.0 && y == -1.0)
                return "topRight"
            if (x == -1.0 && y == 0.0)
                return "centerLeft"
            if (x == 0.0 && y == 0.0)
                return "center"
            if (x == 1.0 && y == 0.0)
                return "centerRight"
            if (x == -1.0 && y == 1.0)
                return "bottomLeft"
            if (x == 0.0 && y == 1.0)
                return "bottomCenter"
            if (x == 1.0 && y == 1.0)
                return "bottomRight"
            return "Alignment(${x.toStringAsFixed(1)}, " +
                    "${y.toStringAsFixed(1)})"
        }
    }

    override val _x: Double = x

    override val _start: Double = 0.0

    override val _y: Double = y

    override fun add(other: AlignmentGeometry): AlignmentGeometry {
        if (other is Alignment)
            return this + other
        return super.add(other)
    }

    // / Returns the difference between two [Alignment]s.
    operator fun minus(other: Alignment): Alignment {
        return Alignment(x - other.x, y - other.y)
    }

    // / Returns the sum of two [Alignment]s.
    operator fun plus(other: Alignment): Alignment {
        return Alignment(x + other.x, y + other.y)
    }

    // / Returns the negation of the given [Alignment].
    override operator fun unaryMinus(): Alignment {
        return Alignment(-x, -y)
    }

    // / Scales the [Alignment] in each dimension by the given factor.
    override operator fun times(other: Double): Alignment {
        return Alignment(x * other, y * other)
    }

    // / Divides the [Alignment] in each dimension by the given factor.
    override operator fun div(other: Double): Alignment {
        return Alignment(x / other, y / other)
    }

    // / Integer divides the [Alignment] in each dimension by the given factor.
    override fun truncDiv(other: Double): Alignment {
        return Alignment(x.truncDiv(other).toDouble(), y.truncDiv(other).toDouble())
    }

    // / Computes the remainder in each dimension by the given factor.
    override operator fun rem(other: Double): Alignment {
        return Alignment(x % other, y % other)
    }

    // / Returns the offset that is this fraction in the direction of the given offset.
    fun alongOffset(other: Offset): Offset {
        val centerX = other.dx / 2.0
        val centerY = other.dy / 2.0
        return Offset(centerX + x * centerX, centerY + y * centerY)
    }

    // / Returns the offset that is this fraction within the given size.
    fun alongSize(other: Size): Offset {
        val centerX = other.width / 2.0
        val centerY = other.height / 2.0
        return Offset(centerX + x * centerX, centerY + y * centerY)
    }

    // / Returns the point that is this fraction within the given rect.
    fun withinRect(rect: Rect): Offset {
        val halfWidth = rect.width / 2.0
        val halfHeight = rect.height / 2.0
        return Offset(
            rect.left + halfWidth + x * halfWidth,
            rect.top + halfHeight + y * halfHeight
        )
    }

//    /// Returns a rect of the given size, aligned within given rect as specified
//    /// by this alignment.
//    ///
//    /// For example, a 100×100 size inscribed on a 200×200 rect using
//    /// [Alignment.topLeft] would be the 100×100 rect at the top left of
//    /// the 200×200 rect.
//    Rect inscribe(Size size, Rect rect) {
//        final double halfWidthDelta = (rect.width - size.width) / 2.0;
//        final double halfHeightDelta = (rect.height - size.height) / 2.0;
//        return new Rect.fromLTWH(
//                rect.left + halfWidthDelta + x * halfWidthDelta,
//        rect.top + halfHeightDelta + y * halfHeightDelta,
//        size.width,
//        size.height,
//        );
//    }

    override fun resolve(direction: TextDirection?): Alignment = this

    override fun toString() = _stringify(x, y)
}