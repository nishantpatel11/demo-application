package com.spring.security.demo.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.spring.security.demo.model.Poll;
import com.spring.security.demo.payload.ApiResponse;
import com.spring.security.demo.payload.PagedResponse;
import com.spring.security.demo.payload.PollRequest;
import com.spring.security.demo.payload.PollResponse;
import com.spring.security.demo.payload.VoteRequest;
import com.spring.security.demo.security.CurrentUser;
import com.spring.security.demo.security.UserPrincipal;
import com.spring.security.demo.service.PollService;
import com.spring.security.demo.util.AppConstants;




@RestController
@RequestMapping(AppConstants.API+AppConstants.POLLS)
public class PollController {

	@Autowired
	private PollService pollService;

	@GetMapping
	public PagedResponse<PollResponse> getPolls(@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
		return pollService.getAllPolls(currentUser, page, size);
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest) {
		Poll poll = pollService.createPoll(pollRequest);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{pollId}")
				.buildAndExpand(poll.getId()).toUri();

		return ResponseEntity.created(location)
				.body(new ApiResponse(true, "Poll Created Successfully"));
	}


	@GetMapping("/{pollId}")
	public PollResponse getPollById(@CurrentUser UserPrincipal currentUser,
			@PathVariable Long pollId) {
		return pollService.getPollById(pollId, currentUser);
	}

	@PostMapping("/{pollId}"+AppConstants.VOTES)
	@PreAuthorize("hasRole('USER')")
	public PollResponse castVote(@CurrentUser UserPrincipal currentUser,
			@PathVariable Long pollId,
			@Valid @RequestBody VoteRequest voteRequest) {
		return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, currentUser);
	}
}
