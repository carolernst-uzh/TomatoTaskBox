package com.example.tomatotaskbox.data

import androidx.room.*
import com.example.tomatotaskbox.models.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): Category?

    @Query("SELECT * FROM categories WHERE parentCategoryId IS NULL")
    suspend fun getRootCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE parentCategoryId = :parentId")
    suspend fun getSubcategories(parentId: Long): List<Category>
}