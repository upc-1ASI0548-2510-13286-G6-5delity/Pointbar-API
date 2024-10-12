package com.metasoft.pointbarmetasoft.tablemanagement.application.services.Implements;

import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.shared.storage.FirebaseFileService;
import com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.requestDto.TableSpaceRequestDto;
import com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.responseDto.TableSpaceResponseDto;
import com.metasoft.pointbarmetasoft.tablemanagement.domain.entities.TableSpace;
import com.metasoft.pointbarmetasoft.tablemanagement.infraestructure.repositories.TableSpaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableSpaceServiceImplTest {

    @Mock
    private TableSpaceRepository tableSpaceRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private FirebaseFileService firebaseFileService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TableSpaceServiceImpl tableSpaceService;

    private Long businessId = 1L;
    private Long tableSpaceId = 1L;


    @Test
    void getAllTableSpaces() {
        // Arrange
        TableSpace tableSpace = new TableSpace();
        tableSpace.setId(tableSpaceId);
        tableSpace.setName("Salon Principal");

        when(tableSpaceRepository.findByBusinessId(businessId)).thenReturn(Collections.singletonList(tableSpace));
        when(modelMapper.map(tableSpace, TableSpaceResponseDto.class)).thenReturn(new TableSpaceResponseDto());

        // Act
        List<TableSpaceResponseDto> response = tableSpaceService.getAllTableSpaces(businessId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(tableSpaceRepository, times(1)).findByBusinessId(businessId);
    }

    @Test
    void updateTableSpace() throws IOException {
        // Arrange
        TableSpaceRequestDto requestDto = new TableSpaceRequestDto();
        requestDto.setName("Sala Principal Actualizado");
        requestDto.setNumberOfTables(15);

        Business business = new Business();
        business.setId(businessId);

        TableSpace existingTableSpace = new TableSpace();
        existingTableSpace.setId(tableSpaceId);
        existingTableSpace.setBusiness(business);

        when(tableSpaceRepository.findById(tableSpaceId)).thenReturn(Optional.of(existingTableSpace));

        MockMultipartFile mockImage = new MockMultipartFile("image", "image.png", "image/png", "imageData".getBytes());
        requestDto.setImage(mockImage);

        when(firebaseFileService.saveImage(any())).thenReturn("newImageUrl");

        when(tableSpaceRepository.save(any(TableSpace.class))).thenReturn(existingTableSpace);

        // Act
        ApiResponse<?> response = tableSpaceService.updateTableSpace(tableSpaceId, requestDto, businessId);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Table space updated successfully", response.getMessage());
    }

    @Test
    void deleteTableSpace() {
        // Arrange
        Business business = new Business();
        business.setId(businessId);

        TableSpace existingTableSpace = new TableSpace();
        existingTableSpace.setId(tableSpaceId);
        existingTableSpace.setBusiness(business);

        when(tableSpaceRepository.findById(tableSpaceId)).thenReturn(Optional.of(existingTableSpace));

        // Act
        ApiResponse<?> response = tableSpaceService.deleteTableSpace(tableSpaceId, businessId);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Table space deleted successfully", response.getMessage());

        verify(tableSpaceRepository, times(1)).delete(existingTableSpace);
    }
}