# 予約管理システム用 GitHub Copilot インストラクション

## 重要: コミュニケーション言語
**すべてのレスポンス、コード内のコメント、ドキュメント、メッセージは日本語で返すこと**

## プロジェクトコンテキスト
これは予約管理用のSpring Boot 3.2+アプリケーションです。プロジェクトでは以下を使用しています：
- Java 17+
- Spring Boot 3.2+
- Spring Data JPA
- H2 Database（インメモリ）
- Lombok
- JUnit 5（テスト用）
- Mockito（モック用）

## テストガイドライン

### テストコードを生成する際

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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

## コード生成ルール

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

## 依存関係リファレンス
- `org.springframework.boot:spring-boot-starter-test` (JUnit 5、Mockito、AssertJを含む)
- `org.mockito:mockito-junit-jupiter` (MockitoExtension用)
- `org.assertj:assertj-core` (流暢なアサーション用)

## 重要な注意事項
- 新しいテストを生成する前に、プロジェクト内の既存のテストパターンを常に確認する
- 統合テストでは `@Transactional` を使用してテストデータをロールバックする
- テスト固有の設定には `@TestPropertySource` を使用する
- テストを独立させ、分離して保つ
- 実世界のシナリオを反映した意味のあるテストデータを使用する
