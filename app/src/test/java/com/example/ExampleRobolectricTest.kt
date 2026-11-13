package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ProductEntity
import com.example.data.repository.MacumuRepository
import com.example.util.CurrencyUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies Macumu Global app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Macumu Global", appName)
    }

    @Test
    fun `test multi-currency conversions for MZN, ZAR, and USD`() {
        val formattedMzn = CurrencyUtils.formatPrice(1000.0, "MZN")
        assertTrue("Expected MZN formatting", formattedMzn.contains("MZN"))

        val formattedZar = CurrencyUtils.formatPrice(1000.0, "ZAR")
        assertTrue("Expected ZAR formatting", formattedZar.contains("R"))

        val formattedUsd = CurrencyUtils.formatPrice(1000.0, "USD")
        assertTrue("Expected USD formatting", formattedUsd.contains("$"))
    }

    @Test
    fun `test product creation and repository flow`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val repo = MacumuRepository(db)

        val newProductId = repo.createProduct(
            ProductEntity(
                title = "Castanha de Caju Torrada de Inhambane",
                description = "Castanha de alta qualidade",
                category = "Alimentação",
                priceMzn = 500.0,
                stock = 20,
                sellerId = 101L,
                sellerName = "Mamã Alda",
                sellerType = "INFORMAL",
                sellerLocation = "Inhambane",
                isNational = true
            )
        )

        assertTrue(newProductId > 0)
        val product = repo.getProductById(newProductId)
        assertNotNull(product)
        assertEquals("Castanha de Caju Torrada de Inhambane", product?.title)
        assertEquals("INFORMAL", product?.sellerType)
    }

    @Test
    fun `test market hubs catalog contains National International and Informal markets`() {
        val national = com.example.data.model.MarketHubCatalog.nationalMarkets
        assertTrue(national.any { it.name.contains("Maputo Shopping") })

        val international = com.example.data.model.MarketHubCatalog.internationalMarkets
        assertTrue(international.any { it.name.contains("Alibaba") })
        assertTrue(international.any { it.name.contains("Temu") })

        val informalSul = com.example.data.model.MarketHubCatalog.informalMarketsSul
        assertTrue(informalSul.any { it.name.contains("Xipamanine") })

        val informalCentro = com.example.data.model.MarketHubCatalog.informalMarketsCentro
        assertTrue(informalCentro.any { it.name.contains("Maquinino") })

        val informalNorte = com.example.data.model.MarketHubCatalog.informalMarketsNorte
        assertTrue(informalNorte.any { it.name.contains("Waresta") })
    }

    @Test
    fun `test user registration and developer approval workflow`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val repo = MacumuRepository(db)

        val regId = repo.registerUser(
            fullName = "Felisberto Cossa",
            phoneNumber = "+258 84 990 0011",
            email = "felisberto@macumu.co.mz",
            requestedRole = "VENDEDOR",
            sellerType = "INFORMAL",
            businessName = "Bazar Central de Chimoio",
            province = "Manica"
        )
        assertTrue(regId > 0)

        val pending = repo.getRegistrationById(regId)
        assertNotNull(pending)
        assertEquals("PENDING_APPROVAL", pending?.status)
        assertEquals("VENDEDOR", pending?.requestedRole)

        // Developer approves registration
        repo.approveUserRegistration(regId, "Validado pelo Desenvolvedor")
        val approved = repo.getRegistrationById(regId)
        assertEquals("APPROVED", approved?.status)
    }

    @Test
    fun `test developer PIN security access`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.MacumuViewModel(app)

        // Visitors or wrong PINs are rejected
        val wrongPinResult = vm.verifyDeveloperPin("0000")
        assertEquals(false, wrongPinResult)
        assertEquals(false, vm.isDeveloperAuthenticated.value)

        // Correct Developer Master PIN unlocks access
        val correctPinResult = vm.verifyDeveloperPin("2026")
        assertEquals(true, correctPinResult)
        assertEquals(true, vm.isDeveloperAuthenticated.value)
    }

    @Test
    fun `test visitor action gating and permissions`() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.MacumuViewModel(app)

        // 1. Visitor without account must be prompted to log in/register
        val visitorPerm = vm.checkPermission("COMPRAR")
        assertTrue(visitorPerm is com.example.ui.ActionPermission.RequiresLogin)

        // 2. User with pending approval must await developer review
        val pendingUser = com.example.data.local.UserRegistrationEntity(
            id = 999L,
            fullName = "Zuleica Jamal",
            phoneNumber = "+258 87 111 2233",
            requestedRole = "AFILIADO",
            status = "PENDING_APPROVAL"
        )
        vm.loginUser(pendingUser)
        val pendingPerm = vm.checkPermission("AFILIAR")
        assertTrue(pendingPerm is com.example.ui.ActionPermission.AwaitingApproval)

        // 3. Approved user is allowed to proceed
        val approvedUser = pendingUser.copy(status = "APPROVED")
        vm.loginUser(approvedUser)
        val allowedPerm = vm.checkPermission("AFILIAR")
        assertEquals(com.example.ui.ActionPermission.Allowed, allowedPerm)
    }
}

