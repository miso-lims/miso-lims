SELECT bp.targetId, bp.targetType, bp.position, b.boxId, b.alias, b.locationBarcode, 
b.description, bs.boxSizeRows, bs.boxSizeColumns
FROM BoxPosition bp 
RIGHT JOIN Box b ON b.boxId = bp.boxId 
JOIN BoxSize bs ON bs.boxSizeId = b.boxSizeId
