# Prompt — App Android de Notas (Clean Architecture + MVI + Jetpack Compose)

> Copia todo lo que sigue como prompt. Está escrito para que el modelo genere un proyecto
> consistente, sin inventar librerías ni capas fuera del alcance.

---

## 1. Rol

Actúa como **Android Engineer senior**, experto en Clean Architecture, patrones de diseño, MVI, Jetpack Compose y Material Design 3. Escribes código de producción: explícito, testeable y sin atajos.

Reglas de interacción:

- Genera **una capa a la vez**, de `domain` hacia afuera. Nunca todas en una sola respuesta.
- Antes de escribir cada archivo, di **en qué capa vive y por qué**.
- No inventes librerías fuera del stack declarado.
- Si falta un dato para decidir, **pregunta en lugar de asumir**.

---

## 2. Objetivo

Un **bloc de notas Android** local, sin backend. Sirve como referencia didáctica de Clean Architecture + MVI con Compose, y como base de ejercicios: dos funcionalidades quedan deliberadamente sin implementar.

---

## 3. Stack obligatorio

| Área | Decisión |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose. **Nunca XML de vistas ni ViewBinding.** |
| Design system | Material Design 3 (`androidx.compose.material3`) |
| Arquitectura | Clean Architecture (data / domain / presentation) + **MVI** |
| MVI | Implementado a mano. **Sin Orbit, Mavericks ni MVIKotlin.** |
| DI | Hilt (+ `hilt-navigation-compose`) |
| Persistencia | **Room, únicamente local.** Sin red, sin sincronización. |
| Asincronía | Coroutines + Flow |
| Navegación | Navigation Compose type-safe con `kotlinx.serialization` |
| Build | Gradle KTS + version catalog (`libs.versions.toml`) + KSP para Room |
| SDK | `minSdk 24`, `compileSdk`/`targetSdk` estable actual |

Módulo único (`:app`), **paquetes por capa**.

---

## 4. Alcance funcional

| Funcionalidad | Estado esperado |
|---|---|
| **Listar notas** | ✅ Funcional de punta a punta |
| **Crear nota** | ✅ Funcional de punta a punta |
| **Editar nota** | ⛔ Botón visible en cada ítem. No opera: caso de uso con `TODO()` |
| **Eliminar nota** | ⛔ Botón visible en cada ítem. No opera: caso de uso con `TODO()` |

Los botones de editar y eliminar **se ven, están habilitados y son clicables**. Al pulsarlos la app muestra un `Snackbar` de "no implementado" y **no crashea** (ver §14).

---

## 5. Regla de dependencias

```
presentation  →  domain  ←  data
```

- **domain**: Kotlin puro. Cero imports de Android, Room, Hilt o Compose. Si un archivo de domain necesita framework, es un error de diseño, no un import faltante.
- **data**: implementa las interfaces declaradas en domain.
- **presentation**: consume solo casos de uso. Nunca toca un `Entity` de Room ni el `NoteDao`.

---

## 6. Estructura de paquetes

```
com.example.notes/
├── NotesApplication.kt              @HiltAndroidApp
├── MainActivity.kt                  @AndroidEntryPoint, setContent { NotesApp() }
├── di/
│   ├── DatabaseModule.kt            provee NotesDatabase y NoteDao
│   └── RepositoryModule.kt          @Binds NoteRepositoryImpl → NoteRepository
├── domain/
│   ├── model/                       Note
│   ├── repository/                  NoteRepository (interfaz)
│   └── usecase/                     GetNotesUseCase, CreateNoteUseCase,
│                                    GetNoteByIdUseCase, UpdateNoteUseCase,
│                                    DeleteNoteUseCase
├── data/
│   ├── local/
│   │   ├── dao/                     NoteDao
│   │   ├── entity/                  NoteEntity
│   │   └── NotesDatabase.kt
│   ├── mapper/                      NoteMapper.kt (funciones de extensión)
│   └── repository/                  NoteRepositoryImpl
└── presentation/
    ├── navigation/                  NotesNavHost.kt, Routes.kt
    ├── notelist/                    NoteListContract, NoteListViewModel,
    │                                NoteListScreen, NoteItem
    ├── noteeditor/                  NoteEditorContract, NoteEditorViewModel,
    │                                NoteEditorScreen
    └── theme/                       Color.kt, Type.kt, Theme.kt
```

---

## 7. Modelo de datos

### Domain (Kotlin puro)

```kotlin
/**
 * Nota del usuario. Vive en domain: no conoce Room ni Android.
 * Las fechas son epoch millis para no arrastrar dependencias de formato.
 */
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

### Data (Room)

```kotlin
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

Mappers como funciones de extensión: `NoteEntity.toDomain()`, `Note.toEntity()`.

### DAO

```kotlin
@Dao
interface NoteDao {
    // Flow: Room re-emite solo cuando cambia la tabla; la UI se actualiza sola.
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun observeNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
```

> El DAO y el `NoteRepositoryImpl` se implementan **completos**, incluidos update y delete. La pieza faltante vive en los casos de uso y el ViewModel (§14): así el ejercicio se resuelve en las capas que se están enseñando, no reescribiendo el acceso a datos.

### Repositorio (interfaz en domain)

```kotlin
interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun createNote(title: String, content: String)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Long)
}
```

---

## 8. Casos de uso

Un caso de uso = una clase = un `operator fun invoke()`.

| Clase | Estado |
|---|---|
| `GetNotesUseCase` | ✅ implementado |
| `CreateNoteUseCase` | ✅ implementado (valida título no vacío, sella `createdAt`/`updatedAt`) |
| `GetNoteByIdUseCase` | ⛔ `TODO()` |
| `UpdateNoteUseCase` | ⛔ `TODO()` |
| `DeleteNoteUseCase` | ⛔ `TODO()` |

---

## 9. Contrato MVI

Cada pantalla declara su contrato en un archivo propio, con esta forma exacta:

```kotlin
/** Contrato MVI de la pantalla de listado. */
interface NoteListContract {

    /** Estado único e inmutable que la UI renderiza. Sin lógica de negocio. */
    data class State(
        val notes: List<Note> = emptyList(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    ) {
        // Derivado: evita que la UI recalcule condiciones de vacío.
        val isEmpty: Boolean get() = !isLoading && notes.isEmpty()
    }

    /** Todo lo que el usuario puede hacer en la pantalla. */
    sealed interface Intent {
        data object CreateNoteClicked : Intent
        data class EditNoteClicked(val noteId: Long) : Intent
        data class DeleteNoteClicked(val noteId: Long) : Intent
    }

    /** Eventos de una sola vez: navegación y mensajes. Nunca viven en el State. */
    sealed interface Effect {
        data object NavigateToEditor : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
```

Reglas duras de MVI:

- El ViewModel expone **un solo** `StateFlow<State>` público e inmutable (`asStateFlow()`).
- Los `Effect` viajan por `Channel(Channel.BUFFERED)` expuesto como `receiveAsFlow()`. Nunca por el `State`.
- Punto de entrada único: `fun onIntent(intent: Intent)` con `when` **exhaustivo** (sin `else`).
- El `State` se actualiza siempre con `update { it.copy(...) }`. Nada de mutación parcial fuera del `update`.
- El listado se alimenta de `GetNotesUseCase()` con `stateIn`/`onEach`; nunca se recarga a mano tras crear una nota (Room re-emite solo).

---

## 10. Navegación

```kotlin
@Serializable data object NoteListRoute
@Serializable data class NoteEditorRoute(val noteId: Long? = null)
```

- `NotesNavHost` con `composable<NoteListRoute>` y `composable<NoteEditorRoute>`.
- El `noteId` se lee con `savedStateHandle.toRoute<NoteEditorRoute>()` en el ViewModel.
- Hoy el editor **siempre** llega con `noteId = null` (modo creación). El parámetro ya existe para que el ejercicio de edición no obligue a tocar navegación.

---

## 11. UI

### Pantalla de listado (`NoteListScreen`)

- `Scaffold` con `TopAppBar` (título "Mis notas") y `FloatingActionButton` "Nueva nota".
- `LazyColumn` con `items(notes, key = { it.id })`.
- Cada ítem es un `Card` con: título (`titleMedium`), preview del contenido (`bodyMedium`, `maxLines = 2`, `TextOverflow.Ellipsis`), fecha formateada (`labelSmall`) y una fila con dos `IconButton`: `Icons.Default.Edit` y `Icons.Default.Delete`.
- Estado vacío: ícono + mensaje centrado cuando `state.isEmpty`.
- Estado de carga: `CircularProgressIndicator` centrado.
- `SnackbarHost` conectado a los `Effect`.

### Pantalla de editor (`NoteEditorScreen`)

- `TopAppBar` con navegación atrás y acción "Guardar", habilitada solo si el título no está en blanco.
- `OutlinedTextField` para título (una línea) y otro para contenido (multilínea, ocupa el alto restante).
- Al guardar: `CreateNoteUseCase` → `Effect.NavigateBack`.

### Reglas de Compose

- Composables **stateless** que reciben `state: State` y `onIntent: (Intent) -> Unit`. El composable con `hiltViewModel()` es solo el wrapper de la ruta.
- Consumo de estado con `collectAsStateWithLifecycle()`.
- Consumo de efectos con `LaunchedEffect(Unit) { viewModel.effect.collect { ... } }`.
- `@Preview` para cada composable stateless, con datos falsos y tema aplicado.
- `Modifier` siempre como primer parámetro opcional, con default `Modifier`.

### Material 3

- `MaterialTheme` con **dynamic color** en Android 12+ y esquema propio de respaldo.
- Tema oscuro obligatorio y edge-to-edge (`enableEdgeToEdge()`).
- **Cero colores hardcodeados**: siempre `MaterialTheme.colorScheme.*`. Un `Color(0xFFFF5722)` en un composable es un bug.
- **Cero tamaños de texto hardcodeados**: siempre `MaterialTheme.typography.*`.
- Espaciado en múltiplos de 8dp (4dp aceptable para ajustes finos).
- Todo texto en `strings.xml`. Cero literales en composables.
- `contentDescription` en todo ícono clicable; área táctil mínima de 48dp.

---

## 12. Inyección de dependencias

- `DatabaseModule` (`@InstallIn(SingletonComponent::class)`): provee `NotesDatabase` con `Room.databaseBuilder` y el `NoteDao`.
- `RepositoryModule`: `@Binds` de `NoteRepositoryImpl` a `NoteRepository`.
- Casos de uso con `@Inject constructor`, sin módulo propio.
- ViewModels con `@HiltViewModel`.

---

## 13. Idioma del código

**Regla estricta, sin excepciones:**

- **Código 100% en inglés**: clases, funciones, variables, paquetes, parámetros, constantes, nombres de archivo, claves de `strings.xml`, columnas de Room.
- **Comentarios y KDoc en español**, cortos y objetivos. Explican **por qué**, no **qué**. Nada de `// asigna el título al estado`.
- **Valores de `strings.xml` en español** (los ve el usuario), con claves en inglés:
  `<string name="note_list_empty_message">Aún no tienes notas</string>`

Estilo esperado:

```kotlin
/**
 * Expone las notas ordenadas por última edición.
 * Devuelve Flow para que la UI reaccione a los cambios de Room sin recargar.
 */
class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = repository.observeNotes()
}
```

---

## 14. Lo que queda sin implementar (ejercicios)

Tres casos de uso se entregan declarados pero vacíos, con KDoc que describe el ejercicio:

```kotlin
/**
 * Elimina una nota por id.
 *
 * EJERCICIO 1 — Eliminar
 * 1. Delega en NoteRepository.deleteNote(id) (ya implementado en la capa data).
 * 2. En NoteListViewModel, maneja Intent.DeleteNoteClicked invocando este caso de uso
 *    dentro de viewModelScope.
 * 3. No recargues la lista a mano: el Flow del DAO re-emite solo.
 * 4. Opcional: pide confirmación con un AlertDialog antes de borrar.
 */
class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long) {
        TODO("Ejercicio 1: implementar la eliminación de la nota")
    }
}
```

Mismo tratamiento para `GetNoteByIdUseCase` y `UpdateNoteUseCase` (**Ejercicio 2 — Editar**: cargar la nota en el editor cuando `noteId != null`, precargar los campos, y guardar con `updatedAt` refrescado).

**Comportamiento en runtime, importante:** los `Intent.EditNoteClicked` y `Intent.DeleteNoteClicked` **sí existen** en el `when` del ViewModel, pero su rama emite `Effect.ShowMessage` con el texto de "no implementado" y lleva un `// TODO(Ejercicio N)`. Así el botón responde y la app **no lanza `NotImplementedError`** durante una demo. Los `TODO()` viven solo dentro de los casos de uso, que hoy nadie invoca.

---

## 15. Criterios de aceptación

1. El proyecto compila y corre sin warnings de dependencias faltantes.
2. `domain` no tiene un solo import de Android, Room, Hilt ni Compose.
3. Crear una nota la hace aparecer en el listado **sin refrescar manualmente**.
4. Las notas sobreviven al cierre de la app (persistencia real en Room).
5. Rotar el dispositivo no pierde el estado ni el texto del editor.
6. Tema oscuro correcto en ambas pantallas; sin colores ni tamaños hardcodeados.
7. Pulsar editar o eliminar muestra el `Snackbar` y no crashea.
8. Cada `when` sobre `Intent` es exhaustivo, sin `else`.
9. Todo comentario y KDoc está en español; todo identificador en inglés.

---

## 16. Orden de generación

1. `libs.versions.toml` + `build.gradle.kts` (proyecto y módulo) + `AndroidManifest.xml`
2. Capa **domain**: `Note`, `NoteRepository`, los 5 casos de uso
3. Capa **data**: `NoteEntity`, `NoteDao`, `NotesDatabase`, `NoteMapper`, `NoteRepositoryImpl`
4. **di**: `DatabaseModule`, `RepositoryModule`, `NotesApplication`
5. **presentation/notelist**: contrato → ViewModel → screen → ítem → preview
6. **presentation/noteeditor**: contrato → ViewModel → screen → preview
7. **navigation** + **theme** + `strings.xml` + `MainActivity`
8. `README.md` con: cómo correr el proyecto, mapa de capas y los dos ejercicios pendientes

Al terminar cada bloque, resume en dos líneas qué quedó listo antes de pasar al siguiente.

---

## 17. Fuera de alcance

No generes: Retrofit, Ktor, Supabase, Firebase, autenticación, multi-módulo Gradle, XML de vistas, ViewBinding, `LiveData`, RxJava, librerías MVI de terceros, búsqueda, categorías, adjuntos, papelera ni widgets.