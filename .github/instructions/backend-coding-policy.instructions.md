# Backend Coding Policy

このファイルは、バックエンド開発における共通コーディング規約の単一情報源（SSOT）です。  
各モジュールの `AGENTS.md` から参照されます。

## 🧰 技術スタック
- Language: Java 17
- Runtime: JDK 17
- Framework: Spring Boot 3.2.x, Spring Data JPA
- Database (dev): H2 Database
- Build/Test: Maven 3.6+

## 📏 命名規則
- Classes: `PascalCase`（名詞、役割を明確に）
- Methods/Functions: `lowerCamelCase`（動詞+目的語）
- Constants: `UPPER_SNAKE_CASE`
- Files/Packages: `lowercase`（パッケージはドメイン単位）

例:
```java
// ✅ Good
package com.example.booking.service;

public class BookingService {
    private static final int MAX_RETRY_COUNT = 3;

    public BookingResult createBooking(BookingRequest request) {
        // ...
        return BookingResult.success();
    }
}

// ❌ Bad
package com.example.Booking.Service;

public class bookingservice {
    private static final int maxRetryCount = 3;

    public BookingResult Create(BookingRequest req) {
        // ...
        return BookingResult.success();
    }
}
```

## 🏗️ アーキテクチャパターン
- 典型的な層構造: Controller → Service → Repository
- Controller は HTTP の入出力に専念し、ビジネスロジックは Service に置く
- Repository は JPA による永続化のみを担当

依存性注入の例:
```java
// ✅ Good
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// ❌ Bad
@RestController
public class BookingController {
    private final BookingService bookingService = new BookingService(); // manual instantiation
}
```

## 🔒 セキュリティ
- 認可はメソッドレベルで明示する（`@PreAuthorize` など）
- 入力は `@Valid` と Bean Validation で検証する
- SQL は JPA/パラメタバインディングで実行し、文字列結合で組み立てない
- 秘密情報は環境変数/Secret 管理を前提にし、コードに埋め込まない

認証・認可:
```java
// ✅ Good
@PreAuthorize("hasRole('ADMIN')")
public BookingResponse cancel(String id) {
    // ...
}

// ❌ Bad
public BookingResponse cancel(String id) {
    if (!currentUser.isAdmin()) { // ad-hoc auth
        throw new AccessDeniedException("forbidden");
    }
    // ...
}
```

入力バリデーション:
```java
// ✅ Good
public record BookingRequest(
    @NotBlank String customerName,
    @Email String email,
    @Future @NotNull LocalDate visitDate
) {}

// ❌ Bad
public record BookingRequest(String customerName, String email, LocalDate visitDate) {}
```

SQL 注入対策:
```java
// ✅ Good
@Query("select b from Booking b where b.customerName = :name")
List<Booking> findByCustomerName(@Param("name") String name);

// ❌ Bad
@Query(value = "select * from booking where customer_name = '" + name + "'", nativeQuery = true)
List<Booking> findByCustomerName(String name);
```

秘密情報管理:
```java
// ✅ Good
@Value("${app.jwt.secret}")
private String jwtSecret;

// ❌ Bad
private static final String JWT_SECRET = "hard-coded-secret";
```

## 🧪 テスト
- テストフレームワーク: JUnit 5（Spring Boot Test を使用）
- 重要なドメインロジックはユニットテスト必須
- 主要な API は統合テストで検証する

### テストガイドライン

#### 1. テストクラスの構造
- テストクラスは `src/test/java/com/booking/{package}/{ClassName}Test.java` に作成する
- ユニットテストには `@ExtendWith(MockitoExtension.class)` を使用する
- 統合テストには `@SpringBootTest` を使用する
- 命名規則に従う: `{ClassName}Test.java`

#### 2. テストメソッドの命名
以下のパターンに従った説明的な名前を使用する：
- `should_{期待される動作}_when_{条件}()`
- 例: `should_sendNotification_when_bookingIsCreated()`
- 代替案: `test_{メソッド名}_{シナリオ}()` も使用可能

#### 3. テストメソッドの構造（AAAパターン）
常にArrange-Act-Assertパターンに従う：

```java
@Test
void should_sendNotification_when_bookingIsCreated() {
    // Arrange: テストデータとモックを設定
    Booking booking = createTestBooking();
    Notification expectedNotification = createTestNotification();
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(expectedNotification);
    
    // Act: テスト対象のメソッドを実行
    Notification result = notificationService.sendBookingCreatedNotification(booking);
    
    // Assert: 結果を検証
    assertThat(result).isNotNull();
    assertThat(result.getType()).isEqualTo("CREATED");
    assertThat(result.getRecipientEmail()).isEqualTo(booking.getCustomerEmail());
    verify(notificationRepository).save(any(Notification.class));
}
```

#### 4. モッキング
- 依存関係には `@Mock` を使用する
- テスト対象クラスには `@InjectMocks` を使用する
- スタブには `when().thenReturn()` を使用する
- メソッド呼び出しの確認には `verify()` を使用する
- Spring Bootテストでは `@MockBean` を使用する

#### 5. アサーション
- AssertJを優先: `assertThat(actual).isEqualTo(expected)`
- フォールバックとしてJUnitアサーションを使用: `assertEquals(expected, actual)`
- 例外の場合: `assertThrows(ExceptionClass.class, () -> method())`

#### 6. テストカバレッジ
常に以下を含める：
- ✅ 正常系テスト（通常のシナリオ）
- ✅ エラーケーステスト（例外、無効な入力）
- ✅ エッジケーステスト（null値、空のコレクション、境界値）
- ✅ ビジネスロジック検証テスト

#### 7. テストデータのセットアップ
- ヘルパーメソッドを作成: `createTestBooking()`, `createTestResource()`
- 共通のセットアップには `@BeforeEach` を使用する
- テストデータ作成メソッドはテストクラスの下部に配置する

### 例: NotificationServiceTest

`NotificationService` のテストを生成する際は、以下の構造に従う：

```java
package com.booking.service;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.model.Notification;
import com.booking.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testBooking = createTestBooking();
    }

    @Test
    void should_sendNotification_when_bookingIsCreated() {
        // Arrange
        Notification savedNotification = createTestNotification("CREATED");
        when(notificationRepository.save(any(Notification.class)))
            .thenReturn(savedNotification);

        // Act
        Notification result = notificationService.sendBookingCreatedNotification(testBooking);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("CREATED");
        assertThat(result.getRecipientEmail()).isEqualTo(testBooking.getCustomerEmail());
        assertThat(result.getSubject()).contains("予約が作成されました");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void should_sendNotification_when_bookingIsUpdated() {
        // Arrange
        Notification savedNotification = createTestNotification("UPDATED");
        when(notificationRepository.save(any(Notification.class)))
            .thenReturn(savedNotification);

        // Act
        Notification result = notificationService.sendBookingUpdatedNotification(testBooking);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("UPDATED");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void should_sendNotification_when_bookingIsCancelled() {
        // Arrange
        Notification savedNotification = createTestNotification("CANCELLED");
        when(notificationRepository.save(any(Notification.class)))
            .thenReturn(savedNotification);

        // Act
        Notification result = notificationService.sendBookingCancelledNotification(testBooking);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("CANCELLED");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void should_handleError_when_notificationSendingFails() {
        // Arrange
        when(notificationRepository.save(any(Notification.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() ->
            notificationService.sendBookingCreatedNotification(testBooking)
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("Database error");
        
        verify(notificationRepository).save(any(Notification.class));
    }

    // ヘルパーメソッド
    private Booking createTestBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setResourceId(1L);
        booking.setCustomerName("テストユーザー");
        booking.setCustomerEmail("test@example.com");
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        booking.setStatus(BookingStatus.PENDING);
        return booking;
    }

    private Notification createTestNotification(String type) {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setBookingId(1L);
        notification.setType(type);
        notification.setRecipientEmail("test@example.com");
        notification.setSubject("テスト件名");
        notification.setBody("テスト本文");
        notification.setStatus("SENT");
        notification.setSentAt(LocalDateTime.now());
        return notification;
    }
}
```

### コード生成ルール

テストコードを生成する際は、常に以下を実行する：
1. ✅ AAAパターン（Arrange-Act-Assert）に従う
2. ✅ 説明的なテストメソッド名を使用する
3. ✅ 正常系とエラーケースの両方のテストを含める
4. ✅ すべての外部依存関係をモックする
5. ✅ モックとの相互作用を検証する
6. ✅ アサーションにはAssertJを使用する
7. ✅ テストデータ作成のヘルパーメソッドを追加する
8. ✅ 既存のコードスタイルとパターンに従う
9. ✅ すべてのpublicメソッドをテストする
10. ✅ エッジケースと境界条件を含める

### 依存関係リファレンス
- `org.springframework.boot:spring-boot-starter-test` (JUnit 5、Mockito、AssertJを含む)
- `org.mockito:mockito-junit-jupiter` (MockitoExtension用)
- `org.assertj:assertj-core` (流暢なアサーション用)

### 重要な注意事項
- 新しいテストを生成する前に、プロジェクト内の既存のテストパターンを常に確認する
- 統合テストでは `@Transactional` を使用してテストデータをロールバックする
- テスト固有の設定には `@TestPropertySource` を使用する
- テストを独立させ、分離して保つ
- 実世界のシナリオを反映した意味のあるテストデータを使用する

統合テスト例:
```java
@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIT {
    @Autowired private MockMvc mockMvc;

    @Test
    void create_returns201() throws Exception {
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"customerName":"Alice","email":"a@example.com","visitDate":"2030-01-01"}
                """))
            .andExpect(status().isCreated());
    }
}
```

## 📊 ロギング
- `INFO`: 正常系の主要イベント（作成/更新/削除）
- `WARN`: 想定外だが継続可能な状態（入力不備など）
- `ERROR`: 例外や処理失敗（スタックトレースを含める）
- 個人情報や秘密情報はログに出さない

実装例:
```java
private static final Logger log = LoggerFactory.getLogger(BookingService.class);

public BookingResponse create(BookingRequest request) {
    log.info("Creating booking for customerName={}", request.customerName());
    // ...
    return BookingResponse.success();
}
```

## 🚫 禁止事項
- 例外の握りつぶし: 障害解析が困難になる
- コントローラにビジネスロジックを実装: テスト性と再利用性が低下
- 文字列連結による SQL 生成: SQL 注入のリスク
- 秘密情報のハードコード: 重大なセキュリティ事故につながる
- N+1 クエリを放置: パフォーマンス劣化の原因
- 無制限のログ出力: コスト増と監視ノイズの原因
- 巨大なクラス/メソッド: 変更影響が読めず保守が困難

## 📚 参考リソース
- Spring Boot: https://docs.spring.io/spring-boot/
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Java 17: https://docs.oracle.com/en/java/javase/17/
