package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Delivery
import com.example.data.DeliveryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryViewModel(private val repository: DeliveryRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val deliveriesState: StateFlow<List<Delivery>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllDeliveries()
            } else {
                repository.searchDeliveries(query.trim())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addDelivery(
        customerName: String,
        phone: String,
        pin: String,
        quantity: Int,
        deliveryDate: String
    ) {
        viewModelScope.launch {
            val delivery = Delivery(
                customerName = customerName,
                phone = phone,
                pin = pin,
                quantity = quantity,
                deliveryDate = deliveryDate
            )
            repository.insertDelivery(delivery)
        }
    }

    fun deleteDelivery(delivery: Delivery) {
        viewModelScope.launch {
            repository.deleteDelivery(delivery)
        }
    }
}

class DeliveryViewModelFactory(private val repository: DeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeliveryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeliveryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
