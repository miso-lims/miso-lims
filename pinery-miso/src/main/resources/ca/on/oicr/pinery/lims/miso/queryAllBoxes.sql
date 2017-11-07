SELECT bp.targetId, bp.targetType, bp.position, bp.boxId, b.alias, b.locationBarcode, 
b.description, bs.rows, bs.columns 
FROM BoxPosition bp 
JOIN Box b ON b.boxId = bp.boxId 
JOIN BoxSize bs ON bs.boxSizeId = b.boxSizeId
