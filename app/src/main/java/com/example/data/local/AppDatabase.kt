package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        LedgerEntryEntity::class,
        WithdrawalEntity::class,
        AffiliateLinkEntity::class,
        DisputeEntity::class,
        NotificationEntity::class,
        UserRegistrationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun financeDao(): FinanceDao
    abstract fun affiliateDao(): AffiliateDao
    abstract fun disputeDao(): DisputeDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userRegistrationDao(): UserRegistrationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "macumu_global_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val productDao = db.productDao()
            val initialProducts = listOf(
                // ==================== MERCADOS NACIONAIS FORMAIS (SHOPPINGS) ====================
                ProductEntity(
                    title = "Smartphone Galaxy A15 128GB Dual SIM Oficial",
                    description = "Novo na caixa com garantia oficial de 12 meses. Loja autorizada no Maputo Shopping Centre.",
                    category = "Electrónica",
                    priceMzn = 9500.0,
                    promoPriceMzn = 8990.0,
                    stock = 15,
                    sellerId = 201L,
                    sellerName = "MozTech Oficial",
                    sellerType = "FORMAL",
                    sellerLocation = "Maputo Shopping Centre (Baixa)",
                    marketName = "Maputo Shopping Centre",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 4.9f,
                    reviewCount = 58,
                    deliveryEstimate = "24 horas",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Sapatilhas Desportivas Urban Running",
                    description = "Calçado desportivo original ultra-confortável com sola amortecedora. Disponível na boutique do Maputo Shopping.",
                    category = "Roupas e acessórios",
                    priceMzn = 3400.0,
                    promoPriceMzn = 2950.0,
                    stock = 18,
                    sellerId = 202L,
                    sellerName = "SportsZone Maputo Shopping",
                    sellerType = "FORMAL",
                    sellerLocation = "Maputo Shopping Centre (Piso 1)",
                    marketName = "Maputo Shopping Centre",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 4.8f,
                    reviewCount = 27,
                    deliveryEstimate = "24 horas",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Televisor Smart LED 43\" 4K Ultra HD HDR",
                    description = "Televisão inteligente com Netflix, YouTube e Wi-Fi integrado. Garantia formal de 2 anos na loja do Baía Mall.",
                    category = "Electrónica",
                    priceMzn = 18500.0,
                    promoPriceMzn = 16900.0,
                    stock = 10,
                    sellerId = 203L,
                    sellerName = "HiperElec Baía Mall",
                    sellerType = "FORMAL",
                    sellerLocation = "Baía Mall (Av. Marginal)",
                    marketName = "Baía Mall",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 4.9f,
                    reviewCount = 34,
                    deliveryEstimate = "24 horas",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Berbequim de Impacto Profissional 750W com Acessórios",
                    description = "Ferramenta de alta precisão para construção e bricolage. Loja de ferragens de Novare Matola.",
                    category = "Construção",
                    priceMzn = 3200.0,
                    promoPriceMzn = 2850.0,
                    stock = 14,
                    sellerId = 204L,
                    sellerName = "Ferreira Ferramentas",
                    sellerType = "FORMAL",
                    sellerLocation = "Novare Matola Mall (EN4)",
                    marketName = "Novare Matola Mall",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 4.7f,
                    reviewCount = 19,
                    deliveryEstimate = "24-48 horas",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Jogo de Malas de Viagem Executivas Rígidas 3 Peças",
                    description = "Malas com cadeado TSA integrado e rodas 360 graus. Disponíveis no Beira Mall.",
                    category = "Roupas e acessórios",
                    priceMzn = 6500.0,
                    promoPriceMzn = 5800.0,
                    stock = 8,
                    sellerId = 205L,
                    sellerName = "Beira Travel Store",
                    sellerType = "FORMAL",
                    sellerLocation = "Beira Mall (Sofala)",
                    marketName = "Beira Mall",
                    regionZone = "CENTRO",
                    isNational = true,
                    rating = 4.8f,
                    reviewCount = 15,
                    deliveryEstimate = "48 horas",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Tablet Android 10.1\" 64GB com Teclado e Caneta",
                    description = "Tablet de alto desempenho para estudo e trabalho executivo. Loja oficial de Nampula Plaza.",
                    category = "Electrónica",
                    priceMzn = 7900.0,
                    promoPriceMzn = 7200.0,
                    stock = 12,
                    sellerId = 206L,
                    sellerName = "Nampula Tech Center",
                    sellerType = "FORMAL",
                    sellerLocation = "Nampula Plaza (Av. Samora Machel)",
                    marketName = "Nampula Plaza",
                    regionZone = "NORTE",
                    isNational = true,
                    rating = 4.8f,
                    reviewCount = 22,
                    deliveryEstimate = "48 horas",
                    isFeatured = false
                ),

                // ==================== MERCADOS INTERNACIONAIS (ALIBABA, TEMU, ETC.) ====================
                ProductEntity(
                    title = "Lote de Auriculares Sem Fios Bluetooth 5.3 Pro (Preço de Fábrica)",
                    description = "Importação directa via Alibaba Global. Cancelamento activo de ruído e estojo de carregamento inteligente.",
                    category = "Electrónica",
                    priceMzn = 1100.0,
                    promoPriceMzn = 890.0,
                    stock = 100,
                    sellerId = 301L,
                    sellerName = "Alibaba Direct Sourcing",
                    sellerType = "FORMAL",
                    sellerLocation = "China (Importação Directa)",
                    marketName = "Alibaba Global",
                    regionZone = "INTERNACIONAL",
                    isNational = false,
                    rating = 4.8f,
                    reviewCount = 89,
                    deliveryEstimate = "7-12 dias (Internacional)",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Mini Projector Portátil Full HD Cinema em Casa 1080P",
                    description = "Projector compacto com Wi-Fi para filmes e apresentações. Logística e envio directo integrado da Temu.",
                    category = "Electrónica",
                    priceMzn = 3450.0,
                    promoPriceMzn = 2990.0,
                    stock = 35,
                    sellerId = 302L,
                    sellerName = "Temu Express Global",
                    sellerType = "FORMAL",
                    sellerLocation = "Ásia / EUA (Temu Express)",
                    marketName = "Temu Express",
                    regionZone = "INTERNACIONAL",
                    isNational = false,
                    rating = 4.9f,
                    reviewCount = 142,
                    deliveryEstimate = "6-10 dias (Internacional)",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Organizador Multifunções 360 Graus para Cozinha e Despensa",
                    description = "Prateleira giratória de alta resistência para condimentos e frascos. Tendência internacional da Temu.",
                    category = "Casa e decoração",
                    priceMzn = 780.0,
                    promoPriceMzn = 590.0,
                    stock = 60,
                    sellerId = 302L,
                    sellerName = "Temu Express Global",
                    sellerType = "FORMAL",
                    sellerLocation = "Global (Temu Express)",
                    marketName = "Temu Express",
                    regionZone = "INTERNACIONAL",
                    isNational = false,
                    rating = 4.7f,
                    reviewCount = 65,
                    deliveryEstimate = "6-10 dias (Internacional)",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Smartwatch Fitness Tracker Resistente à Água IP68",
                    description = "Mede batimentos, oxigénio no sangue e passos diários com autonomia de 10 dias. Envio directo AliExpress.",
                    category = "Electrónica",
                    priceMzn = 1450.0,
                    promoPriceMzn = 1200.0,
                    stock = 45,
                    sellerId = 303L,
                    sellerName = "AliExpress Direct Hub",
                    sellerType = "FORMAL",
                    sellerLocation = "Hong Kong / China",
                    marketName = "AliExpress Direct",
                    regionZone = "INTERNACIONAL",
                    isNational = false,
                    rating = 4.8f,
                    reviewCount = 94,
                    deliveryEstimate = "8-14 dias (Internacional)",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Vestido Longo Boémio Floral de Verão Shein",
                    description = "Tecido leve, fresco e fluido com corte elegante. Colecção internacional da Shein.",
                    category = "Roupas e acessórios",
                    priceMzn = 1250.0,
                    promoPriceMzn = 980.0,
                    stock = 50,
                    sellerId = 304L,
                    sellerName = "Shein Fashion Direct",
                    sellerType = "FORMAL",
                    sellerLocation = "Guangzhou (Shein)",
                    marketName = "Shein Fashion",
                    regionZone = "INTERNACIONAL",
                    isNational = false,
                    rating = 4.9f,
                    reviewCount = 76,
                    deliveryEstimate = "7-10 dias (Internacional)",
                    isFeatured = false
                ),

                // ==================== MERCADOS INFORMAIS: ZONA SUL ====================
                ProductEntity(
                    title = "Capulana Original Estampada Tradicional (Moçambique)",
                    description = "Capulana 100% algodão genuína com padrões culturais vivos. Tecido resistente ideal para vestuário e cerimónias no Mercado Xipamanine.",
                    category = "Roupas e acessórios",
                    priceMzn = 850.0,
                    promoPriceMzn = 750.0,
                    stock = 45,
                    sellerId = 101L,
                    sellerName = "Mamã Esperança (Bazar Xipamanine)",
                    sellerType = "INFORMAL",
                    sellerLocation = "Maputo - Mercado Xipamanine",
                    marketName = "Mercado Xipamanine",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 4.9f,
                    reviewCount = 48,
                    deliveryEstimate = "Mesmo dia (Maputo)",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Saco de Batata Reno e Cebola Fresca de Primeira (25Kg)",
                    description = "Alimentos frescos acabados de descarregar dos camiões do campo no maior entreposto de frescos de Moçambique.",
                    category = "Alimentação",
                    priceMzn = 1150.0,
                    promoPriceMzn = 990.0,
                    stock = 80,
                    sellerId = 102L,
                    sellerName = "Armazém Grossista do Zimpeto",
                    sellerType = "INFORMAL",
                    sellerLocation = "Maputo - Bazar do Zimpeto",
                    marketName = "Bazar do Zimpeto",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 4.8f,
                    reviewCount = 61,
                    deliveryEstimate = "Mesmo dia (Maputo/Matola)",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Cesta de Palha Artesanal e Piri-Piri Moçambicano Moído Puro",
                    description = "Cesto tradicional tecido à mão com frasco de 250g de piri-piri vermelho ardente do Mercado Central de Maputo.",
                    category = "Casa e decoração",
                    priceMzn = 650.0,
                    promoPriceMzn = 550.0,
                    stock = 25,
                    sellerId = 103L,
                    sellerName = "Banca de Artesanato Baixa",
                    sellerType = "INFORMAL",
                    sellerLocation = "Maputo - Mercado Central",
                    marketName = "Mercado Central de Maputo",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 5.0f,
                    reviewCount = 37,
                    deliveryEstimate = "24 horas",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Castanha de Caju Torrada de Primeira Selecção de Gaza (1Kg)",
                    description = "Castanha fresca colhida em Gaza (Chibuto/Xai-Xai). Torrada artesanalmente sem conservantes, estaladiça e muito saborosa.",
                    category = "Alimentação",
                    priceMzn = 650.0,
                    promoPriceMzn = 590.0,
                    stock = 40,
                    sellerId = 104L,
                    sellerName = "Produtores de Xai-Xai",
                    sellerType = "INFORMAL",
                    sellerLocation = "Gaza - Mercado de Xai-Xai",
                    marketName = "Mercado de Xai-Xai",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 5.0f,
                    reviewCount = 72,
                    deliveryEstimate = "24-48 horas",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Óleo Puro de Coco e Manteiga de Karité Natural (250ml)",
                    description = "Produto 100% biológico prensado a frio em Inhambane. Hidratação profunda para pele e cabelo.",
                    category = "Beleza e cuidados pessoais",
                    priceMzn = 450.0,
                    promoPriceMzn = null,
                    stock = 50,
                    sellerId = 105L,
                    sellerName = "BioInhambane Cosméticos",
                    sellerType = "INFORMAL",
                    sellerLocation = "Inhambane - Mercado de Maxixe",
                    marketName = "Mercado de Maxixe",
                    regionZone = "SUL",
                    isNational = true,
                    rating = 4.8f,
                    reviewCount = 31,
                    deliveryEstimate = "48 horas",
                    isFeatured = false
                ),

                // ==================== MERCADOS INFORMAIS: ZONA CENTRO ====================
                ProductEntity(
                    title = "Tecido Wax Africano Padrão Chiveve (6 Jardas)",
                    description = "Tecido colorido vibrante tradicional do Mercado do Maquinino na Beira. Alta durabilidade e cores não desbotam.",
                    category = "Roupas e acessórios",
                    priceMzn = 1200.0,
                    promoPriceMzn = 1050.0,
                    stock = 30,
                    sellerId = 106L,
                    sellerName = "Mamã Joana (Maquinino)",
                    sellerType = "INFORMAL",
                    sellerLocation = "Sofala - Mercado do Maquinino",
                    marketName = "Mercado do Maquinino",
                    regionZone = "CENTRO",
                    isNational = true,
                    rating = 4.9f,
                    reviewCount = 29,
                    deliveryEstimate = "24-48 horas",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Peixe Seco Salgado Tradicional de Rio e Costa (1Kg)",
                    description = "Peixe seco de excelente qualidade preparado nas margens do Rio dos Bons Sinais em Quelimane.",
                    category = "Alimentação",
                    priceMzn = 500.0,
                    promoPriceMzn = 450.0,
                    stock = 35,
                    sellerId = 107L,
                    sellerName = "Pescadores de Quelimane",
                    sellerType = "INFORMAL",
                    sellerLocation = "Zambézia - Mercado Central de Quelimane",
                    marketName = "Mercado Central de Quelimane",
                    regionZone = "CENTRO",
                    isNational = true,
                    rating = 4.8f,
                    reviewCount = 24,
                    deliveryEstimate = "48 horas",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Mel Silvestre Puro e Frutas Tropicais Desidratadas (500g)",
                    description = "Mel das montanhas de Manica puro sem aditivos. Colhido por apicultores tradicionais da Feira de Chimoio.",
                    category = "Alimentação",
                    priceMzn = 480.0,
                    promoPriceMzn = 420.0,
                    stock = 25,
                    sellerId = 108L,
                    sellerName = "Apicultura Chimoio",
                    sellerType = "INFORMAL",
                    sellerLocation = "Manica - Feira de Chimoio",
                    marketName = "Feira de Chimoio",
                    regionZone = "CENTRO",
                    isNational = true,
                    rating = 4.9f,
                    reviewCount = 18,
                    deliveryEstimate = "48 horas",
                    isFeatured = false
                ),

                // ==================== MERCADOS INFORMAIS: ZONA NORTE ====================
                ProductEntity(
                    title = "Amendoim Vermelho Seleccionado de Nampula (5Kg)",
                    description = "Amendoim graúdo para caril de amendoim tradicional e manteiga caseira, vindo directo do Mercado Grossista do Waresta.",
                    category = "Alimentação",
                    priceMzn = 750.0,
                    promoPriceMzn = 680.0,
                    stock = 50,
                    sellerId = 109L,
                    sellerName = "Grossistas do Waresta",
                    sellerType = "INFORMAL",
                    sellerLocation = "Nampula - Mercado do Waresta",
                    marketName = "Mercado Grossista do Waresta",
                    regionZone = "NORTE",
                    isNational = true,
                    rating = 5.0f,
                    reviewCount = 44,
                    deliveryEstimate = "48 horas",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Capulana Especial de Gala e Cerimónias do Norte",
                    description = "Padrões nobres tradicionais muito procurados em casamentos e celebrações no Mercado de Belenenses.",
                    category = "Roupas e acessórios",
                    priceMzn = 950.0,
                    promoPriceMzn = 880.0,
                    stock = 28,
                    sellerId = 110L,
                    sellerName = "Moda Belenenses",
                    sellerType = "INFORMAL",
                    sellerLocation = "Nampula - Mercado de Belenenses",
                    marketName = "Mercado de Belenenses",
                    regionZone = "NORTE",
                    isNational = true,
                    rating = 4.9f,
                    reviewCount = 33,
                    deliveryEstimate = "48 horas",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Camarão Tigre Gigante Seco de Pemba (500g)",
                    description = "Camarão nobre capturado nas águas limpas da Baía de Pemba em Cabo Delgado. Sabor inigualável.",
                    category = "Alimentação",
                    priceMzn = 1200.0,
                    promoPriceMzn = 1080.0,
                    stock = 20,
                    sellerId = 111L,
                    sellerName = "Pescadores da Baía de Pemba",
                    sellerType = "INFORMAL",
                    sellerLocation = "Cabo Delgado - Mercado do Peixe de Pemba",
                    marketName = "Mercado do Peixe de Pemba",
                    regionZone = "NORTE",
                    isNational = true,
                    rating = 5.0f,
                    reviewCount = 26,
                    deliveryEstimate = "48-72 horas",
                    isFeatured = true
                )
            )
            productDao.insertProducts(initialProducts)

            // Seed initial affiliate links
            val affiliateDao = db.affiliateDao()
            affiliateDao.insertAffiliateLink(
                AffiliateLinkEntity(
                    affiliateCode = "AFIL-MOZ-88",
                    productId = 1L,
                    productTitle = "Smartphone Galaxy A15 128GB Oficial",
                    productPriceMzn = 8990.0,
                    commissionPercent = 5.0,
                    uniqueUrl = "https://macumu.co.mz/p/1?ref=AFIL-MOZ-88",
                    clicks = 340,
                    conversions = 8,
                    totalEarnedMzn = 3596.0
                )
            )
            affiliateDao.insertAffiliateLink(
                AffiliateLinkEntity(
                    affiliateCode = "AFIL-MOZ-88",
                    productId = 12L,
                    productTitle = "Capulana Original Estampada Tradicional",
                    productPriceMzn = 750.0,
                    commissionPercent = 10.0,
                    uniqueUrl = "https://macumu.co.mz/p/12?ref=AFIL-MOZ-88",
                    clicks = 180,
                    conversions = 14,
                    totalEarnedMzn = 1050.0
                )
            )

            // Seed initial notification
            val notificationDao = db.notificationDao()
            notificationDao.insertNotification(
                NotificationEntity(
                    targetRole = "COMPRADOR",
                    title = "Bem-vindo ao Macumu Global!",
                    message = "Explore mercados nacionais (Maputo Shopping, Baía Mall), internacionais (Alibaba, Temu) e bazares informais do Norte, Centro e Sul de Moçambique."
                )
            )
            notificationDao.insertNotification(
                NotificationEntity(
                    targetRole = "VENDEDOR",
                    title = "Venda em qualquer mercado",
                    message = "Vendedores formais e informais recebem comissões directas na carteira com levantamento facilitado para M-Pesa e e-Mola."
                )
            )

            // Seed initial User Registrations for Developer/Admin Validation
            val userRegDao = db.userRegistrationDao()
            userRegDao.insertRegistration(
                UserRegistrationEntity(
                    fullName = "Mussagy Ibraimo",
                    phoneNumber = "+258 84 332 9981",
                    email = "mussagy.ibraimo@gmail.com",
                    requestedRole = "VENDEDOR",
                    sellerType = "INFORMAL",
                    businessName = "Bazar Xipamanine Frutas & Especiarias",
                    province = "Maputo Cidade",
                    status = "PENDING_APPROVAL",
                    registeredAt = System.currentTimeMillis() - 7200000L
                )
            )
            userRegDao.insertRegistration(
                UserRegistrationEntity(
                    fullName = "Tânia Matsinhe",
                    phoneNumber = "+258 87 554 1122",
                    email = "tania.matsinhe@outlook.com",
                    requestedRole = "AFILIADO",
                    businessName = "",
                    province = "Gaza",
                    status = "PENDING_APPROVAL",
                    registeredAt = System.currentTimeMillis() - 14400000L
                )
            )
            userRegDao.insertRegistration(
                UserRegistrationEntity(
                    fullName = "Alberto Mondlane",
                    phoneNumber = "+258 82 889 0011",
                    email = "alberto.mondlane@gmail.com",
                    requestedRole = "COMPRADOR",
                    businessName = "",
                    province = "Sofala",
                    status = "PENDING_APPROVAL",
                    registeredAt = System.currentTimeMillis() - 28800000L
                )
            )
            userRegDao.insertRegistration(
                UserRegistrationEntity(
                    fullName = "Armando Chissano",
                    phoneNumber = "+258 84 123 4567",
                    email = "armando.chissano@macumu.co.mz",
                    requestedRole = "COMPRADOR",
                    businessName = "",
                    province = "Maputo Cidade",
                    status = "APPROVED",
                    reviewerNotes = "Validado pelo Desenvolvedor do Macumu Global",
                    registeredAt = System.currentTimeMillis() - 86400000L,
                    approvedAt = System.currentTimeMillis() - 43200000L
                )
            )
        }
    }
}

