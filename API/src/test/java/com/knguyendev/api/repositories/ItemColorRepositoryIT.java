package com.knguyendev.api.repositories;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemColorRepositoryIT {

    private final ItemColorRepository underTest;
    @Autowired
    public ItemColorRepositoryIT(ItemColorRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatItemColorCanBeCreatedAndFound() {
        ItemColorEntity itemColorA = TestUtil.createTestItemColorA();
        underTest.save(itemColorA);
        Optional<ItemColorEntity> result = underTest.findById(itemColorA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(itemColorA);
    }

    @Test
    public void testThatManyItemColorsCanBeCreatedAndFound() {
        ItemColorEntity itemColorA = TestUtil.createTestItemColorA();
        ItemColorEntity itemColorB = TestUtil.createTestItemColorB();
        underTest.save(itemColorA);
        underTest.save(itemColorB);
        Iterable<ItemColorEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(2).
                containsExactly(itemColorA, itemColorB);
    }

    @Test
    public void testThatItemColorCanBeDeleted() {
        ItemColorEntity itemColorA = TestUtil.createTestItemColorA();
        underTest.save(itemColorA);
        underTest.deleteById(itemColorA.getId());
        Optional<ItemColorEntity> result = underTest.findById(itemColorA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatItemColorCanBeUpdated() {
        ItemColorEntity itemColorA = TestUtil.createTestItemColorA();
        underTest.save(itemColorA);
        itemColorA.setName("New Color");
        underTest.save(itemColorA);
        Optional<ItemColorEntity> result = underTest.findById(itemColorA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(itemColorA);
    }

    @Test
    public void testFindByNameOrHexCode() {
        ItemColorEntity itemColorA = TestUtil.createTestItemColorA();
        underTest.save(itemColorA);

        // Check that ItemColor can be found via a good name
        Optional<ItemColorEntity> result1 = underTest.findByNameOrHexCode(itemColorA.getName(), "");
        assertThat(result1).isPresent();
        assertThat(result1.get()).isEqualTo(itemColorA);

        // Check that ItemColor can be found via a hexCode
        Optional<ItemColorEntity> result2 = underTest.findByNameOrHexCode("", itemColorA.getHexCode());
        assertThat(result2).isPresent();
        assertThat(result2.get()).isEqualTo(itemColorA);
    }


}
