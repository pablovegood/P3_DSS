# Práctica 3 DSS: Desarrollo de cliente responsivo para dispositivos móviles para el servicio web RESTful

Aplicación Android de tipo e-commerce que consume una **API REST** para mostrar un **catálogo de productos** (en este caso, *libros*), permitir **búsqueda**, gestionar un **carrito** y ejecutar un **checkout** (pago simulado). Incluye acceso **Admin** para **añadir/eliminar productos** mediante autenticación **Basic**.

---

## Instalación y ejecución

### Requisitos
- **Android Studio** (versión estable reciente recomendada)
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

✅ **Antes de ejecutar la app**, levanta tu backend en el puerto **8080** (por ejemplo `http://localhost:8080/` en el PC).
Si no se dispone del backend, usar el archivo **`p1_backend_spring.zip`** ubicado en la carpeta raíz del repositorio.

> Si ejecutas la app en un **móvil físico**, sustituye `10.0.2.2` por la **IP local** del PC en la misma red (por ejemplo `http://192.168.X.Y:8080/`) y actualiza `ApiClient.kt`.

### 4) Ejecutar
Pulsa **Run ▶** en Android Studio y elige tu emulador/dispositivo.

---

## Dependencias utilizadas

Estas dependencias están declaradas en `app/build.gradle.kts`:

- **Retrofit** (HTTP): `com.squareup.retrofit2:retrofit:2.11.0`
- **Converter Gson**: `com.squareup.retrofit2:converter-gson:2.11.0`
- **OkHttp**: `com.squareup.okhttp3:okhttp:4.12.0`
- **Logging Interceptor**: `com.squareup.okhttp3:logging-interceptor:4.12.0`
- **Gson**: `com.google.code.gson:gson:2.10.1`
- **Material Components**: `com.google.android.material:material:1.12.0`
- **RecyclerView**: `androidx.recyclerview:recyclerview:1.3.2`
- **ConstraintLayout**: `androidx.constraintlayout:constraintlayout:2.1.4`
- **AppCompat**: `androidx.appcompat:appcompat:1.7.0`
- **Google Maps SDK for Android**: `com.google.android.gms:play-services-maps:18.2.0`
- **Google Play Services Location**: `com.google.android.gms:play-services-location:21.3.0`

---

## Google Maps (almacenes cercanos)

La pantalla de mapa (`MapActivity`) implementa:
- Muestra un mapa con el título **"Almacenes Cerca"**.
- Solicita permiso de ubicación (FINE) y, si se concede, centra la cámara en la posición del usuario.
- Genera una lista de **almacenes mock** cercanos y añade marcadores en el mapa.
- Al pulsar un marcador, abre **Google Maps** con indicaciones hacia el almacén seleccionado.

### Configurar Google Maps API Key
Para usar Google Maps, necesitas una API key:
1. Crear una key en Google Cloud (habilitar **Maps SDK for Android**).
2. Añadir la key en el `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI"/>
```

---

## Permisos y configuración de red

En `AndroidManifest.xml` se declaran los permisos:
- `android.permission.INTERNET`
- `android.permission.ACCESS_FINE_LOCATION`
- `android.permission.ACCESS_COARSE_LOCATION`

Además, la app permite tráfico HTTP sin TLS (`usesCleartextTraffic="true"`) para poder consumir el backend en `http://...` durante la práctica.

---

## Estructura de carpetas / organización del código

> Estructura típica de un proyecto Android. El paquete principal es `com.example.p3_dss`.

### Código Kotlin
Ruta habitual:
```
app/src/main/java/com/example/p3_dss/
```

Organización principal:
- **Activities**
  - `MainActivity.kt`: catálogo + búsqueda + navegación inferior + acceso admin.
  - `CartActivity.kt`: pantalla del carrito (usa almacenamiento local).
  - `CheckoutActivity.kt`: checkout con **pago simulado** y limpieza del carrito local.
  - `AdminProductsActivity.kt`: gestión admin (alta/baja productos).
  - `MapActivity.kt`: mapa con almacenes cercanos (mock) y navegación externa a Google Maps.
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
  - `ResponseModel.kt`: modelo de respuesta usado en operaciones de borrado admin.
- **Persistencia**
  - `CartStorage.kt`: carrito en almacenamiento local (SharedPreferences).
  - `AdminSession.kt`: persistencia de sesión/credenciales admin (SharedPreferences).

### Recursos Android
Ruta habitual:
```
app/src/main/res/
```

- `layout/`: pantallas (`activity_main.xml`, `activity_cart.xml`, `activity_checkout.xml`, `activity_map.xml`, etc.) y celdas (`product_item.xml`, etc.)
- `menu/`: menús (`bottom_nav_menu.xml`, `menu_products.xml`, etc.)
- `values/`: `strings.xml`, `colors.xml`, `themes.xml`
- `drawable/`: iconos y recursos gráficos

---

## Endpoints API utilizados

Base URL configurada en la app: `http://10.0.2.2:8080/`

| Método | Endpoint | Descripción |
|---|---|---|
| **GET** | `/api/productos` | Devuelve el listado de productos (libros). Admite filtro opcional por texto mediante `query`. |
| **GET** | `/api/cart` | Obtiene el estado del carrito (si el backend lo soporta). |
| **POST** | `/api/cart/add/{id}` | Añade un producto al carrito por `id` (si el backend lo soporta). |
| **POST** | `/api/cart/remove/{id}` | Elimina una unidad del producto del carrito por `id` (si el backend lo soporta). |
| **POST** | `/api/cart/clear` | Vacía el carrito (si el backend lo soporta). |
| **POST** | `/api/cart/checkout` | Realiza el checkout / finaliza compra (en la app el pago se simula). |
| **POST** | `/api/productos` | Crea un producto (requiere `Authorization: Basic ...`). |
| **DELETE** | `/api/productos/{id}` | Elimina un producto por `id` (requiere `Authorization: Basic ...`). |
| **GET** | `/api/auth/me` | Valida credenciales de administrador (requiere `Authorization: Basic ...`). |

### Autenticación Admin
Los endpoints admin requieren cabecera:
- `Authorization: Basic base64(usuario:contraseña)`

La app valida credenciales usando:
- `GET /api/auth/me`

  
