package com.whatthefork.resourcereservation.resource.controller;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateConferenceRoomRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateConferenceRoomRequest;
import com.whatthefork.resourcereservation.resource.dto.response.ConferenceRoomResponse;
import com.whatthefork.resourcereservation.resource.service.ConferenceRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "ConferenceRoom", description = "회의실 API (추가, 수정, 삭제, 조회")
@Slf4j
@RestController
@RequestMapping("/conference-rooms")
@RequiredArgsConstructor
public class ConferenceRoomController {

    private final ConferenceRoomService conferenceRoomService;

    @Operation(summary = "회의실 전체 조회", description = "존재하는 회의실 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponse> conferenceRoom() {

        List<ConferenceRoomResponse> conferenceRoomList = conferenceRoomService.getAllConferenceRooms();

        log.info("get all conference rooms: {} ", conferenceRoomList);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomList));
    }

    @Operation(summary = "회의실 추가", description = "새로운 회의실 추가")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createConferenceRoom(@RequestBody CreateConferenceRoomRequest conferenceRoom) {
        log.info("create conference room: {}", conferenceRoom);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomService.createConferenceRoom(conferenceRoom)));
    }

    @Operation(summary = "회의실 삭제", description = "존재하는 회의실 삭제")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteConferenceRoom(@PathVariable Long id) {
        log.info("delete conference room: {}", id);

        conferenceRoomService.deleteConferenceRoom(id);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "회의실 수정", description = "회의실에 대한 정보 수정")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateConferenceRoom(@PathVariable Long id, @RequestBody UpdateConferenceRoomRequest roomRequest) {
        log.info("update conference room: {}", id);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomService.updateConferenceRoomById(id, roomRequest)));
    }

    @Operation(summary = "회의실 이름 검색", description = "이름으로 회의실 검색")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getConferenceRoomById(@PathVariable String name) {
        log.info("get conference room by name: {}", name);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomService.getConferenceRoomByName(name)));
    }

    @Operation(summary = "회의실 최대인원 검색", description = "회의실 최대 인원으로 검색")
    @GetMapping("/maxCapacity/{maxCapacity}")
    public ResponseEntity<ApiResponse> getConferenceRoomByMaxCapacity(@PathVariable int maxCapacity) {
        log.info("get conference room by maxCapacity: {}", maxCapacity);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomService.getConferenceRoomByMaxCapacity(maxCapacity)));
    }
}