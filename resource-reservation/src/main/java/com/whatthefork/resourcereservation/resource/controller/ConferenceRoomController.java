package com.whatthefork.resourcereservation.resource.controller;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateConferenceRoomRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateConferenceRoomRequest;
import com.whatthefork.resourcereservation.resource.dto.response.ConferenceRoomResponse;
import com.whatthefork.resourcereservation.resource.service.ConferenceRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/conference-rooms")
@RequiredArgsConstructor
public class ConferenceRoomController {

    private final ConferenceRoomService conferenceRoomService;

    @GetMapping
    public ResponseEntity<ApiResponse> conferenceRoom() {

        List<ConferenceRoomResponse> conferenceRoomList = conferenceRoomService.getAllConferenceRooms();

        log.info("get all conference rooms: {} ", conferenceRoomList);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomList));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createConferenceRoom(@RequestBody CreateConferenceRoomRequest conferenceRoom) {
        log.info("create conference room: {}", conferenceRoom);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomService.createConferenceRoom(conferenceRoom)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteConferenceRoom(@PathVariable Long id) {
        log.info("delete conference room: {}", id);

        conferenceRoomService.deleteConferenceRoom(id);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateConferenceRoom(@PathVariable Long id, @RequestBody UpdateConferenceRoomRequest roomRequest) {
        log.info("update conference room: {}", id);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomService.updateConferenceRoomById(id, roomRequest)));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getConferenceRoomById(@PathVariable String name) {
        log.info("get conference room by name: {}", name);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomService.getConferenceRoomByName(name)));
    }

    @GetMapping("/maxCapacity/{maxCapacity}")
    public ResponseEntity<ApiResponse> getConferenceRoomByMaxCapacity(@PathVariable int maxCapacity) {
        log.info("get conference room by maxCapacity: {}", maxCapacity);

        return ResponseEntity.ok(ApiResponse.success(conferenceRoomService.getConferenceRoomByMaxCapacity(maxCapacity)));
    }
}