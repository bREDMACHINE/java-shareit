package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemOwnerIdOrderByStartDesc(long id, Pageable pageable);

    List<Booking> findByBookerIdOrderByStartDesc(long id, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeAndStatus(Long id, LocalDateTime now, Status status);

    Booking findFirstByItemIdAndStartAfterAndStatus(Long id, LocalDateTime now, Status status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBefore(Long id, Long id1, LocalDateTime now);
}