package com.malpro.importer.processor;

import com.malpro.importer.dto.ItemDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

/**
 * Created by fahian on 02.12.22.
 */
public class ItemDtoProcessor implements ItemProcessor<ItemDto, ItemDto> {

    @Override
    public ItemDto process(@NonNull final ItemDto itemDto) {

        System.out.println("Processing " + itemDto );

        return itemDto;
    }

}
