package com.team05.todolist.controller;

import com.team05.todolist.domain.dto.ClassifiedCardsDTO;
import com.team05.todolist.domain.dto.ListResponseDTO;
import com.team05.todolist.domain.dto.LogDTO;
import com.team05.todolist.service.CardService;
import com.team05.todolist.service.LogService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class HomeController {

	private final CardService cardService;
	private final LogService logService;
	private final Logger logger = LoggerFactory.getLogger(CardController.class);

	public HomeController(CardService cardService, LogService logService) {
		this.cardService = cardService;
		this.logService = logService;
	}

	@ApiOperation("전체 카드 조회")
	@GetMapping("/cards")
	public ResponseEntity<ListResponseDTO> inquireAll() {
		ClassifiedCardsDTO classifiedCards = cardService.findCards();
		List<LogDTO> logs = logService.findLogs(0, 10);
		return ResponseEntity.ok().body(new ListResponseDTO(classifiedCards, logs));
	}

	@ApiOperation("전체 인덱스 갱신")
	@GetMapping("/batch-process")
	public ResponseEntity<String> batchProcess() {
		cardService.batchProcess();
		return ResponseEntity.ok().body("batch process complete!");
	}

	@ApiOperation("Log 페이징 - nowNumberOfLogs: 현재 로그 개수, number: 추가적으로 원하는 로그 개수")
	@GetMapping("/logs")
	public ResponseEntity<List<LogDTO>> logPaging(int nowNumberOfLogs, int number) {
		List<LogDTO> logs = logService.findLogs(nowNumberOfLogs, number);
		logger.debug("넘겨주는 log의 수: {}", logs.size());
		return ResponseEntity.ok().body(logs);
	}
}