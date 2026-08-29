package com.example.data.repository

import com.example.data.local.CalculationEntity
import com.example.data.local.HistoryDao
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<CalculationEntity>> = historyDao.getAllHistory()
    val favoriteHistory: Flow<List<CalculationEntity>> = historyDao.getFavoriteHistory()

    fun search(query: String): Flow<List<CalculationEntity>> = historyDao.searchHistory(query)

    suspend fun insert(expression: String, result: String, category: String = "Standard"): Long {
        return historyDao.insertCalculation(
            CalculationEntity(
                expression = expression,
                result = result,
                category = category
            )
        )
    }

    suspend fun update(entity: CalculationEntity) {
        historyDao.updateCalculation(entity)
    }

    suspend fun toggleFavorite(entity: CalculationEntity) {
        historyDao.updateCalculation(entity.copy(isFavorite = !entity.isFavorite))
    }

    suspend fun updateNote(entity: CalculationEntity, note: String) {
        historyDao.updateCalculation(entity.copy(note = note))
    }

    suspend fun delete(entity: CalculationEntity) {
        historyDao.deleteCalculation(entity)
    }

    suspend fun deleteById(id: Long) {
        historyDao.deleteById(id)
    }

    suspend fun clearAll() {
        historyDao.clearAllHistory()
    }
}
