package com.malpro.importer.step;

import com.malpro.importer.dto.ItemDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

/**
 * Created by fahian on 02.12.22.
 */
@Slf4j
public class ItemDtoProcessor implements ItemProcessor<ItemDto, ItemDto> {

    @Override
    public ItemDto process(@NonNull final ItemDto itemDto) {

        log.debug("Processing {}", itemDto.getEtimClass() );

        return itemDto;
    }

}
