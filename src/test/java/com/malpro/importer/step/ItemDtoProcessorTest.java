package com.malpro.importer.step;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.malpro.importer.dto.ItemDto;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
class ItemDtoProcessorTest {

    @InjectMocks
    private ItemDtoProcessor itemDtoProcessor;

    @Test
    @DisplayName("Item processed test")
    void itemProcessedTest(@Random ItemDto itemDto) {

        if(itemDto != null) {
            ItemDto returnedItemDto = itemDtoProcessor.process(itemDto);
            assertThat(returnedItemDto, Matchers.equalTo(itemDto));
        }
    }
}
