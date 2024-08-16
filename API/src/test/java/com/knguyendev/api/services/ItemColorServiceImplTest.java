package com.knguyendev.api.services;


import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.ItemColorMapper;
import com.knguyendev.api.repositories.ItemColorRepository;
import com.knguyendev.api.services.impl.ItemColorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * When testing the service layer, it should be unit tests. Your tests should run very
 * fast, and you shouldn't be actually interacting with a database, in memory or whatever.
 * So you need to mock the database interaction.
 * <p>
 * Also, when you use some other code, you don't want it to change. You want it to be non-deterministic,
 * meaning whatever you're unit testing, the code that you're using always returns the same value. Your functions
 * should be able to return the same outputs given the same inputs.
 * The inclusion of a database may make it so that your tests and code aren't non-deterministic (unpredictable) and
 * that's why we mock things.
 * <p>
 * So you'd mock the repository, but when you would also mock repository methods such as
 * someRepository.save() or someRepository.findById(). We're mocking the repository layer which gives us more control on
 * what is tested, rather than relying on the database. This also makes our tests quicker.
 * <p>
 *  We use '@InjectMocks' to inject in our service class. So this indicates the thing we are mocking, and this allows
 *  Mockito to look at any other
 * <p>
 * So this is the service we want to test, and usually these classes have dependencies. This annotation will create an
 * instance of the ItemColorServiceImpl class, and then it'll inject any mock dependencies into the class. These are
 * dependencies that are created using '@Mock' or '@Spy' annotation. So for the ItemColorMapper, it injects a mock version of
 * this type and replaces it in our service class, and so on.
 * <p>
 * + Testing our own code:
 * Before when we were testing repositories, we kind of didn't have to look at what we were testing. It was kind of obvious.
 * Now though, we need to make sure when we are using our mocked dependencies and act accordingly.
 * <p>
 * <a href="https://www.youtube.com/watch?v=4l3EFprMqpU&list=PL82C6-O4XrHcg8sNwpoDDhcxUCbFy855E&index=5">...</a>
 */
@ExtendWith(MockitoExtension.class) // allows us to mock in our tests
public class ItemColorServiceImplTest {

    // The service we want to test
    @InjectMocks
    private ItemColorServiceImpl itemColorService;

    // Then we need to mock any dependencies of that service. So our implementation includes mappers and repositories
    // So we'll mock those as well
    @Mock
    private ItemColorMapper itemColorMapper;

    // Mocking our itemColorRepository
    @Mock
    private ItemColorRepository itemColorRepository;

    // Test for creating a new item color successfully
    @Test
    public void testCreateWhenSuccess() {
        // Arrange test data
        ItemColorCreateDTO itemColorCreateDTO = TestUtil.createItemColorCreateDTOA();
        ItemColorEntity unsavedItemColor = TestUtil.createUnsavedItemColorA();
        ItemColorEntity savedItemColor = TestUtil.createSavedItemColorA();
        ItemColorDTO expectedDTO = TestUtil.createItemColorDTOA();

        // Simulate: No conflict or existing item color, and also mapping and saving
        when(itemColorRepository.findByNameOrHexCode(itemColorCreateDTO.getName(), itemColorCreateDTO.getHexCode()))
                .thenReturn(Optional.empty());

        when(itemColorMapper.toEntity(itemColorCreateDTO)).thenReturn(unsavedItemColor);
        when(itemColorRepository.save(unsavedItemColor)).thenReturn(savedItemColor);
        when(itemColorMapper.toDTO(savedItemColor)).thenReturn(expectedDTO);

        // Act
        ItemColorDTO resultDTO = itemColorService.create(itemColorCreateDTO);

        // Assert results were good
        assertEquals(expectedDTO, resultDTO);

        // Verify dependency functions were called
        verify(itemColorRepository, times(1)).findByNameOrHexCode(itemColorCreateDTO.getName(), itemColorCreateDTO.getHexCode());
        verify(itemColorMapper, times(1)).toEntity(itemColorCreateDTO);
        verify(itemColorMapper, times(1)).toDTO(savedItemColor);
    }

    @Test
    public void testCreateWhenNameAlreadyExists(){
        ItemColorCreateDTO createDTO = TestUtil.createItemColorCreateDTOA();
        ItemColorEntity existingItemColor = TestUtil.createSavedItemColorA();

        // Make the hex codes are different to ensure only the name is the same
        createDTO.setHexCode("HexCodeA");
        existingItemColor.setHexCode("HexCodeB");

        // Simulate that we found an existing entity.
        when(itemColorRepository.findByNameOrHexCode(createDTO.getName(), createDTO.getHexCode()))
                .thenReturn(Optional.of(existingItemColor));


        ServiceException exception = assertThrows(ServiceException.class, () -> itemColorService.create(createDTO));

        assertEquals("ItemColor with the same name already exists!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testCreateWhenHexCodeAlreadyExists() {
        ItemColorCreateDTO createDTO = TestUtil.createItemColorCreateDTOA();
        ItemColorEntity existingItemColor = TestUtil.createSavedItemColorA();

        // Make the names are different to ensure only the hexCode is the same
        createDTO.setName("NameA");
        existingItemColor.setName("NameB");

        // Simulate that we found an existing entity.
        when(itemColorRepository.findByNameOrHexCode(createDTO.getName(), createDTO.getHexCode()))
                .thenReturn(Optional.of(existingItemColor));


        ServiceException exception = assertThrows(ServiceException.class, () -> itemColorService.create(createDTO));

        assertEquals("ItemColor with the same hexCode already exists!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testFindAll() {
        // Arrange
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA();
        ItemColorEntity itemColorB = TestUtil.createSavedItemColorB();
        ItemColorDTO itemColorDTOA = TestUtil.createItemColorDTOA();
        ItemColorDTO itemColorDTOB = TestUtil.createItemColorDTOB();
        List<ItemColorEntity> itemColors = List.of(itemColorA, itemColorB);

        // Simulate repository method and mapping
        when(itemColorRepository.findAll()).thenReturn(itemColors);
        when(itemColorMapper.toDTO(itemColorA)).thenReturn(itemColorDTOA);
        when(itemColorMapper.toDTO(itemColorB)).thenReturn(itemColorDTOB);

        // Act
        List<ItemColorDTO> result = itemColorService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(itemColorDTOA, result.get(0));
        assertEquals(itemColorDTOB, result.get(1));

        // Verify
        verify(itemColorRepository, times(1)).findAll();
        verify(itemColorMapper, times(1)).toDTO(itemColorA);
        verify(itemColorMapper, times(1)).toDTO(itemColorB);
    }

    @Test
    public void testFindByIdWhenSuccess() {
        // Arrange
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA();
        ItemColorDTO expectedDTO = TestUtil.createItemColorDTOA();

        // Simulate
        when(itemColorRepository.findById(itemColorA.getId())).thenReturn(Optional.of(itemColorA));
        when(itemColorMapper.toDTO(itemColorA)).thenReturn(expectedDTO);

        // Act
        ItemColorDTO result = itemColorService.findById(itemColorA.getId());

        // Assert
        assertEquals(expectedDTO, result);

        // Verify
        verify(itemColorRepository, times(1)).findById(itemColorA.getId());
        verify(itemColorMapper, times(1)).toDTO(itemColorA);
    }

    @Test
    public void testFindByIdWhenNotFound() {
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA();

        // Simulate
        when(itemColorRepository.findById(itemColorA.getId())).thenReturn(Optional.empty());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> itemColorService.findById(itemColorA.getId()));

        // Assert
        assertEquals("ItemColor not found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        // Verify
        verify(itemColorRepository, times(1)).findById(itemColorA.getId());
    }

    @Test
    public void testUpdateByIdWhenNotFound() {
        // Arrange
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA();
        ItemColorCreateDTO createDTOB = TestUtil.createItemColorCreateDTOB();

        // Simulate
        when(itemColorRepository.findById(itemColorA.getId())).thenReturn(
                Optional.empty()
        );

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> itemColorService.updateById(itemColorA.getId(), createDTOB));

        // Assert
        assertEquals("Item color being updated not found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        // Verify
        verify(itemColorRepository, times(1)).findById(itemColorA.getId());
    }

    @Test
    public void testUpdateByIdWhenNothingChanged() {
        // Arrange
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA();
        ItemColorCreateDTO createDTOA = TestUtil.createItemColorCreateDTOA();
        ItemColorDTO expectedDTO = TestUtil.createItemColorDTOA();

        // Simulate
        when(itemColorRepository.findById(itemColorA.getId())).thenReturn(
                Optional.of(itemColorA)
        );
        when(itemColorMapper.toDTO(itemColorA)).thenReturn(expectedDTO);

        // Act
        ItemColorDTO resultDTO = itemColorService.updateById(itemColorA.getId(), createDTOA);

        // Assert
        assertEquals(expectedDTO, resultDTO);

        // Verify the functions that were called
        verify(itemColorRepository, times(1)).findById(itemColorA.getId());
        verify(itemColorMapper, times(1)).toDTO(itemColorA);

        // These functions shouldn't have been called.
        verify(itemColorRepository, never()).findByNameOrHexCode(anyString(), anyString());
        verify(itemColorRepository, never()).save(any(ItemColorEntity.class));
        verify(itemColorMapper, times(1)).toDTO(itemColorA);

    }

    @Test
    public void testUpdateByIdWhenConflictingItemColor() {
        // Arrange
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA(); // ItemColor being updated
        ItemColorEntity itemColorB = TestUtil.createSavedItemColorB(); // Existing item color
        ItemColorCreateDTO createDTOB = TestUtil.createItemColorCreateDTOB(); // DTO containing ItemColor data we're applying

        // Simulate
        when(itemColorRepository.findById(itemColorA.getId())).thenReturn(
                Optional.of(itemColorA)
        );
        when(itemColorRepository.findByNameOrHexCode(createDTOB.getName(), createDTOB.getHexCode())).thenReturn(
                Optional.of(itemColorB)
        );

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> itemColorService.updateById(itemColorA.getId(), createDTOB));

        // Assert
        assertEquals("ItemColor with the same name already exists!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Verify the functions that were called
        verify(itemColorRepository, times(1)).findById(itemColorA.getId());
        verify(itemColorRepository, times(1)).findByNameOrHexCode(createDTOB.getName(), createDTOB.getHexCode());

        // These functions shouldn't have been called.
        verify(itemColorRepository, never()).save(any(ItemColorEntity.class));
        verify(itemColorMapper, never()).toDTO(itemColorA);
    }

    @Test
    public void testUpdateByIdWhenSuccess() {
        // Arrange
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA(); // ItemColor being updated
        ItemColorCreateDTO createDTOB = TestUtil.createItemColorCreateDTOB(); // DTO containing ItemColor data we're applying

        // Create the updated version of itemColorA such that itemColorA has DTO B's data
        ItemColorEntity updatedItemColorA = TestUtil.createSavedItemColorA();
        updatedItemColorA.setName(createDTOB.getName());
        updatedItemColorA.setHexCode(createDTOB.getHexCode());

        // Create our expectedDTO, has the same ID value, but the name and hexCode match the data carried in createDTOB
        ItemColorDTO expectedDTO = TestUtil.createItemColorDTOA();
        expectedDTO.setName(createDTOB.getName());
        expectedDTO.setHexCode(createDTOB.getHexCode());

        // Simulate
        when(itemColorRepository.findById(itemColorA.getId())).thenReturn(
                Optional.of(itemColorA)
        );
        when(itemColorRepository.findByNameOrHexCode(createDTOB.getName(), createDTOB.getHexCode())).thenReturn(
                Optional.empty()
        );
        when(itemColorRepository.save(itemColorA)).thenReturn(updatedItemColorA);
        when(itemColorMapper.toDTO(itemColorA)).thenReturn(expectedDTO);

        // Act
        ItemColorDTO resultDTO = itemColorService.updateById(itemColorA.getId(), createDTOB);

        // Assert
        assertEquals(resultDTO, expectedDTO);

        // Verify functions that were called
        verify(itemColorRepository, times(1)).findById(itemColorA.getId());
        verify(itemColorRepository, times(1)).findByNameOrHexCode(createDTOB.getName(), createDTOB.getHexCode());
        verify(itemColorRepository, times(1)).save(updatedItemColorA);
        verify(itemColorMapper, times(1)).toDTO(itemColorA);
    }

    @Test
    public void testDeleteByIdWhenNotFound() {
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA();

        // Simulate not found
        when(itemColorRepository.findById(itemColorA.getId())).thenReturn(
                Optional.empty()
        );

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> itemColorService.deleteById(itemColorA.getId()));

        // Assert
        assertEquals("ItemColor with id '" + itemColorA.getId() + "' wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        // Verify
        verify(itemColorRepository, times(1)).findById(itemColorA.getId());
        verify(itemColorRepository, never()).deleteById(itemColorA.getId());
        verify(itemColorMapper, never()).toDTO(itemColorA);
    }

    @Test
    public void testDeleteByIdWhenSuccess() {
        ItemColorEntity itemColorA = TestUtil.createSavedItemColorA();
        ItemColorDTO expectedDTO = TestUtil.createItemColorDTOA();

        // Simulate that the ItemColorEntity was found
        when(itemColorRepository.findById(itemColorA.getId())).thenReturn(
                Optional.of(itemColorA)
        );
        when(itemColorMapper.toDTO(itemColorA)).thenReturn(expectedDTO);

        // Act
        ItemColorDTO resultDTO = itemColorService.deleteById(itemColorA.getId());

        // Assert
        assertEquals(resultDTO, expectedDTO);

        // Verify
        verify(itemColorRepository, times(1)).findById(itemColorA.getId());
        verify(itemColorRepository, times(1)).deleteById(itemColorA.getId());
        verify(itemColorMapper, times(1)).toDTO(itemColorA);
    }
}
