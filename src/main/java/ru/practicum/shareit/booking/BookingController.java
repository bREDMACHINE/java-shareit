package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.ErrorResponse;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        log.info("Получен Post запрос к эндпоинту /bookings");
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId, @RequestParam boolean approved) {
        log.info("Получен Patch запрос к эндпоинту /bookings/{}", bookingId);
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId) {
        log.info("Получен Get запрос к эндпоинту /bookings/{}", bookingId);
        return bookingService.getStatus(userId, bookingId);
    }

    @GetMapping()
    public List<BookingOutDto> getStateBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(required = false, defaultValue = "100") @Positive int size) {

        State stateEnum = check(state);
        log.info("Получен Get запрос к эндпоинту /bookings");
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.getStateBooker(userId, stateEnum, pageable);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getStateOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state,
                                             @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(required = false, defaultValue = "100") @Positive int size) {
        State stateEnum = check(state);
        log.info("Получен Get запрос к эндпоинту /bookings/owner");
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.getStateOwner(userId, stateEnum, pageable);
    }

    private static State check(String stateString) {
        try {
            return State.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new ErrorResponse("Unknown state: " + stateString);
        }
    }
}
