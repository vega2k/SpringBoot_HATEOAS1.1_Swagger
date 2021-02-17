package myboot.vega2k.rest.events;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {
	public void validate(EventDto eventDto, Errors errors) {
		if(eventDto.getBasePrice() > eventDto.getMaxPrice() && 
				eventDto.getMaxPrice() != 0) {
			//Field Error
			errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
			errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong");
			//Global Error
			errors.reject("wrongPrices", "Values for prices are wrong");
		}
		
		LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
		if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
		   endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
		   endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ) {
			errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong");
		}
	}
}