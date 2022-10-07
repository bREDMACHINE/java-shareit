package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingServiceUpdate;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceUpdateImpl implements BookingServiceUpdate {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public BookingOutDto updateStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Указанный bookingId не существует"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Невозможно повторное изменение статуса");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        if (Objects.equals(booking.getItem().getOwner().getId(), user.getId())) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            return BookingMapper.toBookingOutDto(bookingRepository.save(booking));
        } else {
            throw new NotFoundException("userId не является владельцем, недоступно изменение параметров");
        }
    }
}
