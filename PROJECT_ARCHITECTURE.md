# Kiến trúc tổng thể của project `Draw`

Tài liệu này mô tả kiến trúc tổng quan của project (module, source sets KMP, các layer chính, DI, navigation), nhằm giúp tech lead/code reviewer nắm nhanh “bức tranh lớn”.

> **Scope**: tổng quan kiến trúc project. Chi tiết state management xem `STATE_MANAGEMENT.md`.

---

## 1) Tổng quan công nghệ (Tech stack)

Project là **Kotlin Multiplatform + Compose Multiplatform (CMP)**, target:

- **Android** (`androidTarget`)
- **iOS** (`iosArm64`, `iosSimulatorArm64`) — framework `ComposeApp` (static)
- **Web (Wasm)** (`wasmJs`)

Các thư viện chính (từ `composeApp/build.gradle.kts`):

- **Compose Multiplatform**: runtime, foundation, material3, ui, resources, preview
- **Lifecycle**: `androidx.lifecycle.viewmodelCompose`, `androidx.lifecycle.runtimeCompose` (collect lifecycle-aware)
- **DI**: **Koin** (`koin-core`, `koin-compose`)
- **Navigation / ScreenModel**: **Voyager** (`voyager-navigator`, `voyager-screenmodel`, `voyager-koin`)

---

## 2) Cấu trúc module

### 2.1. Root project
- `settings.gradle.kts` include đúng 1 module:
  - `:composeApp`

### 2.2. Module `composeApp`
- Đây là module “shared app” của Compose Multiplatform.
- Chứa phần lớn code UI và business logic dùng chung giữa Android/iOS/Web.

### 2.3. Thư mục `iosApp`
- Chứa entry point iOS (SwiftUI/App) để host phần UI Compose nếu cần.
- Đây là “container app” cho iOS, tách biệt khỏi `composeApp`.

---

## 3) KMP source sets (Shared vs Platform-specific)

Trong `composeApp/src/`:

- `commonMain/`: code dùng chung cho mọi platform (phần lớn logic hiện nằm ở đây).
- `androidMain/`: Android-specific.
- `iosMain/`: iOS-specific.
- `webMain/`, `wasmJsMain/`: Web-specific.

**Quy ước**:
- Logic/feature nên ưu tiên đặt ở `commonMain`.
- Các phần cần API hệ điều hành (file system, permission, share sheet, …) đặt ở platform source set và “wiring” qua `expect/actual` hoặc DI.

---

## 4) Layers & package layout (theo `commonMain`)

Entry package: `com.example.draw` (`composeApp/src/commonMain/kotlin/com/example/draw`).

### 4.1. App entry
- `App.kt`: root composable.
  - Wrap `MaterialTheme`.
  - Khởi tạo `Navigator(DrawingScreen())`.

**Ý nghĩa kiến trúc**:
- Navigation được quản lý tại app root bằng **Voyager**.

### 4.2. Dependency Injection
- `di/AppModule.kt`:
  - `initKoin(appDeclaration)` để start Koin.
  - `expect val platformModule: Module` để mỗi platform tự provide dependency đặc thù.
  - `sharedModule` (common):
    - `factory { DrawingScreenViewModel(get()) }`

**Ý nghĩa kiến trúc**:
- `sharedModule` cung cấp dependency thuần common.
- `platformModule` (expect/actual) là điểm móc để inject platform service (vd: implement `ImageRepository`, context Android, path iOS,…).

### 4.3. Data layer
- `data/model/**`: các model phục vụ domain (vd: brush, path, layer).
- `data/repository/ImageRepository.kt`: interface lưu ảnh.

**Quy ước**:
- `data/repository` chứa *contract* (interface).
- Implementation nên đặt ở platform source set (vd: `androidMain`/`iosMain`/`wasmJsMain`) hoặc trong một package `data/repository/impl` tuỳ chiến lược.

### 4.4. UI layer
- `ui/` được chia thành:
  - `ui/common/`: component dùng chung (button, panel, preview helpers, …)
  - `ui/feature/`: feature chính của app
  - `ui/support_feature/`: các “feature phụ”/tooling panel (brush config, color picker, layer config, undo/redo…)

Hiện tại feature chính:
- `ui/feature/drawing/`
  - `view/` (Compose screens)
  - `viewModel/` (state holder, event handling)
  - `component/` (Canvas, input handlers…)

**Ý nghĩa kiến trúc**:
- Feature-first structure: gom UI + state holder theo feature.
- “support_feature” gom nhóm tool panel dạng modular (có thể tái dùng sang screen khác).

### 4.5. Platform utilities
- `platform/util/**`: các extension/util có tính platform.
  - Ví dụ đang được dùng: `toPngByteArray()` (convert ImageBitmap to PNG bytes), phục vụ tính năng Save.

---

## 5) Navigation & Screen model

- Voyager `Navigator` được khởi tạo trong `App()`.
- Mỗi screen implement `cafe.adriel.voyager.core.screen.Screen`.
- `DrawingScreen` lấy ViewModel bằng `koinScreenModel<DrawingScreenViewModel>()`.

**Ý nghĩa kiến trúc**:
- Screen lifecycle gắn với Navigator.
- ViewModel (Voyager `ScreenModel`) có `screenModelScope` để chạy coroutine theo vòng đời screen.

---

## 6) Một ví dụ “end-to-end flow” theo kiến trúc

Ví dụ: User nhấn nút Save trong `DrawingScreen`:

1. UI capture `ImageBitmap` từ `graphicsLayer`.
2. UI dispatch `DrawingEvent.SaveDrawing(bitmap)`.
3. `DrawingScreenViewModel` nhận event và chạy `screenModelScope.launch { ... }`.
4. Convert bitmap → bytes (`toPngByteArray`).
5. Gọi `ImageRepository.saveImage(bytes, name)`.
6. (Hiện tại) log bằng `println`.

Điểm review:
- Tách IO ra repository là đúng hướng.
- Có thể cần thêm “effect channel” để UI hiển thị kết quả thay vì `println` (xem `STATE_MANAGEMENT.md`).

---

## 7) Architectural decisions (hiện trạng + đề xuất)

### 7.1. Hiện trạng
- Feature-first packages.
- UDF/MVI-ish cho state trong feature chính (xem `STATE_MANAGEMENT.md`).
- DI qua Koin + wiring platform bằng `expect/actual platformModule`.

### 7.2. Đề xuất để scale
- Chuẩn hoá **layering** rõ hơn:
  - `ui` (Compose)
  - `presentation` (viewModel/state/events/effects)
  - `domain` (use cases, pure logic)
  - `data` (repo interfaces + impl)
- Với app lớn hơn, tách `support_feature` thành các module con hoặc package có contract rõ.
- Chuẩn hoá “effects”: snackbar/navigation/save result.

---

## 8) File map nhanh

- Root
  - `README.md`: hướng dẫn build/run.
  - `STATE_MANAGEMENT.md`: kiến trúc state cho màn hình vẽ.
  - `PROJECT_ARCHITECTURE.md`: tài liệu này.
- `composeApp/src/commonMain/kotlin/com/example/draw`
  - `App.kt`: app root.
  - `di/AppModule.kt`: Koin init + modules.
  - `data/**`: models + repository contracts.
  - `ui/**`: UI components + features.
  - `platform/**`: utilities.
- `iosApp/`: iOS entry.

---

## 9) Notes khi onboard dev mới

- Nếu cần thêm service “phụ thuộc platform” (filesystem, share, permissions):
  1. Tạo interface ở `commonMain`.
  2. Implement ở `androidMain/iosMain/wasmJsMain`.
  3. Bind vào `platformModule` (actual) tương ứng.

- Nếu thêm screen/feature mới:
  - Tạo `ui/feature/<featureName>/view` + `viewModel`.
  - Register navigation bằng Voyager (push screen từ Navigator).
  - Wiring ViewModel qua Koin nếu cần dependencies.
