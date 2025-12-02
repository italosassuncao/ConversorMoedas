package com.example.conversormoedas.presentation.detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.conversormoedas.data.models.PriceHistory

// Componente Composable para desenhar um gráfico de linhas para o histórico de preços
@Composable
fun LineChart(
    data: List<PriceHistory>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Spacer(modifier = modifier)
        return
    }

    // Cor baseada na variaçao de preço
    val startPrice = data.firstOrNull()?.price ?: 0.0

    val endPrice = data.lastOrNull()?.price ?: 0.0

    val chartColor = if (endPrice >= startPrice) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.tertiary

    val secondaryColor = chartColor.copy(alpha = 0.2f)

    // Valores máximo e mínimo para escalonamento
    val prices = data.map { it.price }
    val minPrice = prices.minOrNull() ?: 0.0
    val maxPrice = prices.maxOrNull() ?: 1.0
    val priceRange = maxPrice - minPrice
    val normalizedData = data.map { it.price }

    Canvas(modifier = modifier.height(200.dp)) {
        // Largura e altura disponiveis para o desenho
        val width = size.width
        val height = size.height

        // Caminho para o gráfico de linhas e para o preenchimento de área
        val linePath = Path()
        val areaPath = Path()

        // Ponto de início
        areaPath.moveTo(0f, height)

        normalizedData.forEachIndexed { index, price ->
            val yNormalized = if (priceRange != 0.0) {
                (price - minPrice) / priceRange
            } else {
                0.5
            }

            val x = index * (width / (data.size - 1).toFloat())
            val y = height - (yNormalized * height).toFloat()

            if (index == 0) {
                linePath.moveTo(x, y)
                areaPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                areaPath.lineTo(x, y)
            }
        }

        areaPath.lineTo(width, height)
        areaPath.lineTo(0f, height)
        areaPath.close()

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(chartColor.copy(alpha = 0.4f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        drawPath(
            path = linePath,
            color = chartColor,
            style = Stroke(width = 4.dp.toPx())
        )
    }
}