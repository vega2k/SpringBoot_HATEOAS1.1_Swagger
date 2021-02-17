package myboot.vega2k.rest.runner;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import myboot.vega2k.rest.events.Event;
import myboot.vega2k.rest.events.EventRepository;
import myboot.vega2k.rest.events.EventStatus;

@Component
public class EventInsertRunner implements ApplicationRunner {
	@Autowired
	EventRepository eventRepository;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		IntStream.range(0, 15).forEach(this::generateEvent);
	}
	
    private Event generateEvent(int index) {
        Event event = buildEvent(index);
        return this.eventRepository.save(event);
    }

    private Event buildEvent(int index) {
        return Event.builder()
                    .name(index + " event ")
                    .description("test event")
                    .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                    .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                    .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                    .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("강남역 D2 스타텁 팩토리")
                    .free(false)
                    .offline(true)
                    .eventStatus(EventStatus.DRAFT)
                    .build();
    }
}