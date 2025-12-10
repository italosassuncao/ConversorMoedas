package com.example.conversormoedas.presentation.explore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.conversormoedas.data.models.Quotation
import java.text.DecimalFormat

// Componente Composable que representa um único item de cotação na lista.
@Composable
fun QuotationItem(
    quotation: Quotation,
    onItemClick: (Quotation) -> Unit
) {
    // Definir a cor da variação de preço
    val changeColor = if (quotation.priceChange24h >= 0) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    // Formatação de Preço e Porcentagem
    val priceFormatter = DecimalFormat("#,##0.00")
    val percentFormatter = DecimalFormat("0.00%")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(quotation) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                val painter = rememberAsyncImagePainter(
                    model = quotation.imageUrl
                        ?: "https://placehold.co/40x40/aaaaaa/ffffff?text=${quotation.symbol}"
                )
                Image(
                    painter = painter,
                    contentDescription = "${quotation.name} Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    /*bitmap = TODO(),
                    alignment = TODO(),
                    alpha = TODO(),
                    colorFilter = TODO(),
                    filterQuality = TODO()*/
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = quotation.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = quotation.symbol,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${priceFormatter.format(quotation.currentPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val sign = if (quotation.priceChange24h >= 0) "+" else ""
                    Text(
                        text = "$sign${percentFormatter.format(quotation.priceChange24h)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = changeColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}