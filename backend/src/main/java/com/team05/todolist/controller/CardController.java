package com.team05.todolist.controller;

import com.team05.todolist.domain.Event;
import com.team05.todolist.domain.dto.CardDTO;
import com.team05.todolist.domain.dto.CreateCardDTO;
import com.team05.todolist.domain.dto.LogDTO;
import com.team05.todolist.domain.dto.MoveCardDTO;
import com.team05.todolist.domain.dto.ResponseDTO;
import com.team05.todolist.domain.dto.UpdateCardDTO;
import com.team05.todolist.service.CardService;
import com.team05.todolist.service.LogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class CardController {

	private final CardService cardService;
	private final LogService logService;

	public CardController(CardService cardService, LogService logService) {
		this.cardService = cardService;
		this.logService = logService;
	}

	@ApiOperation("카드 등록")
	@PostMapping("/cards")
	public ResponseEntity<ResponseDTO> create(@RequestHeader(value = "user-agent") String userAgent, @RequestBody CreateCardDTO createCardDto) {
		CardDTO newCardDto = cardService.save(createCardDto, userAgent);
		LogDTO log = logService.save(Event.CREATE, createCardDto.getTitle(), createCardDto.getSection());

		return ResponseEntity.ok().body(new ResponseDTO(newCardDto, log));
	}

	@ApiOperation("카드 수정")
	@PutMapping("/cards/{id}")
	public ResponseEntity<ResponseDTO> update(@RequestHeader(value = "user-agent") String userAgent,
		@PathVariable int id, @RequestBody UpdateCardDTO updateCardDto) {
		CardDTO updatedCardDto = cardService.update(id, updateCardDto, userAgent);
		LogDTO log = logService.save(Event.UPDATE, updateCardDto.getTitle(), updateCardDto.getSection());

		return ResponseEntity.ok().body(new ResponseDTO(updatedCardDto, log));
	}

	@ApiOperation("카드 이동")
	@PutMapping("/cards/{id}/move")
	public ResponseEntity<ResponseDTO> move(@RequestHeader(value = "user-agent") String userAgent,
		@PathVariable int id, @RequestBody MoveCardDTO moveCardDto) {
		CardDTO cardDto = cardService.move(id, moveCardDto, userAgent);
		LogDTO log = logService.save(Event.MOVE, cardDto.getTitle(), moveCardDto.getPrevSection(), moveCardDto.getSection());

		return ResponseEntity.ok().body(new ResponseDTO(cardDto, log));
	}

	@ApiOperation("카드 삭제")
	@DeleteMapping("/cards/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable int id) {
		CardDTO deletedCardDto = cardService.delete(id);
		LogDTO log = logService.delete(Event.DELETE, deletedCardDto.getTitle(), deletedCardDto.getSection());
		return ResponseEntity.ok().body(new ResponseDTO(deletedCardDto, log));
	}

}
