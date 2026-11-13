package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 ORDER BY isFeatured DESC, id DESC")
    fun getApprovedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 AND isNational = :isNational ORDER BY id DESC")
    fun getProductsByScope(isNational: Boolean): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 AND category = :category ORDER BY id DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 AND sellerType = :sellerType ORDER BY id DESC")
    fun getProductsBySellerType(sellerType: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY id DESC")
    fun getSellerProducts(sellerId: Long): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET isApproved = :approved WHERE id = :id")
    suspend fun setProductApproval(id: Long, approved: Boolean)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun removeCartItem(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getOrdersForSeller(sellerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderCode = :orderCode")
    suspend fun getOrderByCode(orderCode: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status, deliveredAt = :deliveredAt WHERE orderCode = :orderCode")
    suspend fun updateOrderStatus(orderCode: String, status: String, deliveredAt: Long? = null)

    @Query("UPDATE orders SET paymentStatus = :status, completedAt = :completedAt WHERE orderCode = :orderCode")
    suspend fun updateOrderPaymentStatus(orderCode: String, status: String, completedAt: Long? = null)
}

@Dao
interface FinanceDao {
    @Query("SELECT * FROM ledger_entries ORDER BY timestamp DESC")
    fun getAllLedgerEntries(): Flow<List<LedgerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: LedgerEntryEntity): Long

    @Query("SELECT * FROM withdrawals ORDER BY requestedAt DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE beneficiaryType = :type ORDER BY requestedAt DESC")
    fun getWithdrawalsByType(type: String): Flow<List<WithdrawalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity): Long

    @Query("UPDATE withdrawals SET status = :status, processedAt = :processedAt WHERE id = :id")
    suspend fun updateWithdrawalStatus(id: Long, status: String, processedAt: Long = System.currentTimeMillis())
}

@Dao
interface AffiliateDao {
    @Query("SELECT * FROM affiliate_links ORDER BY createdAt DESC")
    fun getAllAffiliateLinks(): Flow<List<AffiliateLinkEntity>>

    @Query("SELECT * FROM affiliate_links WHERE affiliateCode = :code ORDER BY createdAt DESC")
    fun getLinksForAffiliate(code: String): Flow<List<AffiliateLinkEntity>>

    @Query("SELECT * FROM affiliate_links WHERE productId = :productId AND affiliateCode = :code")
    suspend fun getLinkByProductAndCode(productId: Long, code: String): AffiliateLinkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAffiliateLink(link: AffiliateLinkEntity): Long

    @Query("UPDATE affiliate_links SET clicks = clicks + 1 WHERE id = :id")
    suspend fun recordClick(id: Long)

    @Query("UPDATE affiliate_links SET conversions = conversions + 1, totalEarnedMzn = totalEarnedMzn + :commission WHERE affiliateCode = :code AND productId = :productId")
    suspend fun recordConversion(code: String, productId: Long, commission: Double)
}

@Dao
interface DisputeDao {
    @Query("SELECT * FROM disputes ORDER BY createdAt DESC")
    fun getAllDisputes(): Flow<List<DisputeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: DisputeEntity): Long

    @Query("UPDATE disputes SET status = :status, adminNotes = :notes WHERE id = :id")
    suspend fun resolveDispute(id: Long, status: String, notes: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE targetRole = :role ORDER BY timestamp DESC")
    fun getNotificationsForRole(role: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)
}

@Dao
interface UserRegistrationDao {
    @Query("SELECT * FROM user_registrations ORDER BY registeredAt DESC")
    fun getAllRegistrations(): Flow<List<UserRegistrationEntity>>

    @Query("SELECT * FROM user_registrations WHERE status = 'PENDING_APPROVAL' ORDER BY registeredAt DESC")
    fun getPendingRegistrations(): Flow<List<UserRegistrationEntity>>

    @Query("SELECT * FROM user_registrations WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getRegistrationByPhone(phone: String): UserRegistrationEntity?

    @Query("SELECT * FROM user_registrations WHERE id = :id")
    suspend fun getRegistrationById(id: Long): UserRegistrationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: UserRegistrationEntity): Long

    @Query("UPDATE user_registrations SET status = :status, reviewerNotes = :notes, approvedAt = :approvedAt WHERE id = :id")
    suspend fun updateRegistrationStatus(
        id: Long,
        status: String,
        notes: String,
        approvedAt: Long = System.currentTimeMillis()
    )
}

