package co.com.techtest.dynamodb.inventory.mapper;

import co.com.techtest.dynamodb.inventory.model.InventoryData;
import co.com.techtest.model.inventory.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryDataMapper {

    Inventory toDomain(InventoryData inventoryData);

    InventoryData toData(Inventory inventory);
}
