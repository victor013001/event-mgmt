package co.com.techtest.api.mapper;

import co.com.techtest.api.dto.response.inventory.InventoryResponse;
import co.com.techtest.model.inventory.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InventoryApiMapper {

    InventoryApiMapper MAPPER = Mappers.getMapper(InventoryApiMapper.class);

    InventoryResponse toResponse(Inventory inventory);
}
