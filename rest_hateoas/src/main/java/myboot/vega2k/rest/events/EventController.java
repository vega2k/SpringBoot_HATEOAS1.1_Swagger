package myboot.vega2k.rest.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import myboot.vega2k.rest.account.Account;
import myboot.vega2k.rest.account.CurrentUser;
import myboot.vega2k.rest.common.ErrorsResource;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final EventValidator eventValidator;

	public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}
	
	@PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();
        System.out.println("==> existingEvent.getManager() " + existingEvent.getManager());
        System.out.println("==> currentUser " + currentUser );
        //로그인한 사용자가 이 Event를 등록한 사용자가 아니면 인가되지 않았다는 에러를 발생시킨다.
        if((existingEvent.getManager() != null) && (!existingEvent.getManager().equals(currentUser))) {
        	return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        
        System.out.println("DB 있는 Event : " + existingEvent);
        System.out.println("입력 받은 Event " + eventDto);
        this.modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);

        return ResponseEntity.ok(eventResource);

	}
	
	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id,
			@CurrentUser Account currentUser) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		if(optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		
		if((event.getManager() != null) && (event.getManager().equals(currentUser))) {
			eventResource.add(linkTo(EventController.class)
			.slash(event.getId()).withRel("update-event"));
		}

		return ResponseEntity.ok(eventResource);
	}


	@GetMapping
	public ResponseEntity queryEvents(Pageable pageable, 
			PagedResourcesAssembler<Event> assembler,
			@CurrentUser Account account) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		
		/*
		PagedModel<EntityModel<Event>> pagedResources =
				assembler.toModel(page);
		*/		
		
		PagedModel<RepresentationModel<EventResource>> pagedResources =
				assembler.toModel(page, event -> new EventResource(event));
		
		if(account != null) {
			pagedResources.add(linkTo(EventController.class).withRel("create-event"));
		}	
	    return ResponseEntity.ok(pagedResources);
	}

	
	@PostMapping
	public ResponseEntity<?> createEvent(@RequestBody @Valid EventDto eventDto, 
			Errors errors, @CurrentUser Account currentUser) {
		
		if(errors.hasErrors()) {
			return badRequest(errors);
		}		
		
		eventValidator.validate(eventDto, errors);
		if(errors.hasErrors()) {
			return badRequest(errors);
		}
		
		Event event = modelMapper.map(eventDto, Event.class);
		event.update();
		event.setManager(currentUser);
		Event addEvent = this.eventRepository.save(event);
		
		WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(addEvent.getId());
		URI createUri = selfLinkBuilder.toUri();
		//URI uri = WebMvcLinkBuilder.linkTo(EventController.class).slash(event.getId()).toUri();
		
		EventResource eventResource = new EventResource(addEvent);
		eventResource.add(linkTo(EventController.class).withRel("query-events"));
		//eventResource.add(selfLinkBuilder.withSelfRel());
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		return ResponseEntity.created(createUri).body(eventResource);
	}

	private ResponseEntity<?> badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));//.build();
	}
}
