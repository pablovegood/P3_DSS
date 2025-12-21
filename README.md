# Práctica 3 DSS: Desarrollo de cliente responsivo para dispositivos móviles para el servicio web RESTful


Aplicación Android de tipo e-commerce que consume una **API REST** para mostrar un **catálogo de productos** (en mi caso libros), permitir **búsqueda**, gestionar un **carrito** y ejecutar un **checkout** (pago simulado). Incluye acceso **Admin** para **añadir/eliminar productos** mediante autenticación **Basic**.


---

## Instalación y ejecución

### Requisitos
- **Android Studio** (recomendado: versión estable reciente)
- SDK Android instalado desde Android Studio
- Un **emulador** o dispositivo Android

### 1) Clonar el repositorio
```bash
git clone https://github.com/pablovegood/P3_DSS.git
cd P3_DSS
```

### 2) Abrir y sincronizar
1. Abre el proyecto con **Android Studio** (`File > Open` → carpeta `P3_DSS`)
2. Espera a que **Gradle** sincronice dependencias

### 3) Backend (API REST)
La app está configurada para usar como base URL:

- **Emulador Android → PC**: `http://10.0.2.2:8080/`

Esto apunta al `localhost` de tu PC desde el emulador (`10.0.2.2`).

✅ **Antes de ejecutar la app**, levanta tu backend en el puerto **8080** (por ejemplo `http://localhost:8080/` en el PC). Si no se tiene el backend, tomarlo del archivo zip p1_backend_spring.zip en la carpeta raiz.

> Si ejecutas la app en un **móvil físico**, sustituye `10.0.2.2` por la **IP local** del PC en la misma red (por ejemplo `http://192.168.X.Y:8080/`) y actualiza `ApiClient.kt`.

### 4) Ejecutar
Pulsa **Run ▶** en Android Studio y elige tu emulador/dispositivo.

---

## Dependencias utilizadas

- **Retrofit**: cliente HTTP para consumir la API REST.
- **Gson** (converter): serialización/deserialización JSON.
- **Material Components**: `MaterialToolbar`, `MaterialButton`, `MaterialCardView`, `TextInputLayout`, etc.
- **RecyclerView**: listado de productos y carrito.
- **Google Maps SDK for Android** para mostrar los almacenes cercanos.

### Google Maps API (si aplica)
Para usar Google Maps, necesitas una API key:
1. Crear una key en Google Cloud (habilitar **Maps SDK for Android**).
2. Añadir la key en el `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI"/>
```

---

## Estructura de carpetas / organización del código

> Estructura típica de un proyecto Android. Los paquetes pueden variar según la configuración del proyecto.

### Código Kotlin
Ruta habitual:
```
app/src/main/java/com/example/p3_dss/
```

Principales clases (según implementación actual):
- **Activities**
  - `MainActivity.kt`: catálogo + búsqueda + navegación inferior + acceso admin.
  - `CartActivity.kt`: pantalla del carrito (usa almacenamiento local).
  - `CheckoutActivity.kt`: checkout con **pago simulado** y limpieza del carrito local.
  - `AdminProductsActivity.kt`: gestión admin (alta/baja productos).
- **Adapters**
  - `ProductAdapter.kt`: `RecyclerView` del catálogo.
  - `CartAdapter.kt`: `RecyclerView` del carrito.
  - `AdminProductsAdapter.kt`: `RecyclerView` de gestión admin.
- **Red (API)**
  - `ApiClient.kt`: configuración de Retrofit y `BASE_URL`.
  - `ApiService.kt`: definición de endpoints REST.
- **Modelos**
  - `Product.kt`: modelo de producto (mapeo JSON → Kotlin).
  - `ProductCreateRequest.kt`: payload para crear producto.
- **Persistencia**
  - `CartStorage.kt`: carrito en almacenamiento local (SharedPreferences).
  - `AdminSession.kt`: persistencia de sesión/credenciales admin (SharedPreferences).

### Recursos Android
Ruta habitual:
```
app/src/main/res/
```

- `layout/`: pantallas (`activity_main.xml`, `activity_cart.xml`, `activity_checkout.xml`, etc.) y celdas (`product_item.xml`, etc.)
- `menu/`: menús (`bottom_nav_menu.xml`, `menu_products.xml`, etc.)
- `values/`: strings, colores, temas
- `drawable/`: iconos y recursos gráficos

---

## Endpoints API utilizados

Base URL configurada en la app: `http://10.0.2.2:8080/`

| Método | Endpoint | Descripción |
|---|---|---|
| **GET** | `/api/productos` | Devuelve el listado de productos. Admite filtro opcional por texto mediante `query`. |
| **GET** | `/api/cart` | Obtiene el estado actual del carrito (si el backend lo soporta). |
| **POST** | `/api/cart/add/{id}` | Añade un producto al carrito por id (si el backend lo soporta). |
| **POST** | `/api/cart/remove/{id}` | Elimina una unidad del producto del carrito por id (si el backend lo soporta). |
| **POST** | `/api/cart/clear` | Vacía el carrito (si el backend lo soporta). |
| **POST** | `/api/cart/checkout` | Realiza el checkout / finaliza compra (en esta app el pago se simula y el carrito se limpia localmente). |
| **POST** | `/api/productos` | Crea un producto (requiere `Authorization: Basic ...`). |
| **DELETE** | `/api/productos/{id}` | Elimina un producto por id (requiere `Authorization: Basic ...`). |
| **GET** | `/api/auth/me` | Valida credenciales de administrador (requiere `Authorization: Basic ...`). |

### Autenticación Admin
Los endpoints admin requieren cabecera:
- `Authorization: Basic base64(usuario:contraseña)`

La app valida credenciales usando:
- `GET /api/auth/me`


