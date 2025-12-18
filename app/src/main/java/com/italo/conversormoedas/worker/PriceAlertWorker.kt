package com.italo.conversormoedas.worker

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.italo.conversormoedas.data.local.AlertDao
import com.italo.conversormoedas.data.models.QuotationApiService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import android.app.NotificationManager
import androidx.core.app.NotificationCompat

/**
 * Worker responsável por verificar preços em segundo plano e notificar o usuário.
 */
class PriceAlertWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val alertDao: AlertDao by inject()
    private val apiService: QuotationApiService by inject()

    override suspend fun doWork(): Result {
        val activeAlerts = alertDao.getActiveAlerts()
        if (activeAlerts.isEmpty()) return Result.success()

        try {
            // Procura os preços atuais para as cryptos nos alertas
            val ids = activeAlerts.map { it.quotationId }.distinct().joinToString(",")
            val response = apiService.getCryptoQuotations(currency = "usd")

            // Verifica cada alerta contra o preço retornado pela API
            activeAlerts.forEach { alert ->
                val currentAsset = response.find { it.id == alert.quotationId }
                currentAsset?.let {
                    val price = it.currentPrice
                    val triggered = if (alert.isAbove) price >= alert.targetPrice
                        else price <= alert.targetPrice

                    if (triggered) {
                        showNotification(alert.symbol,
                            price,
                            alert.isAbove, alert.targetPrice)
                        alertDao.updateAlert(alert.copy(isActive = false))
                    }
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun showNotification(symbol: String, currentPrice: Double, isAbove: Boolean, target: Double) {
        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "price_alerts_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Preço",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val direction = if (isAbove) "subiu para" else "caiu para"
        val message = "O $symbol $direction $currentPrice (Alvo: $target)"

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Substituir por ícone real
            .setContentTitle("Alerta de Preço Atingido!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}