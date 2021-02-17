package myboot.vega2k.rest.events;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class EventResource extends RepresentationModel<EventResource> {

	
	@JsonUnwrapped
	private Event event;
	
	
	public EventResource(Event event, Link...links) {
		this.event = event;
		//super.of(event);
		//super(event,links);
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}
	
	
	public Event getEvent() {
		return event;
	}
	
}
