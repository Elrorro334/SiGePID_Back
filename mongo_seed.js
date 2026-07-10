// =============================================================================
// SiGePID - MongoDB Seed Script
// Siembra las 5 categorías y 25 productos del dataset en MongoDB
// Ejecutar con: mongosh sigepid_catalog --file mongo_seed.js
// =============================================================================

print("=".repeat(60));
print("  SiGePID - Sembrando datos en MongoDB");
print("=".repeat(60));

// =========================================================================
// LIMPIAR COLECCIONES EXISTENTES
// =========================================================================
db.categories.deleteMany({});
db.products.deleteMany({});
print("\n✓ Colecciones limpiadas");

// =========================================================================
// INSERTAR CATEGORÍAS
// =========================================================================
const categoriesResult = db.categories.insertMany([
    { _id: ObjectId("6863a0000000000000000001"), name: "Electrónica", description: "Smartphones, laptops, tablets, smartwatches y audio", active: true },
    { _id: ObjectId("6863a0000000000000000002"), name: "Ropa", description: "Moda, calzado y accesorios para toda la familia", active: true },
    { _id: ObjectId("6863a0000000000000000003"), name: "Hogar", description: "Electrodomésticos, iluminación y artículos del hogar", active: true },
    { _id: ObjectId("6863a0000000000000000004"), name: "Deportes", description: "Equipamiento deportivo y artículos fitness", active: true },
    { _id: ObjectId("6863a0000000000000000005"), name: "Juguetes", description: "Juguetes educativos, juegos de mesa y entretenimiento", active: true }
]);
print(`✓ Categorías insertadas: ${categoriesResult.insertedIds.length}`);

const CAT = {
    electronica: "6863a0000000000000000001",
    ropa: "6863a0000000000000000002",
    hogar: "6863a0000000000000000003",
    deportes: "6863a0000000000000000004",
    juguetes: "6863a0000000000000000005"
};

const now = new Date();

// =========================================================================
// INSERTAR PRODUCTOS (25 productos del dataset)
// Precios = promedio del rango definido en el dataset generator
// =========================================================================
const productsResult = db.products.insertMany([
    // --- ELECTRÓNICA ---
    {
        name: "Smartphone",
        description: "Smartphone de última generación con cámara de alta resolución, pantalla AMOLED y batería de larga duración.",
        sku: "ELEC-SMART-001",
        price: NumberDecimal("9250.00"),
        stock: 85,
        categoryId: CAT.electronica, imageUrl: "https://images.samsung.com/is/image/samsung/p6pim/mx/s2602/gallery/mx-galaxy-s26-ultra-s948-sm-s948bzvmltm-thumb-550798102",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Laptop",
        description: "Laptop profesional con procesador de alto rendimiento, 16GB RAM y SSD ultrarrápido para trabajo y creatividad.",
        sku: "ELEC-LAPT-002",
        price: NumberDecimal("16500.00"),
        stock: 42,
        categoryId: CAT.electronica, imageUrl: "https://dlcdnwebimgs.asus.com/gain/6c49e355-de25-403c-b8c2-eb0ad4cc8a78/",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Tablet",
        description: "Tablet versátil con pantalla de alta resolución, ideal para estudio, entretenimiento y navegación cotidiana.",
        sku: "ELEC-TABL-003",
        price: NumberDecimal("7250.00"),
        stock: 60,
        categoryId: CAT.electronica, imageUrl: "https://images.samsung.com/is/image/samsung/p6pim/cl/sm-x400nzahcho/gallery/cl-galaxy-tab-s10-lite-sm-x406-sm-x400nzahcho-thumb-550431099",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Auriculares Inalámbricos",
        description: "Auriculares Bluetooth con cancelación activa de ruido, 30 horas de batería y sonido de alta fidelidad.",
        sku: "ELEC-AURI-004",
        price: NumberDecimal("1650.00"),
        stock: 110,
        categoryId: CAT.electronica, imageUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8jmg8hhN45A9SQeSbGbRyy8i-s-vsLrK1nX7K_K_XFw&s=10",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Smartwatch",
        description: "Reloj inteligente con monitoreo de salud, GPS integrado, notificaciones y resistencia al agua.",
        sku: "ELEC-WATC-005",
        price: NumberDecimal("4750.00"),
        stock: 55,
        categoryId: CAT.electronica, imageUrl: "https://ae-pic-a1.aliexpress-media.com/kf/S8422e2841ce3411388cdaa8799f70209a.png_960x960.png?has_lang=1&ver=2",
        active: true, createdAt: now, updatedAt: now
    },

    // --- ROPA ---
    {
        name: "Camisa de Algodón",
        description: "Camisa de algodón 100% natural, corte regular, disponible en múltiples colores para ocasiones casuales y formales.",
        sku: "ROPA-CAMI-001",
        price: NumberDecimal("500.00"),
        stock: 200,
        categoryId: CAT.ropa, imageUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQoRhZtlrGzEHr7lx36Z9DKs1nfDkdpo4JOfKGE7EGL1Q&s",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Pantalón de Mezclilla",
        description: "Pantalón denim de alta calidad con corte moderno, resistente y cómodo para uso diario.",
        sku: "ROPA-PANT-002",
        price: NumberDecimal("950.00"),
        stock: 150,
        categoryId: CAT.ropa, imageUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQh9QumerUAEskbaV3TtkeMnlWdtuE03bDx_Tr0GDOL5w&s=10",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Zapatillas Deportivas",
        description: "Zapatillas con tecnología de amortiguación avanzada, suela antideslizante y diseño ergonómico.",
        sku: "ROPA-ZAPA-003",
        price: NumberDecimal("1800.00"),
        stock: 90,
        categoryId: CAT.ropa, imageUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXN7GdLGM84OXuku8YSrBT6YIWrDf9M7lNGtwyOs7IFw&s=10",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Chaqueta de Cuero",
        description: "Chaqueta de cuero genuino con forro interior, cremalleras metálicas y estilo atemporal.",
        sku: "ROPA-CHAQ-004",
        price: NumberDecimal("3250.00"),
        stock: 35,
        categoryId: CAT.ropa, imageUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLhwKUWcqzeawt4nUWWyGq52JO-fhEEku-s5txkMxzhg&s=10",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Vestido de Noche",
        description: "Vestido elegante para ocasiones especiales, tela premium con corte favorecedor y acabados de alta costura.",
        sku: "ROPA-VEST-005",
        price: NumberDecimal("2400.00"),
        stock: 40,
        categoryId: CAT.ropa, imageUrl: "https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcTQA_5REQObiucHyBos0Ct8TK6eqYQV9c4PGtNqRQxGsB-GkizNHHV8_OGL6TJFJP905wR8OEQiliX3ykeKdtAVyY6467ZrAS_Bj0iTuCw",
        active: true, createdAt: now, updatedAt: now
    },

    // --- HOGAR ---
    {
        name: "Lámpara de Escritorio",
        description: "Lámpara LED de escritorio con intensidad regulable, temperatura de color ajustable y brazo articulado.",
        sku: "HOGA-LAMP-001",
        price: NumberDecimal("700.00"),
        stock: 75,
        categoryId: CAT.hogar, imageUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9U0D1bkYOgEdrP7mGRfyoiJT9joQXLsfs53EUaJ2Wyw&s=10",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Cafetera Automática",
        description: "Cafetera programable con molinillo integrado, 12 tazas de capacidad y sistema antiderrame.",
        sku: "HOGA-CAFE-002",
        price: NumberDecimal("1750.00"),
        stock: 48,
        categoryId: CAT.hogar, imageUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLpQBWcOFTIdJDkaSqSWlw1A7qG-NjXLlxl8-l5UqiaQ&s=10",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Aspiradora Robot",
        description: "Robot aspirador con mapeo inteligente, control por app, ideal para mantener el hogar limpio sin esfuerzo.",
        sku: "HOGA-ASPI-003",
        price: NumberDecimal("7500.00"),
        stock: 22,
        categoryId: CAT.hogar, imageUrl: "https://i02.appmifile.com/285_item_mx/18/09/2024/73a84b5ac0f0b7964ccf25831c2ac96d.png",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Set de Sartenes",
        description: "Set de sartenes antiadherentes de acero inoxidable, apto para todo tipo de cocinas incluyendo inducción.",
        sku: "HOGA-SART-004",
        price: NumberDecimal("1200.00"),
        stock: 65,
        categoryId: CAT.hogar, imageUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSE7yqrOg4kbQeej4sMjZpzN11GlIkKULFHGNA-jH1LZg&s=10",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Manta Térmica",
        description: "Manta eléctrica con 6 niveles de calor, apagado automático de seguridad y tacto ultra suave.",
        sku: "HOGA-MANT-005",
        price: NumberDecimal("900.00"),
        stock: 80,
        categoryId: CAT.hogar, imageUrl: "https://flppfftm.filerobot.com/MMX/Productos/Manta+sherpa+de+microvis%C3%B3n+ultrasuave.png?p=n",
        active: true, createdAt: now, updatedAt: now
    },

    // --- DEPORTES ---
    {
        name: "Bicicleta de Montaña",
        description: "Bicicleta de montaña con cuadro de aluminio, suspensión delantera, 21 velocidades y frenos de disco.",
        sku: "DEPO-BICI-001",
        price: NumberDecimal("9000.00"),
        stock: 18,
        categoryId: CAT.deportes, imageUrl: "https://loremflickr.com/400/400/mountain+bike,product/all",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Set de Pesas",
        description: "Set de mancuernas ajustables de acero con revestimiento antideslizante, de 1 a 20 kg.",
        sku: "DEPO-PESA-002",
        price: NumberDecimal("1750.00"),
        stock: 55,
        categoryId: CAT.deportes, imageUrl: "https://loremflickr.com/400/400/dumbbell,product/all",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Balón de Fútbol",
        description: "Balón oficial de fútbol talla 5, cubierta de PU de alta durabilidad, ideal para césped natural y artificial.",
        sku: "DEPO-BALO-003",
        price: NumberDecimal("600.00"),
        stock: 120,
        categoryId: CAT.deportes, imageUrl: "https://loremflickr.com/400/400/soccer+ball,product/all",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Raqueta de Tenis",
        description: "Raqueta de tenis con marco de grafito, cabeza de 100 pulgadas y grip ergonómico antivibración.",
        sku: "DEPO-RAQU-004",
        price: NumberDecimal("1750.00"),
        stock: 40,
        categoryId: CAT.deportes, imageUrl: "https://loremflickr.com/400/400/tennis+racket,product/all",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Esterilla de Yoga",
        description: "Esterilla antideslizante de 6mm de grosor, material ecológico TPE, con correa de transporte incluida.",
        sku: "DEPO-ESTE-005",
        price: NumberDecimal("500.00"),
        stock: 95,
        categoryId: CAT.deportes, imageUrl: "https://loremflickr.com/400/400/yoga+mat,product/all",
        active: true, createdAt: now, updatedAt: now
    },

    // --- JUGUETES ---
    {
        name: "Set de Construcción Lego",
        description: "Set de construcción de 1000+ piezas compatibles, estimula la creatividad y el pensamiento lógico.",
        sku: "JUGT-LEGO-001",
        price: NumberDecimal("1400.00"),
        stock: 70,
        categoryId: CAT.juguetes, imageUrl: "https://loremflickr.com/400/400/lego,product/all",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Muñeca Articulada",
        description: "Muñeca articulada de 30cm con accesorios intercambiables, ropa y peinados de moda.",
        sku: "JUGT-MUNE-002",
        price: NumberDecimal("475.00"),
        stock: 85,
        categoryId: CAT.juguetes, imageUrl: "https://loremflickr.com/400/400/doll,product/all",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Rompecabezas 1000 piezas",
        description: "Rompecabezas de 1000 piezas con imagen de alta calidad, piezas de encaje preciso y caja coleccionable.",
        sku: "JUGT-ROMP-003",
        price: NumberDecimal("300.00"),
        stock: 100,
        categoryId: CAT.juguetes, imageUrl: "https://loremflickr.com/400/400/puzzle,product/all",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Coche a Control Remoto",
        description: "Auto a control remoto 4x4, velocidad máxima 25 km/h, batería recargable y resistente a golpes.",
        sku: "JUGT-COCH-004",
        price: NumberDecimal("1150.00"),
        stock: 55,
        categoryId: CAT.juguetes, imageUrl: "https://loremflickr.com/400/400/rc+car,product/all",
        active: true, createdAt: now, updatedAt: now
    },
    {
        name: "Juego de Mesa Estratégico",
        description: "Juego de mesa para 2-6 jugadores, temática de estrategia y conquista, incluye tablero, cartas y dados.",
        sku: "JUGT-MESA-005",
        price: NumberDecimal("700.00"),
        stock: 60,
        categoryId: CAT.juguetes, imageUrl: "https://loremflickr.com/400/400/board+game,product/all",
        active: true, createdAt: now, updatedAt: now
    }
]);

print(`✓ Productos insertados: ${productsResult.insertedIds.length}`);

// =========================================================================
// RESUMEN
// =========================================================================
print("\n" + "=".repeat(60));
print("  SEED COMPLETADO");
print("=".repeat(60));
print(`  Categorías: ${db.categories.countDocuments()}`);
print(`  Productos:  ${db.products.countDocuments()}`);
print("\n  Distribución por categoría:");
["Electrónica", "Ropa", "Hogar", "Deportes", "Juguetes"].forEach(cat => {
    const catDoc = db.categories.findOne({ name: cat });
    if (catDoc) {
        const count = db.products.countDocuments({ categoryId: catDoc._id.toString() });
        print(`    ${cat}: ${count} productos`);
    }
});

