package com.example.AppRazer.data.model

import com.example.AppRazer.presentation.screens.home.ProductInfo

object ProductRepository {
    private val allProducts = listOf(
        ProductInfo(
            id = "blade14",
            title = "Nuevo Razer Blade 14",
            subtitle = "Portátil gaming ultrapotente de 14\nAMD Ryzen™ 98945HS + NVIDIA® GeForce RTX™ 40",
            extra = "Disponible en Mercury o negro mate.",
            price = "Desde\n2.499,99 €",
            priceValue = 2499.99,
            imageUrl = "https://i.ibb.co/zNNf0v0/laptop1.png",
            category = "laptops"
        ),
        ProductInfo(
            id = "blade16",
            title = "Nuevo Razer Blade 16",
            subtitle = "Portátil gaming de 16' con pantalla OLED a 240 Hz",
            extra = "Disponible en Mercury o negro mate.",
            price = "Desde\n3.499,99 €",
            priceValue = 3499.99,
            imageUrl = "https://i.ibb.co/3ry5bH3/laptop2.png",
            category = "laptops"
        ),
        ProductInfo(
            id = "blade15_2023",
            title = "Nuevo Razer Blade 15 (2023)",
            subtitle = "Portátil NVIDIA® GeForce RTX™ serie 40 de\n15' con procesador Intel® Core™ i7 de 13ª\ngeneración (14 núcleos)",
            price = "Desde\n2.199,99 €",
            priceValue = 2199.99,
            imageUrl = "https://i.ibb.co/y8yXVBw/2.png",
            category = "laptops"
        ),
        ProductInfo(
            id = "blade18_4k_mercury",
            title = "Nuevos Razer Blade 18 4K Mercury",
            subtitle = "Portátil gaming de 18\" de alto rendimiento\ncon pantalla 4K a 200 Hz",
            extra = "Modelo 4K Disponible en Breve",
            price = "Desde\n5.199,99 €",
            priceValue = 5199.99,
            imageUrl = "https://i.ibb.co/Mh5YD80/4.png",
            category = "laptops"
        ),
        ProductInfo(
            id = "blade18_4k",
            title = "Nuevos Razer Blade 18 4K",
            subtitle = "Portátil gaming de 18\" de alto rendimiento\ncon pantalla 4K a 200 Hz",
            extra = "Modelo 4K Disponible en Breve",
            price = "Desde\n5.399,99 €",
            priceValue = 5399.99,
            imageUrl = "https://i.ibb.co/FgDXCTn/5.png",
            category = "laptops"
        ),
        ProductInfo(
            id = "blade18_qhd",
            title = "Nuevos Razer Blade 18",
            subtitle = "Portátil gaming de 18\" de alto rendimiento\ncon pantalla QHD+ Mini-LED a 240 Hz",
            extra = "Consigue el juego Star Wars Outlaw™ al comprar un portátil con procesador Intel® Core™ i9-14900HX.",
            price = "Desde\n3.599,99 €",
            priceValue = 3599.99,
            imageUrl = "https://i.ibb.co/FgDXCTn/5.png",
            category = "laptops"
        ),
        ProductInfo(
            id = "cobra_pro",
            title = "Razer Cobra Pro",
            subtitle = "Ratón gaming inalámbrico con Razer Chroma RGB",
            price = "Desde\n149,99 €",
            priceValue = 149.99,
            imageUrl = "https://i.ibb.co/vZpkCH3/mouseblanco.png",
            category = "mouse"
        ),
        ProductInfo(
            id = "naga_left",
            title = "Razer Naga Left-Handed Edition",
            subtitle = "Ratón ergonómico para juegos MMO\ndiseñado para usuarios zurdos",
            price = "Desde\n109,99 €",
            priceValue = 109.99,
            imageUrl = "https://i.ibb.co/FzZw4wk/maus.png",
            category = "mouse"
        ),
        ProductInfo(
            id = "blackwidow_v4_pro",
            title = "Razer BlackWidow V4 Pro",
            subtitle = "Teclado mecánico gaming con Razer Chroma RGB",
            price = "Desde\n269,49 €",
            priceValue = 269.49,
            imageUrl = "https://i.ibb.co/ckn2X3R/teclado1.png",
            category = "teclados"
        ),
        ProductInfo(
            id = "seiren_v3",
            title = "Razer Seiren V3 CHROMA",
            subtitle = "Micrófono USB RGB con pulsación para silenciar",
            price = "Desde\n159,99 €",
            priceValue = 159.99,
            imageUrl = "https://i.ibb.co/WvgW9NR/microfono.png",
            category = "microfonos"
        ),
        ProductInfo(
            id = "iskur_negro",
            title = "Razer Iskur - Negro",
            subtitle = "Silla para gamers con soporte lumbar integrado",
            price = "Desde\n384,99 €",
            priceValue = 384.99,
            imageUrl = "https://i.ibb.co/8DwynCn/descarga-8-fotor-bg-remover-20240611145035.png",
            category = "sillas"
        ),
        ProductInfo(
            id = "kaira_pro",
            title = "Razer Kaira Pro HyperSpeed PlayStation Licensed",
            subtitle = "Auriculares inalámbricos gaming multiplataforma",
            price = "Desde\n167,99 €",
            priceValue = 167.99,
            imageUrl = "https://i.ibb.co/P6YC2cC/descarga-5-fotor-bg-remover-20240611145046.png",
            category = "auriculares"
        )
    )

    fun getById(id: String) = allProducts.find { it.id == id }
    fun getRelated(id: String) = allProducts.filter { it.id != id }.take(4)
    fun getAll() = allProducts
    fun getByCategory(category: String) = allProducts.filter { it.category == category }
    fun search(query: String): List<ProductInfo> {
        val q = query.lowercase().trim()
        return allProducts.filter { product ->
            product.title.lowercase().contains(q) ||
                    product.subtitle.lowercase().contains(q) ||
                    product.id.lowercase().contains(q)
        }
    }
}