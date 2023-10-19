package com.codesquad.controlG.domain.group.controller;

import com.codesquad.controlG.domain.group.dto.GroupCreateRequest;
import com.codesquad.controlG.domain.group.service.GroupService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/groups")
@RestController
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Void> createGroup(@ModelAttribute @Valid GroupCreateRequest groupCreateRequest) {
        groupService.create(groupCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
