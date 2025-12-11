package dev.pace.cs639project.data

import android.content.Context
import kotlin.collections.setOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.ZonedDateTime
import java.time.LocalDate

class HealthApiRepository(
    private val context: Context,
    private val healthConnectClient: HealthConnectClient 
) {

    private val stepsPermission = HealthPermission.getReadPermission(StepsRecord::class)
    private val permissionsSet = setOf(stepsPermission)


    suspend fun arePermissionsGranted(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissionsSet)
    }

    suspend fun getStepsForDay(day: LocalDate): Long {
        if (!arePermissionsGranted()) return 0L 

        val startOfDay = ZonedDateTime.of(day.atStartOfDay(), ZonedDateTime.now().zone).toInstant()
        val endOfDay = startOfDay.plusSeconds(86399)

        val timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)

        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = timeRangeFilter,
                dataOriginFilter = emptySet()
            )
        )

        return response[StepsRecord.COUNT_TOTAL] ?: 0L
    }
}