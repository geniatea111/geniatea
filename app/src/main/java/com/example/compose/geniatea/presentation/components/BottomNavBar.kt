package com.example.compose.geniatea.presentation.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation

class BottomNavBar(
    private val dockRadius: Float,   // Size of the cutout
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {


        // baseRect: Creates a rounded rectangle covering the entire size, with rounded top corners. This is the basic shape of the bottom navigation.
        val baseRect = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(Offset.Zero, Offset(size.width, size.height))
                ),
            )
        }

        // rect1: Creates a rectangle from the left edge to just before the center "dock", filling the full height.  Will be cut later to add a corner radius
        val rect1 = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(Offset.Zero, Offset(size.width / 2 - dockRadius + 1f, size.height))),
            )
        }

        // rect1A: Same rectangle as rect1, but has a smaller topLeft Radius to create a small difference
        val rect1A = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(Offset.Zero, Offset(size.width / 2 - dockRadius + 1f, size.height)),
                ),
            )
        }

        // rect1B: This calculates the area between rect1 and rect1A and is one of the path that cuts into baseRect.
        val rect1B = Path.combine(
            operation = PathOperation.Difference,
            path1 = rect1,
            path2 = rect1A,
        )

        // rect2: Creates a rectangle from just after the center "dock" to the right edge, filling the full height.
        val rect2 = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(Offset(size.width / 2 + dockRadius - 1f, 1f), Offset(size.width, size.height)),
                ),
            )
        }

        // rect2A: Same rectangle as rect2, but has a smaller topRight Radius to create a small difference
        val rect2A = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(Offset(size.width / 2 + dockRadius - 1f, 1f), Offset(size.width, size.height)),
                ),
            )
        }

        // rect2B: This calculates the area between rect2 and rect2A and is one of the path that cuts into baseRect.
        val rect2B = Path.combine(
            operation = PathOperation.Difference,
            path1 = rect2,
            path2 = rect2A,
        )

        val cutoutWidth = dockRadius * 0.63f   // how wide the cutout is
        val cutoutHeight = dockRadius * 0.6f  // how tall the cutout is
        val cornerRadius = dockRadius * 0.4f  // corner roundness

        val rectCutout = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        Offset(size.width / 2 - cutoutWidth, -cutoutHeight),
                        Offset(size.width / 2 + cutoutWidth, cutoutHeight)
                    ),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            )
        }

        // path1: Subtracts the "circle" path from the base rectangle, creating the main shape with the cut-out for the dock.
        val path1 = Path.combine(
            operation = PathOperation.Difference,
            path1 = baseRect,
            path2 = rectCutout,
        )

        // path2: Subtracts the "rect1B" from the base rectangle, create more curvature on the sides
        val path2 = Path.combine(
            operation = PathOperation.Difference,
            path1 = path1,
            path2 = rect1B,
        )

        // path: Subtracts the "rect2B" from the base rectangle, create more curvature on the sides
        val path = Path.combine(
            operation = PathOperation.Difference,
            path1 = path2,
            path2 = rect2B,
        )

        // Return the final path
        return Outline.Generic(path)
    }
}