package com.whatthefork.resourcereservation.resource.service;

import com.whatthefork.resourcereservation.exception.BusinessException;
import com.whatthefork.resourcereservation.exception.ErrorCode;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateConferenceRoomRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateConferenceRoomRequest;
import com.whatthefork.resourcereservation.resource.dto.response.ConferenceRoomResponse;
import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import com.whatthefork.resourcereservation.resource.repository.ConferenceRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConferenceRoomService {

    private final ConferenceRoomRepository conferenceRoomRepository;

    public List<ConferenceRoomResponse> getAllConferenceRooms() {

        List<ConferenceRoom> conferenceRoomEntityList = new ArrayList<>();

        conferenceRoomEntityList = conferenceRoomRepository.findAll();

        return conferenceRoomEntityList.stream()
                .map(resource -> {return new ConferenceRoomResponse(resource);})
                .collect(Collectors.toList());
    }

    public ConferenceRoomResponse createConferenceRoom(CreateConferenceRoomRequest roomRequest) {

        return new ConferenceRoomResponse(conferenceRoomRepository.save(
                ConferenceRoom.builder()
                        .name(roomRequest.name())
                        .maxCapacity(roomRequest.maxCapacity())
                        .build()
        ));
    }

    public void deleteConferenceRoom(Long id) {

        conferenceRoomRepository.deleteById(id);
    }

    public ConferenceRoomResponse updateConferenceRoomById(Long id, UpdateConferenceRoomRequest roomRequest) {

        ConferenceRoom target = conferenceRoomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        target.updateAll(roomRequest);

        return new ConferenceRoomResponse(conferenceRoomRepository.save(target));
    }

    public ConferenceRoomResponse getResourceById(Long id) {

        return new ConferenceRoomResponse(conferenceRoomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }

    public ConferenceRoomResponse getConferenceRoomByName(String name) {

        return new ConferenceRoomResponse(conferenceRoomRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }

    public ConferenceRoomResponse getConferenceRoomByMaxCapacity(int maxCapacity) {

        return new ConferenceRoomResponse(conferenceRoomRepository.findByMaxCapacity(maxCapacity));
    }
}
