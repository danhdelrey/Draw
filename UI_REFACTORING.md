# UI Refactoring Documentation

## Overview
This document describes the refactoring of UI components to properly utilize the newly refactored data models, improving code quality, maintainability, and extensibility.

## Key Changes

### 1. DrawingState Refactoring

#### Before
```kotlin
data class DrawingState(
    val currentBrush: Brush = SolidBrush(),
    val currentTouchPosition: Offset? = null,
    val currentDrawingPath: DrawingPath? = null,
    val currentActiveLayer: Layer = VectorLayer(id = "default_layer"),
    val currentLayers: List<Layer> = listOf(VectorLayer(id = "default_layer")),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)
```

#### After
```kotlin
data class DrawingState(
    // Centralized canvas state
    val canvas: DrawingCanvas = createDefaultCanvas(),
    
    // Drawing tool state
    val currentBrush: Brush = SolidBrush.default(),
    
    // Ephemeral drawing state
    val currentTouchPosition: Offset? = null,
    val currentDrawingPath: DrawingPath? = null,
    
    // Undo/Redo state
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
) {
    // Convenience accessors
    val activeLayer: VectorLayer?
        get() = canvas.activeLayer as? VectorLayer
    
    val layers: List<Layer>
        get() = canvas.layers
}
```

**Benefits:**
- ✅ Centralized canvas state using `DrawingCanvas` model
- ✅ Factory methods for default values (`SolidBrush.default()`)
- ✅ Convenience accessors for cleaner code
- ✅ Better separation between persistent and ephemeral state
- ✅ Easier to add canvas-level features (background, metadata, etc.)

### 2. DrawingCommand Refactoring

#### Before
Commands manually managed layer lists with direct `copy()` operations:
```kotlin
val updatedLayers = state.currentLayers.map { layer ->
    if (layer.id == layerId && layer is VectorLayer) {
        layer.copy(paths = layer.paths + path)
    } else {
        layer
    }
}
return state.copy(currentLayers = updatedLayers)
```

#### After
Commands use `DrawingCanvas` update methods:
```kotlin
val updatedCanvas = state.canvas.updateLayer(layerId) { layer ->
    if (layer is VectorLayer) {
        layer.addPath(path)
    } else {
        layer
    }
}
return state.copy(canvas = updatedCanvas)
```

**New Commands Added:**
1. ✅ `UpdateLayerOpacityCommand` - Change layer transparency
2. ✅ `RenameLayerCommand` - Change layer name
3. ✅ `MoveLayerCommand` - Reorder layers
4. ✅ `ClearLayerCommand` - Clear all paths in a layer

**Benefits:**
- ✅ Cleaner, more readable code
- ✅ Leverages model update methods
- ✅ Easier to add new layer operations
- ✅ Better encapsulation of state changes
- ✅ More comprehensive undo/redo support

### 3. DrawingScreenViewModel Refactoring

#### Improvements

**Better Code Organization:**
```kotlin
fun onEvent(event: DrawingEvent) {
    when (event) {
        is DrawingEvent.StartDrawing -> handleStartDrawing(event)
        is DrawingEvent.UpdateDrawing -> handleUpdateDrawing(event)
        // ... other events
    }
}

private fun handleStartDrawing(event: DrawingEvent.StartDrawing) {
    _state.value = _state.value.copy(
        currentDrawingPath = DrawingPath.create(
            initialPoint = event.currentTouchPosition,
            brush = _state.value.currentBrush
        ),
        currentTouchPosition = event.currentTouchPosition
    )
}
```

**Factory Method Usage:**
```kotlin
private fun handleAddLayer() {
    val layerCount = _state.value.layers.size
    val newLayer = VectorLayer.create(
        id = generateId(),
        name = "Layer ${layerCount + 1}"
    )
    val command = AddLayerCommand(newLayer)
    performCommand(command)
}
```

**Benefits:**
- ✅ Separate handler methods for each event type
- ✅ Uses factory methods from models
- ✅ Better logging and debugging
- ✅ Easier to test individual handlers
- ✅ Clearer code flow

### 4. View Layer Updates

#### DrawingScreen
```kotlin
// Before
activeLayer = state.currentActiveLayer,
currentLayers = state.currentLayers,

// After
activeLayer = state.canvas.activeLayer,
currentLayers = state.layers,
```

#### DrawingCanvasContent
```kotlin
// Before
state.currentLayers.forEach { layer ->
    val pathBeingDrawn = if (layer.id == state.currentActiveLayer.id) 
        state.currentDrawingPath else null
    
    // ... rendering code
}

// After
state.layers.forEach { layer ->
    val isActiveLayer = layer.id == state.canvas.activeLayerId
    val pathBeingDrawn = if (isActiveLayer) 
        state.currentDrawingPath else null
    
    // ... rendering code
}
```

**Benefits:**
- ✅ Cleaner property access
- ✅ More explicit active layer checking
- ✅ Better code readability
- ✅ Leverages convenience accessors

### 5. BrushSelection Component

#### Before
```kotlin
val brushList = listOf<Brush>(
    SolidBrush(),
    EraserBrush()
)

ImageButton(
    brush.imageResource,
    isSelected = currentBrush::class == brush::class,
    onClick = { /* ... */ }
)
```

#### After
```kotlin
val brushList = remember {
    listOf(
        SolidBrush.default(),
        AirBrush.default(),
        EraserBrush.default()
        // Easy to add: WatercolorBrush.default(), etc.
    )
}

ImageButton(
    brush.imageResource,
    isSelected = currentBrush.type == brush.type,
    onClick = { /* ... */ }
)
```

**Benefits:**
- ✅ Uses factory methods
- ✅ Type-based selection (cleaner than class comparison)
- ✅ Added AirBrush support
- ✅ Documented extension points
- ✅ Memoized brush list for performance

### 6. BrushConfigBottomSheet

#### Improvements
```kotlin
BrushSelection(
    initialBrush = newBrush,
    onBrushSelected = { selectedBrush ->
        // Preserve size, opacity, and color when switching brush type
        newBrush = selectedBrush
            .updateSize(brushSize)
            .updateOpacity(brushOpacity)
            .updateColor(initialBrush.colorArgb)
    }
)
```

**Benefits:**
- ✅ Better property preservation
- ✅ Clear documentation of behavior
- ✅ Uses update methods from models
- ✅ Improved user experience

## Architecture Benefits

### 1. **Separation of Concerns**
```
┌─────────────────────────────────────┐
│         UI Layer (Views)            │
│  - DrawingScreen                    │
│  - DrawingCanvasContent             │
│  - BrushConfigBottomSheet           │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│    ViewModel Layer (State)          │
│  - DrawingScreenViewModel           │
│  - DrawingState                     │
│  - DrawingCommand                   │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│      Model Layer (Data)             │
│  - DrawingCanvas                    │
│  - Layer (VectorLayer, BitmapLayer) │
│  - Brush (SolidBrush, AirBrush...)  │
│  - DrawingPath                      │
└─────────────────────────────────────┘
```

### 2. **Unidirectional Data Flow (UDF)**
```
User Action
    ↓
DrawingEvent
    ↓
ViewModel.onEvent()
    ↓
DrawingCommand.execute()
    ↓
Update DrawingState
    ↓
State Flow emission
    ↓
UI Recomposition
```

### 3. **Command Pattern for Undo/Redo**
```kotlin
Command Pattern Benefits:
1. Each command encapsulates one atomic change
2. Commands know how to undo themselves
3. Stack-based undo/redo management
4. Easy to add new undoable operations
5. History branching handled automatically
```

## Code Quality Improvements

### 1. **Type Safety**
- ✅ Sealed interfaces prevent invalid states
- ✅ Enum-based type checking
- ✅ Compile-time guarantees
- ✅ No magic strings or numbers

### 2. **Testability**
```kotlin
// Easy to test state transformations
val initialState = DrawingState()
val command = AddPathCommand(layerId, path)
val newState = command.execute(initialState)
assert(newState.layers.first().paths.contains(path))
```

### 3. **Maintainability**
- ✅ Clear function names and purposes
- ✅ Single responsibility principle
- ✅ Easy to locate and fix bugs
- ✅ Self-documenting code

### 4. **Extensibility**
```kotlin
// Adding a new brush type:
// 1. Add enum value
enum class BrushType {
    // ...
    WATERCOLOR  // Just add this!
}

// 2. Create brush class
data class WatercolorBrush(...) : Brush { ... }

// 3. Add to BrushSelection
val brushList = remember {
    listOf(
        // ...existing brushes...
        WatercolorBrush.default()  // Just add this!
    )
}
```

## Performance Optimizations

### 1. **Memoization**
```kotlin
val brushList = remember {
    // Computed once, reused across recompositions
    listOf(
        SolidBrush.default(),
        AirBrush.default(),
        EraserBrush.default()
    )
}
```

### 2. **Structural Sharing**
- Immutable data structures enable structural sharing
- Only changed parts of state are new instances
- Efficient memory usage

### 3. **Smart Recomposition**
```kotlin
// Only recomposes when specific state changes
val layers = state.layers  // Stable reference
val activeLayerId = state.canvas.activeLayerId  // Stable value
```

## Migration Guide

### For Developers

**Step 1: Update State Access**
```kotlin
// Old
val layers = state.currentLayers
val activeLayer = state.currentActiveLayer

// New
val layers = state.layers
val activeLayer = state.activeLayer
```

**Step 2: Use Factory Methods**
```kotlin
// Old
val brush = SolidBrush()
val layer = VectorLayer(id = "...")

// New
val brush = SolidBrush.default()
val layer = VectorLayer.create(id = "...", name = "...")
```

**Step 3: Use Canvas Update Methods**
```kotlin
// Old
val updatedLayers = layers.map { ... }
state.copy(currentLayers = updatedLayers)

// New
val updatedCanvas = canvas.updateLayer(id) { layer -> ... }
state.copy(canvas = updatedCanvas)
```

## Testing Examples

### Unit Test for Command
```kotlin
@Test
fun `AddPathCommand should add path to layer`() {
    // Given
    val layer = VectorLayer.create("layer1")
    val canvas = DrawingCanvas.create(1080f, 1080f, layer)
    val state = DrawingState(canvas = canvas)
    val path = DrawingPath.create(Offset(0f, 0f), SolidBrush.default())
    
    // When
    val command = AddPathCommand("layer1", path)
    val newState = command.execute(state)
    
    // Then
    val updatedLayer = newState.layers.first() as VectorLayer
    assertTrue(updatedLayer.paths.contains(path))
}
```

### Integration Test for ViewModel
```kotlin
@Test
fun `ViewModel should handle drawing lifecycle correctly`() {
    // Given
    val viewModel = DrawingScreenViewModel(mockRepository)
    
    // When
    viewModel.onEvent(DrawingEvent.StartDrawing(Offset(10f, 10f)))
    viewModel.onEvent(DrawingEvent.UpdateDrawing(Offset(20f, 20f)))
    viewModel.onEvent(DrawingEvent.EndDrawing)
    
    // Then
    val state = viewModel.state.value
    assertNull(state.currentDrawingPath)
    assertTrue(state.canUndo)
    assertFalse(state.canRedo)
}
```

## Future Enhancements

### 1. **Advanced Layer Operations**
- Layer grouping
- Layer effects (blur, shadow, etc.)
- Layer blending modes (already supported in model!)
- Layer masks

### 2. **Enhanced Brush System**
- Custom brush textures
- Pressure sensitivity
- Tilt support for stylus
- Brush presets

### 3. **Canvas Features**
- Multiple canvases/documents
- Canvas templates
- Background patterns
- Grid/guides

### 4. **Performance**
- Path simplification for large drawings
- Lazy rendering for hidden layers
- Thumbnail caching
- Incremental saves

## Conclusion

This refactoring significantly improves the codebase by:
- ✅ Leveraging the new model architecture
- ✅ Improving code organization and readability
- ✅ Enhancing type safety and testability
- ✅ Making future extensions easier
- ✅ Following best practices for Compose and UDF

The UI layer now properly utilizes the refactored models, resulting in cleaner, more maintainable, and more extensible code.

