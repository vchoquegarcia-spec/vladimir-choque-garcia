package com.example.data

import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val deliveryDao: DeliveryDao) {
    fun getAllDeliveries(): Flow<List<Delivery>> {
        return deliveryDao.getAllDeliveries()
    }

    fun searchDeliveries(query: String): Flow<List<Delivery>> {
        return deliveryDao.searchDeliveries(query)
    }

    suspend fun insertDelivery(delivery: Delivery) {
        deliveryDao.insertDelivery(delivery)
    }

    suspend fun deleteDelivery(delivery: Delivery) {
        deliveryDao.deleteDelivery(delivery)
    }

    suspend fun deleteDeliveryById(id: Int) {
        deliveryDao.deleteDeliveryById(id)
    }
}
