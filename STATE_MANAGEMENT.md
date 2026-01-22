# Kiến Trúc Quản Lý State trong Ứng Dụng Vẽ

Tài liệu này mô tả **thiết kế quản lý state** của màn hình vẽ trong dự án, ở mức đủ chi tiết để review kiến trúc với tech lead.

> **Scope**: tài liệu chỉ tập trung vào state management (không đi sâu UI layout/Compose theming).

---

## 1) Executive summary (dành cho tech lead)

Ứng dụng đang áp dụng **Unidirectional Data Flow (UDF)** theo phong cách **MVI-ish**:

- **UI (Compose)** chỉ *render* theo `DrawingState` và phát sinh `DrawingEvent`.
- **ViewModel** xử lý `DrawingEvent` và cập nhật `MutableStateFlow<DrawingState>`.
- Các thay đổi dữ liệu “có thể hoàn tác” (Undo/Redo) được đóng gói bằng **Command Pattern** (`DrawingCommand`).

**Điểm nổi bật**:
- Single source of truth cho màn hình: `DrawingState`.
- Tách “event xử lý” khỏi “render”: UI không tự mutate state.
- Undo/redo theo lệnh, quản lý bằng `ArrayDeque` (undoStack/redoStack).
- Side-effect lưu ảnh đi qua `ImageRepository` trong `screenModelScope`.

**Rủi ro kỹ thuật cần lưu ý**:
- `DrawingState` đang dùng `var` thay vì `val` → làm yếu tính bất biến.
- `AddPathCommand.undo()` xoá path bằng `- path` (phụ thuộc equality/reference) → có thể sai nếu `DrawingPath` thay đổi semantics hoặc bị copy.
- `SaveDrawing(imageBitmap)` đang là Event (Intent) trộn lẫn IO side-effect → nên cân nhắc tách “Effect channel”.

---

## 2) Thành phần và ranh giới (Boundaries)

### 2.1. UI layer (Compose)
- File tiêu biểu: `ui/feature/drawing/view/DrawingScreen.kt`.
- Trách nhiệm:
  - Collect state: `val state by viewModel.state.collectAsStateWithLifecycle()`.
  - Render dựa trên `state`.
  - Map tương tác UI → `DrawingEvent` và gọi `viewModel.onEvent(event)`.
- Local UI-only state vẫn có thể tồn tại bằng `remember { mutableStateOf(...) }` khi:
  - Không ảnh hưởng dữ liệu nghiệp vụ (ví dụ: `showLayerListPanel`).

### 2.2. State holder / ViewModel
- File: `ui/feature/drawing/viewModel/DrawingScreenViewModel.kt`.
- Trách nhiệm:
  - Giữ `MutableStateFlow<DrawingState>`.
  - Thực thi “business rules” ở mức màn hình.
  - Điều phối Undo/Redo stacks.
  - Thực thi side-effects (ví dụ lưu ảnh qua repository).

### 2.3. Domain/Data layer
- Model: `DrawingPath`, `Brush`, `Layer`/`VectorLayer`.
- Repository: `ImageRepository`.
- Ý nghĩa kiến trúc:
  - ViewModel không trực tiếp thao tác filesystem; nó gọi `ImageRepository.saveImage(...)`.

---

## 3) Contract (API nội bộ) cho State / Event / Command

### 3.1. DrawingState = “Single Source of Truth”
`DrawingState` đang đại diện toàn bộ state cần để render màn hình vẽ.

**Shape hiện tại** (rút gọn):
- `currentBrush`: brush đang dùng.
- `currentDrawingPath`: nét vẽ tạm (đang kéo tay).
- `currentTouchPosition`: vị trí touch hiện tại (phục vụ preview/UX).
- `currentLayers`: danh sách layer.
- `currentActiveLayer`: layer đang được vẽ.
- `canUndo/canRedo`: derived-state từ stack.

**Invariants mong muốn** (để state nhất quán và dễ reasoning):
1. `currentActiveLayer.id` **phải tồn tại** trong `currentLayers` (trừ trường hợp khởi tạo đặc biệt).
2. `currentLayers` luôn **có ít nhất 1 layer** (mặc định `default_layer`).
3. Sau `EndDrawing`: `currentDrawingPath == null` và `currentTouchPosition == null`.
4. `canUndo == undoStack.isNotEmpty()` và `canRedo == redoStack.isNotEmpty()`.
5. Layer `default_layer` không bị xoá (đã enforced trong ViewModel).

> Ghi chú: hiện `DrawingState` dùng `var`. Với UDF, nên coi state là immutable snapshot và ưu tiên `val` + `copy()`.

### 3.2. DrawingEvent = “Intent” từ UI
`DrawingEvent` là sealed interface mô tả toàn bộ tương tác (input) từ UI.

**Taxonomy (phân loại) theo mục tiêu xử lý**:
- **Gesture/Draw events**: `StartDrawing`, `UpdateDrawing`, `EndDrawing`
  - Update “ephemeral state” (preview path) + commit path vào layer khi kết thúc.
- **Tool/Config events**: `ChangeBrush`
  - Pure state update.
- **Layer mgmt events**: `AddLayer`, `DeleteLayer`, `SelectLayer`, `ToggleLayerVisibility`
  - Một số hành động thay đổi dữ liệu và cần Undo/Redo.
  - `SelectLayer` chỉ đổi “view focus”, không undo.
- **Side-effect event**: `SaveDrawing(imageBitmap)`
  - Triggers IO.

### 3.3. DrawingCommand = “Undoable mutation”
Các thay đổi có thể undo/redo được đóng gói trong `DrawingCommand`:

- `execute(state) -> newState`
- `undo(state) -> newState`

**Command hiện có**:
- `AddPathCommand(layerId, path)`
- `AddLayerCommand(newLayer)`
- `DeleteLayerCommand(layerToDelete, index)`
- `ToggleLayerVisibilityCommand(layerId)`

**Quy ước**:
- Chỉ những thay đổi “làm biến đổi document” mới nên đi qua Command (để undo/redo đúng semantics).
- Các UI-only selection (VD: `SelectLayer`) không vào stack.

---

## 4) Luồng dữ liệu một chiều (UDF) — mô tả chính xác theo code

### 4.1. UI collect state (lifecycle-aware)
`DrawingScreen` dùng:
- `collectAsStateWithLifecycle()` để:
  - Tránh leak.
  - Chỉ collect khi lifecycle STARTED/RESUMED.

### 4.2. UI phát event
Ví dụ:
- Nút undo/redo chỉ enable khi `state.canUndo/canRedo`.
- Nút save:
  - UI chụp `ImageBitmap` từ `graphicsLayer`.
  - Gọi `viewModel.onEvent(DrawingEvent.SaveDrawing(bitmap))`.

### 4.3. ViewModel xử lý event → update StateFlow
- Pure cập nhật: `ChangeBrush`, `SelectLayer`.
- Gesture draw:
  - `StartDrawing`: tạo `DrawingPath` mới theo `currentBrush`.
  - `UpdateDrawing`: append points.
  - `EndDrawing`:
    1. Tạo `AddPathCommand`.
    2. `performCommand(command)` để commit vào layer.
    3. Clear ephemeral state (`currentDrawingPath/currentTouchPosition`).

---

## 5) Undo/Redo design (Command Pattern)

### 5.1. Data structure
- `undoStack`: `ArrayDeque<DrawingCommand>`
- `redoStack`: `ArrayDeque<DrawingCommand>`

### 5.2. Algorithm
**performCommand(command)**:
1. `newState = command.execute(currentState)`
2. `_state.value = newState`
3. `undoStack.addLast(command)`
4. `redoStack.clear()` (branch reset)
5. Update derived availability: `canUndo/canRedo`

**Undo**:
1. Pop last command from undo.
2. Apply `command.undo(state)`.
3. Push command to redo.
4. Update derived availability.

**Redo**:
1. Pop last command from redo.
2. Apply `command.execute(state)`.
3. Push command to undo.
4. Update derived availability.

### 5.3. Semantics & trade-offs
**Ưu điểm**:
- Không phải snapshot toàn bộ `DrawingState` cho mỗi bước (tiết kiệm hơn snapshot-based).
- Mỗi command tự chịu trách nhiệm logic undo/redo.

**Nhược điểm / risk**:
- Command phải đủ thông tin để undo chính xác (ví dụ DeleteLayer cần index).
- Nếu equality của model thay đổi, undo logic có thể sai.

### 5.4. Edge cases đã xử lý
- Không cho xoá `default_layer`.
- Delete layer đang active → chọn layer khác làm active.
- Toggle visibility dùng toggle 2 lần để undo.

### 5.5. Edge cases nên cover thêm (đề xuất)
- Nếu active layer bị hidden: có cho phép vẽ không? Nếu không, rule nên nằm ở ViewModel.
- Khi `currentLayers` chỉ còn 1 layer: disable delete hoặc enforce invariant.
- Undo/redo với layer có nhiều paths: đảm bảo không O(n^2) quá lớn khi file lớn.

---

## 6) Side-effects & concurrency

### 6.1. SaveDrawing hiện tại
- Event: `SaveDrawing(imageBitmap)`.
- ViewModel thực thi trong `screenModelScope.launch { ... }`.
- Convert bitmap → PNG bytes: `toPngByteArray()`.
- Call `imageRepository.saveImage(bytes, name)`.

**Điểm cần lưu ý**:
- Side-effect đang được kích hoạt bởi Event và trực tiếp `println` kết quả.
- Không có “effect stream” để UI show snackbar/toast.

### 6.2. Threading
- UI chụp bitmap đang chạy `Dispatchers.Default` ở phía UI.
- ViewModel save chạy trong `screenModelScope` (dispatcher mặc định tuỳ platform).

**Khuyến nghị**:
- Chụp bitmap nên đảm bảo đúng thread requirement của Compose/GraphicsLayer theo platform.
- Lưu file và mã hoá PNG nên chạy background dispatcher (VM side) để thống nhất.

### 6.3. Đề xuất: tách UiEffect
Để scale tốt hơn (snackbar, navigation, permission…), cân nhắc:
- `State`: dữ liệu render.
- `Event`: intent từ UI.
- `Effect`: one-off signal (toast/snackbar/share/save-result).

Ví dụ structure (ý tưởng):
- `val effects: Flow<DrawingEffect>` (SharedFlow/Channel)
- `DrawingEffect.ShowMessage("Saved")`

---

## 7) Những điểm cần chỉnh để “đúng chuẩn state management”

### 7.1. Tính bất biến của state
Hiện tại `DrawingState` khai báo `var`. Dù code sử dụng `.copy()`, `var` khiến:
- Dễ bị mutate ngoài ý muốn.
- Khó đảm bảo snapshot semantics.

**Khuyến nghị**: chuyển tất cả field trong `DrawingState` thành `val`.

### 7.2. Undo logic của AddPathCommand
`undo()` hiện đang `layer.copy(paths = layer.paths - path)`.
- Nếu `DrawingPath.equals()` là structural và path có thể bị copy, vẫn ok.
- Nếu path identity thay đổi hoặc list có duplicates, có thể remove nhầm.

**Khuyến nghị**:
- Gắn `id` cho `DrawingPath` (UUID/Long) và undo theo id.
- Hoặc lưu index insert và undo theo index.

### 7.3. Derived state (canUndo/canRedo)
Hiện `canUndo/canRedo` được lưu trong State và update bằng hàm riêng.

**Option**:
- Giữ như hiện tại: đơn giản cho UI.
- Hoặc derive trực tiếp từ stack và expose riêng (nhưng stack đang private).

Khuyến nghị trung dung:
- Giữ trường `canUndo/canRedo` trong state nhưng coi là “derived” và luôn update trong cùng transaction với stack.

---

## 8) Testing strategy (đủ để thuyết phục tech lead)

### 8.1. Unit test cho reducer-like logic
Test theo kịch bản Event → State:
- StartDrawing → UpdateDrawing → EndDrawing adds path to active layer.
- AddLayer -> active layer switched.
- DeleteLayer default_layer is ignored.

### 8.2. Unit test cho undo/redo invariants
- performCommand clears redoStack.
- Undo then Redo returns to same state.
- Undo after AddLayer restores previous active layer properly.

### 8.3. Contract tests cho command
- `execute` và `undo` là inverse trong phạm vi cần thiết:
  - `undo(execute(S)) == S` (tối thiểu với những field liên quan).

---

## 9) Roadmap cải tiến (tùy mức ưu tiên)

1. **Make State immutable** (`val`), audit điểm mutate.
2. **Introduce PathId** để undo path chính xác.
3. **Effect stream** cho Save result + error handling.
4. Tách “DrawingEngine”/use-cases khỏi VM nếu state logic lớn (test dễ hơn, reuse cho web/desktop).

---

## Phụ lục A — Mapping code → kiến trúc

- `DrawingScreen.kt`: UI collect state + dispatch events.
- `DrawingScreenViewModel.kt`: state holder + event handler + undo/redo.
- `DrawingState.kt`: state snapshot của màn hình.
- `DrawingEvent.kt`: intents.
- `DrawingCommand.kt`: undoable mutations.

---

## Phụ lục B — Luồng hoạt động (tóm tắt 5 bước)

1. UI collect `state: StateFlow<DrawingState>`.
2. User interaction → UI phát `DrawingEvent`.
3. ViewModel `onEvent` xử lý event.
4. ViewModel cập nhật `_state.value = _state.value.copy(...)` hoặc `performCommand(...)`.
5. Compose recompose theo state mới.
