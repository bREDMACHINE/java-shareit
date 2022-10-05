package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ErrorResponse;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookingDto bookingDto) {
		log.info("Creating booking {}, userId={}", bookingDto, userId);
		return bookingClient.createBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
									  @PathVariable long bookingId, @RequestParam(name = "approved") Boolean approved) {
		log.info("Patch booking {}, approved={}", bookingId, approved);
		return bookingClient.updateStatus(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getStatus(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getStatus(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getStateBooker(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new ErrorResponse("Unknown state: " + stateParam));
		log.info("Get booking from booker with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getStateBooker(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getStateOwner(@RequestHeader("X-Sharer-User-Id") long userId,
												 @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
												 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new ErrorResponse("Unknown state: " + stateParam));
		log.info("Get booking from owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getStateOwner(userId, state, from, size);
	}
}
