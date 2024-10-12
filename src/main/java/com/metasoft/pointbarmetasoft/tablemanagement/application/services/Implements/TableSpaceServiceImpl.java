package com.metasoft.pointbarmetasoft.tablemanagement.application.services.Implements;
import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.shared.storage.FirebaseFileService;
import com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.requestDto.TableSpaceRequestDto;
import com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.responseDto.TableSpaceResponseDto;
import com.metasoft.pointbarmetasoft.tablemanagement.application.services.ITableSpaceService;
import com.metasoft.pointbarmetasoft.tablemanagement.domain.entities.TableSpace;
import com.metasoft.pointbarmetasoft.tablemanagement.infraestructure.repositories.TableSpaceRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableSpaceServiceImpl implements ITableSpaceService {
    private final TableSpaceRepository tableSpaceRepository;
    private final BusinessRepository businessRepository;
    private final FirebaseFileService firebaseFileService;
    private final ModelMapper modelMapper;

    public TableSpaceServiceImpl(TableSpaceRepository tableSpaceRepository,
                                 BusinessRepository businessRepository,
                                 FirebaseFileService firebaseFileService,
                                 ModelMapper modelMapper) {
        this.tableSpaceRepository = tableSpaceRepository;
        this.businessRepository = businessRepository;
        this.firebaseFileService = firebaseFileService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ApiResponse<?> createTableSpace(TableSpaceRequestDto requestDto, Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        TableSpace tableSpace = new TableSpace();
        tableSpace.setName(requestDto.getName());
        tableSpace.setNumberOfTables(requestDto.getNumberOfTables());
        tableSpace.setBusiness(business);

        if (requestDto.getImage() != null && !requestDto.getImage().isEmpty()) {
            String imageUrl;
            try {
                imageUrl = firebaseFileService.saveImage(requestDto.getImage());
            } catch (IOException e) {
                throw new RuntimeException("Error uploading the image", e);
            }
            tableSpace.setImageUrl(imageUrl);
        }

        TableSpace savedTableSpace = tableSpaceRepository.save(tableSpace);
        return new ApiResponse<>(true, "Table space created successfully", modelMapper.map(savedTableSpace, TableSpaceResponseDto.class));
    }

    @Override
    public List<TableSpaceResponseDto> getAllTableSpaces(Long businessId) {
        List<TableSpace> tableSpaces = tableSpaceRepository.findByBusinessId(businessId);

        return tableSpaces.stream()
                .map(tableSpace -> modelMapper.map(tableSpace, TableSpaceResponseDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public ApiResponse<?> updateTableSpace(Long id, TableSpaceRequestDto requestDto, Long businessId) {
        TableSpace tableSpace = tableSpaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table space not found"));

        if (!tableSpace.getBusiness().getId().equals(businessId)) {
            return new ApiResponse<>(false, "You don't have permission to update this table space", null);
        }

        tableSpace.setName(requestDto.getName());
        tableSpace.setNumberOfTables(requestDto.getNumberOfTables());

        if (requestDto.getImage() != null && !requestDto.getImage().isEmpty()) {
            String imageUrl;
            try {
                imageUrl = firebaseFileService.saveImage(requestDto.getImage());
            } catch (IOException e) {
                throw new RuntimeException("Error uploading the image", e);
            }
            tableSpace.setImageUrl(imageUrl);
        }

        TableSpace updatedTableSpace = tableSpaceRepository.save(tableSpace);
        return new ApiResponse<>(true, "Table space updated successfully", modelMapper.map(updatedTableSpace, TableSpaceResponseDto.class));
    }


    @Override
    public ApiResponse<?> deleteTableSpace(Long id, Long businessId) {
        TableSpace tableSpace = tableSpaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table space not found"));

        if (!tableSpace.getBusiness().getId().equals(businessId)) {
            return new ApiResponse<>(false, "You don't have permission to delete this table space", null);
        }

        tableSpaceRepository.delete(tableSpace);
        return new ApiResponse<>(true, "Table space deleted successfully", null);
    }
}
