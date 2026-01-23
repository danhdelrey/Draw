# Model Refactoring Documentation

## Overview
This document describes the comprehensive refactoring of all data models in the Draw application to ensure easy future extensibility and maintainability.

## Design Principles

### 1. **Immutability**
All models are fully immutable with:
- All properties declared as `val` (not `var`)
- Update methods return new instances via `copy()`
- No mutable state within model objects

### 2. **Sealed Interfaces over Abstract Classes**
Models use `sealed interface` instead of `sealed class` or `abstract class`:
- More flexible composition
- Can be implemented by data classes
- Better support for type hierarchies

### 3. **Extensibility**
Models support extension through:
- Property maps for custom data
- Metadata objects for versioning
- Enum-based type systems
- Factory methods for common use cases

### 4. **Platform Independence**
All models work across Android, iOS, and Web:
- No platform-specific APIs in common code
- `expect/actual` pattern for platform utilities
- Custom ID generation and timestamp functions

## Model Architecture

### Brush Models (`data/model/brush/`)

#### Base Interface: `Brush`
```kotlin
sealed interface Brush {
    val id: String                    // Unique instance identifier
    val type: BrushType               // Type classification (enum)
    val size: Float                   // Stroke width
    val opacity: Float                // Transparency (0f..1f)
    val colorArgb: Long               // Color (ARGB format)
    val imageResource: DrawableResource  // UI icon
    val properties: BrushProperties   // Extensible properties
    
    fun updateSize(size: Float): Brush
    fun updateOpacity(opacity: Float): Brush
    fun updateColor(colorArgb: Long): Brush
    fun updateProperties(properties: BrushProperties): Brush
}
```

#### Brush Types
- **SolidBrush**: Standard drawing with uniform stroke
- **AirBrush**: Spray-paint effect with density control
- **EraserBrush**: Removes content (fixed transparent color)
- **BucketBrush**: Flood-fill with tolerance settings

#### Extensible Properties
`BrushProperties` uses a map-based approach:
```kotlin
data class BrushProperties(
    val properties: Map<String, Any> = emptyMap()
)
```

**Benefits:**
- Add new properties without breaking existing code
- Type-safe getters for common properties
- Easy serialization/deserialization

**Example Usage:**
```kotlin
val airBrush = AirBrush()
    .updateDensity(0.7f)
    .updateSize(40f)

// Access properties
val density = airBrush.density  // Convenience property
val customProp = airBrush.properties.getFloat("myCustom", 0f)
```

### Layer Models (`data/model/layer/`)

#### Base Interface: `Layer`
```kotlin
sealed interface Layer {
    val id: String              // Unique identifier
    val name: String            // Display name
    val isVisible: Boolean      // Visibility toggle
    val isLocked: Boolean       // Lock editing
    val opacity: Float          // Layer opacity
    val blendMode: BlendMode    // Compositing mode
    val metadata: LayerMetadata // Timestamps & properties
    
    fun updateVisibility(isVisible: Boolean): Layer
    fun updateLocked(isLocked: Boolean): Layer
    fun updateOpacity(opacity: Float): Layer
    fun updateName(name: String): Layer
    fun updateBlendMode(blendMode: BlendMode): Layer
    fun updateMetadata(metadata: LayerMetadata): Layer
}
```

#### Layer Types
- **VectorLayer**: Stores paths (resolution-independent)
- **BitmapLayer**: Stores raster data (reference to bitmap storage)

#### Layer Metadata
```kotlin
data class LayerMetadata(
    val type: LayerType,
    val createdAt: Long,
    val modifiedAt: Long,
    val properties: Map<String, Any>
)
```

**Benefits:**
- Track creation and modification times
- Support versioning and migration
- Extensible custom properties

### Drawing Path Models (`data/model/base/`)

#### DrawingPath
```kotlin
data class DrawingPath(
    val id: String,
    val points: List<Offset>,
    val brush: Brush,
    val metadata: PathMetadata
)
```

**Features:**
- Unique ID for each stroke
- Complete brush information embedded
- Metadata for pressure, timestamps, etc.
- Computed properties: `isValid`, `bounds`

**Benefits:**
- Self-contained rendering information
- Support for stylus pressure
- Efficient culling with bounding boxes

### Canvas Models (`data/model/canvas/`)

#### DrawingCanvas
```kotlin
data class DrawingCanvas(
    val id: String,
    val width: Float,
    val height: Float,
    val layers: List<Layer>,
    val activeLayerId: String,
    val metadata: CanvasMetadata
)
```

**Convenience Methods:**
```kotlin
fun updateLayer(layerId: String, update: (Layer) -> Layer): DrawingCanvas
fun addLayer(layer: Layer, position: Int? = null): DrawingCanvas
fun removeLayer(layerId: String): DrawingCanvas
fun moveLayer(fromIndex: Int, toIndex: Int): DrawingCanvas
```

**Benefits:**
- Functional layer management
- Immutable updates
- Type-safe operations

#### CanvasConfig
```kotlin
data class CanvasConfig(
    val width: Float,
    val height: Float,
    val backgroundColor: Long,
    val maxLayers: Int,
    val maxUndoSteps: Int,
    // ... more settings
)
```

**Presets:**
```kotlin
CanvasConfig.Presets.SQUARE_1080
CanvasConfig.Presets.HD_LANDSCAPE
CanvasConfig.Presets.A4_PORTRAIT_300DPI
// ... and more
```

## Platform Utilities (`data/model/util/`)

### ID Generation
```kotlin
fun generateId(): String
```
Platform-independent unique ID generation using timestamp + random.

### Timestamp
```kotlin
expect fun currentTimeMillis(): Long
```

**Implementations:**
- **Android**: `System.currentTimeMillis()`
- **iOS**: `NSDate().timeIntervalSince1970`
- **WasmJS**: `Date.now()` via `@JsFun`

## Migration Guide

### From Old to New Models

#### Brush Updates
**Before:**
```kotlin
sealed class Brush {
    abstract val size: Float
    // ...
}
```

**After:**
```kotlin
sealed interface Brush {
    val id: String
    val type: BrushType
    val size: Float
    val properties: BrushProperties
    // ...
}
```

**Migration Steps:**
1. Add `id` property to all brush instances (automatically generated)
2. Add `type` property (use enum value)
3. Move custom properties to `BrushProperties`
4. Implement `updateProperties()` method

#### Layer Updates
**Before:**
```kotlin
data class VectorLayer(
    override val id: String,
    override val isVisible: Boolean,
    val paths: List<DrawingPath>
) : Layer()
```

**After:**
```kotlin
data class VectorLayer(
    override val id: String,
    override val name: String,
    override val isVisible: Boolean,
    override val isLocked: Boolean,
    override val opacity: Float,
    override val blendMode: BlendMode,
    override val metadata: LayerMetadata,
    val paths: List<DrawingPath>
) : Layer
```

**Migration Steps:**
1. Add `name` property with default value
2. Add `blendMode` with `BlendMode.NORMAL`
3. Add `metadata` with `LayerMetadata(type = LayerType.VECTOR)`
4. Implement all interface methods

#### CanvasConfig Updates
**Before:**
```kotlin
object CanvasConfig {
    const val FIXED_WIDTH = 1080f
    const val FIXED_HEIGHT = 1080f
}
```

**After:**
```kotlin
data class CanvasConfig(
    val width: Float = DEFAULT_WIDTH,
    val height: Float = DEFAULT_HEIGHT,
    // ... more settings
)
```

**Migration Steps:**
1. Replace `CanvasConfig.FIXED_WIDTH` with `CanvasConfig.DEFAULT_WIDTH`
2. Replace `CanvasConfig.FIXED_HEIGHT` with `CanvasConfig.DEFAULT_HEIGHT`
3. Use presets for common sizes: `CanvasConfig.Presets.SQUARE_1080`

## Future Extension Examples

### Adding a New Brush Type

```kotlin
// 1. Add enum value
enum class BrushType {
    // ...existing...
    WATERCOLOR
}

// 2. Create brush class
data class WatercolorBrush(
    override val id: String = generateId(),
    override val size: Float = 25f,
    override val opacity: Float = 0.5f,
    override val colorArgb: Long = 0xFF000000,
    override val imageResource: DrawableResource = Res.drawable.watercolor,
    override val properties: BrushProperties = BrushProperties(
        mapOf(
            "wetness" to 0.7f,
            "bleeding" to 0.3f
        )
    )
) : Brush {
    override val type: BrushType = BrushType.WATERCOLOR
    
    val wetness: Float
        get() = properties.getFloat("wetness", 0.7f)
    
    // Implement interface methods...
}
```

### Adding a New Layer Type

```kotlin
// 1. Add enum value
enum class LayerType {
    // ...existing...
    TEXT
}

// 2. Create layer class
data class TextLayer(
    override val id: String,
    override val name: String = "Text Layer",
    // ...standard layer properties...
    val text: String,
    val fontSize: Float,
    val fontFamily: String
) : Layer {
    // Implement interface methods...
}
```

### Adding Custom Properties

```kotlin
// To a brush
val customBrush = SolidBrush()
    .updateProperties(
        BrushProperties(mapOf(
            "customEffect" to "glow",
            "glowIntensity" to 0.8f
        ))
    )

// To a layer
val customLayer = vectorLayer.updateMetadata(
    layerMetadata.withProperty("exportQuality", "high")
        .withProperty("filterApplied", "blur")
)
```

## Benefits of This Refactoring

### 1. **Type Safety**
- Sealed interfaces prevent invalid states
- Enum-based typing
- Compile-time guarantees

### 2. **Testability**
- Pure data models
- No side effects
- Easy to construct test data

### 3. **Serialization Ready**
- All models are data classes
- Map-based properties serialize easily
- Versioning support via metadata

### 4. **Performance**
- Immutable structures enable caching
- Structural sharing in collections
- Efficient updates via copy()

### 5. **Documentation**
- Clear property documentation
- Factory methods for common cases
- Examples in companion objects

## Best Practices

### 1. Always Use Factory Methods
```kotlin
// Good
val brush = SolidBrush.withColor(0xFF0000FF)

// Avoid
val brush = SolidBrush(colorArgb = 0xFF0000FF)
```

### 2. Use Convenience Properties
```kotlin
// Good
val density = airBrush.density

// Avoid
val density = airBrush.properties.getFloat(BrushProperties.DENSITY)
```

### 3. Update Methods Return New Instances
```kotlin
// Good
val newBrush = brush.updateSize(30f)

// Wrong - doesn't work (immutable)
brush.size = 30f
```

### 4. Use Metadata for Extensibility
```kotlin
// Good - extensible
layer.updateMetadata(
    metadata.withProperty("customData", myData)
)

// Avoid - not extensible
// Adding new fields to layer class
```

## Conclusion

This refactoring establishes a solid foundation for the Draw application's data layer. The models are:
- ✅ Fully immutable
- ✅ Type-safe
- ✅ Extensible
- ✅ Platform-independent
- ✅ Well-documented
- ✅ Future-proof

The architecture supports easy addition of new brush types, layer types, and custom properties without breaking existing code.

