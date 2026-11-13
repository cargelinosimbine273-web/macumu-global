package com.example.data.model

data class MarketHub(
    val id: String,
    val name: String,
    val subtitle: String,
    val location: String,
    val categoryType: String, // "NACIONAL_FORMAL", "INTERNACIONAL", "INFORMAL"
    val regionZone: String, // "SUL", "CENTRO", "NORTE", "INTERNACIONAL"
    val badge: String,
    val estimatedDelivery: String,
    val isVerified: Boolean = true
)

object MarketHubCatalog {
    val nationalMarkets = listOf(
        MarketHub(
            id = "maputo_shopping",
            name = "Maputo Shopping Centre",
            subtitle = "Lojas oficiais, moda e marcas de prestígio",
            location = "Maputo - Baixa da Cidade",
            categoryType = "NACIONAL_FORMAL",
            regionZone = "SUL",
            badge = "Shopping Oficial 🇲🇿",
            estimatedDelivery = "24 horas (Maputo)"
        ),
        MarketHub(
            id = "baia_mall",
            name = "Baía Mall",
            subtitle = "Electrodomésticos, tecnologia e decoração",
            location = "Maputo - Av. Marginal",
            categoryType = "NACIONAL_FORMAL",
            regionZone = "SUL",
            badge = "Centro Comercial 🇲🇿",
            estimatedDelivery = "24 horas (Maputo)"
        ),
        MarketHub(
            id = "novare_matola",
            name = "Novare Matola Mall",
            subtitle = "Hipermercados, materiais e utilidades",
            location = "Matola - EN4 Rio Cávado",
            categoryType = "NACIONAL_FORMAL",
            regionZone = "SUL",
            badge = "Matola Mall 🇲🇿",
            estimatedDelivery = "24-48 horas"
        ),
        MarketHub(
            id = "polana_shopping",
            name = "Polana Shopping",
            subtitle = "Boutiques de moda, perfumaria e calçado",
            location = "Maputo - Av. Julius Nyerere",
            categoryType = "NACIONAL_FORMAL",
            regionZone = "SUL",
            badge = "Boutiques Formais 🇲🇿",
            estimatedDelivery = "24 horas"
        ),
        MarketHub(
            id = "beira_mall",
            name = "Beira Mall",
            subtitle = "Comércio formal do centro de Moçambique",
            location = "Sofala - Cidade da Beira",
            categoryType = "NACIONAL_FORMAL",
            regionZone = "CENTRO",
            badge = "Shopping Centro 🇲🇿",
            estimatedDelivery = "48 horas (Centro)"
        ),
        MarketHub(
            id = "nampula_plaza",
            name = "Nampula Plaza",
            subtitle = "Comércio formal e eletrónica do norte",
            location = "Nampula - Av. Samora Machel",
            categoryType = "NACIONAL_FORMAL",
            regionZone = "NORTE",
            badge = "Shopping Norte 🇲🇿",
            estimatedDelivery = "48-72 horas"
        )
    )

    val internationalMarkets = listOf(
        MarketHub(
            id = "alibaba_global",
            name = "Alibaba Global",
            subtitle = "Importação directa de fábrica por grosso e retalho",
            location = "China (Importação Directa)",
            categoryType = "INTERNACIONAL",
            regionZone = "INTERNACIONAL",
            badge = "Alibaba Direct 🌍",
            estimatedDelivery = "7 a 12 dias úteis"
        ),
        MarketHub(
            id = "temu_express",
            name = "Temu Express",
            subtitle = "Gadgets, casa e vestuário com envio aéreo económico",
            location = "Global / Ásia & EUA",
            categoryType = "INTERNACIONAL",
            regionZone = "INTERNACIONAL",
            badge = "Temu Global 🌍",
            estimatedDelivery = "6 a 10 dias úteis"
        ),
        MarketHub(
            id = "aliexpress_direct",
            name = "AliExpress Direct",
            subtitle = "Eletrónica, smartphones e peças originais",
            location = "China / Hong Kong",
            categoryType = "INTERNACIONAL",
            regionZone = "INTERNACIONAL",
            badge = "AliExpress 🌍",
            estimatedDelivery = "8 a 14 dias úteis"
        ),
        MarketHub(
            id = "shein_fashion",
            name = "Shein Fashion",
            subtitle = "Tendências de moda feminina, masculina e infantil",
            location = "Internacional / Guangzhou",
            categoryType = "INTERNACIONAL",
            regionZone = "INTERNACIONAL",
            badge = "Shein Moda 🌍",
            estimatedDelivery = "7 a 10 dias úteis"
        ),
        MarketHub(
            id = "amazon_global",
            name = "Amazon Global Hub",
            subtitle = "Equipamentos profissionais e livros internacionais",
            location = "África do Sul & EUA",
            categoryType = "INTERNACIONAL",
            regionZone = "INTERNACIONAL",
            badge = "Amazon Hub 🌍",
            estimatedDelivery = "4 a 7 dias úteis"
        )
    )

    val informalMarketsSul = listOf(
        MarketHub(
            id = "mercado_xipamanine",
            name = "Mercado Xipamanine",
            subtitle = "O coração do comércio popular: capulanas e bazar geral",
            location = "Maputo - Bairro Xipamanine",
            categoryType = "INFORMAL",
            regionZone = "SUL",
            badge = "Bazar Sul 🇲🇿",
            estimatedDelivery = "Mesmo dia (Maputo)"
        ),
        MarketHub(
            id = "bazar_zimpeto",
            name = "Bazar do Zimpeto",
            subtitle = "Maior mercado grossista de alimentos e hortícolas",
            location = "Maputo - Zimpeto",
            categoryType = "INFORMAL",
            regionZone = "SUL",
            badge = "Grossista Sul 🇲🇿",
            estimatedDelivery = "Mesmo dia (Maputo/Matola)"
        ),
        MarketHub(
            id = "mercado_central_maputo",
            name = "Mercado Central de Maputo",
            subtitle = "Artesanato genuíno, mariscos, frutos secos e cestaria",
            location = "Maputo - Av. 25 de Setembro",
            categoryType = "INFORMAL",
            regionZone = "SUL",
            badge = "Mercado Histórico 🇲🇿",
            estimatedDelivery = "24 horas"
        ),
        MarketHub(
            id = "mercado_janet_estrela",
            name = "Mercado Janet (Estrela)",
            subtitle = "Eletrónica informal, calçado e peças de vestuário",
            location = "Maputo - Estrela Vermelha",
            categoryType = "INFORMAL",
            regionZone = "SUL",
            badge = "Informal Sul 🇲🇿",
            estimatedDelivery = "24 horas"
        ),
        MarketHub(
            id = "mercado_xai_xai",
            name = "Mercado de Xai-Xai",
            subtitle = "Castanhas de caju, mandioca e produtos rurais de Gaza",
            location = "Gaza - Cidade de Xai-Xai",
            categoryType = "INFORMAL",
            regionZone = "SUL",
            badge = "Gaza Sul 🇲🇿",
            estimatedDelivery = "24-48 horas"
        ),
        MarketHub(
            id = "mercado_maxixe",
            name = "Mercado de Maxixe",
            subtitle = "Óleo de coco biológico, farinha de milho e marisco",
            location = "Inhambane - Maxixe",
            categoryType = "INFORMAL",
            regionZone = "SUL",
            badge = "Inhambane Sul 🇲🇿",
            estimatedDelivery = "48 horas"
        )
    )

    val informalMarketsCentro = listOf(
        MarketHub(
            id = "mercado_maquinino",
            name = "Mercado do Maquinino",
            subtitle = "Centro pulsante do comércio informal da Beira",
            location = "Sofala - Beira (Maquinino)",
            categoryType = "INFORMAL",
            regionZone = "CENTRO",
            badge = "Bazar Centro 🇲🇿",
            estimatedDelivery = "24 horas (Beira)"
        ),
        MarketHub(
            id = "mercado_quelimane",
            name = "Mercado Central de Quelimane",
            subtitle = "Peixe seco do rio dos Bons Sinais, coco e tecidos",
            location = "Zambézia - Quelimane",
            categoryType = "INFORMAL",
            regionZone = "CENTRO",
            badge = "Zambézia Centro 🇲🇿",
            estimatedDelivery = "48 horas"
        ),
        MarketHub(
            id = "feira_chimoio",
            name = "Feira de Chimoio",
            subtitle = "Legumes frescos, vestuário e trocas transfronteiriças",
            location = "Manica - Chimoio",
            categoryType = "INFORMAL",
            regionZone = "CENTRO",
            badge = "Manica Centro 🇲🇿",
            estimatedDelivery = "48 horas"
        ),
        MarketHub(
            id = "mercado_kwachena",
            name = "Mercado Kwachena",
            subtitle = "Comércio dinâmico e artesanato do Vale do Zambeze",
            location = "Tete - Cidade de Tete",
            categoryType = "INFORMAL",
            regionZone = "CENTRO",
            badge = "Tete Centro 🇲🇿",
            estimatedDelivery = "48 horas"
        )
    )

    val informalMarketsNorte = listOf(
        MarketHub(
            id = "mercado_waresta",
            name = "Mercado Grossista do Waresta",
            subtitle = "O maior entreposto comercial informal do norte",
            location = "Nampula - Waresta",
            categoryType = "INFORMAL",
            regionZone = "NORTE",
            badge = "Grossista Norte 🇲🇿",
            estimatedDelivery = "24-48 horas"
        ),
        MarketHub(
            id = "mercado_belenenses",
            name = "Mercado de Belenenses",
            subtitle = "Capulanas raras, vestuário tradicional e calçado",
            location = "Nampula - Belenenses",
            categoryType = "INFORMAL",
            regionZone = "NORTE",
            badge = "Nampula Norte 🇲🇿",
            estimatedDelivery = "24-48 horas"
        ),
        MarketHub(
            id = "mercado_pemba",
            name = "Mercado do Peixe de Pemba",
            subtitle = "Pescado de águas profundas, camarão gigante e lagosta",
            location = "Cabo Delgado - Baía de Pemba",
            categoryType = "INFORMAL",
            regionZone = "NORTE",
            badge = "Cabo Delgado 🇲🇿",
            estimatedDelivery = "48-72 horas"
        ),
        MarketHub(
            id = "mercado_lichinga",
            name = "Mercado Central de Lichinga",
            subtitle = "Batata de Niassa, feijão e tecidos do planalto",
            location = "Niassa - Lichinga",
            categoryType = "INFORMAL",
            regionZone = "NORTE",
            badge = "Niassa Norte 🇲🇿",
            estimatedDelivery = "48-72 horas"
        )
    )

    fun getAllInformalMarkets(): List<MarketHub> =
        informalMarketsSul + informalMarketsCentro + informalMarketsNorte

    fun getAllMarkets(): List<MarketHub> =
        nationalMarkets + internationalMarkets + getAllInformalMarkets()
}
