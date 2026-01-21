package com.booking.service;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private LocalDateTime testStartTime;
    private LocalDateTime testEndTime;

    @BeforeEach
    void setUp() {
        testStartTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        testEndTime = testStartTime.plusHours(2);
        testBooking = createTestBooking();
    }

    // ========== Read Operations ==========

    @Test
    void should_returnAllBookings_when_getAllBookings() {
        // Arrange
        List<Booking> expectedBookings = Arrays.asList(testBooking, createTestBooking(2L));
        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getAllBookings();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedBookings);
        verify(bookingRepository).findAll();
    }

    @Test
    void should_returnEmptyList_when_noBookingsExist() {
        // Arrange
        when(bookingRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Booking> result = bookingService.getAllBookings();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(bookingRepository).findAll();
    }

    @Test
    void should_returnBooking_when_bookingExists() {
        // Arrange
        Long bookingId = 1L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        // Act
        Optional<Booking> result = bookingService.getBookingById(bookingId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testBooking);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void should_returnEmpty_when_bookingNotFound() {
        // Arrange
        Long bookingId = 999L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act
        Optional<Booking> result = bookingService.getBookingById(bookingId);

        // Assert
        assertThat(result).isEmpty();
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void should_returnBookings_when_resourceIdExists() {
        // Arrange
        Long resourceId = 1L;
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(bookingRepository.findByResourceId(resourceId)).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getBookingsByResourceId(resourceId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expectedBookings);
        verify(bookingRepository).findByResourceId(resourceId);
    }

    @Test
    void should_returnBookings_when_statusMatches() {
        // Arrange
        BookingStatus status = BookingStatus.CONFIRMED;
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(bookingRepository.findByStatus(status)).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getBookingsByStatus(status);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(bookingRepository).findByStatus(status);
    }

    @Test
    void should_returnBookings_when_customerEmailMatches() {
        // Arrange
        String email = "test@example.com";
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(bookingRepository.findByCustomerEmail(email)).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getBookingsByCustomerEmail(email);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(bookingRepository).findByCustomerEmail(email);
    }

    @Test
    void should_returnBookings_when_timeRangeOverlaps() {
        // Arrange
        LocalDateTime start = testStartTime.minusHours(1);
        LocalDateTime end = testEndTime.plusHours(1);
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(bookingRepository.findByTimeRange(start, end)).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getBookingsByTimeRange(start, end);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(bookingRepository).findByTimeRange(start, end);
    }

    // ========== Availability Check ==========

    @Test
    void should_returnTrue_when_resourceIsAvailable() {
        // Arrange
        Long resourceId = 1L;
        when(bookingRepository.findConflictingBookings(
                eq(resourceId), any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(Collections.emptyList());

        // Act
        boolean result = bookingService.isResourceAvailable(resourceId, testStartTime, testEndTime);

        // Assert
        assertThat(result).isTrue();
        verify(bookingRepository).findConflictingBookings(
                eq(resourceId), eq(testStartTime), eq(testEndTime), anyList());
    }

    @Test
    void should_returnFalse_when_resourceHasConflictingBooking() {
        // Arrange
        Long resourceId = 1L;
        List<Booking> conflicts = Arrays.asList(testBooking);
        when(bookingRepository.findConflictingBookings(
                eq(resourceId), any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(conflicts);

        // Act
        boolean result = bookingService.isResourceAvailable(resourceId, testStartTime, testEndTime);

        // Assert
        assertThat(result).isFalse();
        verify(bookingRepository).findConflictingBookings(
                eq(resourceId), eq(testStartTime), eq(testEndTime), anyList());
    }

    // ========== Create Operation ==========

    @Test
    void should_createBooking_when_validDataProvided() {
        // Arrange
        Booking newBooking = createTestBooking();
        newBooking.setId(null); // New booking should not have ID
        when(bookingRepository.findConflictingBookings(
                eq(newBooking.getResourceId()), any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(newBooking)).thenReturn(testBooking);

        // Act
        Booking result = bookingService.createBooking(newBooking);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        verify(bookingRepository).findConflictingBookings(
                eq(newBooking.getResourceId()), eq(newBooking.getStartTime()), 
                eq(newBooking.getEndTime()), anyList());
        verify(bookingRepository).save(newBooking);
    }

    @Test
    void should_throwException_when_bookingHasId() {
        // Arrange
        Booking bookingWithId = createTestBooking();
        bookingWithId.setId(1L);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createBooking(bookingWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New booking should not have an ID");
        
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void should_throwException_when_endTimeNotAfterStartTime() {
        // Arrange
        Booking invalidBooking = createTestBooking();
        invalidBooking.setId(null);
        invalidBooking.setEndTime(invalidBooking.getStartTime()); // End time equals start time

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createBooking(invalidBooking))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End time must be after start time");
        
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void should_throwConflictException_when_resourceNotAvailable() {
        // Arrange
        Booking newBooking = createTestBooking();
        newBooking.setId(null);
        List<Booking> conflicts = Arrays.asList(createTestBooking(2L));
        when(bookingRepository.findConflictingBookings(
                eq(newBooking.getResourceId()), any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(conflicts);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createBooking(newBooking))
                .isInstanceOf(BookingService.BookingConflictException.class)
                .hasMessageContaining("Resource is not available");
        
        verify(bookingRepository, never()).save(any());
    }

    // ========== Update Operation ==========

    @Test
    void should_updateBooking_when_validDataProvided() {
        // Arrange
        Long bookingId = 1L;
        Booking existingBooking = createTestBooking();
        Booking updatedData = createTestBooking();
        updatedData.setCustomerName("Updated Name");
        updatedData.setNotes("Updated notes");
        // Time and resource are not changed, so conflict check won't be called

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedData);

        // Act
        Booking result = bookingService.updateBooking(bookingId, updatedData);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCustomerName()).isEqualTo("Updated Name");
        assertThat(result.getNotes()).isEqualTo("Updated notes");
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).findConflictingBookings(any(), any(), any(), anyList());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void should_updateStatus_when_bookingStatusChanges() {
        // Arrange
        Long bookingId = 1L;
        Booking existingBooking = createTestBooking();
        Booking updatedData = createTestBooking();
        updatedData.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.updateBooking(bookingId, updatedData);

        // Assert
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(bookingCaptor.getValue().getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void should_throwException_when_bookingNotFoundForUpdate() {
        // Arrange
        Long bookingId = 999L;
        Booking updatedData = createTestBooking();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingService.updateBooking(bookingId, updatedData))
                .isInstanceOf(BookingService.BookingNotFoundException.class)
                .hasMessageContaining("Booking not found with id: " + bookingId);
        
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void should_checkConflicts_when_timeChanged() {
        // Arrange
        Long bookingId = 1L;
        Booking existingBooking = createTestBooking();
        Booking updatedData = createTestBooking();
        updatedData.setStartTime(testStartTime.plusHours(5)); // Time changed

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.findConflictingBookings(
                eq(updatedData.getResourceId()), any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedData);

        // Act
        bookingService.updateBooking(bookingId, updatedData);

        // Assert
        verify(bookingRepository).findConflictingBookings(
                eq(updatedData.getResourceId()), eq(updatedData.getStartTime()), 
                eq(updatedData.getEndTime()), anyList());
    }

    @Test
    void should_throwConflictException_when_updatedTimeConflicts() {
        // Arrange
        Long bookingId = 1L;
        Booking existingBooking = createTestBooking();
        Booking updatedData = createTestBooking();
        updatedData.setStartTime(testStartTime.plusHours(5));
        List<Booking> conflicts = Arrays.asList(createTestBooking(2L));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.findConflictingBookings(
                eq(updatedData.getResourceId()), any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(conflicts);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.updateBooking(bookingId, updatedData))
                .isInstanceOf(BookingService.BookingConflictException.class)
                .hasMessageContaining("Resource is not available");
        
        verify(bookingRepository, never()).save(any());
    }

    // ========== Cancel Operation ==========

    @Test
    void should_cancelBooking_when_bookingExists() {
        // Arrange
        Long bookingId = 1L;
        Booking existingBooking = createTestBooking();
        existingBooking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setStatus(BookingStatus.CANCELLED);
            return saved;
        });

        // Act
        Booking result = bookingService.cancelBooking(bookingId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(existingBooking);
    }

    @Test
    void should_throwException_when_bookingNotFoundForCancel() {
        // Arrange
        Long bookingId = 999L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingService.cancelBooking(bookingId))
                .isInstanceOf(BookingService.BookingNotFoundException.class)
                .hasMessageContaining("Booking not found with id: " + bookingId);
        
        verify(bookingRepository, never()).save(any());
    }

    // ========== Delete Operation ==========

    @Test
    void should_deleteBooking_when_bookingExists() {
        // Arrange
        Long bookingId = 1L;
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(bookingId);

        // Act
        bookingService.deleteBooking(bookingId);

        // Assert
        verify(bookingRepository).existsById(bookingId);
        verify(bookingRepository).deleteById(bookingId);
    }

    @Test
    void should_throwException_when_bookingNotFoundForDelete() {
        // Arrange
        Long bookingId = 999L;
        when(bookingRepository.existsById(bookingId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.deleteBooking(bookingId))
                .isInstanceOf(BookingService.BookingNotFoundException.class)
                .hasMessageContaining("Booking not found with id: " + bookingId);
        
        verify(bookingRepository, never()).deleteById(any());
    }

    // ========== Helper Methods ==========

    private Booking createTestBooking() {
        return createTestBooking(1L);
    }

    private Booking createTestBooking(Long id) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setResourceId(1L);
        booking.setCustomerName("テストユーザー");
        booking.setCustomerEmail("test@example.com");
        booking.setStartTime(testStartTime);
        booking.setEndTime(testEndTime);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setNotes("テスト備考");
        return booking;
    }
}

