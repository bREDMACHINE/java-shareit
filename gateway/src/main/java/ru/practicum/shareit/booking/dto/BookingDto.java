package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

	@NotNull
	long itemId;
	@NotNull
	@FutureOrPresent
	@DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
	private LocalDateTime start;
	@NotNull
	@Future
	@DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
	private LocalDateTime end;
}
