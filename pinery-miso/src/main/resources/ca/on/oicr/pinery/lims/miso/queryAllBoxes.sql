SELECT bp.targetId, bp.targetType, bp.position, b.boxId, b.alias, b.locationBarcode, 
b.description, bs.rows, bs.columns 
FROM BoxPosition bp 
RIGHT JOIN Box b ON b.boxId = bp.boxId 
JOIN BoxSize bs ON bs.boxSizeId = b.boxSizeId
